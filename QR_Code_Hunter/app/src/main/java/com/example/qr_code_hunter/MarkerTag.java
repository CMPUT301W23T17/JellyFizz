package com.example.qr_code_hunter;

import java.util.ArrayList;

public class MarkerTag {
    private ArrayList<NearbyCode> nearbyCodes;

    public MarkerTag(){
        this.nearbyCodes = new ArrayList<NearbyCode>();
    }

    public MarkerTag(ArrayList<NearbyCode> nearbyCodes) {
        this.nearbyCodes = nearbyCodes;
    }

    public ArrayList<NearbyCode> getNearbyCodes() {
        return nearbyCodes;
    }

    public void addTag(NearbyCode newCode) {
        this.nearbyCodes.add(newCode);
    }
}

