package org.intellij.sdk.language;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import org.intellij.sdk.language.psi.BioSyntaxDeclaration;
import org.intellij.sdk.language.psi.BioSyntaxGeneDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

final class BioSyntaxAnnotator implements Annotator {

    public static final String NT_SEQ_PREFIX = "NtSeq:";
    public static final String RNA_SEQ_PREFIX = "RNASeq:";
    public static final String DNA_SEQ_PREFIX = "DNASeq:";
    public static final String AA_SEQ_PREFIX = "AASeq:";
    public static final String GENE_PREFIX = "Gene:";

    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof PsiLiteralExpression literalExpression) {
            annotateStringLiteral(literalExpression, holder);
        } else if (element instanceof BioSyntaxDeclaration declaration) {
            annotateDeclaration(declaration, holder);
        } else if (element instanceof BioSyntaxGeneDefinition geneDefinition) {
            annotateGeneDefinition(geneDefinition, holder);
        }
    }

    private void annotateStringLiteral(PsiLiteralExpression literalExpression, AnnotationHolder holder) {
        String value = literalExpression.getValue() instanceof String ? (String) literalExpression.getValue() : null;

        if (value != null) {
            TextRange literalTextRange = literalExpression.getTextRange();
            int contentStartOffset = literalTextRange.getStartOffset() + 1; // Skip the starting quote
            int contentEndOffset = literalTextRange.getEndOffset() - 1;     // Skip the ending quote

            if (value.startsWith(GENE_PREFIX)) {
                validateGeneProperties(value, contentStartOffset, holder);
            } else {
                String sequencePrefix = getSequencePrefix(value);
                if (sequencePrefix != null) {
                    annotateSequence(sequencePrefix, value, contentStartOffset, contentEndOffset, holder);
                }
            }
        }
    }

    private String getSequencePrefix(String value) {
        if (value.startsWith(NT_SEQ_PREFIX)) return NT_SEQ_PREFIX;
        if (value.startsWith(RNA_SEQ_PREFIX)) return RNA_SEQ_PREFIX;
        if (value.startsWith(DNA_SEQ_PREFIX)) return DNA_SEQ_PREFIX;
        if (value.startsWith(AA_SEQ_PREFIX)) return AA_SEQ_PREFIX;
        return null;
    }

    private void annotateSequence(String prefix, String value, int contentStartOffset, int contentEndOffset, AnnotationHolder holder) {
        String sequence = value.substring(prefix.length()).trim();
        int prefixLength = prefix.length();

        TextRange prefixRange = new TextRange(contentStartOffset, contentStartOffset + prefixLength);
        TextRange sequenceRange = new TextRange(contentStartOffset + prefixLength, contentEndOffset);

        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(prefixRange)
                .textAttributes(BioSyntaxSyntaxHighlighter.TYPE_DECLARATION)
                .create();

        if (!isValidSequence(sequence, prefix)) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Invalid sequence")
                    .range(sequenceRange)
                    .create();
        } else {
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(sequenceRange)
                    .textAttributes(getSequenceTextAttributes(prefix))
                    .create();
        }
    }

    private void validateGeneProperties(String geneDefinition, int contentStartOffset, AnnotationHolder holder) {
        String propertiesPart = geneDefinition.substring(GENE_PREFIX.length()).trim(); // Get properties part
        String[] properties = propertiesPart.split(","); // Split by comma

        TextRange prefixRange = new TextRange(contentStartOffset, contentStartOffset + GENE_PREFIX.length());
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(prefixRange)
                .textAttributes(BioSyntaxSyntaxHighlighter.GENE)
                .create();

        for (String property : properties) {
            String[] keyValue = property.split(":");
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();

                highlightPropertyName(contentStartOffset, propertiesPart, property, key, holder);
                validateAndHighlightPropertyValue(key, value, contentStartOffset, propertiesPart, property, holder);
            }
        }
    }

    private void highlightPropertyName(int contentStartOffset, String propertiesPart, String property, String key, AnnotationHolder holder) {
        int keyStartOffset = contentStartOffset + GENE_PREFIX.length() + propertiesPart.indexOf(property.trim());
        int keyEndOffset = keyStartOffset + key.length();

        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(new TextRange(keyStartOffset, keyEndOffset))
                .textAttributes(BioSyntaxSyntaxHighlighter.PROPERTY_NAME)
                .create();
    }

    private void validateAndHighlightPropertyValue(String key, String value, int contentStartOffset, String propertiesPart, String property, AnnotationHolder holder) {
        int keyEndOffset = contentStartOffset + GENE_PREFIX.length() + propertiesPart.indexOf(property.trim()) + key.length();
        int valueStartOffset = keyEndOffset + 1; // +1 to skip the separating colon
        int valueEndOffset = valueStartOffset + value.length();
        TextRange valueRange = new TextRange(valueStartOffset, valueEndOffset);

        boolean isValid = switch (key) {
            case "Start_Codon", "Stop_Codon", "Coding_Sequence" -> isValidSequence(value, NT_SEQ_PREFIX);
            default -> true;
        };

        if (!isValid) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Invalid " + key + ": " + value)
                    .range(valueRange)
                    .create();
        } else {
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(valueRange)
                    .textAttributes(DefaultLanguageHighlighterColors.NUMBER)
                    .create();
        }
    }

    private void annotateDeclaration(BioSyntaxDeclaration declaration, AnnotationHolder holder) {
        String identifier = declaration.getIdentifier();
        String sequenceText = declaration.getSequenceText();

        if (identifier == null || sequenceText == null) return;

        String prefix = getSequencePrefix(identifier);
        TextRange prefixRange = TextRange.from(declaration.getTextRange().getStartOffset(), Objects.requireNonNull(prefix).length());
        TextRange identifierRange = TextRange.from(prefixRange.getEndOffset(), identifier.length());
        TextRange sequenceRange = TextRange.from(declaration.getTextRange().getStartOffset() + declaration.getText().indexOf(sequenceText),
                sequenceText.length());

        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(prefixRange)
                .textAttributes(BioSyntaxSyntaxHighlighter.TYPE_DECLARATION)
                .create();

        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(identifierRange)
                .textAttributes(BioSyntaxSyntaxHighlighter.IDENTIFIER)
                .create();

        if (!isValidSequence(sequenceText, prefix)) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Invalid sequence")
                    .range(sequenceRange)
                    .create();
        } else {
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(sequenceRange)
                    .textAttributes(getSequenceTextAttributes(prefix))
                    .create();
        }
    }

    private void annotateGeneDefinition(BioSyntaxGeneDefinition geneDefinition, AnnotationHolder holder) {
        String identifier = geneDefinition.getGeneBody().getGenePropertyList().getFirst().getText();
        if (identifier == null) return;

        TextRange prefixRange = TextRange.from(geneDefinition.getTextRange().getStartOffset(), GENE_PREFIX.length());
        TextRange identifierRange = TextRange.from(prefixRange.getEndOffset(), identifier.length());

        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(prefixRange)
                .textAttributes(BioSyntaxSyntaxHighlighter.GENE)
                .create();

        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(identifierRange)
                .textAttributes(BioSyntaxSyntaxHighlighter.IDENTIFIER)
                .create();
    }

    private boolean isValidSequence(String sequence, String type) {
        return switch (type) {
            case NT_SEQ_PREFIX -> sequence.matches("[ATUCG]+");
            case RNA_SEQ_PREFIX -> sequence.matches("[AUGC]+");
            case DNA_SEQ_PREFIX -> sequence.matches("[ATGC]+");
            case AA_SEQ_PREFIX -> sequence.matches("[ACDEFGHIKLMNPQRSTVWY]+");
            default -> false;
        };
    }

    private TextAttributesKey getSequenceTextAttributes(String prefix) {
        return switch (prefix) {
            case NT_SEQ_PREFIX -> BioSyntaxSyntaxHighlighter.NUCLEOTIDE;
            case RNA_SEQ_PREFIX -> BioSyntaxSyntaxHighlighter.RNA_NUCLEOTIDE;
            case DNA_SEQ_PREFIX -> BioSyntaxSyntaxHighlighter.DNA_NUCLEOTIDE;
            default -> BioSyntaxSyntaxHighlighter.AMINO_ACID;
        };
    }
}
