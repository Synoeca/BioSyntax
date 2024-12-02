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
import org.intellij.sdk.language.psi.BioSyntaxDeclaration;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BioSyntaxUtil {
    public static List<BioSyntaxDeclaration> findSequences(Project project) {
        List<BioSyntaxDeclaration> result = new ArrayList<>();
        Collection<VirtualFile> virtualFiles = FileTypeIndex.getFiles(BioSyntaxFileType.INSTANCE,
                GlobalSearchScope.allScope(project));

        for (VirtualFile virtualFile : virtualFiles) {
            BioSyntaxFile bioFile = (BioSyntaxFile) PsiManager.getInstance(project)
                    .findFile(virtualFile);
            if (bioFile != null) {
                BioSyntaxDeclaration[] declarations = PsiTreeUtil
                        .getChildrenOfType(bioFile, BioSyntaxDeclaration.class);
                if (declarations != null) {
                    Collections.addAll(result, declarations);
                }
            }
        }
        return result;
    }

    public static List<BioSyntaxDeclaration> findSequencesByName(Project project, String name) {
        List<BioSyntaxDeclaration> result = new ArrayList<>();
        Collection<VirtualFile> virtualFiles = FileTypeIndex.getFiles(BioSyntaxFileType.INSTANCE,
                GlobalSearchScope.allScope(project));

        for (VirtualFile virtualFile : virtualFiles) {
            BioSyntaxFile bioFile = (BioSyntaxFile) PsiManager.getInstance(project)
                    .findFile(virtualFile);
            if (bioFile != null) {
                BioSyntaxDeclaration[] declarations = PsiTreeUtil
                        .getChildrenOfType(bioFile, BioSyntaxDeclaration.class);
                if (declarations != null) {
                    for (BioSyntaxDeclaration declaration : declarations) {
                        if (name.equals(declaration.getName())) {
                            result.add(declaration);
                        }
                    }
                }
            }
        }
        return result;
    }

    @NotNull
    public static String findDocumentationComment(PsiElement element) {
        List<String> comments = new ArrayList<>();
        PsiElement current = element.getPrevSibling();

        while (current != null) {
            if (current instanceof PsiComment) {
                String commentText = current.getText().replaceFirst("//\\s*", ""); // Remove leading "// "
                comments.add(commentText);
            } else if (!(current instanceof PsiWhiteSpace)) {
                // Stop if we encounter a non-comment and non-whitespace element
                break;
            }
            current = current.getPrevSibling();
        }

        // Reverse the list to maintain the order of comments and join them into a single string
        return comments.isEmpty() ? "" : StringUtil.join(comments, "\n");
    }
}