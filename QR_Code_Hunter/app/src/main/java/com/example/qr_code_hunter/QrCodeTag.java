package com.example.qr_code_hunter;

public class QrCodeTag {
    protected String hashString;
    protected int score;
    protected int nextScore;

    QrCodeTag(String currentHashString, int current, int next) {
        this.hashString = currentHashString;
        this.score = current;
        this.nextScore = next;
    }
}
