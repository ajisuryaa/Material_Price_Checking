package com.putrabatam.materialstore.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.putrabatam.materialstore.R;
import com.putrabatam.materialstore.controller.DataPart;
import com.putrabatam.materialstore.controller.Material;
import com.putrabatam.materialstore.utils.HttpsTrustManager;
import com.putrabatam.materialstore.utils.ImageHandler;
import com.putrabatam.materialstore.utils.PopUpMessage;
import com.putrabatam.materialstore.utils.Server_Configuration;
import com.putrabatam.materialstore.utils.VolleyMultipartRequest;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Form_Material extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    PopUpMessage popUpMessage = new PopUpMessage();

    public final int REQUEST_IMAGE_CAPTURE = 1;
    public final int REQUEST_IMAGE_GALLERY = 2;

    private String string_image="";
    private String id_material = "";
    EditText name, satuan, harga;
    Button simpan, pilih_foto;
    ImageView material_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_material);
        Intent form_page = getIntent();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        progressDialog = new ProgressDialog(Form_Material.this);
        requestQueue = Volley.newRequestQueue(Form_Material.this);
        material_photo = findViewById(R.id.iv_material_photo_fm);
        name = findViewById(R.id.et_name_fm);
        satuan = findViewById(R.id.et_satuan_fm);
        harga = findViewById(R.id.et_harga_fm);
        pilih_foto = findViewById(R.id.btn_choose_photo_fm);
        simpan = findViewById(R.id.btn_save_fm);

        if(!form_page.getStringExtra("type").equals("add")){
            set_form_page(form_page);
        }

        pilih_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(Form_Material.this, v);
                popup.setOnMenuItemClickListener(Form_Material.this);
                popup.inflate(R.menu.menu_choose_image);
                popup.show();
            }
        });

        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapDrawable drawable = (BitmapDrawable) material_photo.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                Material data_material = new Material();
                data_material.set_material(
                        name.getText().toString(),
                        bitmap,
                        Integer.valueOf(harga.getText().toString()),
                        satuan.getText().toString()
                );

                String return_validation = data_material.validation_adding_material();
                if(return_validation.equals("done")){
                    if(form_page.getStringExtra("type").equals("edit")){
                        Update_Material(data_material);
                    } else{
                        Add_New_Material(data_material);
                    }
                } else{
                    popUpMessage.validation_error(return_validation, Form_Material.this);
                }
            }
        });
    }

    //Load data material untuk halaman edit
    private void set_form_page(Intent form_page){
        Picasso.get().load(form_page.getStringExtra("material_photo")).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                // Set it in the ImageView
                string_image = ImageHandler.BitMapToString(bitmap);
                material_photo.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                //
            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        });
        id_material = form_page.getStringExtra("material_id");
        name.setText(form_page.getStringExtra("nama"));
        satuan.setText(String.valueOf(form_page.getIntExtra("satuan", 0)));
        harga.setText(String.valueOf(form_page.getIntExtra("harga", 0)));
    }

    //Fungsi untuk mengirim data material baru
    private void Add_New_Material(final Material material) {
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        progressDialog.setCancelable(false);
        HttpsTrustManager.allowAllSSL();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, Server_Configuration.address_add_new_material,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            if(obj.getBoolean("status")){
                                Toast.makeText(Form_Material.this,
                                        obj.getString("message"), Toast.LENGTH_LONG).show();
                                Intent kembali = new Intent(Form_Material.this, Home_Admin.class);
                                startActivity(kembali);
                                finish();
                            } else{
                                Toast.makeText(Form_Material.this,
                                        obj.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(Form_Material.this,
                                    e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", material.id);
                params.put("satuan", material.satuan);
                params.put("price", String.valueOf(material.price));
                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("photo", new DataPart(imagename + ".jpg", ImageHandler.getFileDataFromDrawable(material.photo)));
                return params;
            }
        };
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    //Fungsi untuk mengupdate data material baru
    private void Update_Material(final Material material) {
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        progressDialog.setCancelable(false);
        HttpsTrustManager.allowAllSSL();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, Server_Configuration.address_update_material,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            if(obj.getBoolean("status")){
                                Toast.makeText(Form_Material.this,
                                        obj.getString("message"), Toast.LENGTH_LONG).show();
                                Intent kembali = new Intent(Form_Material.this, Home_Admin.class);
                                startActivity(kembali);
                                finish();
                            } else{
                                Toast.makeText(Form_Material.this,
                                        obj.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(Form_Material.this,
                                    e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", material.id);
                params.put("name", material.name);
                params.put("satuan", material.satuan);
                params.put("price", String.valueOf(material.price));
                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("photo", new DataPart(imagename + ".jpg", ImageHandler.getFileDataFromDrawable(material.photo)));
                return params;
            }
        };
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
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