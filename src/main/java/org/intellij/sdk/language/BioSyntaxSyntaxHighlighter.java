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

    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] NUCLEOTIDE_KEYS = new TextAttributesKey[]{NUCLEOTIDE};
    private static final TextAttributesKey[] AMINO_ACID_KEYS = new TextAttributesKey[]{AMINO_ACID};
    private static final TextAttributesKey[] TYPE_KEYS = new TextAttributesKey[]{TYPE_DECLARATION};
    private static final TextAttributesKey[] IDENTIFIER_KEYS = new TextAttributesKey[]{IDENTIFIER};
    private static final TextAttributesKey[] CODON_KEYS = new TextAttributesKey[]{CODON};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
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
        if (tokenType.equals(BioSyntaxTypes.AMINO_ACID)) {
            return AMINO_ACID_KEYS;
        }
        if (tokenType.equals(BioSyntaxTypes.NT_SEQ) || tokenType.equals(BioSyntaxTypes.AA_SEQ)) {
            return TYPE_KEYS;
        }
        if (tokenType.equals(BioSyntaxTypes.IDENTIFIER)) {
            return IDENTIFIER_KEYS;
        }
        if (tokenType.equals(BioSyntaxTypes.START_CODON) || tokenType.equals(BioSyntaxTypes.STOP_CODON)) {
            return CODON_KEYS;
        }
        if (tokenType.equals(BioSyntaxTypes.COMMENT)) {
            return COMMENT_KEYS;
        }
        if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        }
        return EMPTY_KEYS;
    }
}