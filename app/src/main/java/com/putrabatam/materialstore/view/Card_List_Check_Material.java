package com.putrabatam.materialstore.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
    ArrayList<Material> dataList;
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
        Material data_material = dataList.get(holder.getAdapterPosition());
        Log.i("Address Image Material", Server_Configuration.address_image + dataList.get(holder.getAdapterPosition()).photo_address);
        holder.nama_material.setText(data_material.name);
        holder.harga.setText(String.valueOf(data_material.formatRupiah(data_material.price)));
        holder.satuan.setText(data_material.satuan);
//        Picasso.get().load(Server_Configuration.address_image + dataList.get(position).photo)
//                .into(holder.foto_pegawai);
        Picasso.get()
                .load(Server_Configuration.address_image + data_material.photo_address)
                .into(holder.foto_material);
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current_quantity = dataList.get(holder.getAdapterPosition()).quantity;
                int new_quantity = current_quantity + 1;
                Log.i("Current Quantity", String.valueOf(dataList.get(holder.getAdapterPosition()).quantity));
                data_material.set_quantity(new_quantity);
                dataList.set(holder.getAdapterPosition(), data_material);
                holder.quantity.setText(String.valueOf(dataList.get(holder.getAdapterPosition()).quantity));
                Log.i("New Quantity", String.valueOf(dataList.get(holder.getAdapterPosition()).quantity));
                adapter.notifyItemChanged(holder.getAdapterPosition());
                adapter.notifyDataSetChanged();
            }
        });
        holder.min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current_quantity = dataList.get(holder.getAdapterPosition()).quantity;
                int new_quantity = current_quantity - 1;
                if(new_quantity >= 1){
                    Log.i("Current Quantity", String.valueOf(dataList.get(holder.getAdapterPosition()).quantity));
                    data_material.set_quantity(new_quantity);
                    dataList.set(holder.getAdapterPosition(), data_material);
                    holder.quantity.setText(String.valueOf(dataList.get(holder.getAdapterPosition()).quantity));
                    Log.i("New Quantity", String.valueOf(dataList.get(holder.getAdapterPosition()).quantity));
                    adapter.notifyItemChanged(holder.getAdapterPosition());
                    adapter.notifyDataSetChanged();
                }
            }
        });
        holder.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataList.remove(holder.getAdapterPosition());
                adapter.notifyItemRemoved(holder.getAdapterPosition());
                adapter.notifyItemRangeChanged(holder.getAdapterPosition(), dataList.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }


    public class PastBookingViewHolder extends RecyclerView.ViewHolder{
        TextView nama_material;
        TextView harga, satuan;
        EditText quantity;
        ImageButton add, min, close;
        ImageView foto_material;
        public PastBookingViewHolder(View itemView, final int position) {
            super(itemView);
            this.nama_material = itemView.findViewById(R.id.txt_name_material_clc);
            this.harga = itemView.findViewById(R.id.txt_harga_material_clc);
            this.satuan = itemView.findViewById(R.id.txt_satuan_material_clc);
            this.foto_material = itemView.findViewById(R.id.image_view_material_clc);
            this.add = itemView.findViewById(R.id.btn_add_quantity_clc);
            this.min = itemView.findViewById(R.id.btn_min_quantity_clc);
            this.quantity = itemView.findViewById(R.id.et_quantity_clc);
            this.close = itemView.findViewById(R.id.btn_close_clc);
        }
    }
}
