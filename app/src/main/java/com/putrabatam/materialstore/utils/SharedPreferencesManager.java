package com.putrabatam.materialstore.utils;

import android.content.SharedPreferences;
import android.util.Log;
import com.putrabatam.materialstore.controller.Account;

public class SharedPreferencesManager {
    public static final String my_shared_preferences = "my_shared_preferences";

    //Fungsi untuk membuka otomatis halaman home page jika user belum logout
    public Account mode_login(SharedPreferences setting){
//        if(setting.getBoolean("session_status", false)==true){
//            Account data_akun = new Account();
//            data_akun.setUsername(setting.getString("username", ""));
//            data_akun.setUsername(setting.getString("name", ""));
//            data_akun.setUsername(setting.getString("password", ""));
//            data_akun.setUsername(setting.getString("photo", ""));
//            data_akun.setUsername(setting.getString("employee_position", ""));
//            return data_akun;
//        }
//        else{
//            Log.e("Login Stat: ", "false");
//            return null;
//        }
        Log.i("Session Status: ", setting.getString("username", "KOSONG"));
        return null;
    }

    public void logout_session(SharedPreferences sharedPreferences){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("session_status", false);
        editor.putString("username", "");
        editor.putString("name", "");
        editor.putString("password", "");
        editor.putString("photo", "");
        editor.putString("employee_position", "");
        editor.commit();
    }
}
