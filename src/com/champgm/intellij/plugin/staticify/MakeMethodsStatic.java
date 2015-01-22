package com.champgm.intellij.plugin.staticify;

import com.champgm.intellij.plugin.Maction;
import com.champgm.intellij.plugin.PluginUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.PsiMethod;

public class MakeMethodsStatic extends Maction {
    @Override
    protected void doAction(final AnActionEvent actionEvent) {
        final PsiMethod[] methods = PluginUtil.getPsiClassFromContext(actionEvent).getMethods();
        for (final PsiMethod method : methods) {

        }
    }
}
