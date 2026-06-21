package com.uepb.lexer;

import java.io.Closeable;
import java.io.IOException;

public class Lexer implements Closeable {
    private Buffer buffer;

    public Lexer(String fileName) throws IOException {
        this.buffer = new Buffer(fileName);
    }

    public Token readNextToken() throws IOException {
        while (!buffer.isEOF()) {
            if (buffer.isEOL()) {
                buffer.readNextLine();
            } else {
                boolean matched = false;

                for (var tp : TokenPattern.values()) {
                    var lexema = buffer.tryMatch(tp.getPattern());
                    
                    if (lexema != null) {
                        buffer.consume(lexema);
                        matched = true;

                        // Ignora espaço em branco e comentarios 
                        if (tp.getType() == TokenType.WS || tp.getType() == TokenType.COMMENT) {
                            break; // Quebra o for para continuar pro proximo token
                        }

                        // Se for um token relevante para a sintaxe retorna normalmente
                        return new Token(lexema, tp.getType());
                    }
                }

                
                if (!matched) {
                    throw new RuntimeException(String.format(
                        "Erro Léxico: Caractere não reconhecido na linha %d, coluna %d perto de: '%s'",
                        buffer.getRow(), buffer.getCol(), buffer.getCurrentChar()
                    ));
                }
            }
        }
        return new Token("", TokenType.EOF);
    }

    @Override
    public void close() throws IOException {
        if (buffer != null) {
            buffer.close();
        }
    }
}