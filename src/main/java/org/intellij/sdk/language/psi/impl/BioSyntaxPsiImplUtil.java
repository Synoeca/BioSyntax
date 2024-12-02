package org.intellij.sdk.language.psi.impl;

import com.intellij.lang.ASTNode;
import org.intellij.sdk.language.psi.*;

public class BioSyntaxPsiImplUtil {
    public static String getSequenceText(BioSyntaxDeclaration element) {
        ASTNode sequenceNode = element.getNode().findChildByType(BioSyntaxTypes.NUCLEOTIDE);
        if (sequenceNode == null) {
            sequenceNode = element.getNode().findChildByType(BioSyntaxTypes.AMINO_ACID);
        }
        return sequenceNode != null ? sequenceNode.getText() : null;
    }

    public static String getIdentifier(BioSyntaxDeclaration element) {
        ASTNode idNode = element.getNode().findChildByType(BioSyntaxTypes.IDENTIFIER);
        return idNode != null ? idNode.getText() : null;
    }

    public static String getName(BioSyntaxDeclaration element) {
        return getIdentifier(element);
    }

    public static String getName(BioSyntaxGeneDefinition element) {
        ASTNode idNode = element.getNode().findChildByType(BioSyntaxTypes.IDENTIFIER);
        return idNode != null ? idNode.getText() : null;
    }

    public static String getGeneStructure(BioSyntaxGeneDefinition element) {
        BioSyntaxGeneBody body = element.getGeneBody();
        return body != null ? body.getText() : null;
    }
}