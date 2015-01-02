package com.champgm.intellij.plugin.parameters;

import com.champgm.intellij.plugin.PluginUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiParameter;

public class MakeParametersFinal extends AnAction {
    public void actionPerformed(final AnActionEvent actionEvent) {
        final PsiClass psiClass = PluginUtil.getPsiClassFromContext(actionEvent);
        new WriteCommandAction.Simple(psiClass.getProject(), psiClass.getContainingFile()) {
            @Override
            protected void run() throws Throwable {
                addNecessaryFinalModifiers(psiClass);
            }
        }.execute();
    }

    private void addNecessaryFinalModifiers(final PsiClass psiClass) {
        final PsiMethod[] methods = psiClass.getMethods();
        for (PsiMethod method : methods) {
            final PsiParameter[] parameters = method.getParameterList().getParameters();
            for (PsiParameter parameter : parameters) {
                final PsiModifierList modifierList = parameter.getModifierList();
                if (modifierList != null && !modifierList.hasExplicitModifier("final")) {
                    modifierList.setModifierProperty("final", true);
                }
            }
        }
    }

    @Override
    public void update(final AnActionEvent e) {
        PsiClass psiClass = PluginUtil.getPsiClassFromContext(e);
        e.getPresentation().setEnabled(psiClass != null);
    }
}