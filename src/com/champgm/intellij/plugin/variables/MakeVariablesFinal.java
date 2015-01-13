package com.champgm.intellij.plugin.variables;

import org.jetbrains.annotations.NotNull;

import com.champgm.intellij.plugin.PluginUtil;
import com.google.common.collect.ImmutableSet;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiCodeBlock;
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
    public void actionPerformed(@NotNull final AnActionEvent actionEvent) {
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
        final ImmutableSet.Builder<PsiVariable> unmodifiedLocalVariables = ImmutableSet.builder();
        for (final PsiMethod method : methods) {
            final PsiCodeBlock body = method.getBody();
            if (body != null) {
                final PsiElement[] methodStatements = body.getChildren();
                for (PsiElement statement : methodStatements) {
                    if (statement instanceof PsiDeclarationStatement) {
                        final PsiDeclarationStatement localVariableDeclaration = (PsiDeclarationStatement) statement;
                        final PsiElement[] declarationPieces = localVariableDeclaration.getChildren();
                        for (PsiElement psiElement : declarationPieces) {
                            if (psiElement instanceof PsiLocalVariable) {
                                final PsiLocalVariable localVariable = (PsiLocalVariable) psiElement;
                                if (!PluginUtil.isModified(localVariable)) {
                                    unmodifiedLocalVariables.add(localVariable);
                                }
                            }
                        }
                    } else {
                        if (statement instanceof PsiForeachStatement) {
                            final PsiForeachStatement foreach = (PsiForeachStatement) statement;
                            final PsiParameter iterationParameter = foreach.getIterationParameter();
                            if (!PluginUtil.isModified(iterationParameter)) {
                                unmodifiedLocalVariables.add(iterationParameter);
                            }
                        }
                    }
                }
            }
        }
        for (final PsiVariable variable : unmodifiedLocalVariables.build()) {
            final PsiModifierList modifierList = variable.getModifierList();
            if (modifierList != null) {
                modifierList.setModifierProperty("final", true);
            }
        }
    }

    @Override
    public void update(@NotNull final AnActionEvent e) {
        final PsiClass psiClass = PluginUtil.getPsiClassFromContext(e);
        e.getPresentation().setEnabled(psiClass != null);
    }
}
