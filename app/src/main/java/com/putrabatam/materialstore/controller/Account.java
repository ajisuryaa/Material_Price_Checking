package com.putrabatam.materialstore.controller;

import android.graphics.Bitmap;

import com.putrabatam.materialstore.utils.ImageHandler;

public class Account {
    public String username, password, name_account;

    public Account(){

    }

    public Account(String username, String password){
        this.username = username;
        this.password = password;
    }

    public Account(String username, String password, String name_account){
        this.username = username;
        this.password = password;
        this.name_account = name_account;
    }

    public String validation_login(Account data){
        if(data.username.equals("") || data.username.isEmpty() && data.password.equals("")
                || password.isEmpty()){
            return "username atau kata sandi tidak boleh kosong!";
        } else if(data.username.length() > 0 && data.password.equals("")
                || password.length() == 0){
            return "Password tidak boleh kosong";
        } else if(data.password.length() > 0 && data.username.equals("")
                || data.username.length() == 0){
            return "Username tidak boleh kosong";
        } else{
            return "lolos validasi";
        }
    }
}
