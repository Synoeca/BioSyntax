package org.intellij.sdk.language;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.lang.documentation.DocumentationMarkup;
import com.intellij.psi.PsiElement;
import com.intellij.psi.presentation.java.SymbolPresentationUtil;
import org.intellij.sdk.language.psi.BioSyntaxProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

final class BioSyntaxDocumentationProvider extends AbstractDocumentationProvider {

    private String determineSequenceType(String value) {
    if (value == null || value.isEmpty()) return "Property";

    // Check for DNA sequence (ATCG pattern)
    if (value.matches("[ATCG\\s]+")) return "DNA Sequence";

    // Check for RNA sequence (AUCG pattern)
    if (value.matches("[AUCG\\s]+")) return "RNA Sequence";

    // Check for Amino Acid sequence
    if (value.matches("(ALA|ARG|ASN|ASP|CYS|GLN|GLU|GLY|HIS|ILE|LEU|LYS|MET|PHE|PRO|SER|THR|TRP|TYR|VAL|\\s)+"))
      return "Protein Sequence";

    // Check for known property types
    if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"))
      return "Boolean Property";

    if (value.matches("-?\\d+(\\.\\d+)?"))
      return "Numeric Property";

    return "BioSyntax Property";
  }

  @Override
  public @Nullable String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
    if (element instanceof BioSyntaxProperty) {
      final String key = String.valueOf(((BioSyntaxProperty) element).getKey());
      final String value = String.valueOf(((BioSyntaxProperty) element).getValue());
      final String file = SymbolPresentationUtil.getFilePathPresentation(element.getContainingFile());
      final String docComment = BioSyntaxUtil.findDocumentationComment((BioSyntaxProperty) element);
      final String type = determineSequenceType(value);

      return renderFullDoc(type, key, value, file, docComment);
    }
    return null;
  }

  @Override
  public @Nullable String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
    if (element instanceof BioSyntaxProperty) {
      final String key = String.valueOf(((BioSyntaxProperty) element).getKey());
      final String value = String.valueOf(((BioSyntaxProperty) element).getValue());
      final String type = determineSequenceType(value);
      final String file = SymbolPresentationUtil.getFilePathPresentation(element.getContainingFile());
      return type + " \"" + key + "\" in " + file;
    }
    return null;
  }

  @Override
  public @Nullable String generateHoverDoc(@NotNull PsiElement element, @Nullable PsiElement originalElement) {
    return generateDoc(element, originalElement);
  }

  private void addKeyValueSection(String key, String value, StringBuilder sb) {
    sb.append(DocumentationMarkup.SECTION_HEADER_START);
    sb.append(key);
    sb.append(DocumentationMarkup.SECTION_SEPARATOR);
    sb.append("<p>");
    sb.append(value);
    sb.append(DocumentationMarkup.SECTION_END);
  }

  private String renderFullDoc(String type, String key, String value, String file, String docComment) {
    StringBuilder sb = new StringBuilder();
    sb.append(DocumentationMarkup.DEFINITION_START);
    sb.append(type);
    sb.append(DocumentationMarkup.DEFINITION_END);
    sb.append(DocumentationMarkup.CONTENT_START);
    sb.append(value);
    sb.append(DocumentationMarkup.CONTENT_END);
    sb.append(DocumentationMarkup.SECTIONS_START);
    addKeyValueSection("Type:", type, sb);
    addKeyValueSection("Key:", key, sb);
    addKeyValueSection("Value:", value, sb);
    addKeyValueSection("File:", file, sb);
    addKeyValueSection("Comment:", docComment, sb);
    sb.append(DocumentationMarkup.SECTIONS_END);
    return sb.toString();
  }
}