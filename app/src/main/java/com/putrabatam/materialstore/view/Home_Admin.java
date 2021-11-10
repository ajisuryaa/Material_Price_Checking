package com.putrabatam.materialstore.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.putrabatam.materialstore.R;

public class Home_Admin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_admin);
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