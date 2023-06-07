package com.example.storiiapp.data;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.storiiapp.R;
import com.example.storiiapp.screen.ProductDetail;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ProductAdapter extends BaseAdapter {
    private ArrayList<Product> listProduct;
    private Context context;
    LayoutInflater layoutInflater;

    public ProductAdapter(Context context, ArrayList<Product> listProduct) {
        this.context = context;
        this.listProduct = listProduct;
    }

    @Override
    public int getCount() {
        return listProduct.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (layoutInflater == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (view == null) {
            view = layoutInflater.inflate(R.layout.product_item, null);
        }

        ImageView gridImage = view.findViewById(R.id.grid_image);
        TextView gridName = view.findViewById(R.id.gird_name);
        TextView gridPrice = view.findViewById(R.id.gird_price);
        TextView gridSold = view.findViewById(R.id.gird_sold);
        TextView gridSale = view.findViewById(R.id.gird_sale);
        TextView gridStar = view.findViewById(R.id.gird_star);


        DecimalFormat myFormatter = new DecimalFormat("###,###");
        int price = Integer.parseInt(listProduct.get(i).getPrice());
        String sold = "Đã bán " + listProduct.get(i).getSold() + "k";

        String star = String.valueOf((double) Integer.parseInt(listProduct.get(i).getStar()) / 10);

        double priceNew = Double.parseDouble(listProduct.get(i).getPrice());
        double priceOld = Double.parseDouble(listProduct.get(i).getOld_price());
        int saleInt = (int) (100 - Math.round(priceNew / priceOld * 100));

        String sale = "Giảm " + saleInt + "%";

        Glide.with(context).load(listProduct.get(i).getImage()).into(gridImage);
        gridName.setText(listProduct.get(i).getName());
        gridPrice.setText(myFormatter.format(price));
        gridSold.setText(sold);
        gridStar.setText(star);
        gridSale.setText(sale);

        // Set OnClickListener for each item in GridView
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle onClick event here
                // For example, start a new activity to display the details of the clicked item
                Intent intent = new Intent(context, ProductDetail.class);
                intent.putExtra("id", listProduct.get(i).getId());
                intent.putExtra("image", listProduct.get(i).getImage());
                intent.putExtra("name", listProduct.get(i).getName());
                intent.putExtra("description", listProduct.get(i).getDescription());
                intent.putExtra("price", listProduct.get(i).getPrice());
                intent.putExtra("old_price", listProduct.get(i).getOld_price());
                intent.putExtra("sold", listProduct.get(i).getSold());
                intent.putExtra("sale", String.valueOf(saleInt));
                intent.putExtra("review", listProduct.get(i).getReview());
                intent.putExtra("star", star);
                context.startActivity(intent);
            }
        });

        return view;
    }

    public void searchDataList(ArrayList<Product> searchList) {
        listProduct = searchList;
        notifyDataSetChanged();
    }
}
