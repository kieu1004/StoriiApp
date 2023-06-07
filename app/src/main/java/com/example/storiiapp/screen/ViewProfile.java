package com.example.storiiapp.screen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Outline;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.storiiapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ViewProfile extends AppCompatActivity {
    private ImageView imgAvatar;
    private TextView txtFullName;
    private TextView txtPhone;
    private TextView txtDateOfBirth;
    private TextView txtAddress;
    private TextView txtNickName;
    private TextView txtEmail;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        init();

        updateStatusBarColor("#8BC34A");

        // Set the outline provider to create a circular outline
        imgAvatar.setOutlineProvider(new ViewOutlineProvider() {

            @Override
            public void getOutline(View view, Outline outline) {
                outline.setOval(0, 0, view.getWidth(), view.getHeight());
            }
        });
        imgAvatar.setClipToOutline(true);

        SharedPreferences sharedPreferences = getSharedPreferences("SaveUser", Context.MODE_PRIVATE);
        String key = sharedPreferences.getString("key", "");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(key);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Lấy dữ liệu từ DataSnapshot và gán vào các thành phần giao diện
                String avatarUrl = snapshot.child("avatar").getValue(String.class);
                String fullName = snapshot.child("fullName").getValue(String.class);
                String phone = snapshot.child("phoneNumber").getValue(String.class);
                String dateOfBirth = snapshot.child("dateOfBirth").getValue(String.class);
                String address = snapshot.child("address").getValue(String.class);
                String nickname = snapshot.child("nickname").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);

                //Toast.makeText(context, fullName, Toast.LENGTH_SHORT).show();

                Picasso.get().load(avatarUrl).into(imgAvatar);
                txtFullName.setText(fullName);
                txtPhone.setText(phone);
                txtDateOfBirth.setText(dateOfBirth);
                txtAddress.setText(address);
                txtNickName.setText(nickname);
                txtEmail.setText(email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void init() {
        txtFullName = findViewById(R.id.txt_fullname);
        txtAddress = findViewById(R.id.txt_address);
        txtPhone = findViewById(R.id.txt_phone);
        txtEmail = findViewById(R.id.txt_email);
        txtNickName = findViewById(R.id.txt_username);
        txtDateOfBirth = findViewById(R.id.txt_dateofbirth);
        imgAvatar = findViewById(R.id.img_avatar);
    }

    @SuppressLint("ObsoleteSdkInt")
    public void updateStatusBarColor(String color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }
}