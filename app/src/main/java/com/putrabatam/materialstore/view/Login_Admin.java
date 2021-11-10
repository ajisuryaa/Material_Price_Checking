package com.putrabatam.materialstore.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.putrabatam.materialstore.R;

public class Login_Admin extends AppCompatActivity {
    Button btn_masuk;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_admin);

        btn_masuk = findViewById(R.id.btnMasuk);
        btn_masuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(Login_Admin.this, Home_Admin.class);
                startActivity(back);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back = new Intent(Login_Admin.this, MainActivity.class);
        startActivity(back);
        finish();
    }
}