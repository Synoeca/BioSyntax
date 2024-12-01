package org.intellij.sdk.language;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static org.intellij.sdk.language.psi.BioSyntaxTypes.*;

%%

%{
  public BioSyntaxLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class BioSyntaxLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

CRLF=\R
WHITE_SPACE=[\ \n\t\f]
FIRST_VALUE_CHARACTER=[^ \n\f\\#]
VALUE_CHARACTER=[^\n\f\\#]
VALUE_LINE={FIRST_VALUE_CHARACTER}{VALUE_CHARACTER}*
END_OF_LINE_COMMENT=("#"|"!")[^\r\n]*
SEPARATOR=[:=]
KEY_CHARACTER=[^:=\ \n\t\f\\] | "\\ "
BASE_PAIR=[ATCG]
AMINO_ACID=("ALA"|"ARG"|"ASN"|"ASP"|"CYS"|"GLN"|"GLU"|"GLY"|"HIS"|"ILE"|"LEU"|"LYS"|"MET"|"PHE"|"PRO"|"SER"|"THR"|"TRP"|"TYR"|"VAL")
DNA_CODON={BASE_PAIR}{BASE_PAIR}{BASE_PAIR}
LINE_CONTINUATION=\\[ \t]*({END_OF_LINE_COMMENT})?{CRLF}[ \t]*

%state WAITING_VALUE

%%

<WAITING_VALUE> {VALUE_LINE}({LINE_CONTINUATION}{VALUE_LINE})*  { return VALUE; }
<YYINITIAL> {END_OF_LINE_COMMENT}                           { return COMMENT; }
<YYINITIAL> {KEY_CHARACTER}+                                { return KEY; }
<YYINITIAL> {SEPARATOR}                                     { yybegin(WAITING_VALUE); return SEPARATOR; }
<YYINITIAL> {DNA_CODON}                                    { return CODON; }
<YYINITIAL> {BASE_PAIR}                                    { return BASE_PAIR; }
<YYINITIAL> {AMINO_ACID}                                   { return AMINO_ACID; }

<WAITING_VALUE> {WHITE_SPACE}+                              { yybegin(WAITING_VALUE); return WHITE_SPACE; }
<WAITING_VALUE> {LINE_CONTINUATION}                         { yybegin(WAITING_VALUE); }
<WAITING_VALUE> {END_OF_LINE_COMMENT}                       { yybegin(YYINITIAL); return COMMENT; }
<WAITING_VALUE> {FIRST_VALUE_CHARACTER}{VALUE_CHARACTER}*   { return VALUE; }
<WAITING_VALUE> {DNA_CODON}                                { return CODON; }
<WAITING_VALUE> {AMINO_ACID}                               { return AMINO_ACID; }
<WAITING_VALUE> {CRLF}                                     { yybegin(YYINITIAL); return WHITE_SPACE; }

({CRLF}|{WHITE_SPACE})+                                    { yybegin(YYINITIAL); return WHITE_SPACE; }

[^]                                                        { return BAD_CHARACTER; }