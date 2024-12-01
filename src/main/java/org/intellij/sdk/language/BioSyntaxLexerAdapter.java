package org.intellij.sdk.language;

import com.intellij.lexer.FlexAdapter;

public class BioSyntaxLexerAdapter extends FlexAdapter {

  public BioSyntaxLexerAdapter() {
    super(new BioSyntaxLexer(null));
  }

}
