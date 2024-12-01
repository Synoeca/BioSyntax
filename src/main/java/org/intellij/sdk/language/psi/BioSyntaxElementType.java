package org.intellij.sdk.language.psi;

import com.intellij.psi.tree.IElementType;
import org.intellij.sdk.language.BioSyntaxLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class BioSyntaxElementType extends IElementType {

  public BioSyntaxElementType(@NotNull @NonNls String debugName) {
    super(debugName, BioSyntaxLanguage.INSTANCE);
  }

}
