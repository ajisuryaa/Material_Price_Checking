package com.putrabatam.materialstore.view;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Form_Material extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    PopUpMessage popUpMessage = new PopUpMessage();

    public final int REQUEST_CAMERA_CAPTURE = 1;
    public final int REQUEST_IMAGE_GALLERY = 2;

    private String string_image = "";
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

        if (!form_page.getStringExtra("type").equals("add")) {
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
                if(!harga.getText().toString().equals("")){
                    data_material.set_material(
                            id_material,
                            name.getText().toString(),
                            bitmap,
                            Integer.parseInt(harga.getText().toString()),
                            satuan.getText().toString()
                    );
                    String return_validation = data_material.validation_adding_material();
                    if (return_validation.equals("done")) {
                        if (form_page.getStringExtra("type").equals("edit")) {
                            Update_Material(data_material);
                        } else {
                            Add_New_Material(data_material);
                        }
                    } else {
                        popUpMessage.validation_error(return_validation, Form_Material.this);
                    }
                } else{
                    harga.setText("0");
                    popUpMessage.validation_error("Harga yang anda cantumkan tidak sesuai!", Form_Material.this);
                }
            }
        });
    }

    //Load data material untuk halaman edit
    private void set_form_page(Intent form_page) {
        Picasso.get().load(form_page.getStringExtra("photo")).into(new Target() {
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
        id_material = form_page.getStringExtra("id");
        name.setText(form_page.getStringExtra("name"));
        satuan.setText(form_page.getStringExtra("satuan"));
        harga.setText(String.valueOf(form_page.getIntExtra("price", 0)));
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
                            if (obj.getBoolean("status")) {
                                Toast.makeText(Form_Material.this,
                                        obj.getString("message"), Toast.LENGTH_LONG).show();
                                Intent kembali = new Intent(Form_Material.this, Home_Admin.class);
                                startActivity(kembali);
                                finish();
                            } else {
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
                            if (obj.getBoolean("status")) {
                                Toast.makeText(Form_Material.this,
                                        obj.getString("message"), Toast.LENGTH_LONG).show();
                                Intent kembali = new Intent(Form_Material.this, Home_Admin.class);
                                startActivity(kembali);
                                finish();
                            } else {
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
                // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                CameraActivityResultLauncher.launch(camera_intent);
                return true;
            case R.id.choose_folder:
                Intent gallery_intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                DirectoryActivityResultLauncher.launch(gallery_intent);
                return true;
            default:
                return false;
        }
    }

    public static String getPath(Context context, Uri uri) {
        String result = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        if (result == null) {
            result = "Not found";
        }
        return result;
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    ActivityResultLauncher<Intent> CameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        File f = new File(Environment.getExternalStorageDirectory().toString());
                        for (File temp : f.listFiles()) {
                            if (temp.getName().equals("temp.jpg")) {
                                f = temp;
                                break;
                            }
                        }
                        try {
                            Bitmap bitmap;
                            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                            bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                                    bitmapOptions);
                            Bitmap scaledBitmap = scaleDown(bitmap, 300, true);
                            material_photo.setImageBitmap(scaledBitmap);
                            String path = android.os.Environment
                                    .getExternalStorageDirectory()
                                    + File.separator
                                    + "Phoenix" + File.separator + "default";
                            f.delete();
                            OutputStream outFile = null;
                            File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                            try {
                                outFile = new FileOutputStream(file);
                                //bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                                outFile.flush();
                                outFile.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else{
                        Log.e("Failed", "Failed Load Image From Camera!");
                    }
                }
            });

    ActivityResultLauncher<Intent> DirectoryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        Uri fileUri = data.getData();
                        String selectedImagePath = getPath(getApplicationContext(), fileUri);
                        if (selectedImagePath != "Not found") {
                            ContentResolver contentResolver = getContentResolver();

                            try {
                                // Open the file input stream by the uri.
                                InputStream inputStream = contentResolver.openInputStream(fileUri);

                                // Get the bitmap.
                                Bitmap imgBitmap = BitmapFactory.decodeStream(inputStream);
                                Bitmap scaledBitmap = scaleDown(imgBitmap, 300, true);
                                material_photo.setImageBitmap(scaledBitmap);
                                inputStream.close();
                            } catch (FileNotFoundException ex) {
                                Log.e("FAILED", ex.getMessage(), ex);
                            } catch (IOException ex) {
                                Log.e("FAILED", ex.getMessage(), ex);
                            }
                        }
                    } else{
                        Log.e("Failed", "Failed Load Image From Directory!");
                    }
                }
            });
}