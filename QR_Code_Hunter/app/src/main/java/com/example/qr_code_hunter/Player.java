package com.example.qr_code_hunter;

import java.util.ArrayList;

public class Player {
    private ArrayList<QRCode> myQR_Codes = new ArrayList<QRCode>();

    /**
     * Returns QR Code which has the highest score
     * @return a  QR code
     */
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
    /**
     * Returns QR Code which has the lowest score
     * @return a  QR code
     */
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
    /**
     * Returns the sum of QR Code score
     * @return integer
     */
    public Integer sumScoreQR_Code(){
        Integer result = 0;
        for (QRCode item : myQR_Codes) {
            result = result + item.getScore();
        }
        return result;
    };
}
