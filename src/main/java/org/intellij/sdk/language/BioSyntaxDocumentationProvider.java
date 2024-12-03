package org.intellij.sdk.language;

import com.intellij.lang.ASTNode;
import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.lang.documentation.DocumentationMarkup;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.presentation.java.SymbolPresentationUtil;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.sdk.language.psi.BioSyntaxDeclaration;
import org.intellij.sdk.language.psi.BioSyntaxGeneBody;
import org.intellij.sdk.language.psi.BioSyntaxGeneDefinition;
import org.intellij.sdk.language.psi.BioSyntaxGeneProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BioSyntaxDocumentationProvider extends AbstractDocumentationProvider {
    @Override
    public @Nullable String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        if (element == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        if (isGeneClass(element)) {
            renderGeneDoc(element, sb);
        } else if (element instanceof BioSyntaxDeclaration) {
            renderSequenceDoc(element, (BioSyntaxDeclaration) element, sb);
        }

        return sb.toString();
    }

    private boolean isGeneClass(PsiElement element) {
        return element instanceof BioSyntaxGeneDefinition ||
                (element.getParent() instanceof BioSyntaxGeneDefinition);
    }

    private void renderGeneDoc(PsiElement element, StringBuilder sb) {
        BioSyntaxGeneDefinition gene = (BioSyntaxGeneDefinition) element;
        BioSyntaxGeneBody body = gene.getGeneBody();
        String name = gene.getName();

        sb.append(DocumentationMarkup.DEFINITION_START);
        sb.append("Gene Definition");
        sb.append(DocumentationMarkup.DEFINITION_END);
        sb.append(DocumentationMarkup.CONTENT_START);

        addKeyValueSection("Name:", name, sb);
        addKeyValueSection("Type:", "Gene", sb);

        List<BioSyntaxGeneProperty> properties = PsiTreeUtil.getChildrenOfTypeAsList(body, BioSyntaxGeneProperty.class);
        boolean hasProperties = false;

        for (BioSyntaxGeneProperty prop : properties) {
            hasProperties = true;
            String text = prop.getText();
            String[] parts = text.split("=");

            String key = parts[0].trim().replace("_", " ") + ":";

            String value;
            if (parts.length > 1) {
                String potentialValue = parts[1].trim();

                value = potentialValue.replaceAll("^\"|\"$", "");
                if (value.isEmpty() || value.equals(";")) {
                    value = "unspecified";
                }
            } else {
                value = "unspecified";
            }

            addKeyValueSection(key, value, sb);
        }

        if (!hasProperties) {
            addKeyValueSection("Properties:", "None specified", sb);
        }

        getCommentAndFile(element, sb);
    }

    private void renderSequenceDoc(PsiElement element, BioSyntaxDeclaration declaration, StringBuilder sb) {
        ASTNode node = declaration.getNode();

        String type = extractType(node);
        String name = extractName(node);
        String sequence = extractSequence(node);
        String fullTypeName = getFullTypeName(type);

        sb.append(DocumentationMarkup.DEFINITION_START);
        sb.append(fullTypeName).append(" Definition");
        sb.append(DocumentationMarkup.DEFINITION_END);
        sb.append(DocumentationMarkup.CONTENT_START);

        addKeyValueSection("Name:", name, sb);
        addKeyValueSection("Type:", fullTypeName, sb);
        addKeyValueSection("Sequence:", sequence.isEmpty() ? "unspecified" : sequence, sb);

        addDescription(fullTypeName, sb);

        getCommentAndFile(element, sb);
    }

    private String getFullTypeName(String type) {
        return switch (type) {
            case "NtSeq" -> "Nucleotide Sequence";
            case "AASeq" -> "Amino Acid Sequence";
            case "RNASeq" -> "RNA Sequence";
            case "DNASeq" -> "DNA Sequence";
            default -> type;
        };
    }

    private void addDescription(String fullTypeName, StringBuilder sb) {
        switch (fullTypeName) {
            case "Nucleotide Sequence":
                addKeyValueSection("Description:", "Generic nucleotide sequence that can represent DNA or RNA.", sb);
                break;
            case "Amino Acid Sequence":
                addKeyValueSection("Description:", "Amino acid sequence that represents a protein or peptide.", sb);
                break;
            case "RNA Sequence":
                addKeyValueSection("Description:", "Ribonucleic acid sequence that represents RNA molecules.", sb);
                break;
            case "DNA Sequence":
                addKeyValueSection("Description:", "Deoxyribonucleic acid sequence that represents DNA molecules.", sb);
                break;
        }
    }

    private void getCommentAndFile(PsiElement element, StringBuilder sb) {
        String comment = BioSyntaxUtil.findDocumentationComment(element);
        String file = SymbolPresentationUtil.getFilePathPresentation(element.getContainingFile());
        if (!comment.isEmpty()) {
            addKeyValueSection("Comment:", comment, sb);
        }

        if (!file.isEmpty()) {
            addKeyValueSection("File:", file, sb);
        }

        sb.append(DocumentationMarkup.CONTENT_END);
    }

    private String extractType(ASTNode node) {
        String text = node.getText().trim();
        String[] parts = text.split("\\s+");
        return parts.length > 0 ? parts[0] : "unknown";
    }

    private String extractName(ASTNode node) {
        String text = node.getText().trim();
        String[] parts = text.split("\\s+");
        return parts.length > 1 ? parts[1] : "unknown";
    }

    private String extractSequence(ASTNode node) {
        String text = node.getText().trim();
        int equalIndex = text.indexOf("=");
        if (equalIndex != -1) {
            String sequence = text.substring(equalIndex + 1).trim();
            sequence = sequence.replaceAll("\"", "");
            return sequence;
        }
        return "unknown";
    }

    private void addKeyValueSection(String key, String value, StringBuilder sb) {
        sb.append(DocumentationMarkup.SECTION_HEADER_START);
        sb.append(key);
        sb.append(DocumentationMarkup.SECTION_SEPARATOR);

        sb.append("<p style='margin: 0;'>");
        sb.append(value);
        sb.append("</p>");

        sb.append(DocumentationMarkup.SECTION_END);
    }

    @Override
    public @Nullable String generateHoverDoc(@NotNull PsiElement element, @Nullable PsiElement originalElement) {
        return generateDoc(element, originalElement);
    }

    @Override
    public @Nullable String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        if (element instanceof BioSyntaxDeclaration) {
            String name = ((BioSyntaxDeclaration) element).getIdentifier();
            String file = SymbolPresentationUtil.getFilePathPresentation(element.getContainingFile());
            return "\"" + name + "\" in " + file;
        }
        return null;
    }

    @Override
    public @Nullable PsiElement getCustomDocumentationElement(@NotNull Editor editor,
                                                              @NotNull PsiFile file,
                                                              @Nullable PsiElement context,
                                                              int targetOffset) {
        if (context != null) {
            PsiElement geneDefinition = PsiTreeUtil.getParentOfType(context, BioSyntaxGeneDefinition.class);
            if (geneDefinition != null) {
                return geneDefinition;
            }
            return PsiTreeUtil.getParentOfType(context, BioSyntaxDeclaration.class);
        }
        return null;
    }
}
