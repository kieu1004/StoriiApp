package com.example.storiiapp.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.storiiapp.OnProductItemClickListener;
import com.example.storiiapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommentAdapter extends BaseAdapter {
    private ArrayList<Comment> commentArrayList;
    private Context context;
    private OnProductItemClickListener itemClickListener;

    public OnProductItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    public void setItemClickListener(OnProductItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public CommentAdapter(ArrayList<Comment> commentArrayList, Context context) {
        this.commentArrayList = commentArrayList;
        this.context = context;
    }

    public CommentAdapter() {
    }

    private boolean isTextViewBold(TextView textView) {
        Typeface typeface = textView.getTypeface();
        if (typeface != null) {
            return typeface.getStyle() == Typeface.BOLD || typeface.getStyle() == Typeface.BOLD_ITALIC;
        } else {
            return false;
        }
    }

    public int getIntFormString(String str) {
        String pattern = "\\d+"; // Regular expression to match numbers
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(str);
        if (m.find()) {
            String number = m.group(); // Extract the matched number
            return Integer.parseInt(number);
        }
        return -1;
    }

    @Override
    public int getCount() {
        return commentArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return commentArrayList.get(position);
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
            convertView = inflater.inflate(R.layout.comment_item, null);

            dataItem.iv_avt = convertView.findViewById(R.id.imv_avt_cmt);
            dataItem.tv_name = convertView.findViewById(R.id.tv_name);
            dataItem.tv_cmt = convertView.findViewById(R.id.tv_cmt);
            dataItem.tv_like = convertView.findViewById(R.id.tv_like);
            dataItem.img_heart_icon = convertView.findViewById(R.id.img_heart_icon);
            dataItem.tv_time = convertView.findViewById(R.id.tv_time);

            convertView.setTag(dataItem);
        } else {
            dataItem = (MyView) convertView.getTag();
        }

        Picasso.get().load(commentArrayList.get(position).getAvt()).resize(256, 256).centerCrop().into(dataItem.iv_avt);
        dataItem.tv_name.setText(commentArrayList.get(position).getUsername());
        dataItem.tv_cmt.setText(commentArrayList.get(position).getCmt());

        String idproduct = commentArrayList.get(position).getIdProduct();
        String key = commentArrayList.get(position).getId();

        String likeStr = "Hữu ích (" + commentArrayList.get(position).getLike() + ")";
        dataItem.tv_like.setText(likeStr);
        dataItem.tv_time.setText(commentArrayList.get(position).getTime());

        dataItem.tv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference("Products").child(idproduct).child(key);
                commentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String str = dataItem.tv_like.getText().toString();

                        if (isTextViewBold(dataItem.tv_like)) {
                            dataItem.tv_like.setTypeface(null, Typeface.NORMAL);
                            dataItem.tv_like.setTextColor(Color.parseColor("#A1A1A1"));
                            dataItem.img_heart_icon.setImageResource(R.drawable.i_heart_unlike);
                            dataItem.tv_like.setText("Hữu ích (" + (getIntFormString(str) - 1) + ")");

                        } else {
                            dataItem.tv_like.setTypeface(null, Typeface.BOLD);
                            dataItem.tv_like.setTextColor(Color.parseColor("#4CA71E"));
                            dataItem.img_heart_icon.setImageResource(R.drawable.i_heart_like);
                            dataItem.tv_like.setText("Hữu ích (" + (getIntFormString(str) + 1) + ")");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        return convertView;
    }


    private static class MyView {
        ImageView iv_avt;
        TextView tv_name;
        TextView tv_cmt;
        TextView tv_like;
        ImageView img_heart_icon;
        TextView tv_time;
    }
}