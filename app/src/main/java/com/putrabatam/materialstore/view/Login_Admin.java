package com.putrabatam.materialstore.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.putrabatam.materialstore.R;
import com.putrabatam.materialstore.controller.Account;
import com.putrabatam.materialstore.utils.HttpsTrustManager;
import com.putrabatam.materialstore.utils.PopUpMessage;
import com.putrabatam.materialstore.utils.RequestHandler;
import com.putrabatam.materialstore.utils.Server_Configuration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login_Admin extends AppCompatActivity {
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    PopUpMessage popUpMessage= new PopUpMessage();

    EditText username, password;
    Button btn_masuk;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_admin);

        progressDialog = new ProgressDialog(Login_Admin.this);
        requestQueue = Volley.newRequestQueue(Login_Admin.this);

        username = findViewById(R.id.et_login_username);
        password = findViewById(R.id.et_login_password);
        btn_masuk = findViewById(R.id.btnMasuk);
        btn_masuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Account data_account = new Account(username.getText().toString(), password.getText().toString());
                String return_validation = data_account.validation_login(data_account);
                if(return_validation.equals("lolos validasi")){
                    login_account(data_account);
                } else{
                    PopUpMessage error = new PopUpMessage();
                    error.validation_error(return_validation, Login_Admin.this);
                }
            }
        });
    }

    public void login_account(Account data_akun) {
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        progressDialog.setCancelable(false);
        HttpsTrustManager.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server_Configuration.address_login_admin,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                        progressDialog.dismiss();
                        try {
                            RequestHandler reqHandler = new RequestHandler();
                            reqHandler.parseDataObject(new JSONObject(ServerResponse));
                            String message = reqHandler.message;
                            if(reqHandler.status){
                                //set_session(data_akun);
                                JSONObject obj = reqHandler.data_object;
                                Account account = new Account(
                                        obj.getString("username"),
                                        obj.getString("password"),
                                        obj.getString("name"));
                                Intent home_admin = new Intent(Login_Admin.this, Home_Admin.class);
                                home_admin.putExtra("username", account.username);
                                home_admin.putExtra("password", account.password);
                                home_admin.putExtra("name", account.name_account);
                                startActivity(home_admin);
                                finish();
                            } else{
                                popUpMessage.login_gagal(message, Login_Admin.this);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                        Toast.makeText(Login_Admin.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                        Log.e("Error Vollee", volleyError.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", data_akun.username);
                params.put("password", data_akun.password);
                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(Login_Admin.this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back = new Intent(Login_Admin.this, MainActivity.class);
        startActivity(back);
        finish();
    }
}