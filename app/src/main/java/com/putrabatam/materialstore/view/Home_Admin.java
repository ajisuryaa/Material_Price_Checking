package com.putrabatam.materialstore.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import com.putrabatam.materialstore.R;
import com.putrabatam.materialstore.controller.Material;

import java.util.ArrayList;

public class Home_Admin extends AppCompatActivity {
    private ArrayList<Material> materialArrayList = new ArrayList<Material>();
    private ArrayList<Material> showListMaterial = new ArrayList<Material>();
    private Card_List_Material adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_admin);
    }

    //Code Program pada Method dibawah ini akan Berjalan saat Option Menu Dibuat
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Memanggil/Memasang menu item pada toolbar dari layout menu_bar.xml
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu_home_admin, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i("search query on text submit", query);
                showListMaterial.clear();
                for(int i = 0; i < materialArrayList.size(); i++){
                    if(materialArrayList.get(i).name.toLowerCase().contains(query.toLowerCase())){
                        Log.i("Index yang mirip", i + " " + materialArrayList.get(i).name);
                        showListMaterial.add(materialArrayList.get(i));
                    }
                }
                adapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                Log.i("search query on text change", query);
                showListMaterial.clear();
                for(int i = 0; i < materialArrayList.size(); i++){
                    if(materialArrayList.get(i).name.toLowerCase().contains(query.toLowerCase())){
                        Log.i("Index yang mirip", i + " " + materialArrayList.get(i).name);
                        showListMaterial.add(materialArrayList.get(i));
                    }
                }
                adapter.notifyDataSetChanged();
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ha_add_new_material:
                Intent add_material = new Intent(Home_Admin.this, Form_Material.class);
                add_material.putExtra("type", "add");
                startActivity(add_material);
                finish();
                return true;
            case R.id.ha_logout:
                Intent view_employee = new Intent(Home_Admin.this, MainActivity.class);
                startActivity(view_employee);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}