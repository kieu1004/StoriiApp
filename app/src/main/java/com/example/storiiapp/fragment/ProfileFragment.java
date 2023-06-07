package com.example.storiiapp.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Outline;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.storiiapp.MainActivity;
import com.example.storiiapp.R;
import com.example.storiiapp.screen.Login;
import com.example.storiiapp.screen.ProductRecyclerView;
import com.example.storiiapp.screen.UpdateUser;
import com.example.storiiapp.screen.ViewProfile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ProfileFragment extends Fragment {
    private View view;
    private Context context;
    private TextView txtUpdateProfile, txtViewProfile;
    private Button btnLogout;
    private ImageView imgAvatar;
    private TextView txtFullName, txtRole;
    private ImageView imgSell;
    private View adminBtn;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        context = container.getContext();
        initui();
        logOut();

        ((MainActivity) getActivity()).updateStatusBarColor("#4CA71E");


        txtUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdateUser.class);
                context.startActivity(intent);
            }
        });

        txtViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewProfile.class);
                context.startActivity(intent);
            }
        });

        imgAvatar.setOutlineProvider(new ViewOutlineProvider() {

            @Override
            public void getOutline(View view, Outline outline) {
                outline.setOval(0, 0, view.getWidth(), view.getHeight());
            }
        });
        imgAvatar.setClipToOutline(true);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SaveUser", Context.MODE_PRIVATE);
        String key = sharedPreferences.getString("key", "");
        SharedPreferences.Editor editor = sharedPreferences.edit();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(key);
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Lấy dữ liệu từ DataSnapshot và gán vào các thành phần giao diện
                String avatarUrl = snapshot.child("avatar").getValue(String.class);
                String fullName = snapshot.child("fullName").getValue(String.class);
                String role = snapshot.child("rule").getValue(String.class);

                if (Objects.equals(role, "admin")) {
                    txtRole.setText("Quản trị viên");
                    nextPageAdmin();
                }

                if (fullName != null){
                    Picasso.get().load(avatarUrl).into(imgAvatar);
                    txtFullName.setText(fullName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    private void nextPageAdmin() {
        imgSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductRecyclerView.class);
                context.startActivity(intent);
            }
        });
    }

    private void initui() {
        txtUpdateProfile = view.findViewById(R.id.btn_updateProfile);
        txtViewProfile = view.findViewById(R.id.btn_viewProfile);
        btnLogout = view.findViewById(R.id.btn_logout);
        txtFullName = view.findViewById(R.id.txt_name_profile);
        txtRole = view.findViewById(R.id.tv_role_profile);
        imgAvatar = view.findViewById(R.id.imv_avt_profile);
        imgSell = view.findViewById(R.id.txt_sell_trifarm);
        adminBtn = view.findViewById(R.id.admin_btn);
    }

    private void logOut() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Đăng xuất");
        builder.setMessage("Bạn có chắc muốn đăng xuất?");
        builder.setPositiveButton("Đồng ý", (dialog, which) -> {
            // Xử lý đăng xuất
            // Xóa email và mật khẩu từ SharedPreferences
            if (checkSharedPreferencesExistence("email")) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SaveUser", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("email");
                editor.remove("key");
                editor.apply();

                SharedPreferences sharedPreferencesCart = getActivity().getSharedPreferences("CartPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorCart = sharedPreferencesCart.edit();
                editorCart.remove("cart");
                editorCart.apply();


                Intent intent = new Intent(context, Login.class);
                context.startActivity(intent);
                // Đóng tất cả các hoạt động của ứng dụng
                //getActivity().finishAffinity();
                // Thoát ứng dụng
                //System.exit(0);
            } else {
                Toast.makeText(context, "Đăng xuất thất bại", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Không", (dialog, which) -> {
            // Đóng hộp thoại
            dialog.dismiss();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean checkSharedPreferencesExistence(String keyEmail) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SaveUser", Context.MODE_PRIVATE);
        // Kiểm tra khóa key có tồn tại trong SharedPreferences hay không?
        return sharedPreferences.contains(keyEmail);
    }
}