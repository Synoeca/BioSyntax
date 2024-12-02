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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        System.out.println("Element class: " + element.getClass());

        // Log the parent hierarchy to understand the context
        PsiElement parent = element.getParent();
        System.out.println("Parent chain:");
        while (parent != null) {
            System.out.println(" -> Parent class: " + parent.getClass() + ", Text: " + parent.getText());
            parent = parent.getParent();
        }

        String comment = BioSyntaxUtil.findDocumentationComment(element);
        String file = SymbolPresentationUtil.getFilePathPresentation(element.getContainingFile());

        StringBuilder sb = new StringBuilder();
        sb.append(DocumentationMarkup.DEFINITION_START);

        // Check if the element is a Gene class
        if (isGeneClass(element)) {
            System.out.println("Detected Gene class for element: " + element.getText());
            renderGeneDoc(element, sb); // Render the Gene class documentation
        } else if (isGeneProperty(element)) {
            System.out.println("Detected Gene property for element: " + element.getText());
            renderGenePropertyDoc(element, sb); // Render property documentation
        } else if (element instanceof BioSyntaxDeclaration) {
            System.out.println("Detected BioSyntaxDeclaration for element: " + element.getText());
            renderDeclarationDoc((BioSyntaxDeclaration) element, sb); // Render declarations (NtSeq, AASeq)
        } else {
            System.out.println("Element did not match any known types.");
        }

        if (!comment.isEmpty()) {
            sb.append(DocumentationMarkup.SECTIONS_START);
            addKeyValueSection("Comment:", comment, sb);
            addKeyValueSection("File:", file, sb);
            sb.append(DocumentationMarkup.SECTIONS_END);
        }

        return sb.toString();
    }



    // Check if the element is a Gene class
    private boolean isGeneClass(PsiElement element) {
        // Log the element's class and text to understand what it contains
        System.out.println("Checking element for Gene class: " + element.getClass() + ", Text: " + element.getText());

        // Check if the element text contains the word "Gene" followed by a class or identifier name
        // You may need to adjust this check depending on the specific format of your Gene declarations
        if (element.getText().contains("Gene")) {
            return true;
        }
        return false;
    }


    // Check if the element is part of a Gene property (like Promoter, Start_Codon, etc.)
    private boolean isGeneProperty(PsiElement element) {
        String text = element.getText();
        boolean isGeneProperty = text != null && (text.contains("Promoter") || text.contains("Start_Codon") ||
                text.contains("Stop_Codon") || text.contains("Coding_Sequence") || text.contains("Terminator"));
        if (isGeneProperty) {
            System.out.println("isGeneProperty: Gene property detected in text: " + text);
        }
        return isGeneProperty;
    }

    private void renderGeneDoc(PsiElement element, StringBuilder sb) {
        System.out.println("Rendering documentation for Gene class...");
        sb.append("Gene Class Documentation");
        sb.append(DocumentationMarkup.DEFINITION_END);
        sb.append(DocumentationMarkup.CONTENT_START);
        sb.append("This is a Gene class containing various properties related to gene structure.<br/>");
        sb.append("The Gene class typically includes the following properties:<br/>");
        sb.append("- Promoter: A sequence that controls the transcription of the gene.<br/>");
        sb.append("- Start Codon: Marks the start of the gene's coding sequence.<br/>");
        sb.append("- Coding Sequence: The DNA sequence that codes for a protein.<br/>");
        sb.append("- Stop Codon: Marks the end of the coding sequence.<br/>");
        sb.append("- Terminator: Marks the end of transcription.<br/>");
        sb.append(DocumentationMarkup.CONTENT_END);
    }

    private void renderGenePropertyDoc(PsiElement element, StringBuilder sb) {
        String text = element.getText();
        String description = getGenePropertyDescription(text);

        System.out.println("Rendering documentation for Gene property: " + text);
        sb.append("Gene Property Documentation");
        sb.append(DocumentationMarkup.DEFINITION_END);
        sb.append(DocumentationMarkup.CONTENT_START);
        sb.append(description);
        sb.append(DocumentationMarkup.CONTENT_END);
    }

    private String getGenePropertyDescription(String text) {
        if (text.contains("Start_Codon")) return "Start codon (ATG) marks the beginning of a gene's coding sequence";
        if (text.contains("Stop_Codon")) return "Stop codons (TAA, TAG, TGA) mark the end of a gene's coding sequence";
        if (text.contains("Promoter")) return "Promoter region controls gene transcription";
        if (text.contains("Terminator")) return "Terminator sequence marks transcription end";
        if (text.contains("Coding_Sequence")) return "DNA sequence that codes for protein";

        return "";
    }

    private void renderDeclarationDoc(BioSyntaxDeclaration declaration, StringBuilder sb) {
        ASTNode node = declaration.getNode();

        String type = extractType(node);
        String name = extractName(node);
        String sequence = extractSequence(node);

        sb.append(type).append(" Declaration");
        sb.append(DocumentationMarkup.DEFINITION_END);
        sb.append(DocumentationMarkup.CONTENT_START);
        sb.append("Sequence: ").append(sequence);
        sb.append(DocumentationMarkup.CONTENT_END);

        sb.append(DocumentationMarkup.SECTIONS_START);
        sb.append("Name: ").append(name).append("<br/>");
        sb.append("Type: ").append(type).append("<br/>");

        // Add description for NtSeq and AASeq
        if (type.equals("NtSeq")) {
            sb.append("Description: Nucleotide sequence (DNA) that represents the genetic code.<br/>");
        } else if (type.equals("AASeq")) {
            sb.append("Description: Amino acid sequence that represents a protein coding sequence.<br/>");
        }

        sb.append(DocumentationMarkup.SECTIONS_END);
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
        sb.append(key).append(DocumentationMarkup.SECTION_SEPARATOR).append("<p>").append(value).append(DocumentationMarkup.SECTION_END);
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
        if (context != null && context.getLanguage() == BioSyntaxLanguage.INSTANCE) {
            return PsiTreeUtil.getParentOfType(context, BioSyntaxDeclaration.class);
        }
        return super.getCustomDocumentationElement(editor, file, context, targetOffset);
    }
}
