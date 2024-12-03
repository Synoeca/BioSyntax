package org.intellij.sdk.language;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.intellij.sdk.language.psi.BioSyntaxGeneBody;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BioSyntaxCompletionContributor extends CompletionContributor {

    public BioSyntaxCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(),
                new CompletionProvider<>() {
                    @Override
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               @NotNull ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {
                        PsiElement position = parameters.getPosition();

                        boolean inGene = isInGeneContext(position);
                        boolean afterQuote = position.getPrevSibling() != null &&
                                position.getPrevSibling().getText().equals("\"");

                        if (afterQuote && inGene && isAfterEquals(position)) {
                            addCodonCompletions(resultSet, parameters);
                        } else if (!inGene) {
                            addGlobalCompletions(resultSet);
                        } else if (isAfterEquals(position)) {
                            addCodonCompletions(resultSet, parameters);
                        } else {
                            addGeneBodyCompletions(resultSet, parameters);
                        }
                    }
                }
        );
    }

    private boolean isInGeneContext(PsiElement position) {
        boolean inGeneBlock = false;
        boolean afterGene = false;
        boolean foundLBrace = false;

        PsiElement current = position;
        while (current != null) {
            String text = current.getText();

            switch (text) {
                case "}" -> {
                    if (position.getTextOffset() > current.getTextOffset()) {
                        return false;
                    }
                }
                case "{" -> foundLBrace = true;
                case "Gene" -> afterGene = true;
            }

            if (foundLBrace && afterGene) {
                inGeneBlock = true;
                break;
            }

            current = PsiTreeUtil.prevVisibleLeaf(current);
        }

        return inGeneBlock;
    }

    private boolean isAfterEquals(PsiElement position) {
        PsiElement prev = PsiTreeUtil.prevLeaf(position);

        if (prev != null && (prev.getText().equals("\"") || prev.getText().equals("="))) {
            PsiElement property = findPropertyElement(position);
            return property != null &&
                    (property.getText().equals("Start_Codon") ||
                            property.getText().equals("Stop_Codon"));
        }
        return false;
    }

    private PsiElement findPropertyElement(PsiElement position) {
        PsiElement current = position;

        while (current != null) {
            String text = current.getText();

            if (text.equals("}")) {
                return null; // Stop if we hit a closing brace
            }
            if (text.equals("Start_Codon") || text.equals("Stop_Codon")) {
                return current;
            }
            current = PsiTreeUtil.prevVisibleLeaf(current);
        }
        return null;
    }

    private void addCodonCompletions(CompletionResultSet resultSet, CompletionParameters parameters) {
        PsiElement position = parameters.getPosition();
        PsiElement property = findPropertyElement(position);

        if (property != null) {
            String propertyText = property.getText();
            if (propertyText.equals("Start_Codon")) {
                resultSet.addElement(LookupElementBuilder.create("ATG"));
            } else if (propertyText.equals("Stop_Codon")) {
                resultSet.addElement(LookupElementBuilder.create("TAA"));
                resultSet.addElement(LookupElementBuilder.create("TAG"));
                resultSet.addElement(LookupElementBuilder.create("TGA"));
            }
        }
    }

    private void addGlobalCompletions(CompletionResultSet resultSet) {
        resultSet.addElement(LookupElementBuilder.create("NtSeq"));
        resultSet.addElement(LookupElementBuilder.create("RNASeq"));
        resultSet.addElement(LookupElementBuilder.create("DNASeq"));
        resultSet.addElement(LookupElementBuilder.create("AASeq"));
        resultSet.addElement(LookupElementBuilder.create("Gene"));
    }

    private void addGeneBodyCompletions(CompletionResultSet resultSet, CompletionParameters parameters) {
        if (!isInsidePropertyValue(parameters.getPosition())) {
            resultSet.addElement(LookupElementBuilder.create("Promoter"));
            resultSet.addElement(LookupElementBuilder.create("Start_Codon"));
            resultSet.addElement(LookupElementBuilder.create("Stop_Codon"));
            resultSet.addElement(LookupElementBuilder.create("Coding_Sequence"));
            resultSet.addElement(LookupElementBuilder.create("Terminator"));
        }
    }

    private boolean isInsidePropertyValue(PsiElement position) {
        PsiElement prev = PsiTreeUtil.prevLeaf(position);
        return prev != null && prev.getText().equals("\"") &&
                PsiTreeUtil.prevVisibleLeaf(prev) != null &&
                Objects.requireNonNull(PsiTreeUtil.prevVisibleLeaf(prev)).getText().equals("=");
    }
}
