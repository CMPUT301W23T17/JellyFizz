package com.example.qr_code_hunter;

import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentReference;
import com.google.type.LatLng;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Map;

public class QrCode implements Parcelable {
    private ArrayList<DocumentReference> playerList = new ArrayList<>();
    private String codeName = "";
    private String visualRep = "";
    private String hashString;
    private Integer score;
    private Boolean dataPrivacy;
    private Image objectImage;
    private LatLng geolocation;
    ShaGenerator sha = new ShaGenerator();

    private static Map<Character, Integer> alphabetPoints = Map.of(
            'a', 10,
            'b', 11,
            'c', 12,
            'd', 13,
            'e', 14,
            'f', 15
    );

    QrCode(String scannedString) throws NoSuchAlgorithmException {
        setName(sha.shaGeneratorBinary(scannedString));
        setVisualRep(sha.shaGeneratorBinary(scannedString));
        setScore(sha.shaGeneratorHexadecimal(scannedString));
    }

    protected QrCode(Parcel in) {
        codeName = in.readString();
        visualRep = in.readString();
        hashString = in.readString();
        if (in.readByte() == 0) {
            score = null;
        } else {
            score = in.readInt();
        }
        byte tmpDataPrivacy = in.readByte();
        dataPrivacy = tmpDataPrivacy == 0 ? null : tmpDataPrivacy == 1;
    }

    public static final Creator<QrCode> CREATOR = new Creator<QrCode>() {
        @Override
        public QrCode createFromParcel(Parcel in) {
            return new QrCode(in);
        }

        @Override
        public QrCode[] newArray(int size) {
            return new QrCode[size];
        }
    };

    /**
     * This creates a unique name corresponding to the QrCode using 10 bits
     * @param binary
     *      This is the binary representation string of the SHA-256 hash of the code
     */
    public void setName(String binary) {
        char[] bin = binary.toCharArray();
        String[][] namesArray = new String[][]{
                new String[]{"Red", "Jet"},
                new String[]{"Sea", "Bay"},
                new String[]{"Car", "Gas"},
                new String[]{"Art", "Era"},
                new String[]{"Owl", "Bat"},
                new String[]{"Jaw", "Eye"},
                new String[]{"Oak", "Log"},
                new String[]{"Flu", "Ice"},
                new String[]{"Mud", "Oil"},
                new String[]{"Saw", "Axe"}
        };

        for (int i = 0; i < 10; i++) {
            if (bin[i] == '1') {
                codeName = codeName.concat(namesArray[i][1]);
            } else {
                codeName = codeName.concat(namesArray[i][0]);
            }
        }
    }

    /**
     * This determines how many points the QrCode is worth for
     * @param
     *     hashString the SHA-hash-string of the QrCode
     */
    public void setScore(String hashString) {
        this.score = 0;
        char currentChar = hashString.charAt(0);
        int count = 0;

        for (int i = 1; i < hashString.length(); i++) {

            if (hashString.charAt(i) == currentChar) {
                count++;
            } else {
                int iterationScore = 0;

                if (Character.isDigit(currentChar)) {
                    int repeatedInt = currentChar - 48;

                    if (repeatedInt == 0) {
                        iterationScore = (int) Math.pow(20, count);
                    } else {
                        iterationScore = (int) Math.pow(repeatedInt, count);
                    }
                } else {
                    int valueOfChar = alphabetPoints.get(currentChar);
                    iterationScore = (int) Math.pow(valueOfChar, count);
                }
                if (count > 0) this.score += iterationScore;
                currentChar = hashString.charAt(i);
                count = 0;
            }
        }
    }

