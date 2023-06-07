package com.example.storiiapp.screen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.storiiapp.MainActivity;
import com.example.storiiapp.R;
import com.example.storiiapp.data.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    private EditText edtEmail;
    private EditText edtPassword;
    private TextView txtsignupRedirectText;
    private Button btnLogin;
    private ProgressBar progressBar;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initui();
        nextSignUp();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = edtEmail.getText().toString().trim();
                final String password = edtPassword.getText().toString().trim();

                if (validateEmail(email) && validatePassword(password)) {
                    // Search for user in Firebase Realtime Database
                    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Users");
                    Query query = databaseRef.orderByChild("email").equalTo(email);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean found = false;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                User user = snapshot.getValue(User.class);
                                if (user != null && user.getPassword().equals(password)) {
                                    found = true;
                                    progressBar.setVisibility(View.VISIBLE);

                                    if (saveUser(user.getEmail(), user.getPassword(), user.getKey())) {
                                        SharedPreferences info = getSharedPreferences("Info", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = info.edit();

                                        if (user.getImage() == null && user.getFullName() == null) {
                                            editor.putString("fullname", "Ẩn danh");
                                            editor.putString("avatar", "https://media.istockphoto.com/id/1298261537/vi/vec-to/ch%E1%BB%97-d%C3%A0nh-s%E1%BA%B5n-cho-bi%E1%BB%83u-t%C6%B0%E1%BB%A3ng-%C4%91%E1%BA%A7u-h%E1%BB%93-s%C6%A1-ng%C6%B0%E1%BB%9Di-%C4%91%C3%A0n-%C3%B4ng-tr%E1%BB%91ng.jpg?s=612x612&w=0&k=20&c=Rbi2tNjNA4z86gzSPBhGOefKI-XTKqlqGy-kiPoUvRA=");
                                        } else {
                                            editor.putString("avatar", user.getImage());
                                            editor.putString("fullname", user.getFullName());
                                        }
                                        editor.apply();
                                        Toast.makeText(Login.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Login.this, MainActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(Login.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }

                            if (!found) {
                                // Login failed
                                Toast.makeText(Login.this, "Sai mật khẩu hoặc email.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(Login.this, "Null", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private boolean saveUser(String email, String password, String key) {
        if (!email.isEmpty() && !password.isEmpty()) {
            // Kiểm tra email và password không rỗng
            SharedPreferences sharedPreferences = getSharedPreferences("SaveUser", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("email", email);
            editor.putString("key", key);
            editor.apply();
            return true;
        } else {
            return false;
        }
    }

    private void nextSignUp() {
        txtsignupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });
    }

    private void initui() {
        edtEmail = findViewById(R.id.edt_login_email);
        edtPassword = findViewById(R.id.edt_login_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        txtsignupRedirectText = findViewById(R.id.txt_signupRedirectText);
        progressBar = findViewById(R.id.progressBar);
    }

    private boolean validateEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Vui lòng nhập email của bạn");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Định dạng email không hợp lệ");
            return false;
        }
        return true;
    }

    private boolean validatePassword(String password) {
        if (TextUtils.isEmpty(password)) {
            edtPassword.setError("Vui lòng nhập mật khẩu");
            return false;
        } else if (password.length() < 6) {
            edtPassword.setError("Độ dài mật khẩu phải từ 6 kí tự");
            return false;
        }
        return true;
    }
}