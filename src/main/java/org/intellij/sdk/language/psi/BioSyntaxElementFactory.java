package org.intellij.sdk.language.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import org.intellij.sdk.language.BioSyntaxFileType;

public class BioSyntaxElementFactory {

  public static BioSyntaxProperty createProperty(Project project, String name) {
    final BioSyntaxFile file = createFile(project, name);
    return (BioSyntaxProperty) file.getFirstChild();
  }

  public static BioSyntaxFile createFile(Project project, String text) {
    String name = "dummy.biosyntax";
    return (BioSyntaxFile) PsiFileFactory.getInstance(project).createFileFromText(name, BioSyntaxFileType.INSTANCE, text);
  }

  public static BioSyntaxProperty createProperty(Project project, String name, String value) {
    final BioSyntaxFile file = createFile(project, name + " = " + value);
    return (BioSyntaxProperty) file.getFirstChild();
  }

  public static PsiElement createCRLF(Project project) {
    final BioSyntaxFile file = createFile(project, "\n");
    return file.getFirstChild();
  }

}
