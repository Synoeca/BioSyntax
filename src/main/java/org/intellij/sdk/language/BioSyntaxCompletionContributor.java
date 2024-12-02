package org.intellij.sdk.language;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class BioSyntaxCompletionContributor extends CompletionContributor {

    public BioSyntaxCompletionContributor() {
        // Extend completion for specific contexts
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(BioSyntaxLanguage.INSTANCE), // Adjust to your language
                new CompletionProvider<>() {
                    @Override
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               @NotNull ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {
                        resultSet.addElement(LookupElementBuilder.create("NtSeq"));
                        resultSet.addElement(LookupElementBuilder.create("AASeq"));
                        resultSet.addElement(LookupElementBuilder.create("Gene"));
                        resultSet.addElement(LookupElementBuilder.create("Promoter"));
                        resultSet.addElement(LookupElementBuilder.create("Start_Codon"));
                        resultSet.addElement(LookupElementBuilder.create("Stop_Codon"));
                        resultSet.addElement(LookupElementBuilder.create("Coding_Sequence"));
                        resultSet.addElement(LookupElementBuilder.create("Terminator"));

                        resultSet.addElement(LookupElementBuilder.create("ATG"));
                        resultSet.addElement(LookupElementBuilder.create("TAA"));
                        resultSet.addElement(LookupElementBuilder.create("TAG"));
                        resultSet.addElement(LookupElementBuilder.create("TGA"));
                    }
                }
        );
    }
}