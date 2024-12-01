package org.intellij.sdk.language;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.lang.documentation.DocumentationMarkup;
import com.intellij.psi.PsiElement;
import com.intellij.psi.presentation.java.SymbolPresentationUtil;
import org.intellij.sdk.language.psi.BioSyntaxProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * TODO Please note, it is recommended to utilize the new DocumentationTarget API for
 * plugins targeting IntelliJ Platform version 2023.1 or later.
 *
 * @see <a href="https://plugins.jetbrains.com/docs/intellij/documentation.html">Documentation (IntelliJ Platform Docs)</a>
 */
final class BioSyntaxDocumentationProvider extends AbstractDocumentationProvider {

  /**
   * For the BioSyntax Language, we don't have online documentation. However, if your language provides
   * references pages online, URLs for the element can be returned here.
   */
  @Override
  public @Nullable List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
    return null;
  }

  /**
   * Extracts the key, value, file and documentation comment of a BioSyntax key/value entry and returns
   * a formatted representation of the information.
   */
  @Override
  public @Nullable String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
    if (element instanceof BioSyntaxProperty) {
      final String key = String.valueOf(((BioSyntaxProperty) element).getKey());
      final String value = String.valueOf(((BioSyntaxProperty) element).getValue());
      final String file = SymbolPresentationUtil.getFilePathPresentation(element.getContainingFile());
      final String docComment = BioSyntaxUtil.findDocumentationComment((BioSyntaxProperty) element);

      return renderFullDoc(key, value, file, docComment);
    }
    return null;
  }

  /**
   * Provides the information in which file the BioSyntax language key/value is defined.
   */
  @Override
  public @Nullable String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
    if (element instanceof BioSyntaxProperty) {
      final String key = String.valueOf(((BioSyntaxProperty) element).getKey());
      final String file = SymbolPresentationUtil.getFilePathPresentation(element.getContainingFile());
      return "\"" + key + "\" in " + file;
    }
    return null;
  }

  /**
   * Provides documentation when a BioSyntax Language element is hovered with the mouse.
   */
  @Override
  public @Nullable String generateHoverDoc(@NotNull PsiElement element, @Nullable PsiElement originalElement) {
    return generateDoc(element, originalElement);
  }

  /**
   * Creates a key/value row for the rendered documentation.
   */
  private void addKeyValueSection(String key, String value, StringBuilder sb) {
    sb.append(DocumentationMarkup.SECTION_HEADER_START);
    sb.append(key);
    sb.append(DocumentationMarkup.SECTION_SEPARATOR);
    sb.append("<p>");
    sb.append(value);
    sb.append(DocumentationMarkup.SECTION_END);
  }

  /**
   * Creates the formatted documentation using {@link DocumentationMarkup}. See the Java doc of
   * {@link com.intellij.lang.documentation.DocumentationProvider#generateDoc(PsiElement, PsiElement)} for more
   * information about building the layout.
   */
  private String renderFullDoc(String key, String value, String file, String docComment) {
    StringBuilder sb = new StringBuilder();
    sb.append(DocumentationMarkup.DEFINITION_START);
    sb.append("BioSyntax Property");
    sb.append(DocumentationMarkup.DEFINITION_END);
    sb.append(DocumentationMarkup.CONTENT_START);
    sb.append(value);
    sb.append(DocumentationMarkup.CONTENT_END);
    sb.append(DocumentationMarkup.SECTIONS_START);
    addKeyValueSection("Key:", key, sb);
    addKeyValueSection("Value:", value, sb);
    addKeyValueSection("File:", file, sb);
    addKeyValueSection("Comment:", docComment, sb);
    sb.append(DocumentationMarkup.SECTIONS_END);
    return sb.toString();
  }

}
