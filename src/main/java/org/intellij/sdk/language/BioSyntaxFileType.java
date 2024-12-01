package org.intellij.sdk.language;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public final class BioSyntaxFileType extends LanguageFileType {

  public static final BioSyntaxFileType INSTANCE = new BioSyntaxFileType();

  private BioSyntaxFileType() {
    super(BioSyntaxLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public String getName() {
    return "BioSyntax File";
  }

  @NotNull
  @Override
  public String getDescription() {
    return "BioSyntax language file";
  }

  @NotNull
  @Override
  public String getDefaultExtension() {
    return "biosyntax";
  }

  @Override
  public Icon getIcon() {
    return BioSyntaxIcons.FILE;
  }

}
