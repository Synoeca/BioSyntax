package org.intellij.sdk.language.psi;

import com.intellij.psi.tree.IElementType;
import org.intellij.sdk.language.BioSyntaxLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class BioSyntaxTokenType extends IElementType {
    public static final BioSyntaxTokenType GENE = new BioSyntaxTokenType("GENE");
    public static final BioSyntaxTokenType LBRACE = new BioSyntaxTokenType("LBRACE");
    public static final BioSyntaxTokenType RBRACE = new BioSyntaxTokenType("RBRACE");
    // Type system
    public static final BioSyntaxTokenType NT_SEQ = new BioSyntaxTokenType("NT_SEQ");
    public static final BioSyntaxTokenType AA_SEQ = new BioSyntaxTokenType("AA_SEQ");
    public static final BioSyntaxTokenType IDENTIFIER = new BioSyntaxTokenType("IDENTIFIER");
    public static final BioSyntaxTokenType EQUALS = new BioSyntaxTokenType("EQUALS");
    public static final BioSyntaxTokenType QUOTE = new BioSyntaxTokenType("QUOTE");

    // Previous tokens remain...
    public static final BioSyntaxTokenType ADENINE = new BioSyntaxTokenType("A");
    public static final BioSyntaxTokenType THYMINE = new BioSyntaxTokenType("T");
    public static final BioSyntaxTokenType GUANINE = new BioSyntaxTokenType("G");
    public static final BioSyntaxTokenType CYTOSINE = new BioSyntaxTokenType("C");
    public static final BioSyntaxTokenType URACIL = new BioSyntaxTokenType("U");
    public static final BioSyntaxTokenType START_CODON = new BioSyntaxTokenType("START_CODON");
    public static final BioSyntaxTokenType STOP_CODON = new BioSyntaxTokenType("STOP_CODON");
    public static final BioSyntaxTokenType NUCLEOTIDE = new BioSyntaxTokenType("NUCLEOTIDE");
    public static final BioSyntaxTokenType AMINO_ACID = new BioSyntaxTokenType("AMINO_ACID");
    public static final BioSyntaxTokenType PROMOTER = new BioSyntaxTokenType("PROMOTER");
    public static final BioSyntaxTokenType TERMINATOR = new BioSyntaxTokenType("TERMINATOR");
    public static final BioSyntaxTokenType COMMENT = new BioSyntaxTokenType("COMMENT");

    public BioSyntaxTokenType(@NotNull @NonNls String debugName) {
        super(debugName, BioSyntaxLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "BioSyntaxTokenType." + super.toString();
    }
}