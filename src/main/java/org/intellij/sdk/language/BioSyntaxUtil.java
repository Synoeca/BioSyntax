package org.intellij.sdk.language;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
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
}