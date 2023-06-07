package com.example.storiiapp.fragment;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.example.storiiapp.ExpandableGridView;
import com.example.storiiapp.MainActivity;
import com.example.storiiapp.R;
import com.example.storiiapp.data.Category;
import com.example.storiiapp.data.CategoryAdapter;
import com.example.storiiapp.data.Product;
import com.example.storiiapp.data.ProductAdapter;
import com.example.storiiapp.data.SliderAdapter;
import com.example.storiiapp.data.SliderItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Products");
    private ViewPager2 viewPager2;
    private final Handler sliderHandler = new Handler();
    private LinearLayout searchBtn;
    private ImageView cartBtn;
    private Context context;
    private GridView gridView;
    private ArrayList<Product> productArrayList;
    private ProductAdapter adapter;
    private GridView gridViewCategory;


    public HomeFragment() {
    }
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.onCreate(savedInstanceState);

        viewPager2 = getView().findViewById(R.id.slider);
        List<SliderItem> sliderItems = getListSlider();
        //viewPager2.setCurrentItem(2);
        viewPager2.setAdapter(new SliderAdapter(sliderItems, viewPager2));
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000);
            }
        });

        initui();
        renderData();
        nextFragmentSearch();
        nextFragmentCart();

        CategoryAdapter adapter = new CategoryAdapter(getListCategory(), getActivity().getApplicationContext());
        gridViewCategory.setAdapter(adapter);
        searchCategory();
    }

    private void renderData() {
        ExpandableGridView productGrid = (ExpandableGridView) getView().findViewById(R.id.gridView);
        productGrid.setExpanded(true);
    }

    private void nextFragmentSearch() {
        //next fragment search
        searchBtn = getView().findViewById(R.id.liner_search);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
                // Hiển thị icon search trên BottomNavigationView
                bottomNavigationView.setSelectedItemId(R.id.search);
                //Chuyển fragment page
                replaceFragment(new com.example.storiiapp.fragment.SearchFragment());
            }
        });
    }

    private void nextFragmentCart() {
        //next fragment cart
        cartBtn = getView().findViewById(R.id.cartBtn);
        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
                // Hiển thị icon search trên BottomNavigationView
                bottomNavigationView.setSelectedItemId(R.id.cart);
                //Chuyển fragment page
                replaceFragment(new CartFragment());
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void initui() {
        gridView = getView().findViewById(R.id.gridView);
        productArrayList = new ArrayList<>();
        gridViewCategory = getView().findViewById(R.id.gridviewCategory);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        ((MainActivity)getActivity()).updateStatusBarColor("#FFFFFF");

        context = container.getContext();
        gridView = rootView.findViewById(R.id.gridView);
        productArrayList = new ArrayList<>();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    productArrayList.add(product);
                }
                adapter = new ProductAdapter(context, productArrayList);
                gridView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to read value.", error.toException());
            }
        });

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000); // Slide duration 3 seconds
    }

    private List<SliderItem> getListSlider() {
        List<SliderItem> sliderItems = new ArrayList<>();

        sliderItems.add(new SliderItem(R.drawable.banner1));
        sliderItems.add(new SliderItem(R.drawable.banner2));
        sliderItems.add(new SliderItem(R.drawable.banner3));
        sliderItems.add(new SliderItem(R.drawable.banner4));

        return sliderItems;
    }

    private  ArrayList<Category> getListCategory() {
        ArrayList<Category> categories = new ArrayList<>();
        categories.add(new Category("Album", "Album", "https://scontent.fsgn2-3.fna.fbcdn.net/v/t39.30808-6/352239589_604823641430718_8755971926908195169_n.jpg?_nc_cat=107&ccb=1-7&_nc_sid=730e14&_nc_ohc=PhzbI0CyOHEAX_p7Q6A&_nc_oc=AQkZcwySZ7mNWjnpG8Sz53owBgzLCezQ9_WpYA7OzCR93Bbmsa0U3sez31_103EtR-Y&_nc_ht=scontent.fsgn2-3.fna&oh=00_AfCnYm8DogUZTvK22jD0HTtLUps5KrzeybtgGlR5YxLPIw&oe=64859031"));
        categories.add(new Category("Mechandise", "Mechandise", "https://scontent.fsgn2-7.fna.fbcdn.net/v/t39.30808-6/352385753_651382743052135_1203707139808413772_n.jpg?_nc_cat=100&ccb=1-7&_nc_sid=730e14&_nc_ohc=F1Mk1Zn2dEgAX8VUATn&_nc_ht=scontent.fsgn2-7.fna&oh=00_AfC0brC27-0bMDEldPEqxd5qLtIX_RPMgXW8tO80u1heTw&oe=6483EFC9"));
        categories.add(new Category("Ticket", "Ticket", "https://scontent.fsgn2-4.fna.fbcdn.net/v/t39.30808-6/352275255_122577707516251_5862268642205329842_n.jpg?_nc_cat=101&ccb=1-7&_nc_sid=730e14&_nc_ohc=LWpYu-Up6jkAX8EPseO&_nc_ht=scontent.fsgn2-4.fna&oh=00_AfD3H4tS2bjVTSyjmOHSPOZQJEqiNniqnXNq6-R5laJcEg&oe=6484DD14"));
        categories.add(new Category("Card", "Card", "https://scontent.fsgn2-9.fna.fbcdn.net/v/t39.30808-6/352377450_270839478806067_2131006136577841864_n.jpg?_nc_cat=103&ccb=1-7&_nc_sid=730e14&_nc_ohc=LoPLg1W-_NYAX-ce8Rx&_nc_ht=scontent.fsgn2-9.fna&oh=00_AfDVSujkJEycEYg_eCj5wz91vJ-ik3emK4JKLXcfzaT37g&oe=64844297"));
        categories.add(new Category("Lightstick", "Lightstick", "https://scontent.fsgn2-3.fna.fbcdn.net/v/t39.30808-6/352356242_806418027571333_6466640201394535221_n.jpg?_nc_cat=107&ccb=1-7&_nc_sid=730e14&_nc_ohc=OcfAoDVDvqkAX87-2lo&_nc_ht=scontent.fsgn2-3.fna&oh=00_AfCHm6M1UHUFwevU1TrgOyVIycq9TWod3IiREk_fzPk-og&oe=64850A8E"));
        return categories;
    }

    private  void searchCategory() {
        gridViewCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Tạo đối tượng Bundle để truyền dữ liệu qua Fragment mới
                Bundle bundle = new Bundle();
                bundle.putString("idCategory", getListCategory().get(position).getId());
                // Tạo đối tượng Fragment mới
                com.example.storiiapp.fragment.SearchFragment searchFragment = new com.example.storiiapp.fragment.SearchFragment();
                searchFragment.setArguments(bundle);

                // Lấy đối tượng FragmentManager
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                // Thay thế Fragment hiện tại bằng Fragment mới
                fragmentManager.beginTransaction()
                        // R.id.fragment_container là ID của ViewGroup chứa các Fragment
                        .replace(R.id.frame_layout, searchFragment)
                        // (Tuỳ chọn) Thêm Fragment hiện tại vào Stack để có thể quay lại sau này
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}