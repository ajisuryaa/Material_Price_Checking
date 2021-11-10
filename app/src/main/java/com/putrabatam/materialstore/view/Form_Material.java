package com.putrabatam.materialstore.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;

import com.putrabatam.materialstore.R;

import java.io.File;

public class Form_Material extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    public final int REQUEST_IMAGE_CAPTURE = 1;
    public final int REQUEST_IMAGE_GALLERY = 2;

    Button simpan, pilih_foto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_material);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        pilih_foto = findViewById(R.id.btn_choose_photo_fm);
        simpan = findViewById(R.id.btn_save_fm);

        pilih_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(Form_Material.this, v);
                popup.setOnMenuItemClickListener(Form_Material.this);
                popup.inflate(R.menu.menu_choose_image);
                popup.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back = new Intent(Form_Material.this, Home_Admin.class);
        startActivity(back);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.choose_camera:
                Intent choose_camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                choose_camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                startActivityForResult(choose_camera, REQUEST_IMAGE_CAPTURE);
                return true;
            case R.id.choose_folder:
                Intent choose_folder = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(choose_folder, REQUEST_IMAGE_GALLERY);
                return true;
            default:
                return false;
        }
    }
}