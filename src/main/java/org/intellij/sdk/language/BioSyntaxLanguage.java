package org.intellij.sdk.language;

import com.intellij.lang.Language;

public class BioSyntaxLanguage extends Language {

    public static final BioSyntaxLanguage INSTANCE = new BioSyntaxLanguage();

    private BioSyntaxLanguage() {
        super("BioSyntax");
    }

}
