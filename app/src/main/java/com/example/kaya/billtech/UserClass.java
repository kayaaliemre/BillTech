package com.example.kaya.billtech;

/**
 * Created by KAYA on 12.12.2017.
 */

public class UserClass {
    String userName;
    String userSurname;
    String userEmail;
    String userMac;
    byte[] userImage;
    String userPhone;
    String userAdress;
    Integer userPremium;
    Integer userNumberAdverts;
    Integer notification;
    Integer vibration;

    public UserClass() {
    }

    public UserClass(String userName, String userSurname, String userEmail, String userMac, byte[] userImage, String userPhone, String userAdress, Integer userPremium, Integer userNumberAdverts, Integer notification,Integer vibration) {
        this.userName = userName;
        this.userSurname = userSurname;
        this.userEmail = userEmail;
        this.userMac = userMac;
        this.userImage = userImage;
        this.userPhone = userPhone;
        this.userAdress = userAdress;
        this.userPremium = userPremium;
        this.userNumberAdverts = userNumberAdverts;
        this.notification = notification;
        this.vibration = vibration;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserSurname(String userSurname) {
        this.userSurname = userSurname;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserMac(String userMac) {
        this.userMac = userMac;
    }

    public void setUserImage(byte[] userImage) {
        this.userImage = userImage;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public void setUserAdress(String userAdress) {
        this.userAdress = userAdress;
    }

    public void setUserPremium(Integer userPremium) {
        this.userPremium = userPremium;
    }

    public void setUserNumberAdverts(Integer userNumberAdverts) {
        this.userNumberAdverts = userNumberAdverts;
    }

    public Integer getNotification() {
        return notification;
    }

    public void setNotification(Integer notification) {
        this.notification = notification;
    }

    public Integer getVibration() {
        return vibration;
    }

    public void setVibration(Integer vibration) {
        this.vibration = vibration;
    }


    public String getUserName() {
        return userName;
    }

    public String getUserSurname() {
        return userSurname;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserMac() {
        return userMac;
    }

    public byte[] getUserImage() {
        return userImage;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public String getUserAdress() {
        return userAdress;
    }

    public Integer getUserPremium() {
        return userPremium;
    }

    public Integer getUserNumberAdverts() {
        return userNumberAdverts;
    }
}
