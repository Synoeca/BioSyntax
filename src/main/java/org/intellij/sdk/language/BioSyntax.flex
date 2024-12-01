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
  private IElementType prevToken;
%}

%eof{  return;
%eof}

CRLF=\R
WHITE_SPACE=[\ \n\t\f]
COMMENT="//"[^\r\n]*

// Combined nucleotides and amino acids
NUCLEOTIDE=[ATGC]
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
NT_SEQ="NtSeq"
AA_SEQ="AASeq"

%state IN_NT_STRING, IN_AA_STRING

%{
  private IElementType seqType = null;
%}


%%
<YYINITIAL> {NT_SEQ}            { seqType = BioSyntaxTypes.NT_SEQ; return BioSyntaxTypes.NT_SEQ; }
<YYINITIAL> {AA_SEQ}            { seqType = BioSyntaxTypes.AA_SEQ; return BioSyntaxTypes.AA_SEQ; }
<YYINITIAL> {IDENTIFIER}        { return BioSyntaxTypes.IDENTIFIER; }
<YYINITIAL> {EQUALS}           { return BioSyntaxTypes.EQUALS; }

<YYINITIAL> {QUOTE}            {
    if (seqType == BioSyntaxTypes.NT_SEQ) {
        yybegin(IN_NT_STRING);
    } else if (seqType == BioSyntaxTypes.AA_SEQ) {
        yybegin(IN_AA_STRING);
    }
    return BioSyntaxTypes.QUOTE;
}


<IN_NT_STRING> {NUCLEOTIDE}+          { return BioSyntaxTypes.NUCLEOTIDE; }
<IN_AA_STRING> {AMINO_ACID}+    { return BioSyntaxTypes.AMINO_ACID; }

<IN_NT_STRING> {QUOTE}          { yybegin(YYINITIAL); return BioSyntaxTypes.QUOTE; }
<IN_AA_STRING> {QUOTE}          { yybegin(YYINITIAL); return BioSyntaxTypes.QUOTE; }

<YYINITIAL> {COMMENT}          { return BioSyntaxTypes.COMMENT; }
<YYINITIAL> {PROMOTER}         { return BioSyntaxTypes.PROMOTER; }
<YYINITIAL> {TERMINATOR}       { return BioSyntaxTypes.TERMINATOR; }
<YYINITIAL> {START_CODON}      { return BioSyntaxTypes.START_CODON; }
<YYINITIAL> {STOP_CODON}       { return BioSyntaxTypes.STOP_CODON; }

{WHITE_SPACE}+                 { return TokenType.WHITE_SPACE; }
[^]                           { return TokenType.BAD_CHARACTER; }