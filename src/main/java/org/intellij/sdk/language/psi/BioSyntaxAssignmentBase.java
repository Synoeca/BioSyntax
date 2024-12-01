package org.intellij.sdk.language.psi;

import com.intellij.psi.PsiElement;

public interface BioSyntaxAssignmentBase extends PsiElement {
    String getVariableName();
    String getValue();
}
