package io;

import lexer.Lexer;
import models.treemodels.leaf.Token;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ReadFile {

    private InputStream inputStream;
    private boolean finish;
    private static final ReadFile instance = new ReadFile();
    public static ReadFile getInstance() {
        return instance;
    }

    private ReadFile() {
        finish = false;
        try {
            inputStream = new FileInputStream("testfile.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int readChar() throws IOException {
        int n;
        if (finish) {
            return -1;
        }
        n = inputStream.read();
        if (n == -1) {
            finish = true;
            inputStream.close();
        }
        return n;
    }

    public int readNotNull() throws IOException {
        int n = readChar();
        if (finish) {
            return -1;
        }
        while (n == ' '|| n == '\r') {
            n = readChar();
        }
        return n;
    }

    public Token readIdent(int begin) throws IOException {
        StringBuilder res = new StringBuilder();
        res.append((char) begin);
        int n = readChar();
        while ((n >= 'a' && n <= 'z') || (n >= 'A' && n <= 'Z') ||
                (n >= '0' && n <= '9') || (n == '_')) {
            res.append((char) n);
            n = readChar();
        }
        return new Token(null, res.toString(), n);
    }

    public Token readDigit(int begin) throws IOException {
        StringBuilder res = new StringBuilder();
        res.append((char) begin);
        int n = readChar();
        while (n >= '0' && n <= '9') {
            res.append((char) n);
            n = readChar();
        }
        return new Token(null, res.toString(), n);
    }

    public Token readString(int begin) throws IOException {
        StringBuilder res = new StringBuilder();
        res.append((char) begin);
        int n = readChar();
        while (n != -1 && n != '"') {
            res.append((char) n);
            n = readChar();
        }
        if (n == '"') {
            res.append((char) n);
            n = readChar();
        }
        return new Token(null, res.toString(), n);
    }

    public void readNote(int n) throws IOException {
        if (n == '/') {
            int end = readChar();
            while (end != -1 && end != '\n') {
                end = readChar();
            }
            Lexer.line++;
        }
        else if (n == '*') {
            int end1 = readChar();
            int end2 = readChar();
            if (end1 == '\n') {
                Lexer.line++;
                //the \n in note
            }
            while (!((end1 == -1 || end2 == -1) || (end1 == '*' && end2 == '/'))) {
                end1 = end2;
                end2 = readChar();
            }
        }
    }

}
