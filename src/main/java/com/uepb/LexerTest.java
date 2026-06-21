package com.uepb;

import java.io.IOException;

import com.uepb.lexer.Lexer;
import com.uepb.lexer.Token;
import com.uepb.lexer.TokenType;

public class LexerTest {
    public static void main(String[] args) throws IOException {
        if(args.length == 0)
            throw new IllegalArgumentException(
            "É necessário passar um arquivo de texto no arg[0] para o lexer (ex: java -jar target/lexer.jar teste.uepb"
            );

        try(var lexer = new Lexer(args[0])){
            Token tk = lexer.readNextToken();
            while(tk.type() != TokenType.EOF){
                System.out.print(tk + " ");
                tk = lexer.readNextToken();
            }
        }
    }
}