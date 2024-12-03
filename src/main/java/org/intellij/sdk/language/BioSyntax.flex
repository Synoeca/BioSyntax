package org.intellij.sdk.language;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import org.intellij.sdk.language.psi.BioSyntaxTypes;
import com.intellij.psi.TokenType;

%%

%class BioSyntaxLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%{
  private IElementType seqType = null;
%}

%eof{  return;
%eof}

CRLF=\R
WHITE_SPACE=[\ \n\t\f]
COMMENT="//"[^\r\n]*

// Token types
NT_SEQ="NtSeq"
DNA_SEQ="DNASeq"
RNA_SEQ="RNASeq"
AA_SEQ="AASeq"

// Sequence definitions
NUCLEOTIDE=[ATUGC]
DNA_NUCLEOTIDE=[ATGC]
RNA_NUCLEOTIDE=[AUGC]
AMINO_ACID=[ACDEFGHIKLMNPQRSTVWY]

// Structural elements
PROMOTER="promoter"
TERMINATOR="terminator"
START_CODON=ATG
STOP_CODON=(TAA|TAG|TGA)

// Type system
IDENTIFIER=[a-zA-Z][a-zA-Z0-9_]*
EQUALS="="
QUOTE=\"

// Keywords
GENE="Gene"

// Braces
LBRACE="{"
RBRACE="}"

// Gene properties
PROMOTER="Promoter"
START_CODON="Start_Codon"
CODING_SEQUENCE="Coding_Sequence"
STOP_CODON="Stop_Codon"
TERMINATOR="Terminator"
SEMICOLON=";"

%state IN_NT_STRING, IN_RNA_STRING, IN_DNA_STRING, IN_AA_STRING, IN_GENE_BODY

%%

<YYINITIAL> {NT_SEQ}            { seqType = BioSyntaxTypes.NT_SEQ; return BioSyntaxTypes.NT_SEQ; }
<YYINITIAL> {DNA_SEQ}            { seqType = BioSyntaxTypes.DNA_SEQ; return BioSyntaxTypes.DNA_SEQ; }
<YYINITIAL> {RNA_SEQ}            { seqType = BioSyntaxTypes.RNA_SEQ; return BioSyntaxTypes.RNA_SEQ; }
<YYINITIAL> {AA_SEQ}            { seqType = BioSyntaxTypes.AA_SEQ; return BioSyntaxTypes.AA_SEQ; }
<YYINITIAL> {GENE}              { return BioSyntaxTypes.GENE; }
<YYINITIAL> {IDENTIFIER}        { return BioSyntaxTypes.IDENTIFIER; }
<YYINITIAL> {EQUALS}            { return BioSyntaxTypes.EQUALS; }
<YYINITIAL> {LBRACE}            { yybegin(IN_GENE_BODY); return BioSyntaxTypes.LBRACE; }
<YYINITIAL> {RBRACE}            { yybegin(YYINITIAL); return BioSyntaxTypes.RBRACE; }

<YYINITIAL> {QUOTE}             {
   if (seqType == BioSyntaxTypes.NT_SEQ) {
       yybegin(IN_NT_STRING);
   } else if (seqType == BioSyntaxTypes.AA_SEQ) {
       yybegin(IN_AA_STRING);
   } else if (seqType == BioSyntaxTypes.DNA_SEQ) {
       yybegin(IN_DNA_STRING);
   } else if (seqType == BioSyntaxTypes.RNA_SEQ) {
       yybegin(IN_RNA_STRING);
   }
   return BioSyntaxTypes.QUOTE;
}

<YYINITIAL> {COMMENT}               { return BioSyntaxTypes.COMMENT; }

<IN_NT_STRING> {NUCLEOTIDE}+        { return BioSyntaxTypes.NUCLEOTIDE; }
<IN_RNA_STRING> {RNA_NUCLEOTIDE}+   { return BioSyntaxTypes.RNA_NUCLEOTIDE; }
<IN_DNA_STRING> {DNA_NUCLEOTIDE}+   { return BioSyntaxTypes.DNA_NUCLEOTIDE; }
<IN_AA_STRING> {AMINO_ACID}+        { return BioSyntaxTypes.AMINO_ACID; }
<IN_NT_STRING> {QUOTE}              { yybegin(YYINITIAL); return BioSyntaxTypes.QUOTE; }
<IN_RNA_STRING> {QUOTE}          { yybegin(YYINITIAL); return BioSyntaxTypes.QUOTE; }
<IN_DNA_STRING> {QUOTE}          { yybegin(YYINITIAL); return BioSyntaxTypes.QUOTE; }
<IN_AA_STRING> {QUOTE}              { yybegin(YYINITIAL); return BioSyntaxTypes.QUOTE; }

<IN_GENE_BODY> {PROMOTER}           { return BioSyntaxTypes.PROMOTER; }
<IN_GENE_BODY> {START_CODON}        { return BioSyntaxTypes.START_CODON; }
<IN_GENE_BODY> {CODING_SEQUENCE}     { return BioSyntaxTypes.CODING_SEQUENCE; }
<IN_GENE_BODY> {STOP_CODON}         { return BioSyntaxTypes.STOP_CODON; }
<IN_GENE_BODY> {TERMINATOR}         { return BioSyntaxTypes.TERMINATOR; }
<IN_GENE_BODY> {EQUALS}             { return BioSyntaxTypes.EQUALS; }
<IN_GENE_BODY> {QUOTE}              { return BioSyntaxTypes.QUOTE; }
<IN_GENE_BODY> {SEMICOLON}          { return BioSyntaxTypes.SEMICOLON; }
<IN_GENE_BODY> {NUCLEOTIDE}+        { return BioSyntaxTypes.NUCLEOTIDE; }
<IN_GENE_BODY> {AMINO_ACID}+        { return BioSyntaxTypes.AMINO_ACID; }
<IN_GENE_BODY> {RBRACE}             { yybegin(YYINITIAL); return BioSyntaxTypes.RBRACE; }

{WHITE_SPACE}+                      { return TokenType.WHITE_SPACE; }
[^]                                  { return TokenType.BAD_CHARACTER; }