package org.intellij.sdk.language.psi;

import com.intellij.psi.PsiElement;

public interface BioSyntaxDNASequence extends PsiElement {
    String getSequence();
    boolean isValid();
}
