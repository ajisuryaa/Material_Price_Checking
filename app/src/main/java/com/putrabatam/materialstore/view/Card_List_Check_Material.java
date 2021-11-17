package com.putrabatam.materialstore.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.putrabatam.materialstore.utils.Server_Configuration;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Card_List_Check_Material extends RecyclerView.Adapter<Card_List_Check_Material.PastBookingViewHolder>{
    ProgressDialog progressDialog;
    private ArrayList<Material> dataList;
    private Card_List_Check_Material adapter;

    public Card_List_Check_Material(ArrayList<Material> dataList) {
        this.dataList = dataList;
        this.adapter = this;
    }

    @Override
    public PastBookingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_list_check_material, parent, false);
        progressDialog = new ProgressDialog(view.getContext());
        return new PastBookingViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull PastBookingViewHolder holder, final int position) {
        Log.i("Address Image Material", Server_Configuration.address_image + dataList.get(holder.getAdapterPosition()).photo_address);
        holder.nama_material.setText(dataList.get(holder.getAdapterPosition()).name);
        holder.harga.setText(String.valueOf(dataList.get(holder.getAdapterPosition()).price));
        holder.satuan.setText(String.valueOf(dataList.get(holder.getAdapterPosition()).satuan));
//        Picasso.get().load(Server_Configuration.address_image + dataList.get(position).photo)
//                .into(holder.foto_pegawai);
        Picasso.get()
                .load(Server_Configuration.address_image + dataList.get(holder.getAdapterPosition()).photo_address)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(holder.foto_material);
        holder.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataList.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
            }
        });
    }

    public void Delete_Material(String id_material, Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Pesan Konfirmasi")
                .setMessage("Apakah anda yakin ingin menghapus material " + id_material + "?")
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Request_delete_material(context, id_material);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void Request_delete_material(Context context, String id_material) {
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        progressDialog.setCancelable(false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server_Configuration.address_delete_new_material,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                        try {
                            JSONObject obj = new JSONObject(ServerResponse);
                            Log.i("Hapus Material: ", ServerResponse);
                            boolean status = obj.getBoolean("status");
                            JSONObject data = new JSONObject(obj.getString("data"));
                            Toast.makeText(context, "Berhasil menghapus material " + data.getString("id_material"), Toast.LENGTH_LONG).show();
                            if(status){
                                ((Activity)context).recreate();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                        Toast.makeText(context, volleyError.toString(), Toast.LENGTH_LONG).show();
                        Log.e("Error Volley", volleyError.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id_material);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }


    public class PastBookingViewHolder extends RecyclerView.ViewHolder{
        TextView nama_material;
        TextView harga, satuan;
        EditText amount;
        ImageButton add, min, close;
        ImageView foto_material;
        public PastBookingViewHolder(View itemView, final int position) {
            super(itemView);
            this.nama_material = itemView.findViewById(R.id.txt_name_material_clc);
            this.harga = itemView.findViewById(R.id.txt_harga_material_clc);
            this.satuan = itemView.findViewById(R.id.txt_satuan_material_clc);
            this.foto_material = itemView.findViewById(R.id.image_view_material_clc);
            this.add = itemView.findViewById(R.id.btn_add_clc);
            this.min = itemView.findViewById(R.id.btn_min_clc);
            this.amount = itemView.findViewById(R.id.et_amount_clc);
            this.close = itemView.findViewById(R.id.btn_close_clc);
        }
    }
}
