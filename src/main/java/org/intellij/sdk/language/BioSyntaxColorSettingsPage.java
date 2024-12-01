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
            new AttributesDescriptor("Sequences//Amino Acid", BioSyntaxSyntaxHighlighter.AMINO_ACID),
            new AttributesDescriptor("Types//Sequence Type", BioSyntaxSyntaxHighlighter.TYPE_DECLARATION),
            new AttributesDescriptor("Elements//Identifier", BioSyntaxSyntaxHighlighter.IDENTIFIER),
            new AttributesDescriptor("Elements//Codon", BioSyntaxSyntaxHighlighter.CODON),
            new AttributesDescriptor("Comments", BioSyntaxSyntaxHighlighter.COMMENT),
            new AttributesDescriptor("Bad Character", BioSyntaxSyntaxHighlighter.BAD_CHARACTER)
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
            NtSeq dnaSeq = "ATCG"
            AASeq proteinSeq = "MGKL"
            
            promoter
            ATG // Start codon
            GCTCTTAAGGCT
            ACTGGTCTAGCT
            TAA // Stop codon
            terminator
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