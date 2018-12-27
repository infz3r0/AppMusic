package com.example.pc.appmusic.Activity;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.pc.appmusic.Adapter.MainViewPayerAdapter;
import com.example.pc.appmusic.Fragment.Fragment_Tim_Kiem;
import com.example.pc.appmusic.Fragment.Fragment_Trang_Chu;
import com.example.pc.appmusic.R;

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        anhxa();
        init();

    }

    private void init() {
        MainViewPayerAdapter mainViewPayerAdapter = new MainViewPayerAdapter(getSupportFragmentManager());
        mainViewPayerAdapter.addFragment(new Fragment_Trang_Chu(),"Trang Chu");
        mainViewPayerAdapter.addFragment(new Fragment_Tim_Kiem(),"Tim Kiem");
        viewPager.setAdapter(mainViewPayerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.icontrangchu);
        tabLayout.getTabAt(1).setIcon(R.drawable.iconsearch);
    }

    private void anhxa() {
        tabLayout = findViewById(R.id.myTabLayout);
        viewPager = findViewById(R.id.myViewPayer);
    }

}
