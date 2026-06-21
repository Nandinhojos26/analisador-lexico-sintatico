package com.uepb.lexer;

import java.util.regex.Pattern;

public enum TokenPattern {
    INT("[0-9]+", TokenType.INT),
    ID("[a-zA-Z_][a-zA-Z0-9_]*", TokenType.ID), // Suporte para variaveis A, B, C
    AP("\\(", TokenType.AP),
    FP("\\)", TokenType.FP),
    COMMENT("#[^\\n]*", TokenType.COMMENT), // Ajustado para aceitar cometários vazios
    WS("[ \\t\\r]+", TokenType.WS),
    OP_INC("\\+\\+", TokenType.OP_INC),
    OP_SOMA("\\+", TokenType.OP_SOMA),
    OP_SUB("-", TokenType.OP_SUB),
    OP_MUL("\\*", TokenType.OP_MUL),
    OP_DIV("/", TokenType.OP_DIV),
    OP_MOD("%", TokenType.OP_MOD),
    OP_EXP("\\^", TokenType.OP_EXP)
    ;

    private Pattern pattern;
    private TokenType type;

    TokenPattern(String regex, TokenType type){
        pattern = Pattern.compile(regex);
        this.type = type;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public TokenType getType() {
        return type;
    }
}