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
%eof{  return;
%eof}

CRLF=\R
WHITE_SPACE=[\ \n\t\f]
COMMENT="//"[^\r\n]*

// Nucleotides
ADENINE=[Aa]
THYMINE=[Tt]
GUANINE=[Gg]
CYTOSINE=[Cc]
URACIL=[Uu]

// Amino Acids
AMINO_ACID=[ACDEFGHIKLMNPQRSTVWY]

// Structural elements
PROMOTER="promoter"
TERMINATOR="terminator"
START_CODON=ATG
STOP_CODON=(TAA|TAG|TGA)

%%

<YYINITIAL> {COMMENT}                    { return BioSyntaxTypes.COMMENT; }
<YYINITIAL> {PROMOTER}                   { return BioSyntaxTypes.PROMOTER; }
<YYINITIAL> {TERMINATOR}                 { return BioSyntaxTypes.TERMINATOR; }
<YYINITIAL> {START_CODON}               { return BioSyntaxTypes.START_CODON; }
<YYINITIAL> {STOP_CODON}                { return BioSyntaxTypes.STOP_CODON; }
<YYINITIAL> {ADENINE}                   { return BioSyntaxTypes.ADENINE; }
<YYINITIAL> {THYMINE}                   { return BioSyntaxTypes.THYMINE; }
<YYINITIAL> {GUANINE}                   { return BioSyntaxTypes.GUANINE; }
<YYINITIAL> {CYTOSINE}                  { return BioSyntaxTypes.CYTOSINE; }
<YYINITIAL> {URACIL}                    { return BioSyntaxTypes.URACIL; }
<YYINITIAL> {AMINO_ACID}                { return BioSyntaxTypes.AMINO_ACID; }

{WHITE_SPACE}+                          { return TokenType.WHITE_SPACE; }
[^]                                     { return TokenType.BAD_CHARACTER; }