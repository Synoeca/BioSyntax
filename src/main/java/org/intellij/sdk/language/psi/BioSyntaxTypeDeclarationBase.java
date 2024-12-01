package org.intellij.sdk.language.psi;

import com.intellij.psi.PsiNameIdentifierOwner;

public interface BioSyntaxTypeDeclarationBase extends PsiNameIdentifierOwner {
    String getType();   // Method to get the type of the declaration
    String getName();   // Method to get the name of the declaration
    String getValue();  // Method to get the value associated with the declaration
}