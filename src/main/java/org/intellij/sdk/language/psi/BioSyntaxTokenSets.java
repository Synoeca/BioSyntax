package org.intellij.sdk.language.psi;

import com.intellij.psi.tree.TokenSet;

public interface BioSyntaxTokenSets {

  TokenSet IDENTIFIERS = TokenSet.create(BioSyntaxTypes.KEY);

  TokenSet COMMENTS = TokenSet.create(BioSyntaxTypes.COMMENT);

}
