package com.criticalsoftware;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException {

        // Models: http://opennlp.sourceforge.net/models-1.5/

        InputStream in = Main.class.getResourceAsStream("/models/en-token.bin");
        TokenizerModel model = new TokenizerModel(in);

        Tokenizer tokenizer = new TokenizerME(model);
        String tokens[] = tokenizer.tokenize("An input sample sentence.");

        for (String token : tokens) {
            System.out.println(token);
        }
    }


}


