package org.intellij.sdk.language;

import com.google.common.collect.Lists;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.sdk.language.psi.BioSyntaxFile;
import org.intellij.sdk.language.psi.BioSyntaxProperty;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BioSyntaxUtil {

  /**
   * Searches the entire project for BioSyntax language files with instances of the BioSyntax property with the given key.
   *
   * @param project current project
   * @param key     to check
   * @return matching properties
   */
  public static List<BioSyntaxProperty> findProperties(Project project, String key) {
    List<BioSyntaxProperty> result = new ArrayList<>();
    Collection<VirtualFile> virtualFiles =
        FileTypeIndex.getFiles(BioSyntaxFileType.INSTANCE, GlobalSearchScope.allScope(project));
    for (VirtualFile virtualFile : virtualFiles) {
      BioSyntaxFile bioSyntaxFile = (BioSyntaxFile) PsiManager.getInstance(project).findFile(virtualFile);
      if (bioSyntaxFile != null) {
        BioSyntaxProperty[] properties = PsiTreeUtil.getChildrenOfType(bioSyntaxFile, BioSyntaxProperty.class);
        if (properties != null) {
          for (BioSyntaxProperty property : properties) {
            if (key.equals(property.getKey())) {
              result.add(property);
            }
          }
        }
      }
    }
    return result;
  }

  public static List<BioSyntaxProperty> findProperties(Project project) {
    List<BioSyntaxProperty> result = new ArrayList<>();
    Collection<VirtualFile> virtualFiles =
        FileTypeIndex.getFiles(BioSyntaxFileType.INSTANCE, GlobalSearchScope.allScope(project));
    for (VirtualFile virtualFile : virtualFiles) {
      BioSyntaxFile bioSyntaxFile = (BioSyntaxFile) PsiManager.getInstance(project).findFile(virtualFile);
      if (bioSyntaxFile != null) {
        BioSyntaxProperty[] properties = PsiTreeUtil.getChildrenOfType(bioSyntaxFile, BioSyntaxProperty.class);
        if (properties != null) {
          Collections.addAll(result, properties);
        }
      }
    }
    return result;
  }

  /**
   * Attempts to collect any comment elements above the BioSyntax key/value pair.
   */
  public static @NotNull String findDocumentationComment(BioSyntaxProperty property) {
    List<String> result = new LinkedList<>();
    PsiElement element = property.getPrevSibling();
    while (element instanceof PsiComment || element instanceof PsiWhiteSpace) {
      if (element instanceof PsiComment) {
        String commentText = element.getText().replaceFirst("[!# ]+", "");
        result.add(commentText);
      }
      element = element.getPrevSibling();
    }
    return StringUtil.join(Lists.reverse(result), "\n ");
  }

}
