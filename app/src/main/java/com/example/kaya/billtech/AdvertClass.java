package com.example.kaya.billtech;

import java.util.Date;

/**
 * Created by cagdas on 16.11.2017.
 */

public class AdvertClass {
    int advertId;
    String advertName;
    String advertText;
    int billboardId;
    Date beginDate;
    Date endDate;
    byte[] advertImage;
    int userıd;
    String advertTheme;


    public String getAdvertTheme() {
        return advertTheme;
    }

    public void setAdvertTheme(String advertTheme) {
        this.advertTheme = advertTheme;
    }

    public int getAdvertId() {
        return advertId;
    }

    public void setAdvertId(int advertId) {
        this.advertId = advertId;
    }

    public String getAdvertName() {
        return advertName;
    }

    public void setAdvertName(String advertName) {
        this.advertName = advertName;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public byte[] getAdvertImage() {
        return advertImage;
    }

    public void setAdvertImage(byte[] advertImage) {
        this.advertImage = advertImage;
    }

    public int getUserıd() {
        return userıd;
    }

    public void setUserıd(int userıd) {
        this.userıd = userıd;
    }

    public String getAdvertText() {
        return advertText;
    }

    public void setAdvertText(String advertText) {
        this.advertText = advertText;
    }

    public int getBillboardId() {
        return billboardId;
    }

    public void setBillboardId(int billboardId) {
        this.billboardId = billboardId;
    }
}
