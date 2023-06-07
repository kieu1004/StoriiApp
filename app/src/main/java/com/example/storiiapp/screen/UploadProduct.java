package com.example.storiiapp.screen;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.storiiapp.R;
import com.example.storiiapp.data.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Random;

public class UploadProduct extends AppCompatActivity {

    private Button uploadProduct;
    private ImageView uploadImage;
    EditText edtName, edtDesciption, edtPrice;
    ProgressBar progressBar;
    RadioGroup radioGr1;
    RadioGroup radioGr2;
    private boolean isChecking = true;
    private int mCheckedId = R.id.album_cate;

    private Uri imageUri;
    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Products");
    final private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_product);

        initui();
        UpLoadProduct();

        radioGrHandler();
    }

    private String getDataRadio() {
        String result = "";

        if (mCheckedId == R.id.album_cate) {
            result = "Album";
        } else if (mCheckedId == R.id.merchandise_cate) {
            result = "Merchandise";
        } else if (mCheckedId == R.id.ticket_cate) {
            result = "Ticket";
        } else if (mCheckedId == R.id.card_cate) {
            result = "Card";
        } else if (mCheckedId == R.id.lightstick_cate) {
            result = "Lightstick";
        } else if (mCheckedId == R.id.other_cate) {
            result = "Orther";
        }

        return result;

    }

    private void radioGrHandler() {
        radioGr1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1 && isChecking) {
                    isChecking = false;
                    radioGr2.clearCheck();
                    mCheckedId = checkedId;
                }
                isChecking = true;
            }
        });

        radioGr2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1 && isChecking) {
                    isChecking = false;
                    radioGr1.clearCheck();
                    mCheckedId = checkedId;
                }
                isChecking = true;
            }
        });

    }

    private void UpLoadProduct() {
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    imageUri = data.getData();
                    uploadImage.setImageURI(imageUri);
                } else {
                    Toast.makeText(UploadProduct.this, "Không có hình ảnh", Toast.LENGTH_SHORT).show();
                }
            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent();
                photoPicker.setAction(Intent.ACTION_GET_CONTENT);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });
        uploadProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageUri != null) {
                    uploadToFirebase(imageUri);
                } else {
                    Toast.makeText(UploadProduct.this, "Vui lòng chọn một ảnh", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initui() {
        uploadProduct = findViewById(R.id.btn_uploadProduct);
        edtName = findViewById(R.id.edt_name);
        edtDesciption = findViewById(R.id.edt_description);
        edtPrice = findViewById(R.id.edt_price);
        uploadImage = findViewById(R.id.uploadImage);
        progressBar = findViewById(R.id.progressBar);
        radioGr1 = (RadioGroup) findViewById(R.id.rdg_cate_1);
        radioGr2 = (RadioGroup) findViewById(R.id.rdg_cate_2);
    }

    private void uploadToFirebase(Uri uri) {
        String name = edtName.getText().toString();
        String descripttion = edtDesciption.getText().toString();
        String price = edtPrice.getText().toString();
        int intOldPrice = Integer.parseInt(price) * (new Random().nextInt(300 - 100 + 1) + 100) / 100;
        String oldPrice = String.valueOf(intOldPrice);
        final StorageReference imageReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        imageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //String key = databaseReference.push().getKey();
                        int count = 0;
                        String productId = "s" + (count + 1);
                        Product product = new Product(productId, name, descripttion, uri.toString(), price, oldPrice, "40", "0", "0", getDataRadio());
                        databaseReference.child(productId).setValue(product);
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(UploadProduct.this, "Đăng sản phẩm thành công", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UploadProduct.this, ProductRecyclerView.class);
                        startActivity(intent);
                        finish();
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
                Toast.makeText(UploadProduct.this, "Lỗi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension(Uri fileUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }
}