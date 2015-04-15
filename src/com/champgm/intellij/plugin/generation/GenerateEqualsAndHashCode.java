package com.champgm.intellij.plugin.generation;

import com.champgm.intellij.plugin.Maction;
import com.champgm.intellij.plugin.PluginUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;

public class GenerateEqualsAndHashCode extends Maction {

    @Override
    protected void doAction(final AnActionEvent actionEvent) {
        final PsiClass psiClass = PluginUtil.getPsiClassFromContext(actionEvent);
        final PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiClass.getProject());

        // Generate the hashCode method
        generateHashCode(psiClass, elementFactory);

        // Generate the equals method
        generateEquals(psiClass, elementFactory);

        // check the JD for more info on this
        createImports(actionEvent);
    }

    private void generateHashCode(final PsiClass psiClass, final PsiElementFactory elementFactory) {
        final StringBuilder methodText =
                new StringBuilder("@Override\n")
                        .append("public int hashCode() {\n")
                        .append("return new org.apache.commons.lang.builder.HashCodeBuilder()\n");

        for (final PsiField psiField : psiClass.getFields()) {
            final PsiModifierList modifierList = psiField.getModifierList();
            if (modifierList == null ||
                    !modifierList.hasExplicitModifier("static")) {
                methodText.append(".append(")
                        .append(psiField.getName())
                        .append(")\n");
            }
        }

        methodText.append(".toHashCode();\n}");

        final PsiMethod toStringMethod = elementFactory.createMethodFromText(methodText.toString(), psiClass);
        final PsiElement addingAnchor = psiClass.getRBrace();
        psiClass.addBefore(toStringMethod, addingAnchor);
    }

    private void generateEquals(final PsiClass psiClass, final PsiElementFactory elementFactory) {
        final StringBuilder methodText =
                new StringBuilder("@Override\n")
                        .append("public boolean equals(final Object obj) {\n")
                        // This check
                        .append("if (obj == this) {\n")
                        .append("return true;\n")
                        .append("}\n\n")
                        // Class type check
                        .append("if (obj instanceof ")
                        .append(psiClass.getQualifiedName())
                        .append(") {")
                        // class cast
                        .append("final ")
                        .append(psiClass.getQualifiedName())
                        .append(" other = (")
                        .append(psiClass.getQualifiedName())
                        .append(") obj;\n")

                        // Start building the equals
                        .append("return new org.apache.commons.lang.builder.EqualsBuilder()\n");

        for (final PsiField psiField : psiClass.getFields()) {
            final PsiModifierList modifierList = psiField.getModifierList();
            final String fieldName = psiField.getName();
            // If the field is public, i.e. it can be accessed directly, we will do so
            if (modifierList == null ||
                    !modifierList.hasExplicitModifier("static")) {
                methodText.append(".append(")
                        .append(fieldName)
                        .append(", other.")
                        .append(fieldName)
                        .append(")\n");
            }
        }

        methodText.append(".isEquals();\n}")
                .append("return false;\n}");

        final PsiMethod toStringMethod = elementFactory.createMethodFromText(methodText.toString(), psiClass);
        psiClass.addBefore(toStringMethod, psiClass.getRBrace());
    }
}
