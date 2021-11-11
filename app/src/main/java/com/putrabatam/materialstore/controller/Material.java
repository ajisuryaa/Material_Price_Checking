package com.putrabatam.materialstore.controller;

import android.graphics.Bitmap;

public class Material {
    public String id, name, photo_address;
    public Bitmap photo;
    public String satuan;
    public int price;

    public Material(){

    }

    public Material(String id, String name, String photo, int price, String satuan){
        this.id = id;
        this.name = name;
        this.photo_address = photo;
        this.price = price;
        this.satuan = satuan;
    }

    public void set_material(String name, Bitmap photo, int price, String satuan){
        this.name = name;
        this.photo = photo;
        this.price = price;
        this.satuan = satuan;
    }

    public String validation_adding_material(){
        if(id.equals("") || id.length()<=0 || id.isEmpty()){
            return "ID material tidak boleh kosong";
        } else if(name.equals("") || name.length()<=0 || name.isEmpty()){
            return "Nama material tidak boleh kosong";
        } else if(price == 0){
            return "KMIN material tidak boleh kosong";
        }  else {
            return "done";
        }
    }
}
