package com.champgm.intellij.plugin.finalize;

import com.champgm.intellij.plugin.Maction;
import com.champgm.intellij.plugin.PluginUtil;
import com.google.common.collect.ImmutableSet;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiDeclarationStatement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiForeachStatement;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiVariable;

public class MakeVariablesFinal extends Maction {
    @Override
    protected void doAction(final AnActionEvent actionEvent) {
        final PsiMethod[] methods = PluginUtil.getPsiClassFromContext(actionEvent).getMethods();
        final ImmutableSet.Builder<PsiVariable> unmodifiedLocalVariables = ImmutableSet.builder();
        for (final PsiMethod method : methods) {
            final PsiCodeBlock body = method.getBody();
            if (body != null) {
                final PsiElement[] methodStatements = body.getChildren();
                for (final PsiElement statement : methodStatements) {
                    traverse(unmodifiedLocalVariables, statement);
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

    private void traverse(final ImmutableSet.Builder<PsiVariable> unmodifiedLocalVariables, final PsiElement statement) {
        if (statement instanceof PsiDeclarationStatement) {
            final PsiDeclarationStatement localVariableDeclaration = (PsiDeclarationStatement) statement;
            final PsiElement[] declarationPieces = localVariableDeclaration.getChildren();
            for (final PsiElement psiElement : declarationPieces) {
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
        for (final PsiElement child : statement.getChildren()) {
            traverse(unmodifiedLocalVariables, child);
        }
    }
}
