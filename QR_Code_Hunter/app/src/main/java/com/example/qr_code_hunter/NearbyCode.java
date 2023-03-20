package com.example.qr_code_hunter;

import android.os.Parcel;
import android.os.Parcelable;

public class NearbyCode implements Parcelable {
    private String codeName;
    private String distance;
    private String point;
    private String location;


    public NearbyCode(String codeName, String distance, String point, String location) {
        this.codeName = codeName;
        this.distance = distance;
        this.point = point;
        this.location = location;
    }

    public String getDistance() {
        return distance;
    }

    public String getPoint() {
        return point;
    }

    public String getLocation() {
        return location;
    }

    public String getCodeName() {
        return codeName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(codeName);
        out.writeString(location);
        out.writeString(point);
        out.writeString(distance);
    }

    public static final Creator<NearbyCode> CREATOR = new Creator<NearbyCode>() {
        @Override
        public NearbyCode createFromParcel(Parcel in) {
            return new NearbyCode(in);
        }

        @Override
        public NearbyCode[] newArray(int size) {
            return new NearbyCode[size];
        }
    };

    private NearbyCode(Parcel in) {
        codeName = in.readString();
        location = in.readString();
        point = in.readString();
        distance = in.readString();
    }
}
