package com.champgm.intellij.plugin.generation;

import com.champgm.intellij.plugin.Maction;
import com.champgm.intellij.plugin.PluginUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;

public class GenerateToString extends Maction {

    @Override
    protected void doAction(final AnActionEvent actionEvent) {
        // Generate the toString method
        generateToString(actionEvent);

        // Remove full package paths from the statements we just generated
        // and move them into imports
        createImports(actionEvent);
    }

    private void generateToString(final AnActionEvent actionEvent) {
        final PsiClass psiClass = PluginUtil.getPsiClassFromContext(actionEvent);
        final PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiClass.getProject());

        final StringBuilder methodText =
                new StringBuilder("@Override\n")
                        .append("public String toString() {\n")
                        .append("return new org.apache.commons.lang.builder.ToStringBuilder(this)\n");

        for (final PsiField psiField : psiClass.getFields()) {
            methodText.append(".append(\"")
                    .append(psiField.getName())
                    .append("\", ")
                    .append(psiField.getName())
                    .append(")\n");
        }

        methodText.append(".toString();\n}");

        final PsiMethod toStringMethod = elementFactory.createMethodFromText(methodText.toString(), psiClass);
        psiClass.addBefore(toStringMethod, psiClass.getRBrace());
    }
}
