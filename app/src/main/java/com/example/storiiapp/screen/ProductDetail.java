package com.example.storiiapp.screen;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.storiiapp.ExpandableGridView;
import com.example.storiiapp.R;
import com.example.storiiapp.data.Comment;
import com.example.storiiapp.data.CommentAdapter;
import com.example.storiiapp.data.ProductCart;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProductDetail extends AppCompatActivity {
    ImageView imageView;
    TextView tv_detail_name, tv_detail_name2, tv_detail_description, tv_detail_price, tv_detail_old_price, tv_detail_quantity, tv_detail_sold, tv_detail_sale, tv_detail_star, tv_detail_review;
    ImageView mImagePlus, mImageMinus;
    private Button btnAddCart;
    private int mCount = 1;
    private int mTotal = 0;
    private String price;
    private String old_price;
    private String image;
    private String description;
    private String sold;
    private String star;
    private String sale;
    private String review;
    private String name;
    private String id;
    private int quantity;
    private ImageView btnMinus;
    private ImageView btnPlus;
    private TextView btnSeeAllCmt;
    private GridView gridViewCmtDetail;
    private DecimalFormat myFormatter;
    private Context context = this;
    private String idProduct;
    private DatabaseReference productsRef;
    private SharedPreferences sharedPreferences;
    private ValueEventListener eventListener;
    private ArrayList<Comment> comments, topComments;
    private ArrayList<String> likeData;
    private int startLike;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        init();
        nextOrderPage();
        renderData();

        addToCartView();

        comments = new ArrayList<>();
        topComments = new ArrayList<>();

        likeData = new ArrayList<>();
        likeData.addAll(createArrayInt(100));
        startLike = new Random().nextInt(50);

        CommentAdapter cmtAdapter = new CommentAdapter(topComments, ProductDetail.this);
        gridViewCmtDetail.setAdapter(cmtAdapter);

        //Lấy comment từ firebase
        eventListener = productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                comments.clear();
                topComments.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Comment comment = itemSnapshot.getValue(Comment.class);
                    comment.setLike(likeData.get(startLike + comments.size()));
                    comments.add(comment);
                }

                topComments.addAll(comments);
                while (topComments.size() > 3) topComments.remove(0);

                cmtAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {}
        });

        //Register ContextView
        registerForContextMenu(tv_detail_name);

        quantity = Integer.parseInt(tv_detail_quantity.getText().toString());
        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity <= 1) {
                    tv_detail_quantity.setText(String.valueOf(quantity));
                    return;
                }
                quantity = quantity - 1;
                tv_detail_quantity.setText(String.valueOf(quantity));
            }
        });

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity = quantity + 1;
                tv_detail_quantity.setText(String.valueOf(quantity));
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void renderData() {
        Intent intent = getIntent();
        image = intent.getStringExtra("image");
        name = intent.getStringExtra("name");
        description = intent.getStringExtra("description");
        sold = intent.getStringExtra("sold");
        star = intent.getStringExtra("star");
        review = intent.getStringExtra("review");
        sold = intent.getStringExtra("sold");
        sale = intent.getStringExtra("sale");
        price = intent.getStringExtra("price");
        old_price = intent.getStringExtra("old_price");
        id = intent.getStringExtra("id");

        DecimalFormat myFormatter = new DecimalFormat("###,###");
        int priceFormat = Integer.parseInt(price);
        int oldPriceFormat = Integer.parseInt(old_price);

        // Hiển thị ảnh sản phẩm bằng Glide
        Glide.with(this).load(image).into(imageView);

        // Hiển thị các thông tin khác của sản phẩm
        tv_detail_name.setText(name);
        tv_detail_name2.setText(name);
        tv_detail_description.setText(description);
        tv_detail_price.setText(myFormatter.format(priceFormat));
        tv_detail_old_price.setText(myFormatter.format(oldPriceFormat) + " ₫");
        tv_detail_old_price.setPaintFlags(tv_detail_old_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        tv_detail_sold.setText(sold + "k");
        tv_detail_star.setText(star);
        tv_detail_sale.setText("-" + sale + "%");
        tv_detail_review.setText(review);
    }

    @SuppressLint("CutPasteId")
    private void init() {
        imageView = findViewById(R.id.img_detailproduct);
        tv_detail_name = findViewById(R.id.txt_detailName);
        tv_detail_name2 = findViewById(R.id.txt_detailName2);
        tv_detail_description = findViewById(R.id.txt_detailDes);
        tv_detail_price = findViewById(R.id.txt_detailPrice);
        tv_detail_old_price = findViewById(R.id.txt_oldPrice);
        tv_detail_quantity = findViewById(R.id.txt_quantity);
        tv_detail_sold = findViewById(R.id.txt_detailSold);
        tv_detail_sale = findViewById(R.id.txt_salePrice);
        tv_detail_star = findViewById(R.id.txt_detailStar);
        tv_detail_review = findViewById(R.id.txt_detailReview);
        mImagePlus = findViewById(R.id.btn_plus);
        mImageMinus = findViewById(R.id.btn_minus);
        btnAddCart = findViewById(R.id.btn_addCart);
        btnMinus = findViewById(R.id.btn_minus);
        btnPlus = findViewById(R.id.btn_plus);
        btnSeeAllCmt = findViewById(R.id.btn_see_all);
        gridViewCmtDetail = findViewById(R.id.gridViewCmtDetail);

        ExpandableGridView productGrid = (ExpandableGridView) findViewById(R.id.gridViewCmtDetail);
        productGrid.setExpanded(true);

        Intent intent = getIntent();
        idProduct = intent.getStringExtra("id");
        productsRef = FirebaseDatabase.getInstance().getReference("Products").child(idProduct).child("Comments");
    }

    private void addToCartView() {
        btnAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy thời gian hiện tại
                long currentTimeMillis = System.currentTimeMillis();
                // Tính thời gian hết hạn (5 phút sau thời điểm hiện tại)
                long expiryTimeMillis = currentTimeMillis + (15 * 60 * 1000);

                ProductCart productCart = new ProductCart(id, name, price, String.valueOf(quantity), image, expiryTimeMillis, true);
                if (productCart != null) {
                    addToCart(productCart);
                    Toast.makeText(ProductDetail.this, "Thêm vào giỏ hàng thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProductDetail.this, "Thêm vào giỏ hàng thành thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void addToCart(ProductCart product) {
        // Lấy danh sách sản phẩm trong giỏ hàng từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("CartPrefs", Context.MODE_PRIVATE);
        // Lấy giá trị dạng JSON từ SharedPreferences
        String cartJson = sharedPreferences.getString("cart", "");
        List<ProductCart> cartList = new ArrayList<>();

        // Chuyển đổi dữ liệu JSON thành danh sách sản phẩm
        if (!TextUtils.isEmpty(cartJson)) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<ProductCart>>() {
            }.getType();
            cartList = gson.fromJson(cartJson, type);
        }

        boolean isProductExistInCart = false;

        // Kiểm tra sự tồn tại của sản phẩm trong giỏ hàng
        for (ProductCart item : cartList) {
            if (item.getId().equals(product.getId())) {
                int quantity = Integer.parseInt(item.getQuantity()) + Integer.parseInt(product.getQuantity());
                item.setQuantity(String.valueOf(quantity));
                isProductExistInCart = true;
                break;
            }
        }

        // Nếu sản phẩm không tồn tại trong giỏ hàng, thêm sản phẩm mới vào danh sách
        if (!isProductExistInCart) {
            cartList.add(product);
        }

        // Chuyển danh sách sản phẩm thành chuỗi JSON
        Gson gson = new Gson();
        String newCartJson = gson.toJson(cartList);

        // Lưu danh sách sản phẩm mới vào SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("cart", newCartJson);
        editor.apply();
    }

    private void nextOrderPage() {
        btnSeeAllCmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, Comment.class);
                intent.putExtra("idProduct", id);
                intent.putExtra("likeData", ArrayListToJson(likeData));
                intent.putExtra("startLike", String.valueOf(startLike));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.context_menu, menu);
        super.onCreateContextMenu(menu, v, menuInfo);

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() ==R.id.action_copy) {
            // Xử lý lựa chọn "Copy" ở đây
            String text = tv_detail_name.getText().toString();
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("text", text);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Đã sao chép: " + text, Toast.LENGTH_SHORT).show();
            return true;
        }else {
            return super.onContextItemSelected(item);
        }
    }

    public static String ArrayListToJson(ArrayList<String> strings) {
        Gson gson = new Gson();
        String json = gson.toJson(strings);
        return json;
    }

    public static ArrayList<String> JsonToArrayList(String json) {
        Gson gson = new Gson();
        ArrayList<String> arrayList = gson.fromJson(json, ArrayList.class);
        return arrayList;
    }

    private ArrayList<String> createArrayInt(int quantity) {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            int rand = new Random().nextInt(10);
            result.add(String.valueOf(rand));
        }
        return result;
    }
}