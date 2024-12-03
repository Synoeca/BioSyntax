package org.intellij.sdk.language;

import com.intellij.lang.ASTNode;
import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.lang.documentation.DocumentationMarkup;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.presentation.java.SymbolPresentationUtil;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.sdk.language.psi.BioSyntaxDeclaration;
import org.intellij.sdk.language.psi.BioSyntaxGeneBody;
import org.intellij.sdk.language.psi.BioSyntaxGeneDefinition;
import org.intellij.sdk.language.psi.BioSyntaxGeneProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BioSyntaxDocumentationProvider extends AbstractDocumentationProvider {
    private static class ORF {
        final int start;
        final int end;
        final int frame;
        final int length;
        final String sequence;

        ORF(int start, int end, int frame, String sequence) {
            this.start = start;
            this.end = end;
            this.frame = frame;
            this.length = end - start;
            this.sequence = sequence;
        }

        @Override
        public String toString() {
            return String.format("%d-%d (Frame %d, %dbp)", start + 1, end, frame, length);
        }
    }

    @Override
    public @Nullable String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        if (element == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        if (isGeneClass(element)) {
            renderGeneDoc(element, sb);
        } else if (element instanceof BioSyntaxDeclaration) {
            renderSequenceDoc(element, (BioSyntaxDeclaration) element, sb);
        }

        return sb.toString();
    }

    private static final Map<String, String> CODON_TABLE = initCodonTable();
    private static final Map<String, List<String>> REVERSE_CODON_TABLE = initReverseCodonTable();

    private static Map<String, String> initCodonTable() {
        Map<String, String> table = new HashMap<>();
        table.put("ATG", "M"); // Start/Methionine
        table.put("TAA", "*"); table.put("TAG", "*"); table.put("TGA", "*"); // Stop codons

        // Phenylalanine
        table.put("TTT", "F"); table.put("TTC", "F");

        // Leucine
        table.put("TTA", "L"); table.put("TTG", "L");
        table.put("CTT", "L"); table.put("CTC", "L");
        table.put("CTA", "L"); table.put("CTG", "L");

        // Isoleucine
        table.put("ATT", "I"); table.put("ATC", "I"); table.put("ATA", "I");

        // Valine
        table.put("GTT", "V"); table.put("GTC", "V");
        table.put("GTA", "V"); table.put("GTG", "V");

        // Serine
        table.put("TCT", "S"); table.put("TCC", "S");
        table.put("TCA", "S"); table.put("TCG", "S");

        // Proline
        table.put("CCT", "P"); table.put("CCC", "P");
        table.put("CCA", "P"); table.put("CCG", "P");

        // Threonine
        table.put("ACT", "T"); table.put("ACC", "T");
        table.put("ACA", "T"); table.put("ACG", "T");

        // Alanine
        table.put("GCT", "A"); table.put("GCC", "A");
        table.put("GCA", "A"); table.put("GCG", "A");

        // Tyrosine
        table.put("TAT", "Y"); table.put("TAC", "Y");

        // Histidine
        table.put("CAT", "H"); table.put("CAC", "H");

        // Glutamine
        table.put("CAA", "Q"); table.put("CAG", "Q");

        // Asparagine
        table.put("AAT", "N"); table.put("AAC", "N");

        // Lysine
        table.put("AAA", "K"); table.put("AAG", "K");

        // Aspartic Acid
        table.put("GAT", "D"); table.put("GAC", "D");

        // Glutamic Acid
        table.put("GAA", "E"); table.put("GAG", "E");

        // Cysteine
        table.put("TGT", "C"); table.put("TGC", "C");

        // Tryptophan
        table.put("TGG", "W");

        return table;
    }

    private static Map<String, List<String>> initReverseCodonTable() {
        Map<String, List<String>> reverseTable = new HashMap<>();
        reverseTable.put("M", List.of("ATG")); // Start/Methionine
        reverseTable.put("F", List.of("TTT", "TTC")); // Phenylalanine
        reverseTable.put("L", List.of("TTA", "TTG", "CTT", "CTC", "CTA", "CTG")); // Leucine
        reverseTable.put("I", List.of("ATT", "ATC", "ATA")); // Isoleucine
        reverseTable.put("V", List.of("GTT", "GTC", "GTA", "GTG")); // Valine
        reverseTable.put("S", List.of("TCT", "TCC", "TCA", "TCG")); // Serine
        reverseTable.put("P", List.of("CCT", "CCC", "CCA", "CCG")); // Proline
        reverseTable.put("T", List.of("ACT", "ACC", "ACA", "ACG")); // Threonine
        reverseTable.put("A", List.of("GCT", "GCC", "GCA", "GCG")); // Alanine
        reverseTable.put("Y", List.of("TAT", "TAC")); // Tyrosine
        reverseTable.put("H", List.of("CAT", "CAC")); // Histidine
        reverseTable.put("Q", List.of("CAA", "CAG")); // Glutamine
        reverseTable.put("N", List.of("AAT", "AAC")); // Asparagine
        reverseTable.put("K", List.of("AAA", "AAG")); // Lysine
        reverseTable.put("D", List.of("GAT", "GAC")); // Aspartic Acid
        reverseTable.put("E", List.of("GAA", "GAG")); // Glutamic Acid
        reverseTable.put("C", List.of("TGT", "TGC")); // Cysteine
        reverseTable.put("W", List.of("TGG")); // Tryptophan
        reverseTable.put("*", List.of("TAA", "TAG", "TGA")); // Stop codons
        return reverseTable;
    }

    private String translateToRNA(String sequence) {
        return sequence.replace('T', 'U');
    }

    private String translateToDNA(String sequence) {
        return sequence.replace('U', 'T');
    }

    private String translateToProtein(String dnaSequence) {
        StringBuilder protein = new StringBuilder();
        for (int i = 0; i < dnaSequence.length() - 2; i += 3) {
            String codon = dnaSequence.substring(i, i + 3);
            protein.append(CODON_TABLE.getOrDefault(codon, "X"));
        }
        return protein.toString();
    }

    private String reverseTranslate(String aaSequence) {
        StringBuilder dnaSequence = new StringBuilder();

        for (char aa : aaSequence.toCharArray()) {
            List<String> possibleCodons = REVERSE_CODON_TABLE.get(String.valueOf(aa));
            if (possibleCodons != null && !possibleCodons.isEmpty()) {
                dnaSequence.append(possibleCodons.get(0));
            } else {
                throw new IllegalArgumentException("Invalid amino acid: " + aa);
            }
        }

        return dnaSequence.toString();
    }

    private boolean isGeneClass(PsiElement element) {
        return element instanceof BioSyntaxGeneDefinition ||
                (element.getParent() instanceof BioSyntaxGeneDefinition);
    }

    private void renderGeneDoc(PsiElement element, StringBuilder sb) {
        BioSyntaxGeneDefinition gene = (BioSyntaxGeneDefinition) element;
        BioSyntaxGeneBody body = gene.getGeneBody();
        String name = gene.getName();

        sb.append(DocumentationMarkup.DEFINITION_START)
                .append("Gene Definition")
                .append(DocumentationMarkup.DEFINITION_END)
                .append(DocumentationMarkup.CONTENT_START);

        addKeyValueSection("Name:", name, sb);
        addKeyValueSection("Type:", "Gene", sb);

        List<BioSyntaxGeneProperty> properties = PsiTreeUtil.getChildrenOfTypeAsList(body, BioSyntaxGeneProperty.class);
        if (properties.isEmpty()) {
            addKeyValueSection("Properties:", "None specified", sb);
            return;
        }

        for (BioSyntaxGeneProperty prop : properties) {
            String text = prop.getText();
            String[] parts = text.split("=");
            String key = parts[0].trim().replace("_", " ") + ":";
            String value = parts.length > 1 ?
                    parts[1].trim().replaceAll("^\"|\"$", "") : "unspecified";

            if (value.isEmpty() || value.equals(";")) {
                value = "unspecified";
            }

            addKeyValueSection(key, value, sb);

            if (value.matches("[ATCGU]+")) {
                addKeyValueSection(key + " (RNA):", translateToRNA(value), sb);
                addKeyValueSection(key + " (Protein):", translateToProtein(translateToDNA(value)), sb);
            }
        }

        getCommentAndFile(element, sb);
    }

    private void renderSequenceDoc(PsiElement element, BioSyntaxDeclaration declaration, StringBuilder sb) {
        ASTNode node = declaration.getNode();
        String type = extractType(node);
        String name = extractName(node);
        String sequence = extractSequence(node).replaceAll("\\s+", "");
        String fullTypeName = getFullTypeName(type);

        sb.append(DocumentationMarkup.DEFINITION_START)
                .append(fullTypeName)
                .append(" Definition")
                .append(DocumentationMarkup.DEFINITION_END)
                .append(DocumentationMarkup.CONTENT_START);

        addKeyValueSection("Name:", name, sb);
        addKeyValueSection("Type:", fullTypeName, sb);
        addKeyValueSection("Sequence Length:", sequence.length() + " bp", sb);
        addBasicStats(sequence, sb);
        addKeyValueSection("Sequence:", sequence, sb);

        if (!sequence.isEmpty()) {
            analyzeSequenceFeatures(sequence, type, sb);
            addTranslations(sequence, type, sb);
        }

        addDescription(fullTypeName, sb);
        getCommentAndFile(element, sb);
    }

    private void addBasicStats(String sequence, StringBuilder sb) {
        double gcContent = calculateGCContent(sequence);
        int startCodons = countPattern(sequence, "ATG");
        int stopCodons = countPattern(sequence, "TAA") +
                countPattern(sequence, "TAG") +
                countPattern(sequence, "TGA");

        addKeyValueSection("Basic Statistics:", String.format(
                """
                        GC Content: %.1f%%
                        Start Codons (ATG): %d
                        Stop Codons (TAA/TAG/TGA): %d""",
                gcContent, startCodons, stopCodons
        ), sb);
    }

    private void analyzeSequenceFeatures(String sequence, String type, StringBuilder sb) {
        if (type.equals("AASeq")) {
            // For amino acid sequences, show possible DNA codons
            addKeyValueSection("Possible DNA Codons:", getAminoAcidCodons(sequence), sb);
            return;
        }

        String analyzedSeq = type.equals("RNASeq") ? translateToDNA(sequence) : sequence;
        Map<Integer, List<ORF>> frameORFs = new HashMap<>();

        for (int frame = 0; frame < 3; frame++) {
            List<ORF> orfs = findORFsInFrame(analyzedSeq, frame);
            if (!orfs.isEmpty()) {
                frameORFs.put(frame, orfs);
            }
        }

        if (!frameORFs.isEmpty()) {
            addKeyValueSection("Reading Frames Analysis:", "", sb);
            frameORFs.forEach((frame, orfs) -> {
                addKeyValueSection("Frame " + frame + ":", formatORFDetails(orfs), sb);
                // Add amino acid translations for each frame
                String frameTranslation = translateFrame(analyzedSeq, frame);
                addKeyValueSection("Frame " + frame + " Translation:", formatSequence(frameTranslation), sb);
            });
        }
    }

    private String translateFrame(String sequence, int frame) {
        StringBuilder translation = new StringBuilder();
        for (int i = frame; i < sequence.length() - 2; i += 3) {
            String codon = sequence.substring(i, i + 3);
            translation.append(CODON_TABLE.getOrDefault(codon, "X"));
        }
        return translation.toString();
    }

    private String getAminoAcidCodons(String aaSequence) {
        return aaSequence.chars()
                .mapToObj(aa -> String.format("%c: %s",
                        (char)aa,
                        String.join(", ", REVERSE_CODON_TABLE.getOrDefault(String.valueOf((char)aa), List.of("Unknown")))
                ))
                .collect(Collectors.joining("\n"));
    }

    private List<ORF> findORFsInFrame(String sequence, int frame) {
        List<ORF> orfs = new ArrayList<>();
        int minORFLength = 90; // Minimum 30 amino acids

        for (int i = frame; i < sequence.length() - 2; i += 3) {
            if (sequence.startsWith("ATG", i)) {
                for (int j = i + 3; j < sequence.length() - 2; j += 3) {
                    String codon = sequence.substring(j, j + 3);
                    if (isStopCodon(codon)) {
                        int length = j + 3 - i;
                        if (length >= minORFLength) {
                            String orfSeq = sequence.substring(i, j + 3);
                            orfs.add(new ORF(i, j + 3, frame, orfSeq));
                        }
                        break;
                    }
                }
            }
        }
        return orfs;
    }

    private String formatORFDetails(List<ORF> orfs) {
        return orfs.stream()
                .map(orf -> String.format(
                        """
                                %s
                                  Sequence: %s
                                  Translation: %s
                                  GC Content: %.1f%%
                                  Kozak Context: %s""",
                        orf.toString(),
                        formatSequence(orf.sequence),
                        translateToProtein(orf.sequence),
                        calculateGCContent(orf.sequence),
                        analyzeKozakContext(orf.sequence)
                ))
                .collect(Collectors.joining("\n\n"));
    }

    private String formatSequence(String sequence) {
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < sequence.length(); i += 60) {
            formatted.append(sequence, i, Math.min(i + 60, sequence.length())).append("\n");
        }
        return formatted.toString().trim();
    }

    private boolean isStopCodon(String codon) {
        return codon.equals("TAA") || codon.equals("TAG") || codon.equals("TGA");
    }

    private double calculateGCContent(String sequence) {
        long gcCount = sequence.chars()
                .filter(ch -> ch == 'G' || ch == 'C')
                .count();
        return (double) gcCount / sequence.length() * 100;
    }

    private int countPattern(String sequence, String pattern) {
        return (sequence.length() - sequence.replace(pattern, "").length()) / pattern.length();
    }

    private String analyzeKozakContext(String sequence) {
        if (sequence.length() < 9) return "Sequence too short";
        String kozakRegion = sequence.substring(0, 9);
        return "(" + kozakRegion + ")";
    }

    private void addTranslations(String sequence, String type, StringBuilder sb) {
        switch (type) {
            case "NtSeq", "DNASeq" -> {
                addKeyValueSection("RNA Form:", translateToRNA(sequence), sb);
                addKeyValueSection("DNA Form:", sequence, sb);
                for (int frame = 0; frame < 3; frame++) {
                    String proteinSeq = translateFrame(sequence, frame);
                    if (!proteinSeq.isEmpty()) {
                        addKeyValueSection("Amino Acid (Frame " + frame + "):", formatSequence(proteinSeq), sb);
                    }
                }
            }
            case "RNASeq" -> {
                addKeyValueSection("RNA Form:", sequence, sb);
                addKeyValueSection("DNA Form:", translateToDNA(sequence), sb);
                String dnaSeq = translateToDNA(sequence);
                for (int frame = 0; frame < 3; frame++) {
                    String proteinSeq = translateFrame(dnaSeq, frame);
                    if (!proteinSeq.isEmpty()) {
                        addKeyValueSection("Amino Acid (Frame " + frame + "):", formatSequence(proteinSeq), sb);
                    }
                }
            }
            case "AASeq" -> {
                String dnaForm = reverseTranslate(sequence);
                addKeyValueSection("DNA Form:", dnaForm, sb);
                addKeyValueSection("RNA Form:", translateToRNA(dnaForm), sb);
                addKeyValueSection("Amino Acid:", sequence, sb);
            }
        }
    }



    private String getFullTypeName(String type) {
        return switch (type) {
            case "NtSeq" -> "Nucleotide Sequence";
            case "AASeq" -> "Amino Acid Sequence";
            case "RNASeq" -> "RNA Sequence";
            case "DNASeq" -> "DNA Sequence";
            default -> type;
        };
    }

    private void addDescription(String fullTypeName, StringBuilder sb) {
        switch (fullTypeName) {
            case "Nucleotide Sequence":
                addKeyValueSection("Description:", "Generic nucleotide sequence that can represent DNA or RNA.", sb);
                break;
            case "Amino Acid Sequence":
                addKeyValueSection("Description:", "Amino acid sequence that represents a protein or peptide.", sb);
                break;
            case "RNA Sequence":
                addKeyValueSection("Description:", "Ribonucleic acid sequence that represents RNA molecules.", sb);
                break;
            case "DNA Sequence":
                addKeyValueSection("Description:", "Deoxyribonucleic acid sequence that represents DNA molecules.", sb);
                break;
        }
    }

    private void getCommentAndFile(PsiElement element, StringBuilder sb) {
        String comment = BioSyntaxUtil.findDocumentationComment(element);
        String file = SymbolPresentationUtil.getFilePathPresentation(element.getContainingFile());
        if (!comment.isEmpty()) {
            addKeyValueSection("Comment:", comment, sb);
        }

        if (!file.isEmpty()) {
            addKeyValueSection("File:", file, sb);
        }

        sb.append(DocumentationMarkup.CONTENT_END);
    }

    private String extractType(ASTNode node) {
        String text = node.getText().trim();
        String[] parts = text.split("\\s+");
        return parts.length > 0 ? parts[0] : "unknown";
    }

    private String extractName(ASTNode node) {
        String text = node.getText().trim();
        String[] parts = text.split("\\s+");
        return parts.length > 1 ? parts[1] : "unknown";
    }

    private String extractSequence(ASTNode node) {
        String text = node.getText().trim();
        int equalIndex = text.indexOf("=");
        if (equalIndex != -1) {
            String sequence = text.substring(equalIndex + 1).trim();
            sequence = sequence.replaceAll("\"", "");
            return sequence;
        }
        return "unknown";
    }

    private void addKeyValueSection(String key, String value, StringBuilder sb) {
        sb.append(DocumentationMarkup.SECTION_HEADER_START);
        sb.append(key);
        sb.append(DocumentationMarkup.SECTION_SEPARATOR);

        sb.append("<p style='margin: 0;'>");
        sb.append(value);
        sb.append("</p>");

        sb.append(DocumentationMarkup.SECTION_END);
    }

    @Override
    public @Nullable String generateHoverDoc(@NotNull PsiElement element, @Nullable PsiElement originalElement) {
        return generateDoc(element, originalElement);
    }

    @Override
    public @Nullable String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        if (element instanceof BioSyntaxDeclaration) {
            String name = ((BioSyntaxDeclaration) element).getIdentifier();
            String file = SymbolPresentationUtil.getFilePathPresentation(element.getContainingFile());
            return "\"" + name + "\" in " + file;
        }
        return null;
    }

    @Override
    public @Nullable PsiElement getCustomDocumentationElement(@NotNull Editor editor,
                                                              @NotNull PsiFile file,
                                                              @Nullable PsiElement context,
                                                              int targetOffset) {
        if (context != null) {
            PsiElement geneDefinition = PsiTreeUtil.getParentOfType(context, BioSyntaxGeneDefinition.class);
            if (geneDefinition != null) {
                return geneDefinition;
            }
            return PsiTreeUtil.getParentOfType(context, BioSyntaxDeclaration.class);
        }
        return null;
    }
}
