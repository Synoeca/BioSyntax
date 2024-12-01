package org.intellij.sdk.language;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import org.intellij.sdk.language.psi.BioSyntaxTypes;
import org.jetbrains.annotations.NotNull;

final class BioSyntaxCompletionContributor extends CompletionContributor {

  BioSyntaxCompletionContributor() {
    extend(CompletionType.BASIC, PlatformPatterns.psiElement(BioSyntaxTypes.VALUE),
        new CompletionProvider<>() {
          public void addCompletions(@NotNull CompletionParameters parameters,
                                     @NotNull ProcessingContext context,
                                     @NotNull CompletionResultSet resultSet) {
            resultSet.addElement(LookupElementBuilder.create("Hello"));
          }
        }
    );
  }

}
