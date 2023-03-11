package com.example.qr_code_hunter;

public class PlayerProfile {
    private String phoneNumber;
    private String email;
    private Boolean privacy;

    public PlayerProfile(String phone, String email, Boolean privacy) {
        this.phoneNumber = phone;
        this.email = email;
        this.privacy = privacy;
    }

    /**
     * This returns the phone number of a player
     * @return
     *      Returns a phone number string
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * This returns the email address of a player
     * @return
     *      Returns an email string
     */
    public String getEmail() {
        return email;
    }

    /**
     * This returns the visibility of a player's information
     * @return
     *      Returns true if details are hidden, otherwise false
     */
    public Boolean getPrivacy() {
        return privacy;
    }
}
