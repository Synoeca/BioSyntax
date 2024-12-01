// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package org.intellij.sdk.language.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.intellij.sdk.language.psi.BioSyntaxElementFactory;
import org.intellij.sdk.language.psi.BioSyntaxProperty;
import org.intellij.sdk.language.psi.BioSyntaxTypes;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class BioSyntaxPsiImplUtil {

  public static String getKey(BioSyntaxProperty element) {
    ASTNode keyNode = element.getNode().findChildByType(BioSyntaxTypes.KEY);
    if (keyNode != null) {
      // IMPORTANT: Convert embedded escaped spaces to biosyntax spaces
      return keyNode.getText().replaceAll("\\\\ ", " ");
    } else {
      return null;
    }
  }

  public static String getValue(BioSyntaxProperty element) {
    ASTNode valueNode = element.getNode().findChildByType(BioSyntaxTypes.VALUE);
    if (valueNode != null) {
      return valueNode.getText();
    } else {
      return null;
    }
  }

  public static String getName(BioSyntaxProperty element) {
    return getKey(element);
  }

  public static PsiElement setName(BioSyntaxProperty element, String newName) {
    ASTNode keyNode = element.getNode().findChildByType(BioSyntaxTypes.KEY);
    if (keyNode != null) {
      BioSyntaxProperty property = BioSyntaxElementFactory.createProperty(element.getProject(), newName);
      ASTNode newKeyNode = property.getFirstChild().getNode();
      element.getNode().replaceChild(keyNode, newKeyNode);
    }
    return element;
  }

  public static PsiElement getNameIdentifier(BioSyntaxProperty element) {
    ASTNode keyNode = element.getNode().findChildByType(BioSyntaxTypes.KEY);
    if (keyNode != null) {
      return keyNode.getPsi();
    } else {
      return null;
    }
  }

  public static ItemPresentation getPresentation(final BioSyntaxProperty element) {
    return new ItemPresentation() {
      @Nullable
      @Override
      public String getPresentableText() {
        return String.valueOf(element.getKey());
      }

      @Nullable
      @Override
      public String getLocationString() {
        PsiFile containingFile = element.getContainingFile();
        return containingFile == null ? null : containingFile.getName();
      }

      @Override
      public Icon getIcon(boolean unused) {
        return element.getIcon(0);
      }
    };
  }

}
