package com.example.storiiapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.storiiapp.MainActivity;
import com.example.storiiapp.OnProductItemClickListener;
import com.example.storiiapp.R;
import com.example.storiiapp.data.CartAdapter;
import com.example.storiiapp.data.ProductCart;
import com.example.storiiapp.screen.OrderCart;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

public class CartFragment extends Fragment implements OnProductItemClickListener {
    public CartFragment() {
    }

    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private GridView gridViewCart;
    private TextView txtQuantity, txtTotal, txtOrder;
    private ArrayList<ProductCart> cartProducts;
    private CartAdapter adapter;
    private String totalOrder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        gridViewCart = getView().findViewById(R.id.gridViewCart);
        txtQuantity = getView().findViewById(R.id.txt_quantity_cart);
        txtOrder = getView().findViewById(R.id.btn_order);
        txtTotal = getView().findViewById(R.id.txt_total_cart);
        adapter = new CartAdapter(getCartProducts(), getActivity().getApplicationContext());
        adapter.setItemClickListener(this);
        gridViewCart.setAdapter(adapter);

        nextOrderPage();
    }

    private void nextOrderPage() {
        txtOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartProducts != null) {
                    SharedPreferences sharedPreferencesCart = getActivity().getSharedPreferences("CartPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editorCart = sharedPreferencesCart.edit();
                    editorCart.remove("cart");
                    editorCart.apply();
                    adapter.notifyDataSetChanged();
                    gridViewCart.setAdapter(adapter);
                    Intent intent = new Intent(getActivity(), OrderCart.class);
                    intent.putExtra("totalOrder", totalOrder);
                    startActivity(intent);
                } else
                    Toast.makeText(getActivity(), "Chưa có sản phẩm trong giỏ hàng.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((MainActivity) getActivity()).updateStatusBarColor("#FFFFFF");
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }


    public ArrayList<ProductCart> getCartProducts() {
        // Lấy dữ liệu từ Shared Preferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("CartPrefs", Context.MODE_PRIVATE);
        String cartJson = sharedPreferences.getString("cart", "");

        // Chuyển đổi từ JSON sang danh sách đối tượng ProductCart
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ProductCart>>() {
        }.getType();
        cartProducts = gson.fromJson(cartJson, type);

        // Kiểm tra nếu danh sách chưa tồn tại, khởi tạo danh sách mới
        if (cartProducts == null) {
            cartProducts = new ArrayList<>();
        }

        // Kiểm tra và xóa các sản phẩm đã hết hạn
        if (cartProducts != null && !cartProducts.isEmpty()) {
            long currentTimeMillis = System.currentTimeMillis();
            Iterator<ProductCart> iterator = cartProducts.iterator();
            while (iterator.hasNext()) {
                ProductCart productCart = iterator.next();
                if (productCart.getExpiryTimeMillis() <= currentTimeMillis || !productCart.isStatus()) {
                    iterator.remove();
                }
            }
            // Lưu lại danh sách sản phẩm sau khi xóa vào Shared Preferences
            String updatedCartJson = gson.toJson(cartProducts);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("cart", updatedCartJson);
            editor.apply();
        }
        return cartProducts;
    }

    @Override
    public void onProductItemClick(int position) {

    }

    @Override
    public void onProductItemDeleteClick(int position) {
        // Xóa sản phẩm trong danh sách
        boolean result = adapter.removeProductCart(position);
        if (result) {
            // Cập nhật lại GridView
            gridViewCart.setAdapter(adapter);
        }
    }

    @Override
    public void onCartTotalChanged(int total) {
        DecimalFormat myFormatter = new DecimalFormat("###,### ₫");
        totalOrder = myFormatter.format(total);
        txtTotal.setText(totalOrder);
    }
}






