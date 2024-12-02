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
            System.out.println("generateDoc called with null element.");
            return null;
        }

        // Log the method call and element details
        System.out.println("generateDoc method triggered.");
        System.out.println("Element text: " + element.getText());

        StringBuilder sb = new StringBuilder();

        if (isGeneClass(element)) {
            System.out.println("Detected Gene class for element: " + element.getText());
            renderGeneDoc(element, sb);
        } else if (element instanceof BioSyntaxDeclaration) {
            System.out.println("Detected BioSyntaxDeclaration for element: " + element.getText());
            renderSequenceDoc(element, (BioSyntaxDeclaration) element, sb);
        } else {
            System.out.println("Element did not match any known types.");
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

        sb.append(DocumentationMarkup.DEFINITION_START);
        sb.append(type).append(" Definition");
        sb.append(DocumentationMarkup.DEFINITION_END);
        sb.append(DocumentationMarkup.CONTENT_START);

        addKeyValueSection("Name:", name, sb);
        addKeyValueSection("Type:", type, sb);
        addKeyValueSection("Sequence:", sequence.isEmpty() ? "unspecified" : sequence, sb);

        if (type.equals("NtSeq")) {
            addKeyValueSection("Description:", "Nucleotide sequence (DNA) that represents the genetic code.", sb);
        } else if (type.equals("AASeq")) {
            addKeyValueSection("Description:", "Amino acid sequence that represents a protein coding sequence.", sb);
        }

        getCommentAndFile(element, sb);
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
