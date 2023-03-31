package com.example.qr_code_hunter;

public class QrCodeTag {
    String hashString;
    int score;
    int nextScore;

    QrCodeTag(String currentHashString, int current, int next) {
        this.hashString = currentHashString;
        this.score = current;
        this.nextScore = next;
    }
}