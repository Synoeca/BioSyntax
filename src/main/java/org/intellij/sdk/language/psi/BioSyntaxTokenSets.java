package org.intellij.sdk.language.psi;

import com.intellij.psi.tree.TokenSet;

public interface BioSyntaxTokenSets {
    TokenSet TYPE_DECLARATIONS = TokenSet.create(
            BioSyntaxTypes.NT_SEQ,
            BioSyntaxTypes.AA_SEQ
    );

    TokenSet REGULATORY_ELEMENTS = TokenSet.create(
            BioSyntaxTypes.PROMOTER,
            BioSyntaxTypes.TERMINATOR
    );

    TokenSet CODONS = TokenSet.create(
            BioSyntaxTypes.START_CODON,
            BioSyntaxTypes.STOP_CODON
    );

    TokenSet COMMENTS = TokenSet.create(BioSyntaxTypes.COMMENT);
}