    /**
     * This creates and sets the visual representation of the QrCode
     * @param
     *     binary binary string that determines how the visual representation will look like
     */
    public void setVisualRep(String binary){
        char[] bin = binary.toCharArray();
        if(bin[0] == '1'){
            visualRep ="  _||||||||||||||_  ";
        }else{
            visualRep ="  _--------------_  ";
        }
        if(bin[1] == '1'){
            visualRep = visualRep.concat("\n { ----      ---- } ");
        }else{
            visualRep = visualRep.concat("\n { ~~~)      (~~~ } ");
        }
        if(bin[2] == '1'){
            visualRep = visualRep.concat("\n{| ( + ) || ( + ) |}");
        }else{
            visualRep = visualRep.concat("\n)|-[ @]--||--[ @]-|(");
        }
        if(bin[3] == '1'){
            visualRep = visualRep.concat("\n{|       ||       |}");
            visualRep = visualRep.concat("\n |      {__}      | ");
        }else{
            visualRep = visualRep.concat("\n)|       ||       |(");
            visualRep = visualRep.concat("\n |      (..)      | ");
        }
        if(bin[4] == '1'){
            visualRep = visualRep.concat("\n |   _~~~~~~~~_   | ");
        }else{
            visualRep = visualRep.concat("\n |_              _| ");
        }
        if(bin[5] == '1'){
            if(bin[6] == '1'){
                visualRep = visualRep.concat("\n  |_  (______)  _| ");
            }else{
                visualRep = visualRep.concat("\n  |_  [||||||]  _| ");
            }
            visualRep = visualRep.concat("\n   |_          _| ");
        }else{
            if(bin[6] == '1'){
                visualRep = visualRep.concat("\n |    (______)    | ");
            }else{
                visualRep = visualRep.concat("\n |    [||||||]    | ");
            }
            visualRep = visualRep.concat("\n |                | ");
        }
        if(bin[7] == '1'){
            visualRep = visualRep.concat("\n -----||||||||----- ");
        }else{
            visualRep = visualRep.concat("\n ------------------ ");
        }
    }

    /**
     * This sets the privacy of the QrCode
     * @param
     *     privacy true if hidden, false if shown
     */
    public void setPrivacy(Boolean privacy) {
        dataPrivacy = privacy;
    }

    /**
     * This sets the geolocation of the QrCode
     * @param
     *     location the latitude-longitude pair of the QrCode
     */
    public void setLocation(LatLng location) {
        geolocation = location;
    }

    /**
     * This adds a player to the list of players who have scanned the QrCode
     * @param
     *     username the string of the player username to be added
     */
    public void addPlayerScan(String username) {
        // method not done
    }

    /**
     * This removes a player from the list of players who have scanned the QrCode
     * @param
     *     username the string of the player username to be deleted
     */
    public void removePlayerScan(String username) {
        // method not done
    }

    /**
     * This returns the unique name of a QrCode
     * @return
     *      Returns a string-type
     */
    public String getName() {
        return codeName;
    }

    /**
     * This returns the score of a QrCode
     * @return
     *      Returns an integer-type
     */
    public Integer getScore() {
        return score;
    }

    /**
     * This returns the visual representation of a QrCode
     * @return
     *      Returns a string-type
     */
    public String getVisualRep() {
        return visualRep;
    }

    /**
     * This returns the geolocation of a QrCode
     * @return
     *      Returns a LatLng (Latitude/Longitude pair) type
     */
    public LatLng getGeolocation() {
        return geolocation;
    }

    /**
     * This returns the list of players that have scanned the QrCode
     * @return
     *      Returns an array list of type Document Reference
     */
    public ArrayList<DocumentReference> getPlayerList() {
        return playerList;
    }

    /**
     * This returns the privacy set on the QrCode
     * @return
     *      Returns a Boolean (true if hidden, false if shown)
     */
    public Boolean getPrivacy() {
        return dataPrivacy;
    }

    /**
     * This returns the hash string of the scanned QrCode
     * @return
     *      Returns a string-type
     */
    public String getHashString() {
        return hashString;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(codeName);
        dest.writeString(visualRep);
        dest.writeString(hashString);
        if (score == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(score);
        }
        dest.writeByte((byte) (dataPrivacy == null ? 0 : dataPrivacy ? 1 : 2));
    }
}
