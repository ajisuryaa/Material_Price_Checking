package com.putrabatam.materialstore.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

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
import com.putrabatam.materialstore.utils.Server_Configuration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Home_Admin extends AppCompatActivity {
    RequestQueue requestQueue;
    ProgressDialog progressDialog;

    private ArrayList<Material> materialArrayList = new ArrayList<Material>();
    private ArrayList<Material> showListMaterial = new ArrayList<Material>();
    private Card_List_Material adapter;

    LinearLayout body1, body2;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_admin);
        progressDialog = new ProgressDialog(Home_Admin.this);
        requestQueue = Volley.newRequestQueue(Home_Admin.this);
        body1 = findViewById(R.id.ll_content_empty_ha);
        body2 = findViewById(R.id.ll_content_body_ha);
        recyclerView = findViewById(R.id.rv_list_material_ha);
        adapter = new Card_List_Material(showListMaterial);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Home_Admin.this);
        recyclerView.setLayoutManager(layoutManager);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onChanged() {
                Log.i("ON changed", "true");
                if(showListMaterial.size() <=0 ){
                    body1.setVisibility(View.VISIBLE);
                    body2.setVisibility(View.GONE);
                } else {
                    body1.setVisibility(View.GONE);
                    body2.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                Log.i("ON item range inserted", "true");
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                Log.i("ON item range removed", "true");
                showListMaterial = adapter.dataList;
                if(showListMaterial.size() <=0 ){
                    body1.setVisibility(View.VISIBLE);
                    body2.setVisibility(View.GONE);
                } else {
                    body1.setVisibility(View.GONE);
                    body2.setVisibility(View.VISIBLE);
                }
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setActivated(true);
        Get_List_Material();
    }

    void Get_List_Material() {
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        progressDialog.setCancelable(false);
        materialArrayList.clear();
        showListMaterial.clear();
        HttpsTrustManager.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server_Configuration.address_get_list_material,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                        try {
                            JSONObject obj = new JSONObject(ServerResponse);
                            boolean status = obj.getBoolean("status");
                            String message = obj.getString("message");
                            Log.i("Message List Material ", message);
                            progressDialog.dismiss();
                            if(status){
                                if(message.equals("Data material masih kosong!")){
                                    body1.setVisibility(View.VISIBLE);
                                    body2.setVisibility(View.GONE);
                                } else{
                                    body1.setVisibility(View.GONE);
                                    body2.setVisibility(View.VISIBLE);
                                    JSONArray response = new JSONArray(obj.getString("data"));
                                    for (int i = 0; i < response.length(); i++) {
                                        try {
                                            JSONObject data_material = response.getJSONObject(i);
                                            Material material = new Material(
                                                    data_material.getString("id_material"),
                                                    data_material.getString("nama_material"),
                                                    data_material.getString("photo"),
                                                    data_material.getInt("harga_material"),
                                                    data_material.getString("satuan_material"),
                                                    0
                                            );
                                            materialArrayList.add(material);
                                            showListMaterial.add(material);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }else{
                                body1.setVisibility(View.VISIBLE);
                                body2.setVisibility(View.GONE);
                                Toast.makeText(Home_Admin.this, message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }
                        adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                        Toast.makeText(Home_Admin.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                        Log.e("Error Vollee", volleyError.toString());
                    }
                }) {
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(Home_Admin.this);
        requestQueue.add(stringRequest);
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