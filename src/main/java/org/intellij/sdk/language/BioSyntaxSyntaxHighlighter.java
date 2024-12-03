package org.intellij.sdk.language;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.intellij.sdk.language.psi.BioSyntaxTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class BioSyntaxSyntaxHighlighter extends SyntaxHighlighterBase {
    public static final TextAttributesKey NUCLEOTIDE =
            createTextAttributesKey("BIOSYNTAX_NUCLEOTIDE", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey RNA_NUCLEOTIDE =
            createTextAttributesKey("BIOSYNTAX_RNA_NUCLEOTIDE", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey DNA_NUCLEOTIDE =
            createTextAttributesKey("BIOSYNTAX_DNA_NUCLEOTIDE", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey AMINO_ACID =
            createTextAttributesKey("BIOSYNTAX_AMINO_ACID", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey TYPE_DECLARATION =
            createTextAttributesKey("BIOSYNTAX_TYPE", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey IDENTIFIER =
            createTextAttributesKey("BIOSYNTAX_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER);
    public static final TextAttributesKey CODON =
            createTextAttributesKey("BIOSYNTAX_CODON", DefaultLanguageHighlighterColors.MARKUP_TAG);
    public static final TextAttributesKey COMMENT =
            createTextAttributesKey("BIOSYNTAX_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey BAD_CHARACTER =
            createTextAttributesKey("BIOSYNTAX_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);
    public static final TextAttributesKey GENE =
            createTextAttributesKey("BIOSYNTAX_GENE", DefaultLanguageHighlighterColors.CLASS_NAME);
    public static final TextAttributesKey GENE_PROPERTY =
            createTextAttributesKey("BIOSYNTAX_GENE_PROPERTY", DefaultLanguageHighlighterColors.INSTANCE_FIELD);
    public static final TextAttributesKey PROPERTY_NAME =
            createTextAttributesKey("BIOSYNTAX_PROPERTY_NAME", DefaultLanguageHighlighterColors.INSTANCE_FIELD);

    // Token highlight arrays
    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] NUCLEOTIDE_KEYS = new TextAttributesKey[]{NUCLEOTIDE};
    private static final TextAttributesKey[] RNA_NUCLEOTIDE_KEYS = new TextAttributesKey[]{RNA_NUCLEOTIDE};
    private static final TextAttributesKey[] DNA_NUCLEOTIDE_KEYS = new TextAttributesKey[]{DNA_NUCLEOTIDE};
    private static final TextAttributesKey[] AMINO_ACID_KEYS = new TextAttributesKey[]{AMINO_ACID};
    private static final TextAttributesKey[] TYPE_KEYS = new TextAttributesKey[]{TYPE_DECLARATION};
    private static final TextAttributesKey[] IDENTIFIER_KEYS = new TextAttributesKey[]{IDENTIFIER};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
    private static final TextAttributesKey[] GENE_KEYS = new TextAttributesKey[]{GENE};
    private static final TextAttributesKey[] GENE_PROPERTY_KEYS = new TextAttributesKey[]{GENE_PROPERTY, PROPERTY_NAME};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new BioSyntaxLexerAdapter();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(BioSyntaxTypes.NUCLEOTIDE)) {
            return NUCLEOTIDE_KEYS;
        }
        if (tokenType.equals(BioSyntaxTypes.RNA_NUCLEOTIDE)) {
            return RNA_NUCLEOTIDE_KEYS;
        }
        if (tokenType.equals(BioSyntaxTypes.DNA_NUCLEOTIDE)) {
            return DNA_NUCLEOTIDE_KEYS;
        }
        if (tokenType.equals(BioSyntaxTypes.AMINO_ACID)) {
            return AMINO_ACID_KEYS;
        }
        if (tokenType.equals(BioSyntaxTypes.NT_SEQ) || (tokenType.equals(BioSyntaxTypes.RNA_SEQ)
                || (tokenType.equals(BioSyntaxTypes.DNA_SEQ) || tokenType.equals(BioSyntaxTypes.AA_SEQ)))) {
            return TYPE_KEYS;
        }
        if (tokenType.equals(BioSyntaxTypes.IDENTIFIER)) {
            return IDENTIFIER_KEYS;
        }
        if (tokenType.equals(BioSyntaxTypes.COMMENT)) {
            return COMMENT_KEYS;
        }
        if (tokenType.equals(BioSyntaxTypes.GENE)) {
            return GENE_KEYS;
        }
        if (tokenType.equals(BioSyntaxTypes.PROMOTER) ||
                tokenType.equals(BioSyntaxTypes.TERMINATOR) ||
                tokenType.equals(BioSyntaxTypes.CODING_SEQUENCE) ||
                tokenType.equals(BioSyntaxTypes.START_CODON) ||
                tokenType.equals(BioSyntaxTypes.STOP_CODON)) {
            return GENE_PROPERTY_KEYS;
        }
        if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        }
        return EMPTY_KEYS;
    }
}
