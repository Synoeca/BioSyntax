package org.intellij.sdk.language.psi;

import com.intellij.psi.tree.IElementType;
import org.intellij.sdk.language.BioSyntaxLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class BioSyntaxElementType extends IElementType {
    // DNA/RNA Structure Elements
    public static final BioSyntaxElementType GENE = new BioSyntaxElementType("GENE");
    public static final BioSyntaxElementType CODON_SEQUENCE = new BioSyntaxElementType("CODON_SEQUENCE");
    public static final BioSyntaxElementType PROTEIN = new BioSyntaxElementType("PROTEIN");
    public static final BioSyntaxElementType REGULATORY_SEQUENCE = new BioSyntaxElementType("REGULATORY_SEQUENCE");

    public BioSyntaxElementType(@NotNull @NonNls String debugName) {
        super(debugName, BioSyntaxLanguage.INSTANCE);
    }
}