package com.uepb.parser;

import java.io.Closeable;
import java.io.IOException;

import com.uepb.lexer.TokenType;

public class Parser implements Closeable {

    private TokenBuffer buffer;

    public Parser(String fileName) throws IOException{
        buffer = new TokenBuffer(fileName, 10);
    }

    // parse -> E EOF
    public void parse() throws IOException{
        E();
        buffer.match(TokenType.EOF);
    }

    //E -> TE'
    private void E() throws IOException{
        T();
        E_();
    }

    //E' -> +TE' | e
    private void E_() throws IOException{
        var tk = buffer.lookAhead(1);

        if(tk.type() == TokenType.OP_SOMA){
            buffer.match(TokenType.OP_SOMA);
            T();
            System.out.print("+ ");
            E_();
        }
    }

    //T -> FT'
    private void T() throws IOException{
        F();
        T_();
    }

    //T' -> *FT' | e
    private void T_() throws IOException{
        var tk = buffer.lookAhead(1);

        if(tk.type() == TokenType.OP_MUL){
            buffer.match(TokenType.OP_MUL);
            F();
            System.out.print("* ");
            T_();
        }
    }

    //F -> ( E ) | INT
    private void F() throws IOException{
        var tk = buffer.lookAhead(1);

        if(tk.type() == TokenType.INT){
            buffer.match(TokenType.INT);
            System.out.print(tk.lexema() + " ");
        }else{
            buffer.match(TokenType.AP);
            E();
            buffer.match(TokenType.FP);
        }
    }

    @Override
    public void close() throws IOException {
        if(buffer != null)
            buffer.close();
    }
}
