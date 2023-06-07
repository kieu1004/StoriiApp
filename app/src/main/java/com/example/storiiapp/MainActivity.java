package com.example.storiiapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.example.storiiapp.databinding.ActivityMainBinding;
import com.example.storiiapp.fragment.CartFragment;
import com.example.storiiapp.fragment.HomeFragment;
import com.example.storiiapp.fragment.SearchFragment;
import com.example.storiiapp.fragment.ProfileFragment;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            //Fragment IF - ELSE IF
            if(item.getItemId() == R.id.home){
                replaceFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.search) {
                replaceFragment(new SearchFragment());
            } else if (item.getItemId() == R.id.cart) {
                replaceFragment(new CartFragment());
            } else if(item.getItemId() == R.id.profile) {
                replaceFragment(new ProfileFragment());
            }

            //Fragment Switch
////            switch (item.getItemId()) {
////                case R.id.home:
////                    replaceFragment(new HomeFragment());
////                    break;
////                case R.id.search:
////                    replaceFragment(new SearchFragment());
////                    break;
////                case R.id.cart:
////                    replaceFragment(new CartFragment());
////                    break;
////                case R.id.profile:
////                    replaceFragment(new ProfileFragment());
////                    break;
////            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    public void updateStatusBarColor(String color) {// Color must be in hexadecimal format
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }
}