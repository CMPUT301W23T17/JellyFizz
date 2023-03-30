package com.example.qr_code_hunter;

public class qrCodeTag {
    String hashString;
    int score;
    int nextScore;

    qrCodeTag(String currentHashString, int current, int next) {
        this.hashString = currentHashString;
        this.score = current;
        this.nextScore = next;
    }

}
