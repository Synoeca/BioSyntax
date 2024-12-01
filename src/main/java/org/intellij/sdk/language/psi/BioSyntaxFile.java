package org.intellij.sdk.language.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.intellij.sdk.language.BioSyntaxFileType;
import org.intellij.sdk.language.BioSyntaxLanguage;
import org.jetbrains.annotations.NotNull;

public class BioSyntaxFile extends PsiFileBase {
    public BioSyntaxFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, BioSyntaxLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return BioSyntaxFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "BioSyntax File";
    }
}