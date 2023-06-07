package com.example.storiiapp.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.storiiapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CategoryAdapter extends BaseAdapter {
    private ArrayList<Category> categories;
    private Context context;

    public CategoryAdapter(ArrayList<Category> categories, Context context) {
        this.categories = categories;
        this.context = context;
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int position) {
        return categories.get(position);
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
            convertView = inflater.inflate(R.layout.category_item, null);
            dataItem.iv_photo = convertView.findViewById(R.id.imv_photo);
            dataItem.tv_caption = convertView.findViewById(R.id.tv_title);
            convertView.setTag(dataItem);
        } else {
            dataItem = (MyView) convertView.getTag();
        }

        //new DownloadImage(dataItem.iv_photo).execute(categories.get(position).getSource_photo());
        Picasso.get().load(categories.get(position).getImage()).resize(256, 256).centerCrop().into(dataItem.iv_photo);
        dataItem.tv_caption.setText(categories.get(position).getName());

        return convertView;
    }

    private static class MyView {
        ImageView iv_photo;
        TextView tv_caption;
    }
}
