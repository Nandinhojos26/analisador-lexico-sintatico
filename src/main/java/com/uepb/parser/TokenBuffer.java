package com.uepb.parser;

import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedList;

import com.uepb.lexer.Lexer;
import com.uepb.lexer.Token;
import com.uepb.lexer.TokenType;

public class TokenBuffer implements Closeable {
    
    private Lexer lexer;
    private LinkedList<Token> buffer;
    private int size;

    public TokenBuffer(String fileName, int sizeBuffer) throws IOException{
        lexer = new Lexer(fileName);
        size = sizeBuffer;
        buffer = new LinkedList<>();
        refill();
    }

    private void refill() throws IOException{
        if(buffer.size() > 0 && buffer.getLast().type() == TokenType.EOF)
            return;

        while(buffer.size() < size){
            var tk = lexer.readNextToken();
            buffer.addLast(tk);
        }
    }

    private void consume() throws IOException{
        if(buffer.size() == 0)
            return;

        buffer.removeFirst();
        refill();
    }

    public Token lookAhead(int k){
        if(buffer.size() > 0 && k < 1 && k > buffer.size())
            throw new IllegalAccessError("K está fora dos limites do buffer");

        return buffer.get(k-1);
    }

    public Token match(TokenType expectedType) throws IOException{
        var currentToken = buffer.getFirst();

        if(currentToken.type() == expectedType){
            consume();
            return currentToken;
        }else{
            throw new RuntimeException(String.format(
                "Syntax Error: Expected [%s] but i got [%s]", 
                expectedType.name(),
                currentToken.type().name())
            );
        }
    }

    @Override
    public void close() throws IOException {
        if(lexer != null)
            lexer.close();
    }
}
