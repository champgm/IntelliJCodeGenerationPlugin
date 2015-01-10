package com.champgm.intellij.plugin;

import java.util.Collection;

import com.google.common.collect.ImmutableSet;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiAssignmentExpression;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;

public class PluginUtil {
    public static PsiClass getPsiClassFromContext(final AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if (psiFile == null || editor == null) {
            return null;
        }
        int offset = editor.getCaretModel().getOffset();
        PsiElement elementAt = psiFile.findElementAt(offset);
        return PsiTreeUtil.getParentOfType(elementAt, PsiClass.class);
    }

    public static ImmutableSet<PsiElement> filterOutModifiedElements(Iterable<PsiElement> elements){
        ImmutableSet.Builder<PsiElement> unmodifiedElements = ImmutableSet.builder();
        for (PsiElement element : elements) {
            if (!isModified(element)) {
                unmodifiedElements.add(element);
            }
        }
        return unmodifiedElements.build();
    }

    public static boolean isModified(PsiElement element){
            // All references to those elements
            final Collection<PsiReference> references = ReferencesSearch.search(element).findAll();
            boolean modified = false;
            for (PsiReference psiReference : references) {
                // The piece of a statement which uses the reference to retrieve the field
                final PsiElement referringElement = psiReference.getElement();
                // The full statement containing the referring element
                final PsiElement parent = referringElement.getParent();

                // Thankfully, an assignment is an easily identifiable type of element
                if (parent instanceof PsiAssignmentExpression) {
                    modified = true;
                }
            }
        return modified;
    }
}


