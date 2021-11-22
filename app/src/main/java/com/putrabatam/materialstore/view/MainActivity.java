package com.putrabatam.materialstore.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.putrabatam.materialstore.R;
import com.putrabatam.materialstore.controller.Material;
import com.putrabatam.materialstore.utils.HttpsTrustManager;
import com.putrabatam.materialstore.utils.RequestHandler;
import com.putrabatam.materialstore.utils.Server_Configuration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import info.vividcode.android.zxing.CaptureActivity;
import info.vividcode.android.zxing.CaptureActivityIntents;

public class MainActivity extends AppCompatActivity {
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    AlertDialog.Builder dialog;

    private ArrayList<Material> list_material = new ArrayList<Material>();

    RecyclerView rv_list_material;
    TextView emplty_list_material, total_price;
    Button scanner_kode_qr;

    private Card_List_Check_Material adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(MainActivity.this);
        requestQueue = Volley.newRequestQueue(MainActivity.this);
        scanner_kode_qr = findViewById(R.id.scan_material_main);
        rv_list_material = findViewById(R.id.list_view_main_material);
        total_price = findViewById(R.id.total_price_main);
        emplty_list_material = findViewById(R.id.empty_material_main);

        adapter = new Card_List_Check_Material(list_material);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        rv_list_material.setLayoutManager(layoutManager);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onChanged() {
                Log.i("ON item range inserted", "true");
                Material material = new Material();
                total_price.setText(String.valueOf(material.formatRupiah(material.get_total_price(adapter.dataList))));
                list_material = adapter.dataList;
                if(adapter.getItemCount() > 0){
                    rv_list_material.setVisibility(View.VISIBLE);
                    emplty_list_material.setVisibility(View.GONE);
                } else{
                    rv_list_material.setVisibility(View.GONE);
                    emplty_list_material.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                Log.i("ON item range inserted", "true");
                Material material = new Material();
                total_price.setText(String.valueOf(material.formatRupiah(material.get_total_price(adapter.dataList))));
                list_material = adapter.dataList;
                if(adapter.getItemCount() > 0){
                    rv_list_material.setVisibility(View.VISIBLE);
                    emplty_list_material.setVisibility(View.GONE);
                } else{
                    rv_list_material.setVisibility(View.GONE);
                    emplty_list_material.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                Log.i("ON item range removed", "true");
                Material material = new Material();
                total_price.setText(String.valueOf(material.formatRupiah(material.get_total_price(adapter.dataList))));
                list_material = adapter.dataList;
                if(adapter.getItemCount() > 0){
                    rv_list_material.setVisibility(View.VISIBLE);
                    emplty_list_material.setVisibility(View.GONE);
                } else{
                    rv_list_material.setVisibility(View.GONE);
                    emplty_list_material.setVisibility(View.VISIBLE);
                }
            }
        });
        rv_list_material.setAdapter(adapter);
        rv_list_material.setActivated(true);
        
        if(adapter.getItemCount() > 0){
            rv_list_material.setVisibility(View.VISIBLE);
            emplty_list_material.setVisibility(View.GONE);
        } else{
            rv_list_material.setVisibility(View.GONE);
            emplty_list_material.setVisibility(View.VISIBLE);
        }
        
        scanner_kode_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Scan Button", "CLICKED");
                // Membuat intent baru untuk memanggil CaptureActivity bawaan ZXing
                Intent captureIntent = new Intent(MainActivity.this, CaptureActivity.class);

                // Kemudian kita mengeset pesan yang akan ditampilkan ke user saat menjalankan QRCode scanning
                CaptureActivityIntents.setPromptMessage(captureIntent, "Barcode scanning...");

                // Melakukan startActivityForResult, untuk menangkap balikan hasil dari QR Code scanning
                startActivityForResult(captureIntent, 0);
            }
        });
    }

    public void Get_Data_Material(String id_material) {
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        progressDialog.setCancelable(false);
        HttpsTrustManager.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server_Configuration.address_scan_material,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                        progressDialog.dismiss();
                        try {
                            RequestHandler reqHandler = new RequestHandler();
                            reqHandler.parseDataObject(new JSONObject(ServerResponse));
                            String message = reqHandler.message;
                            Log.i("Result Material", message);

                            Material data_material = new Material(
                                    reqHandler.data_object.getString("id"),
                                    reqHandler.data_object.getString("name"),
                                    reqHandler.data_object.getString("photo"),
                                    Integer.valueOf(reqHandler.data_object.getString("harga")),
                                    reqHandler.data_object.getString("satuan"),
                                    1
                            );
                            int current_size = list_material.size();
                            if(data_material.get_list_id(list_material).contains(data_material.id)){
                                Toast.makeText(MainActivity.this, "Material already exist", Toast.LENGTH_LONG).show();
                            } else{
                                list_material.add(data_material);
                                rv_list_material.scrollToPosition(list_material.size()-1);
                                //total_price.setText(String.valueOf(data_material.get_total_price(list_material)));
                                if(!list_material.isEmpty()){
                                    rv_list_material.setVisibility(View.VISIBLE);
                                    emplty_list_material.setVisibility(View.GONE);
                                }
                                Log.i("Count Data", String.valueOf(list_material.size()));
                                adapter.notifyItemInserted(list_material.size());
                                adapter.notifyItemRangeChanged(current_size, list_material.size());
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
                        Toast.makeText(MainActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                        Log.e("Error Vollee", volleyError.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_material", id_material);
                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                String value = data.getStringExtra("SCAN_RESULT");
                Log.i("Material ID", value);
                Get_Data_Material(value);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("SCAN", "Canceled scan QR");
            }
        } else {

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}