package com.putrabatam.materialstore.controller;

import android.graphics.Bitmap;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Material {
    public String id, name, photo_address;
    public Bitmap photo;
    public String satuan;
    public int quantity;
    public int price;

    public Material(){

    }

    public String formatRupiah(int number){
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(number);
    }

    public void set_quantity(int quantity){
        this.quantity = quantity;
    }

    public int get_quantity(){
        return quantity;
    }

    public Material(String id, String name, String photo, int price, String satuan, int quantity){
        this.id = id;
        this.name = name;
        this.photo_address = photo;
        this.price = price;
        this.satuan = satuan;
        this.quantity = quantity;
    }

    public void set_material(String id, String name, Bitmap photo, int price, String satuan){
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.price = price;
        this.satuan = satuan;
    }

    public int get_total_price(ArrayList<Material> data){
        int total_price = 0;
        for(int i = 0; i < data.size(); i++){
            total_price = total_price + (data.get(i).price * data.get(i).quantity);
        }
        return total_price;
    }

    public String validation_adding_material(){
        if(name.equals("") || name.length()<=0 || name.isEmpty()){
            return "Nama material tidak boleh kosong";
        } else if(price == 0){
            return "Harga material tidak boleh kosong";
        } else if(satuan.equals("") || name.length()<=0 || name.isEmpty()){
            return "Satuan material tidak boleh kosong";
        } else if(photo == null){
            return "Foto material tidak boleh kosong";
        } else {
            return "done";
        }
    }

    public List get_list_id(ArrayList<Material> list_material){
        List id_list = new ArrayList();
        for(int i = 0; i < list_material.size(); i++){
            id_list.add(list_material.get(i).id);
        }
        return id_list;
    }
}
