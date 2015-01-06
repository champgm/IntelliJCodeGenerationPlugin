package com.champgm.intellij.plugin.fields;

import java.util.Collection;

import com.champgm.intellij.plugin.PluginUtil;
import com.google.common.collect.ImmutableSet;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiAssignmentExpression;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;

public class MakeFieldsFinal extends AnAction {

    @Override
    public void actionPerformed(final AnActionEvent actionEvent) {
        final PsiClass psiClass = PluginUtil.getPsiClassFromContext(actionEvent);
        new WriteCommandAction.Simple(psiClass.getProject(), psiClass.getContainingFile()) {
            @Override
            protected void run() throws Throwable {
                addNecessaryFinalModifiers(psiClass);
            }
        }.execute();
    }

    /**
     * This hierarchy is a little complex. We start with a field, a variable defined at the class level. We give
     * that to a util, ReferencesSearch, which finds all references to that field. We then iterate through those
     * references and get the element that uses a specific reference to resolve to the original field. Once we
     * have that element, we get that element's parent in order to inspect the context in which the element,
     * which uses the reference to resolve the field, is used. In this case, we're checking to make sure that
     * this element isn't being used to assign a new value to the original field.
     * 
     * Thanks again to Dmitry Jemerov for help with the plugin
     * https://devnet.jetbrains.com/message/5532238
     */
    private void addNecessaryFinalModifiers(final PsiClass psiClass) {
        final PsiField[] fields = psiClass.getFields();
        // Our fields
        ImmutableSet.Builder<PsiField> unmodifiedFields = ImmutableSet.builder();
        for (PsiField field : fields) {
            // All references to those fields
            final Collection<PsiReference> references = ReferencesSearch.search(field).findAll();
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

            if (!modified) {
                unmodifiedFields.add(field);
            }
        }

        for (PsiField field : unmodifiedFields.build()) {
            final PsiModifierList modifierList = field.getModifierList();
            if (modifierList != null) {
                modifierList.setModifierProperty("final", true);
            }
        }
    }

    @Override
    public void update(final AnActionEvent e) {
        PsiClass psiClass = PluginUtil.getPsiClassFromContext(e);
        e.getPresentation().setEnabled(psiClass != null);
    }
}
