package com.example.qr_code_hunter;

import java.util.ArrayList;

public class Player {
    private ArrayList<QRCode> myQR_Codes = new ArrayList<QRCode>();

    public QRCode highestScoreQR_Code(){
        QRCode result = null;
        for (QRCode item : myQR_Codes) {
            if ( result == null){
                result = item;
            }
            else if ( item.getScore() > result.getScore()){
                result = item;
            }
        }
        return result;
    }
    public QRCode lowestScoreQR_Code(){
        QRCode result = null;
        for (QRCode item : myQR_Codes) {
            if ( result == null){
                result = item;
            }
            else if ( item.getScore() < result.getScore()){
                result = item;
            }
        }
        return result;
    }
    public Integer sumScoreQR_Code(){
        Integer result = 0;
        for (QRCode item : myQR_Codes) {
            result = result + item.getScore();
        }
        return result;
    };
}
