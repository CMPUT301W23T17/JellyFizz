package com.example.qr_code_hunter;

import java.util.ArrayList;

public class Player{
    protected ArrayList<String> myQrCodes = new ArrayList<String>(); // list of SHA-256
    protected String uniqueUsername;
    protected PlayerProfile profileInfo;
    protected int totalCodeScanned;
    protected int totalScore;
    protected int rank;

    public Player(String phone, String email, String username, Boolean privacy, ArrayList<String> codeScanned, int score, int rank) {
        this.profileInfo = new PlayerProfile(phone, email, privacy);
        this.uniqueUsername = username;
        this.myQrCodes = codeScanned;
        this.totalScore = score;
        this.rank = rank;
    }

    public String lowestScoreQrCode() {
        return "method not done";
    }

    public String highestScoreQrCode() {
        return "method not done";
    }

    public void calculateScore() {
        // method not done
    }

    public int getTotalCodeScanned() {
        return totalCodeScanned;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public int getRank() {
        return rank;
    }

    public String getUsername() {
        return uniqueUsername;
    }

    public PlayerProfile getProfileInfo() {
        return profileInfo;
    }


    // old methods
    // /**
//     * Returns QR Code which has the highest score
//     * @return a  QR code
//     */
//    public QRCode highestScoreQrCode(){
//        QRCode result = null;
//        for (QRCode item : myQrCodes) {
//            if ( result == null){
//                result = item;
//            }
//            else if ( item.getScore() > result.getScore()){
//                result = item;
//            }
//        }
//        return result;
//    }
//    /**
//     * Returns QR Code which has the lowest score
//     * @return a  QR code
//     */
//    public QRCode lowestScoreQR_Code(){
//        QRCode result = null;
//        for (QRCode item : myQrCodes) {
//            if ( result == null){
//                result = item;
//            }
//            else if ( item.getScore() < result.getScore()){
//                result = item;
//            }
//        }
//        return result;
//    }
//    /**
//     * Returns the sum of QR Code score
//     * @return integer
//     */
//    public Integer sumScoreQR_Code(){
//        Integer result = 0;
//        for (QRCode item : myQrCodes) {
//            result = result + item.getScore();
//        }
//        return result;
//    };
}
