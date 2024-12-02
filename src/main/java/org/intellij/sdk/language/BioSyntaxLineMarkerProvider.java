package org.intellij.sdk.language;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.impl.source.tree.java.PsiJavaTokenImpl;
import org.intellij.sdk.language.psi.BioSyntaxDeclaration;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

final class BioSyntaxLineMarkerProvider extends RelatedItemLineMarkerProvider {

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element,
                                            @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {

        if (!(element instanceof PsiJavaTokenImpl) || !(element.getParent() instanceof PsiLiteralExpression literalExpression)) {
            return;
        }

        String value = literalExpression.getValue() instanceof String ? (String) literalExpression.getValue() : null;
        if (value == null || !value.startsWith("BioSyntax")) {
            return;
        }

        String propertyName = value.substring("BioSyntax".length());

        Project project = element.getProject();
        List<BioSyntaxDeclaration> declarations = BioSyntaxUtil.findSequencesByName(project, propertyName);

        if (!declarations.isEmpty()) {
            NavigationGutterIconBuilder<PsiElement> builder =
                    NavigationGutterIconBuilder.create(BioSyntaxIcons.FILE)
                            .setTargets(declarations)
                            .setTooltipText("Navigate to BioSyntax declaration");
            result.add(builder.createLineMarkerInfo(element));
        }
    }
}
