package com.uepb.parser;

import java.io.Closeable;
import java.io.IOException;

import com.uepb.lexer.Token;
import com.uepb.lexer.TokenType;

public class Parser implements Closeable {

    private TokenBuffer buffer;

    public Parser(String fileName) throws IOException {
        buffer = new TokenBuffer(fileName, 10);
    }

    // parse -> E EOF
    public void parse() throws IOException {
        String resultado = E();
        buffer.match(TokenType.EOF);
        // Imprime o resultado final traduzido em notação prefixa
        System.out.println(resultado); 
    }

    // E -> T E'
    private String E() throws IOException {
        String valT = T();
        return E_(valT); // Passa o resultado de T como atributo herdado
    }

    // E' -> + T E' | - T E' | e
    private String E_(String leftSide) throws IOException {
        var tk = buffer.lookAhead(1);

        if (tk.type() == TokenType.OP_SOMA) {
            buffer.match(TokenType.OP_SOMA);
            String valT = T();
            // Monta no formato prefixo: Operador + Esquerda + Direita
            String prefixo = "+ " + leftSide + " " + valT;
            return E_(prefixo);
        } else if (tk.type() == TokenType.OP_SUB) {
            buffer.match(TokenType.OP_SUB);
            String valT = T();
            // Monta no formato prefixo: Operador - Esquerda + Direita
            String prefixo = "- " + leftSide + " " + valT;
            return E_(prefixo);
        }
        
        // Caso seja Epsilon (e), apenas retorna o acumulado do lado esquerdo
        return leftSide;
    }

    // T -> P T'
    private String T() throws IOException {
        String valP = P();
        return T_(valP); // Passa o resultado de P como atributo herdado
    }

    // T' -> * P T' | / P T' | % P T' | e
    private String T_(String leftSide) throws IOException {
        var tk = buffer.lookAhead(1);

        if (tk.type() == TokenType.OP_MUL) {
            buffer.match(TokenType.OP_MUL);
            String valP = P();
            String prefixo = "* " + leftSide + " " + valP;
            return T_(prefixo);
        } else if (tk.type() == TokenType.OP_DIV) {
            buffer.match(TokenType.OP_DIV);
            String valP = P();
            String prefixo = "/ " + leftSide + " " + valP;
            return T_(prefixo);
        } else if (tk.type() == TokenType.OP_MOD) {
            buffer.match(TokenType.OP_MOD);
            String valP = P();
            String prefixo = "% " + leftSide + " " + valP;
            return T_(prefixo);
        }
        
        return leftSide;
    }

    // P -> F P' (Regra criada para tratar a Exponenciação)
    // A exponenciação é associativa à direita. Avaliando de forma recursiva
    // diretamente no método, evitamos herança complexa nesta subregra.
    private String P() throws IOException {
        String valF = F();
        var tk = buffer.lookAhead(1);

        if (tk.type() == TokenType.OP_EXP) {
            buffer.match(TokenType.OP_EXP);
            String valP = P(); // Chamada recursiva à direita garante a associatividade correta
            return "^ " + valF + " " + valP;
        }
        
        return valF;
    }

    // F -> + F | - F | INT | ID | ( E )
    private String F() throws IOException {
        var tk = buffer.lookAhead(1);

        // Tratamento de operadores unários (+ e -)
        if (tk.type() == TokenType.OP_SOMA) {
            buffer.match(TokenType.OP_SOMA);
            String valF = F();
            return "+ " + valF;
        } else if (tk.type() == TokenType.OP_SUB) {
            buffer.match(TokenType.OP_SUB);
            String valF = F();
            return "- " + valF;
        } 
        // Tratamento de operandos e escopo
        else if (tk.type() == TokenType.INT) {
            Token t = buffer.match(TokenType.INT);
            return t.lexema();
        } else if (tk.type() == TokenType.ID) {
            Token t = buffer.match(TokenType.ID);
            return t.lexema();
        } else if (tk.type() == TokenType.AP) {
            buffer.match(TokenType.AP);
            String valE = E();
            buffer.match(TokenType.FP);
            return valE;
        } else {
            throw new RuntimeException(String.format(
                "Syntax Error: Esperava número, variável ou '(' mas encontrou [%s] perto do lexema '%s'", 
                tk.type().name(), tk.lexema()
            ));
        }
    }

    @Override
    public void close() throws IOException {
        if (buffer != null)
            buffer.close();
    }
}