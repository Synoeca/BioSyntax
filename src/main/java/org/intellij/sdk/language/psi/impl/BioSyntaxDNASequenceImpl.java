package org.intellij.sdk.language.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.intellij.sdk.language.psi.BioSyntaxDNASequence;
import org.jetbrains.annotations.NotNull;

public class BioSyntaxDNASequenceImpl extends ASTWrapperPsiElement implements BioSyntaxDNASequence {
    public BioSyntaxDNASequenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getSequence() {
        return getText().replaceAll("\\s+", "");
    }

    @Override
    public boolean isValid() {
        return getSequence().matches("[ATCG]+");
    }
}
