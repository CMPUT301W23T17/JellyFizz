package com.example.qr_code_hunter;

public class PlayerProfile {
    protected String phoneNumber;
    protected String email;
    protected Boolean privacy;

    public PlayerProfile(String phone, String email, Boolean privacy) {
        this.phoneNumber = phone;
        this.email = email;
        this.privacy = privacy;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getPrivacy() {
        return privacy;
    }
}
