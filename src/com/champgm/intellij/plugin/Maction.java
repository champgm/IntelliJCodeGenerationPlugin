package com.champgm.intellij.plugin;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiClass;

public abstract class Maction extends AnAction {

    @Override
    public void actionPerformed(@NotNull final AnActionEvent actionEvent) {
        final PsiClass psiClass = PluginUtil.getPsiClassFromContext(actionEvent);
        new WriteCommandAction.Simple(psiClass.getProject(), psiClass.getContainingFile()) {
            @Override
            protected void run() throws Throwable {
                doAction(actionEvent);
            }
        }.execute();
    }

    @Override
    public void update(@NotNull final AnActionEvent actionEvent) {
        final PsiClass psiClass = PluginUtil.getPsiClassFromContext(actionEvent);
        actionEvent.getPresentation().setEnabled(psiClass != null);
    }

    protected abstract void doAction(AnActionEvent actionEvent);
}
