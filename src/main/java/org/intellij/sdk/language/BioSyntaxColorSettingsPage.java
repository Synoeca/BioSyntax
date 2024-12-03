package org.intellij.sdk.language;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

public class BioSyntaxColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Sequences//Nucleotide", BioSyntaxSyntaxHighlighter.NUCLEOTIDE),
            new AttributesDescriptor("Sequences//RNA Nucleotide", BioSyntaxSyntaxHighlighter.RNA_NUCLEOTIDE),
            new AttributesDescriptor("Sequences//DNA Nucleotide", BioSyntaxSyntaxHighlighter.DNA_NUCLEOTIDE),
            new AttributesDescriptor("Sequences//Amino Acid", BioSyntaxSyntaxHighlighter.AMINO_ACID),
            new AttributesDescriptor("Types//Sequence Type", BioSyntaxSyntaxHighlighter.TYPE_DECLARATION),
            new AttributesDescriptor("Elements//Identifier", BioSyntaxSyntaxHighlighter.IDENTIFIER),
            new AttributesDescriptor("Elements//Codon", BioSyntaxSyntaxHighlighter.CODON),
            new AttributesDescriptor("Comments", BioSyntaxSyntaxHighlighter.COMMENT),
            new AttributesDescriptor("Bad Character", BioSyntaxSyntaxHighlighter.BAD_CHARACTER),
            new AttributesDescriptor("Gene//Keyword", BioSyntaxSyntaxHighlighter.GENE),
            new AttributesDescriptor("Gene//Property", BioSyntaxSyntaxHighlighter.GENE_PROPERTY)
    };

    @Override
    public Icon getIcon() {
        return BioSyntaxIcons.FILE;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new BioSyntaxSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return """
            // Example BioSyntax file
            NtSeq ntseq = "ATUGC"
            RNASeq rnaseq = "AUGC"
            DNASeq dnaseq = "ATGC"
            AASeq aminoacidseq = "MGKL"
            
            Gene exampleGene {
                Promoter = "TATAAA"
                Start_Codon = "ATG"
                Coding_Sequence = "GCTCTTAAGGCTACTGGTCTAGCT"
                Stop_Codon = "TAA"
                Terminator = "AATAAA"
            }
            
            Gene minimalGene {
                Promoter;
                Start_Codon = "ATG"
                Coding_Sequence = "ATCGGCT"
                Stop_Codon = "TGA"
                Terminator;
            }
            """;
    }

    @Override
    public AttributesDescriptor @NotNull [] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public ColorDescriptor @NotNull [] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "BioSyntax";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }
}