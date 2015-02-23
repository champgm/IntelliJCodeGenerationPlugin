package com.champgm.intellij.plugin;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;

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

    /**
     * It turns out that creating proper/acceptable imports is a ridiculously complicated process with the given tools.
     * I found this suggested workaround in the IntelliJ forums. Basically just add all classes with their FQDNs and
     * then trigger the project's code style manager and it should organize the imports for the user
     */
    protected void createImports(final AnActionEvent actionEvent) {
        final Project currentProject = getEventProject(actionEvent);
        final PsiFile currentFile = actionEvent.getData(LangDataKeys.PSI_FILE);
        if (currentProject != null && currentFile != null) {
            // Get an instance of this project's coding-style manager
            JavaCodeStyleManager.getInstance(currentProject)
                    // Tell it to shorten all class references accordingly
                    .shortenClassReferences(currentFile);
        }
    }

    protected abstract void doAction(AnActionEvent actionEvent);
}
