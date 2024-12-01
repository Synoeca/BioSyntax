// This is a generated file. Not intended for manual editing.
package org.intellij.sdk.language.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.sdk.language.psi.BioSyntaxTypes.*;
import org.intellij.sdk.language.psi.*;
import com.intellij.navigation.ItemPresentation;

public class BioSyntaxPropertyImpl extends BioSyntaxNamedElementImpl implements BioSyntaxProperty {

  public BioSyntaxPropertyImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull BioSyntaxVisitor visitor) {
    visitor.visitProperty(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BioSyntaxVisitor) accept((BioSyntaxVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  public String getKey() {
    return BioSyntaxPsiImplUtil.getKey(this);
  }

  @Override
  public String getValue() {
    return BioSyntaxPsiImplUtil.getValue(this);
  }

  @Override
  public String getName() {
    return BioSyntaxPsiImplUtil.getName(this);
  }

  @Override
  public PsiElement setName(String newName) {
    return BioSyntaxPsiImplUtil.setName(this, newName);
  }

  @Override
  public PsiElement getNameIdentifier() {
    return BioSyntaxPsiImplUtil.getNameIdentifier(this);
  }

  @Override
  public ItemPresentation getPresentation() {
    return BioSyntaxPsiImplUtil.getPresentation(this);
  }

}
