package com.example.storiiapp.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.storiiapp.OnProductItemClickListener;
import com.example.storiiapp.R;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CartAdapter extends BaseAdapter {
    private ArrayList<ProductCart> productCartList;
    private Context context;
    private OnProductItemClickListener itemClickListener;

    public OnProductItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    public void setItemClickListener(OnProductItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public CartAdapter(ArrayList<ProductCart> productCartList, Context context) {
        this.productCartList = productCartList;
        this.context = context;
    }

    public CartAdapter() {
    }

    @Override
    public int getCount() {
        return productCartList.size();
    }

    @Override
    public Object getItem(int position) {
        return productCartList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final MyView dataItem;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            dataItem = new MyView();
            convertView = inflater.inflate(R.layout.cart_item, null);
            dataItem.iv_photo = convertView.findViewById(R.id.imv_photo);
            dataItem.tv_name = convertView.findViewById(R.id.tv_title);
            dataItem.tv_price = convertView.findViewById(R.id.tv_price);
            dataItem.tv_quantity = convertView.findViewById(R.id.txt_quantity_cart);
            dataItem.btnMinus = convertView.findViewById(R.id.btn_minus_cart);
            dataItem.btnPlus = convertView.findViewById(R.id.btn_plus_cart);
            dataItem.btnDelete = convertView.findViewById(R.id.btn_deleteCart);
            convertView.setTag(dataItem);
        } else {
            dataItem = (MyView) convertView.getTag();
        }

        DecimalFormat myFormatter = new DecimalFormat("###,###");
        int price = Integer.parseInt(productCartList.get(position).getPrice());
        int quantity = Integer.parseInt(productCartList.get(position).getQuantity());
        total(productCartList);
        Picasso.get().load(productCartList.get(position).getImage()).resize(256, 256).centerCrop().into(dataItem.iv_photo);
        dataItem.tv_name.setText(productCartList.get(position).getName());
        dataItem.tv_price.setText(myFormatter.format(price));
        dataItem.tv_quantity.setText(myFormatter.format(quantity));

        dataItem.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onProductItemDeleteClick(position);
                    total(productCartList);
                }
            }
        });

        dataItem.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = Integer.parseInt(dataItem.tv_quantity.getText().toString());
                int currentPirce = price;
                int newQuantity = currentQuantity + 1;
                dataItem.tv_quantity.setText(myFormatter.format(newQuantity));
                productCartList.get(position).setQuantity(String.valueOf(newQuantity));
                total(productCartList);
                updateData();
            }
        });

        dataItem.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = Integer.parseInt(dataItem.tv_quantity.getText().toString());
                int currentPirce = price;
                int newQuantity = currentQuantity - 1;
                dataItem.tv_quantity.setText(myFormatter.format(newQuantity));
                productCartList.get(position).setQuantity(String.valueOf(newQuantity));
                if (newQuantity == 0) {
                    removeProductCart(position);
                }
                total(productCartList);
                updateData();
            }
        });

        return convertView;
    }

    public boolean removeProductCart(int position) {
        if (productCartList != null && position >= 0 && position < productCartList.size()) {
            productCartList.remove(position);

            updateData();
            return true;
        } else {
            return false;
        }
    }

    public void total(ArrayList<ProductCart> productCartList) {
        int total = 0;
        for (int i = 0; i < productCartList.size(); i++) {
            total += Integer.parseInt(productCartList.get(i).price) * Integer.parseInt(productCartList.get(i).getQuantity());
        }
        itemClickListener.onCartTotalChanged(total);
    }

    public static String myFormat(int number, int mode) {
        DecimalFormat myFormatter = new DecimalFormat("###,###");

        if (mode == 1)
            return String.valueOf(number);
        if (mode == 2)
            return myFormatter.format(number);
        return "";
    }

    public static String myFormat(String num) {
        DecimalFormat myFormatter = new DecimalFormat("###,###");

        int price = Integer.parseInt(num);
        return myFormatter.format(price);
    }

    private static class MyView {
        ImageView iv_photo;
        TextView tv_name;
        TextView tv_price;
        TextView tv_quantity;
        ImageView btnPlus, btnMinus;
        ImageView btnDelete;
    }

    private void updateData() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("CartPrefs", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String updatedCartJson = gson.toJson(productCartList);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("cart", updatedCartJson);
        editor.apply();
        notifyDataSetChanged();
    }
}
