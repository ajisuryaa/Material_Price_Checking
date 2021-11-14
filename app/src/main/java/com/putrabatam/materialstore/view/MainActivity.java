package com.putrabatam.materialstore.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.putrabatam.materialstore.R;
import com.putrabatam.materialstore.controller.Material;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    AlertDialog.Builder dialog;

    private ArrayList<Material> list_material = new ArrayList<Material>();

    RecyclerView rv_list_material;
    TextView emplty_list_material;
    Button scanner_kode_qr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scanner_kode_qr = findViewById(R.id.scan_material_main);
        rv_list_material = findViewById(R.id.list_view_main_material);

        if(!list_material.isEmpty()){
            rv_list_material.setVisibility(View.VISIBLE);
            emplty_list_material.setVisibility(View.GONE);
        }

        emplty_list_material = findViewById(R.id.empty_material_main);
        scanner_kode_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    //Code Program pada Method dibawah ini akan Berjalan saat Option Menu Dibuat
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Memanggil/Memasang menu item pada toolbar dari layout menu_bar.xml
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mn_login_login:
                Intent add_material = new Intent(MainActivity.this, Login_Admin.class);
                startActivity(add_material);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}