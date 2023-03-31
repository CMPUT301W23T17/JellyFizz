package com.example.qr_code_hunter;

public class QrCodeTag {
    String hashString;
    int score;
    int nextScore;

    /**
     Represents a tag for a QR code item in the list, which includes the hash string, current score, and next score. This tag will be used
     bvy several other methods later
     */
    QrCodeTag(String currentHashString, int current, int next) {
        this.hashString = currentHashString;
        this.score = current;
        this.nextScore = next;
    }
}