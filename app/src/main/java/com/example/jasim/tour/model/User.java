package com.example.jasim.tour.model;

import java.io.Serializable;


public class User implements Serializable{

    long userID;
    String userName;
    String password;
    String recoveryInfo;
    String userImage;
    String mobileNo;

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String  getUserImage() {
        return userImage;
    }

    public void setUserImage(String  userImage) {
        this.userImage = userImage;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRecoveryInfo() {
        return recoveryInfo;
    }

    public void setRecoveryInfo(String recoveryInfo) {
        this.recoveryInfo = recoveryInfo;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public User() {
    }

    public User(String userName, String password, String recoveryInfo, String userImage, String mobileNo) {
        this.userName = userName;
        this.password = password;
        this.recoveryInfo = recoveryInfo;
        this.userImage = userImage;
        this.mobileNo=mobileNo;
    }

    public User(int userID, String userName, String password, String  userImage) {
        this.userID = userID;
        this.userName = userName;
        this.password = password;
        this.userImage = userImage;
    }
}
