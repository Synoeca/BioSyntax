package org.intellij.sdk.language.psi;

import com.intellij.psi.tree.IElementType;
import org.intellij.sdk.language.BioSyntaxLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class BioSyntaxTokenType extends IElementType {

  public BioSyntaxTokenType(@NotNull @NonNls String debugName) {
    super(debugName, BioSyntaxLanguage.INSTANCE);
  }

  @Override
  public String toString() {
    return "BioSyntaxTokenType." + super.toString();
  }

}
