package org.intellij.sdk.language;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import org.intellij.sdk.language.psi.BioSyntaxDNASequence;
import org.intellij.sdk.language.psi.BioSyntaxProperty;
import org.jetbrains.annotations.NotNull;

import java.util.List;

final class BioSyntaxAnnotator implements Annotator {

  public static final String BIOSYNTAX_PREFIX_STR = "biosyntax";
  public static final String BIOSYNTAX_SEPARATOR_STR = ":";

  @Override
  public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
    // DNA Sequence validation
    if (element instanceof BioSyntaxDNASequence) {
      String sequence = element.getText();
      for (int i = 0; i < sequence.length(); i++) {
        char c = sequence.charAt(i);
        if (!isValidDNABase(c) && !Character.isWhitespace(c)) {
          TextRange range = TextRange.from(element.getTextRange().getStartOffset() + i, 1);
          holder.newAnnotation(HighlightSeverity.ERROR,
                          "Invalid DNA base '" + c + "'. Only A, T, C, G allowed")
                  .range(range)
                  .create();
        }
      }
      return;
    }

    // Original property validation
    if (!(element instanceof PsiLiteralExpression literalExpression)) {
      return;
    }

    String value = literalExpression.getValue() instanceof String ? (String) literalExpression.getValue() : null;
    if (value == null || !value.startsWith(BIOSYNTAX_PREFIX_STR + BIOSYNTAX_SEPARATOR_STR)) {
      return;
    }

    TextRange prefixRange = TextRange.from(element.getTextRange().getStartOffset(), BIOSYNTAX_PREFIX_STR.length() + 1);
    TextRange separatorRange = TextRange.from(prefixRange.getEndOffset(), BIOSYNTAX_SEPARATOR_STR.length());
    TextRange keyRange = new TextRange(separatorRange.getEndOffset(), element.getTextRange().getEndOffset() - 1);

    holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(prefixRange).textAttributes(DefaultLanguageHighlighterColors.KEYWORD).create();
    holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(separatorRange).textAttributes(BioSyntaxSyntaxHighlighter.SEPARATOR).create();

    String key = value.substring(BIOSYNTAX_PREFIX_STR.length() + BIOSYNTAX_SEPARATOR_STR.length());
    List<BioSyntaxProperty> properties = BioSyntaxUtil.findProperties(element.getProject(), key);
    if (properties.isEmpty()) {
      holder.newAnnotation(HighlightSeverity.ERROR, "Unresolved property")
              .range(keyRange)
              .highlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL)
              .withFix(new BioSyntaxCreatePropertyQuickFix(key))
              .create();
    } else {
      holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
              .range(keyRange).textAttributes(BioSyntaxSyntaxHighlighter.VALUE).create();
    }
  }

  private boolean isValidDNABase(char c) {
    return c == 'A' || c == 'T' || c == 'C' || c == 'G';
  }
}