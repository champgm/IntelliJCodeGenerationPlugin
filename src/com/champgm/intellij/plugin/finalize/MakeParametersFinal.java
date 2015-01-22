package com.champgm.intellij.plugin.finalize;

import com.champgm.intellij.plugin.Maction;
import com.champgm.intellij.plugin.PluginUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiParameter;

public class MakeParametersFinal extends Maction {
    @Override
    protected void doAction(final AnActionEvent actionEvent) {
        final PsiMethod[] methods = PluginUtil.getPsiClassFromContext(actionEvent).getMethods();
        for (final PsiMethod method : methods) {
            final PsiParameter[] parameters = method.getParameterList().getParameters();
            for (final PsiParameter parameter : parameters) {
                final PsiModifierList modifierList = parameter.getModifierList();
                if (modifierList != null && !modifierList.hasExplicitModifier("final")) {
                    modifierList.setModifierProperty("final", true);
                }
            }
        }
    }
}