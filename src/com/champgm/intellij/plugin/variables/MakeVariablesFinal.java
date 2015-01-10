package com.champgm.intellij.plugin.variables;

import com.champgm.intellij.plugin.PluginUtil;
import com.google.common.collect.ImmutableSet;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDeclarationStatement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiForeachStatement;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiVariable;

public class MakeVariablesFinal extends AnAction {
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

    private void addNecessaryFinalModifiers(final PsiClass psiClass) {
        final PsiMethod[] methods = psiClass.getMethods();
        ImmutableSet.Builder<PsiVariable> unmodifiedLocalVariables = ImmutableSet.builder();
        for (PsiMethod method : methods) {
            final PsiElement[] methodStatements = method.getBody().getChildren();
            for (PsiElement statement : methodStatements) {
                if(statement instanceof PsiDeclarationStatement){
                    final PsiDeclarationStatement localVariableDeclaration = (PsiDeclarationStatement) statement;
                    final PsiElement[] declarationPieces = localVariableDeclaration.getChildren();
                    for (PsiElement psiElement : declarationPieces) {
                        if(psiElement instanceof PsiLocalVariable){
                            final PsiLocalVariable localVariable = (PsiLocalVariable) psiElement;
                            if (!PluginUtil.isModified(localVariable)) {
                                unmodifiedLocalVariables.add(localVariable);
                            }
                        }
                    }
                }else{
                    if ( statement instanceof PsiForeachStatement) {
                        final PsiForeachStatement foreach = (PsiForeachStatement) statement;
                        final PsiParameter iterationParameter = foreach.getIterationParameter();
                        if(!PluginUtil.isModified(iterationParameter)) {
                            unmodifiedLocalVariables.add(iterationParameter);
                        }
                    }
                }
            }
        }
        for (PsiVariable variable : unmodifiedLocalVariables.build()) {
            final PsiModifierList modifierList = variable.getModifierList();
            if(modifierList!=null){
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
