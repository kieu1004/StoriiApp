package com.example.storiiapp.screen;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Outline;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.storiiapp.R;
import com.example.storiiapp.data.User;
import com.example.storiiapp.fragment.ProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class UpdateUser extends AppCompatActivity {

    private EditText edtFullName;
    private EditText edtPhone;
    private EditText edtDateOfBirth;
    private EditText edtAddress;
    private EditText edtUserName;
    private Button btnSave;
    private ImageView imageAvartar;
    ProgressBar progressBar;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);
        init();

        // Set the outline provider to create a circular outline
        imageAvartar.setOutlineProvider(new ViewOutlineProvider() {

            @Override
            public void getOutline(View view, Outline outline) {
                outline.setOval(0, 0, view.getWidth(), view.getHeight());
            }
        });
        imageAvartar.setClipToOutline(true);

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
                String username = snapshot.child("nickname").getValue(String.class);

                //Toast.makeText(context, fullName, Toast.LENGTH_SHORT).show();

                Picasso.get().load(avatarUrl).into(imageAvartar);
                edtFullName.setText(fullName);
                edtPhone.setText(phone);
                edtDateOfBirth.setText(dateOfBirth);
                edtAddress.setText(address);
                edtUserName.setText(username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            imageUri = data.getData();
                            imageAvartar.setImageURI(imageUri);
                        } else {
                            Toast.makeText(UpdateUser.this, "Không có hình ảnh được chọn", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        imageAvartar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent();
                photoPicker.setAction(Intent.ACTION_GET_CONTENT);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageUri != null) {
                    uploadToFirebase(imageUri);
                } else {
                    Toast.makeText(UpdateUser.this, "Vui lòng chọn hình ảnh", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void init() {
        edtFullName = findViewById(R.id.edt_fullname);
        edtPhone = findViewById(R.id.edt_phone);
        edtDateOfBirth = findViewById(R.id.edt_dateOfBirth);
        edtAddress = findViewById(R.id.edt_address);
        edtUserName = findViewById(R.id.edt_username);
        btnSave = findViewById(R.id.btn_save);
        imageAvartar = findViewById(R.id.upload_Avatar);
        progressBar = findViewById(R.id.progressBar);
    }

    private void uploadToFirebase(Uri uri) {
        String fullName = edtFullName.getText().toString();
        String phoneNumber = edtPhone.getText().toString();
        String dateOfBirth = edtDateOfBirth.getText().toString();
        String address = edtAddress.getText().toString();
        String username = edtUserName.getText().toString();

        // Lấy reference đến Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("avatars");
        final StorageReference imageReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));

        // Tải ảnh lên Firebase Storage
        imageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Lấy đường dẫn (URL) của ảnh vừa tải lên
                imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Lấy email và mật khẩu từ SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("SaveUser", Context.MODE_PRIVATE);
                        String userId = sharedPreferences.getString("key", "");

                        // Cập nhật các trường dữ liệu mới vào người dùng trong Firebase Realtime Database
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
                        User user = new User();
                        user.setFullName(fullName);
                        user.setPhoneNumber(phoneNumber);
                        user.setDateOfBirth(dateOfBirth);
                        user.setAddress(address);
                        user.setNickName(username);
                        user.setAvatar(uri.toString());

                        Map<String, Object> updateData = new HashMap<>();
                        updateData.put("fullName", fullName);
                        updateData.put("phoneNumber", phoneNumber);
                        updateData.put("dateOfBirth", dateOfBirth);
                        updateData.put("address", address);
                        updateData.put("nickname", username);
                        updateData.put("avatar", uri.toString());
                        progressBar.setVisibility(View.INVISIBLE);
                        userRef.updateChildren(updateData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    SharedPreferences info = getSharedPreferences("Info", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = info.edit();
                                    editor.putString("avatar", uri.toString());
                                    editor.putString("fullname", fullName);
                                    editor.apply();

                                    Toast.makeText(getApplicationContext(), "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
                                    ProfileFragment profileFragment = new ProfileFragment();
                                    FragmentManager fragmentManager = getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.layout_update_user, profileFragment);
                                    fragmentTransaction.commit();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Cập nhật thông tin thất bại!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(UpdateUser.this, "Lỗi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension(Uri fileUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }
}