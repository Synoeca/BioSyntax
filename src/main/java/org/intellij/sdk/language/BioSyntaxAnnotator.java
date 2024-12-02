package org.intellij.sdk.language;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import org.intellij.sdk.language.psi.BioSyntaxDeclaration;
import org.intellij.sdk.language.psi.BioSyntaxGeneDefinition;
import org.jetbrains.annotations.NotNull;

final class BioSyntaxAnnotator implements Annotator {

    public static final String NT_SEQ_PREFIX = "NtSeq:";
    public static final String AA_SEQ_PREFIX = "AASeq:";
    public static final String GENE_PREFIX = "Gene:";

    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        // Check if the element is a string literal
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

            if (value.startsWith(NT_SEQ_PREFIX)) {
                String sequence = value.substring(NT_SEQ_PREFIX.length()).trim();
                int prefixLength = NT_SEQ_PREFIX.length();

                TextRange prefixRange = new TextRange(contentStartOffset, contentStartOffset + prefixLength);
                TextRange sequenceRange = new TextRange(contentStartOffset + prefixLength, contentEndOffset);

                // Highlight prefix
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .range(prefixRange)
                        .textAttributes(BioSyntaxSyntaxHighlighter.TYPE_DECLARATION)
                        .create();

                // Validate nucleotide sequence
                if (!isValidSequence(sequence, true)) {
                    holder.newAnnotation(HighlightSeverity.ERROR, "Invalid nucleotide sequence")
                            .range(sequenceRange)
                            .create();
                } else {
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                            .range(sequenceRange)
                            .textAttributes(BioSyntaxSyntaxHighlighter.NUCLEOTIDE)
                            .create();
                }
            } else if (value.startsWith(AA_SEQ_PREFIX)) {
                String sequence = value.substring(AA_SEQ_PREFIX.length()).trim();
                int prefixLength = AA_SEQ_PREFIX.length();

                TextRange prefixRange = new TextRange(contentStartOffset, contentStartOffset + prefixLength);
                TextRange sequenceRange = new TextRange(contentStartOffset + prefixLength, contentEndOffset);

                // Highlight prefix
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .range(prefixRange)
                        .textAttributes(BioSyntaxSyntaxHighlighter.TYPE_DECLARATION)
                        .create();

                // Validate amino acid sequence
                if (!isValidSequence(sequence, false)) {
                    holder.newAnnotation(HighlightSeverity.ERROR, "Invalid amino acid sequence")
                            .range(sequenceRange)
                            .create();
                } else {
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                            .range(sequenceRange)
                            .textAttributes(BioSyntaxSyntaxHighlighter.AMINO_ACID)
                            .create();
                }
            } else if (value.startsWith(GENE_PREFIX)) {
                // Handle gene definitions
                validateGeneProperties(value, contentStartOffset, holder);
            }
        }
    }

    private void validateGeneProperties(String geneDefinition, int contentStartOffset, AnnotationHolder holder) {
        String propertiesPart = geneDefinition.substring(GENE_PREFIX.length()).trim(); // Get properties part
        String[] properties = propertiesPart.split(","); // Split by comma

        TextRange prefixRange = new TextRange(contentStartOffset, contentStartOffset + GENE_PREFIX.length());

        // Highlight "Gene" prefix
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(prefixRange)
                .textAttributes(BioSyntaxSyntaxHighlighter.GENE)
                .create();

        for (String property : properties) {
            String[] keyValue = property.split(":");
            if (keyValue.length == 2) { // Ensure we have both key and value
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();

                // Highlight property name
                int keyStartOffset = contentStartOffset + GENE_PREFIX.length() + propertiesPart.indexOf(property.trim());
                int keyEndOffset = keyStartOffset + key.length();

                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .range(new TextRange(keyStartOffset, keyEndOffset))
                        .textAttributes(BioSyntaxSyntaxHighlighter.PROPERTY_NAME) // Highlight as PROPERTY_NAME
                        .create();

                // Calculate value range
                int valueStartOffset = keyEndOffset + 1; // +1 to skip the separating colon (:)
                int valueEndOffset = valueStartOffset + value.length();

                TextRange valueRange = new TextRange(valueStartOffset, valueEndOffset);

                // Validate and highlight based on property type
                boolean isValid;

                if ("Start_Codon".equals(key) || "Stop_Codon".equals(key)) {
                    isValid = isValidSequence(value, true); // Check as nucleotide for codons
                    if (!isValid) {
                        holder.newAnnotation(HighlightSeverity.ERROR, "Invalid codon: " + value)
                                .range(valueRange)
                                .create();
                    } else {
                        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                                .range(valueRange)
                                .textAttributes(DefaultLanguageHighlighterColors.NUMBER) // Highlight as NUMBER
                                .create();
                    }
                } else if ("Coding_Sequence".equals(key)) {
                    isValid = isValidSequence(value, true); // Check as nucleotide for coding sequences
                    if (!isValid) {
                        holder.newAnnotation(HighlightSeverity.ERROR, "Invalid coding sequence: " + value)
                                .range(valueRange)
                                .create();
                    } else {
                        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                                .range(valueRange)
                                .textAttributes(DefaultLanguageHighlighterColors.NUMBER) // Highlight as NUMBER
                                .create();
                    }
                }

                // Can add more specific validation for other properties here as needed.
            }
        }
    }


    private void annotateDeclaration(BioSyntaxDeclaration declaration, AnnotationHolder holder) {
        String identifier = declaration.getIdentifier();
        String sequenceText = declaration.getSequenceText();

        if (identifier == null || sequenceText == null) {
            return;
        }

        TextRange prefixRange = TextRange.from(declaration.getTextRange().getStartOffset(),
                (identifier.startsWith(NT_SEQ_PREFIX) ? NT_SEQ_PREFIX : AA_SEQ_PREFIX).length());
        TextRange identifierRange = TextRange.from(prefixRange.getEndOffset(), identifier.length());
        TextRange sequenceRange = TextRange.from(declaration.getTextRange().getStartOffset() + declaration.getText().indexOf(sequenceText),
                sequenceText.length());

        // Highlight prefix
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(prefixRange)
                .textAttributes(BioSyntaxSyntaxHighlighter.TYPE_DECLARATION)
                .create();

        // Highlight identifier
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(identifierRange)
                .textAttributes(BioSyntaxSyntaxHighlighter.IDENTIFIER)
                .create();

        // Validate and highlight sequence
        if (!isValidSequence(sequenceText, identifier.startsWith(NT_SEQ_PREFIX))) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Invalid sequence")
                    .range(sequenceRange)
                    .create();
        } else {
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(sequenceRange)
                    .textAttributes(identifier.startsWith(NT_SEQ_PREFIX) ?
                            BioSyntaxSyntaxHighlighter.NUCLEOTIDE :
                            BioSyntaxSyntaxHighlighter.AMINO_ACID)
                    .create();
        }
    }

    private void annotateGeneDefinition(BioSyntaxGeneDefinition geneDefinition, AnnotationHolder holder) {
        String identifier = geneDefinition.getGeneBody().getGenePropertyList().getFirst().getText();
        if (identifier == null) {
            return;
        }

        TextRange prefixRange = TextRange.from(geneDefinition.getTextRange().getStartOffset(), GENE_PREFIX.length());
        TextRange identifierRange = TextRange.from(prefixRange.getEndOffset(), identifier.length());

        // Highlight "Gene" prefix
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(prefixRange)
                .textAttributes(BioSyntaxSyntaxHighlighter.GENE)
                .create();

        // Highlight gene identifier
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(identifierRange)
                .textAttributes(BioSyntaxSyntaxHighlighter.IDENTIFIER)
                .create();

        // Can add more specific annotations for gene properties here
    }

    private boolean isValidSequence(String sequence, boolean isNucleotide) {
        if (isNucleotide) {
            return sequence.matches("[ATCG]+"); // True if the sequence contains only A, T, C, G
        } else {
            return sequence.matches("[ACDEFGHIKLMNPQRSTVWY]+"); // True if the sequence contains valid amino acid codes
        }
    }

}