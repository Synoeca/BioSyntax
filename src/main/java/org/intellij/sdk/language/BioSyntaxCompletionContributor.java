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
                        System.out.println("\n=== Completion Started ===");
                        debugPosition(position);

                        boolean inGene = isInGeneContext(position);
                        boolean afterQuote = position.getPrevSibling() != null &&
                                position.getPrevSibling().getText().equals("\"");

                        System.out.println("After quote: " + afterQuote);
                        System.out.println("\n=== Context Check ===");
                        System.out.println("In Gene Context: " + inGene);
                        System.out.println("After Equals: " + isAfterEquals(position));

                        if (afterQuote && inGene && isAfterEquals(position)) {
                            System.out.println("Adding codon completions in quotes");
                            addCodonCompletions(resultSet, parameters);
                        } else if (!inGene) {
                            System.out.println("Adding global completions");
                            addGlobalCompletions(resultSet);
                        } else if (isAfterEquals(position)) {
                            System.out.println("Adding codon completions");
                            addCodonCompletions(resultSet, parameters);
                        } else {
                            System.out.println("Adding gene body completions");
                            addGeneBodyCompletions(resultSet, parameters);
                        }
                    }
                }
        );
    }

    private boolean isInGeneContext(PsiElement position) {
        debugBacktrack("Starting backtrack", position);
        boolean inGeneBlock = false;
        boolean afterGene = false;
        boolean foundLBrace = false;

        PsiElement current = position;
        while (current != null) {
            String text = current.getText();
            debugBacktrack("Checking element", current);

            switch (text) {
                case "}" -> {
                    debugBacktrack("Found RBRACE", current);
                    if (position.getTextOffset() > current.getTextOffset()) {
                        return false;
                    }
                }
                case "{" -> {
                    debugBacktrack("Found LBRACE", current);
                    foundLBrace = true;
                }
                case "Gene" -> {
                    debugBacktrack("Found Gene", current);
                    afterGene = true;
                }
            }

            if (foundLBrace && afterGene) {
                inGeneBlock = true;
                break;
            }

            current = PsiTreeUtil.prevVisibleLeaf(current);
        }

        debugBacktrack(position,
                "inGeneBlock=" + inGeneBlock +
                        ", afterGene=" + afterGene +
                        ", foundLBrace=" + foundLBrace);
        return inGeneBlock;
    }

    private void debugBacktrack(String context, PsiElement element) {
        System.out.println("BACKTRACK [" + context + "] " +
                "Text='" + element.getText() + "' " +
                "Class=" + element.getClass().getSimpleName() + " " +
                "Offset=" + element.getTextOffset());
    }

    private void debugBacktrack(PsiElement element, String extra) {
        System.out.println("BACKTRACK [" + "Final result" + "] " +
                "Text='" + element.getText() + "' " +
                "Class=" + element.getClass().getSimpleName() + " " +
                "Offset=" + element.getTextOffset() + " " +
                extra);
    }

    private void debugPosition(PsiElement position) {
        System.out.println("\n=== " + "Initial" + " Position ===");
        System.out.println("Text: " + position.getText());
        System.out.println("Class: " + position.getClass().getSimpleName());
        System.out.println("Parent: " + position.getParent().getClass().getSimpleName());
        System.out.println("Gene Body: " + PsiTreeUtil.getParentOfType(position, BioSyntaxGeneBody.class));
        System.out.println("Prev Leaf: " + PsiTreeUtil.prevLeaf(position));
    }

    private boolean isAfterEquals(PsiElement position) {
        PsiElement prev = PsiTreeUtil.prevLeaf(position);
        System.out.println("\n=== isAfterEquals Check ===");
        System.out.println("Position: " + position.getText());
        System.out.println("Prev leaf: " + (prev != null ? prev.getText() : "null"));

        if (prev != null && (prev.getText().equals("\"") || prev.getText().equals("="))) {
            System.out.println("Found quote or equals");
            PsiElement property = findPropertyElement(position);
            System.out.println("Found property: " + (property != null ? property.getText() : "null"));
            return property != null &&
                    (property.getText().equals("Start_Codon") ||
                            property.getText().equals("Stop_Codon"));
        }
        return false;
    }

    private PsiElement findPropertyElement(PsiElement position) {
        PsiElement current = position;
        System.out.println("\n=== Finding Property ===");
        while (current != null) {
            String text = current.getText();
            System.out.println("Checking element: " + text);

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