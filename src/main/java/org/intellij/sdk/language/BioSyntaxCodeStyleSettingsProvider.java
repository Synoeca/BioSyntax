package org.intellij.sdk.language;

import com.intellij.application.options.CodeStyleAbstractConfigurable;
import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.application.options.TabbedLanguageCodeStylePanel;
import com.intellij.psi.codeStyle.CodeStyleConfigurable;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import org.jetbrains.annotations.NotNull;

final class BioSyntaxCodeStyleSettingsProvider extends CodeStyleSettingsProvider {

  @Override
  public CustomCodeStyleSettings createCustomSettings(@NotNull CodeStyleSettings settings) {
    return new BioSyntaxCodeStyleSettings(settings);
  }

  @Override
  public String getConfigurableDisplayName() {
    return "BioSyntax";
  }

  @NotNull
  public CodeStyleConfigurable createConfigurable(@NotNull CodeStyleSettings settings,
                                                  @NotNull CodeStyleSettings modelSettings) {
    return new CodeStyleAbstractConfigurable(settings, modelSettings, this.getConfigurableDisplayName()) {
      @Override
      protected @NotNull CodeStyleAbstractPanel createPanel(@NotNull CodeStyleSettings settings) {
        return new BioSyntaxCodeStyleMainPanel(getCurrentSettings(), settings);
      }
    };
  }

  private static class BioSyntaxCodeStyleMainPanel extends TabbedLanguageCodeStylePanel {

    public BioSyntaxCodeStyleMainPanel(CodeStyleSettings currentSettings, CodeStyleSettings settings) {
      super(BioSyntaxLanguage.INSTANCE, currentSettings, settings);
    }

  }

}
