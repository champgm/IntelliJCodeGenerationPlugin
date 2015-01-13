package com.champgm.intellij.plugin;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiAssignmentExpression;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;

public class PluginUtil {
    public static PsiClass getPsiClassFromContext(final AnActionEvent e) {
        final PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        final Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if (psiFile == null || editor == null) {
            return null;
        }
        final int offset = editor.getCaretModel().getOffset();
        final PsiElement elementAt = psiFile.findElementAt(offset);
        return PsiTreeUtil.getParentOfType(elementAt, PsiClass.class);
    }

    /**
     * Checks if an element is modified anywhere. Useful for determining if an element can be declared as final.
     *
     * @param element
     *            the element to check for modifications
     * @param ignoredAssignments
     *            a set of assignments that should be ignored. Use
     *            {@link com.champgm.intellij.plugin.PluginUtil#getConstructorAssignmentExpressions} to extract a set of
     *            constructor's assignments from a given class
     * @return true or false
     */
    public static boolean isModified(final PsiElement element, final Set<PsiAssignmentExpression> ignoredAssignments) {
        // All references to those elements
        final Collection<PsiReference> references = ReferencesSearch.search(element).findAll();
        boolean modified = false;
        for (final PsiReference psiReference : references) {
            // The piece of a statement which uses the reference to retrieve the field
            final PsiElement referringElement = psiReference.getElement();
            // The full statement containing the referring element
            final PsiElement parent = referringElement.getParent();

            // Thankfully, an assignment is an easily identifiable type of element
            if (parent instanceof PsiAssignmentExpression) {
                // But... make sure the assignment isn't to be ignored
                if (!ignoredAssignments.contains(parent)) {
                    modified = true;
                }
            }
        }
        return modified;
    }

    /**
     * Checks if an element is modified anywhere. Useful for determining if an element can be declared as final.
     *
     * @param element
     *            the element to check for modifications
     * @return true or false
     */
    public static boolean isModified(final PsiElement element) {
        return isModified(element, ImmutableSet.<PsiAssignmentExpression> of());
    }

    /**
     * This will look through all constructors of a given {@link com.intellij.psi.PsiClass} and return all assignment
     * expressions in those constructors. This method is to be used with
     * {@link com.champgm.intellij.plugin.PluginUtil#isModified} to exclude constructor modification/assignment
     * 
     * @param psiClass
     *            the class whose constructors need to be inspected
     * @return
     *         an {@link com.google.common.collect.ImmutableSet} containing all
     *         {@link com.intellij.psi.PsiAssignmentExpression}s
     */
    public static ImmutableSet<PsiAssignmentExpression> getConstructorAssignmentExpressions(final PsiClass psiClass) {
        // Before we start, we need to make a list of all assignments in constructors. These assignments should be
        // considered initializers and not modifiers.
        final PsiMethod[] constructors = psiClass.getConstructors();
        final ImmutableSet.Builder<PsiAssignmentExpression> constructorAssignmentsBuilder = ImmutableSet.builder();
        for (final PsiMethod constructor : constructors) {
            final PsiCodeBlock body = constructor.getBody();
            if (body != null) {
                // get their contents
                for (PsiStatement statement : body.getStatements()) {
                    final PsiElement actualStatement = statement.getFirstChild();
                    // Check for assignments
                    if (actualStatement instanceof PsiAssignmentExpression) {
                        // add them to the constructor map
                        constructorAssignmentsBuilder.add((PsiAssignmentExpression) actualStatement);
                    }
                }
            }
        }
        return constructorAssignmentsBuilder.build();
    }
}
