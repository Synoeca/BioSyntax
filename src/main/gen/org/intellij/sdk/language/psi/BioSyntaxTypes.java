// This is a generated file. Not intended for manual editing.
package org.intellij.sdk.language.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import org.intellij.sdk.language.psi.impl.*;

public interface BioSyntaxTypes {

  IElementType PROPERTY = new BioSyntaxElementType("PROPERTY");

  IElementType COMMENT = new BioSyntaxTokenType("COMMENT");
  IElementType CRLF = new BioSyntaxTokenType("CRLF");
  IElementType KEY = new BioSyntaxTokenType("KEY");
  IElementType SEPARATOR = new BioSyntaxTokenType("SEPARATOR");
  IElementType VALUE = new BioSyntaxTokenType("VALUE");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == PROPERTY) {
        return new BioSyntaxPropertyImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
