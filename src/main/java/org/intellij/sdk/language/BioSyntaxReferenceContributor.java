package org.intellij.sdk.language;

import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import static org.intellij.sdk.language.BioSyntaxAnnotator.BIOSYNTAX_PREFIX_STR;
import static org.intellij.sdk.language.BioSyntaxAnnotator.BIOSYNTAX_SEPARATOR_STR;

final class BioSyntaxReferenceContributor extends PsiReferenceContributor {

  @Override
  public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
    registrar.registerReferenceProvider(PlatformPatterns.psiElement(PsiLiteralExpression.class),
        new PsiReferenceProvider() {
          @Override
          public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element,
                                                                 @NotNull ProcessingContext context) {
            PsiLiteralExpression literalExpression = (PsiLiteralExpression) element;
            String value = literalExpression.getValue() instanceof String ?
                (String) literalExpression.getValue() : null;
            if ((value != null && value.startsWith(BIOSYNTAX_PREFIX_STR + BIOSYNTAX_SEPARATOR_STR))) {
              TextRange property = new TextRange(BIOSYNTAX_PREFIX_STR.length() + BIOSYNTAX_SEPARATOR_STR.length() + 1,
                  value.length() + 1);
              return new PsiReference[]{new BioSyntaxReference(element, property)};
            }
            return PsiReference.EMPTY_ARRAY;
          }
        });
  }

}
