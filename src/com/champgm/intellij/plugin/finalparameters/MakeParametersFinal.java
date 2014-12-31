package com.champgm.intellij.plugin.finalparameters;



import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.util.PsiTreeUtil;

public class MakeParametersFinal extends AnAction {
    public void actionPerformed(final AnActionEvent actionEvent) {
        final PsiClass psiClass = getPsiClassFromContext(actionEvent);
        new WriteCommandAction.Simple(psiClass.getProject(), psiClass.getContainingFile()) {
            @Override
            protected void run() throws Throwable {
                addNecessaryFinalModifiers(psiClass, actionEvent);
            }
        }.execute();
    }

    private void addNecessaryFinalModifiers(PsiClass psiClass, AnActionEvent actionEvent) {
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
    public void update(AnActionEvent e) {
        PsiClass psiClass = getPsiClassFromContext(e);
        e.getPresentation().setEnabled(psiClass != null);
    }

    private PsiClass getPsiClassFromContext(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if (psiFile == null || editor == null) {
            return null;
        }
        int offset = editor.getCaretModel().getOffset();
        PsiElement elementAt = psiFile.findElementAt(offset);
        return PsiTreeUtil.getParentOfType(elementAt, PsiClass.class);
    }
}