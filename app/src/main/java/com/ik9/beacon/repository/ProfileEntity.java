package com.ik9.beacon.repository;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.nio.ByteBuffer;

public class ProfileEntity {

    //region Birth Field
    private String birth = "";

    public String getBirth() {
        return birth;
    }

    public void setBirth(String customerBirth) {
        String day = "";
        String month = "";
        String year = "";

        customerBirth = customerBirth == "" ? "1900/01/01" : customerBirth;

        if (customerBirth.substring(2, 3).equals("/")) {
            day = customerBirth.substring(0, 2);
            month = customerBirth.substring(3, 5);
            year = customerBirth.substring(6);
        } else if (customerBirth.substring(4, 5).equals("/")) {
            year = customerBirth.substring(0, 4);
            month = customerBirth.substring(5, 7);
            day = customerBirth.substring(8);
        }

        birth = year + "/" + month + "/" + day;
    }
    //endregion

    //region City Field
    private String city = "";

    public String getCity() {
        return city;
    }

    public void setCity(String customerCity) {
        city = customerCity == null ? "" : customerCity;
    }
    //endregion

    //region Device_ID
    private String device_id;

    public String getDevice_id(){
        return device_id;
    }

    public void setDevice_id(String customerDeviceID){
        device_id = customerDeviceID == null ? "" : customerDeviceID;
    }
    //endregion

    //region Email Field
    private String email = "";

    public String getEmail() {
        return email;
    }

    public void setEmail(String customerEmail) {
        email = customerEmail == null ? "" : customerEmail;
    }
    //endregion

    //region Gender Field
    private String gender = "";

    public String getGender() {
        return gender;
    }

    public void setGender(String customerGender) {
        gender = customerGender == null ? "" : customerGender;
    }
    //endregion

    //region ID Field
    private String id;

    public String getID() {
        return id;
    }

    public void setID(int customerID) {
        id = customerID < 0 ? "0" : String.valueOf(customerID);
    }
    //endregion

    //region Name Field
    private String name = "";

    public String getName() {
        return name;
    }

    public void setName(String customerName) {
        name = customerName == null ? "" : customerName;
    }
    //endregion

    //region Phone
    private String phone;

    public String getPhone(){
        return phone;
    }

    public void setPhone(String customerPhone){
        phone = customerPhone == null ? "" : customerPhone;
    }
    //endregion

    //region Photo Field
    private String photo;

    public String getUriPhoto() {
        return photo;
    }

    public void setUriPhoto(String customerPhoto) {
        photo = customerPhoto == null ? "" : customerPhoto;
    }
    //endregion

    //region State Field
    private String state = "";

    public String getState() {
        return state;
    }

    public void setState(String customerState) {
        state = customerState == null ? "" : customerState;
    }
    //endregion

}
