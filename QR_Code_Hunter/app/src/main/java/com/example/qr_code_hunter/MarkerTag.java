package com.example.qr_code_hunter;

import java.util.ArrayList;

/**
 * This class is used to store all QrCodes within a same location
 * The object will be set as tag for each marker displayed on google maps
 */
public class MarkerTag {
    private ArrayList<NearbyCode> nearbyCodes;

    public MarkerTag(){this.nearbyCodes = new ArrayList<>();}

    public ArrayList<NearbyCode> getNearbyCodes() {
        return nearbyCodes;
    }

    public void addTag(NearbyCode newCode) {
        this.nearbyCodes.add(newCode);
    }
}


