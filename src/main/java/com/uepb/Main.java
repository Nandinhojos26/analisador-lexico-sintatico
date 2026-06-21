package com.uepb;

import java.io.IOException;

import com.uepb.parser.Parser;

public class Main {
    public static void main(String[] args) throws IOException {
        try(var parser = new Parser(args[0])){
            parser.parse();
        }
    }
}