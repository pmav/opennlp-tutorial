package com.criticalsoftware;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

import java.io.IOException;
import java.io.InputStream;

public class Main {

    public static void main(String[] args) throws IOException {

        String input = "I made her duck. Pierre Vinken is 61 years old.";

        String[] sentences = sentenceSplitting(input);

        for (String sentence : sentences) {
            System.out.println("Sentence: " + sentence);

            String[] tokens = tokenization(sentence);
            for (String token : tokens) {
                System.out.println("\t" + token);
            }

            String[] tags = partOfSpeechTagging(tokens);
            for (String tag : tags) {
                System.out.println("\t" + tag);
            }

            String[] namedEntities = namedEntityExtraction(tokens);
            for (String namedEntity : namedEntities) {
                System.out.println(namedEntity);
            }

            String[] chunkTags = chunking(tokens, tags);
            for (String chunkTag : chunkTags) {
                System.out.println(chunkTag);
            }

            String parsing = parsing(sentence);
            System.out.println(parsing);
        }
    }

    private static String[] sentenceSplitting(String input) throws IOException {
        InputStream modelIn = Main.class.getResourceAsStream("/models/en-sent.bin");
        SentenceModel model = new SentenceModel(modelIn);

        SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
        String sentences[] = sentenceDetector.sentDetect(input);

        return sentences;
    }

    private static String[] tokenization(String input) throws IOException {
        InputStream in = Main.class.getResourceAsStream("/models/en-token.bin");
        TokenizerModel model = new TokenizerModel(in);

        Tokenizer tokenizer = new TokenizerME(model);
        String tokens[] = tokenizer.tokenize(input);

        return tokens;
    }

    private static String[] partOfSpeechTagging(String[] tokens) throws IOException {
        // https://www.ling.upenn.edu/courses/Fall_2003/ling001/penn_treebank_pos.html

        InputStream modelIn = Main.class.getResourceAsStream("/models/en-pos-maxent.bin");
        POSModel model = new POSModel(modelIn);

        POSTaggerME tagger = new POSTaggerME(model);
        String tags[] = tagger.tag(tokens);

        return tags;
    }

    private static String[] namedEntityExtraction(String[] tokens) throws IOException {
        InputStream modelIn = Main.class.getResourceAsStream("/models/en-ner-person.bin");
        TokenNameFinderModel model = new TokenNameFinderModel(modelIn);

        NameFinderME nameFinder = new NameFinderME(model);
        Span nameSpans[] = nameFinder.find(tokens);

        String[] namedEntities = new String[nameSpans.length];
        for (int s = 0; s < nameSpans.length; s++) {
            Span nameSpan = nameSpans[s];
            int init = nameSpan.getStart();
            int end = nameSpan.getEnd();

            String namedEntity = "";
            for (int i = init; i  < end; i++) {
                namedEntity += tokens[i]+" ";
            }
            namedEntities[s] = namedEntity.trim();
        }

        return namedEntities;
    }

    private static String[] chunking(String[] tokens, String[] posTags) throws IOException {
        // 	B-CHUNK for the first word of the chunk and I-CHUNK
        InputStream modelIn =  Main.class.getResourceAsStream("/models/en-chunker.bin");
        ChunkerModel model = new ChunkerModel(modelIn);

        ChunkerME chunker = new ChunkerME(model);
        String tag[] = chunker.chunk(tokens, posTags);

        return tag;
    }

    private static String parsing(String sentence) throws IOException {
        InputStream modelIn = Main.class.getResourceAsStream("/models/en-parser-chunking.bin");
        ParserModel model = new ParserModel(modelIn);

        Parser parser = ParserFactory.create(model);
        Parse topParses[] = ParserTool.parseLine(sentence, parser, 1);

        StringBuffer sb = new StringBuffer();
        if (topParses.length > 0) {
            topParses[0].show(sb);
        }

        return sb.toString();
    }



}


