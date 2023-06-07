package com.example.storiiapp.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.storiiapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHoder> {

    private Context context;
    private List<Product> productList;
    public RecyclerAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public MyViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item,parent,false);
        return new MyViewHoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHoder holder, int position) {
        Glide.with(context).load(productList.get(position).getImage()).into(holder.imageView);
        holder.txt_name.setText(productList.get(position).getName());
        holder.txt_price.setText(CartAdapter.myFormat(productList.get(position).getPrice()) + " ₫");
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class MyViewHoder extends RecyclerView.ViewHolder {
        ImageView imageView,imageDelete;
        TextView txt_name,txt_price;
        public MyViewHoder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.recImage);
            txt_name = itemView.findViewById(R.id.recName);
            txt_price = itemView.findViewById(R.id.recPrice);
            imageDelete = imageView.findViewById(R.id.rec_btn_delete);

            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                // Retrieve the Product object at the current position
                Product product = productList.get(position);
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Products");
                FirebaseStorage storage = FirebaseStorage.getInstance();
                // Initialize storageReference with the appropriate Firebase Storage reference
                StorageReference storageReference = storage.getReferenceFromUrl(product.getImage());
                // Get the key from the Product object at the current position
                String key = product.getId();
                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        reference.child(key).removeValue();
                        Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                        // ... your existing code ...
                    }
                });
            }
        }
    }

    public void searchDataList(ArrayList<Product> searchList){
        productList = searchList;
        notifyDataSetChanged();
    }
}