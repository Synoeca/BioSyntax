package org.intellij.sdk.language;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.tree.TokenSet;
import org.intellij.sdk.language.psi.BioSyntaxProperty;
import org.intellij.sdk.language.psi.BioSyntaxTokenSets;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class BioSyntaxFindUsagesProvider implements FindUsagesProvider {

  @Override
  public WordsScanner getWordsScanner() {
    return new DefaultWordsScanner(new BioSyntaxLexerAdapter(),
        BioSyntaxTokenSets.IDENTIFIERS,
        BioSyntaxTokenSets.COMMENTS,
        TokenSet.EMPTY);
  }

  @Override
  public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
    return psiElement instanceof PsiNamedElement;
  }

  @Nullable
  @Override
  public String getHelpId(@NotNull PsiElement psiElement) {
    return null;
  }

  @NotNull
  @Override
  public String getType(@NotNull PsiElement element) {
    if (element instanceof BioSyntaxProperty) {
      return "biosyntax property";
    }
    return "";
  }

  @NotNull
  @Override
  public String getDescriptiveName(@NotNull PsiElement element) {
    if (element instanceof BioSyntaxProperty) {
      return String.valueOf(((BioSyntaxProperty) element).getKey());
    }
    return "";
  }

  @NotNull
  @Override
  public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
    if (element instanceof BioSyntaxProperty) {
      return ((BioSyntaxProperty) element).getKey() +
          BioSyntaxAnnotator.BIOSYNTAX_SEPARATOR_STR +
          ((BioSyntaxProperty) element).getValue();
    }
    return "";
  }

}
