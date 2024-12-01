package org.intellij.sdk.language.psi;

import com.intellij.psi.tree.IElementType;
import org.intellij.sdk.language.BioSyntaxLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class BioSyntaxTokenType extends IElementType {
    // Basic nucleotides
    public static final BioSyntaxTokenType ADENINE = new BioSyntaxTokenType("A");
    public static final BioSyntaxTokenType THYMINE = new BioSyntaxTokenType("T");
    public static final BioSyntaxTokenType GUANINE = new BioSyntaxTokenType("G");
    public static final BioSyntaxTokenType CYTOSINE = new BioSyntaxTokenType("C");
    public static final BioSyntaxTokenType URACIL = new BioSyntaxTokenType("U");

    // Codons and amino acids
    public static final BioSyntaxTokenType START_CODON = new BioSyntaxTokenType("START_CODON"); // ATG
    public static final BioSyntaxTokenType STOP_CODON = new BioSyntaxTokenType("STOP_CODON"); // TAA, TAG, TGA
    public static final BioSyntaxTokenType AMINO_ACID = new BioSyntaxTokenType("AMINO_ACID");

    // Structural elements
    public static final BioSyntaxTokenType PROMOTER = new BioSyntaxTokenType("PROMOTER");
    public static final BioSyntaxTokenType TERMINATOR = new BioSyntaxTokenType("TERMINATOR");
    public static final BioSyntaxTokenType INTRON = new BioSyntaxTokenType("INTRON");
    public static final BioSyntaxTokenType EXON = new BioSyntaxTokenType("EXON");

    // Syntax elements
    public static final BioSyntaxTokenType SEQUENCE_START = new BioSyntaxTokenType("SEQ_START");
    public static final BioSyntaxTokenType SEQUENCE_END = new BioSyntaxTokenType("SEQ_END");
    public static final BioSyntaxTokenType SEPARATOR = new BioSyntaxTokenType("SEPARATOR");
    public static final BioSyntaxTokenType COMMENT = new BioSyntaxTokenType("COMMENT");

    public BioSyntaxTokenType(@NotNull @NonNls String debugName) {
        super(debugName, BioSyntaxLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "BioSyntaxTokenType." + super.toString();
    }
}