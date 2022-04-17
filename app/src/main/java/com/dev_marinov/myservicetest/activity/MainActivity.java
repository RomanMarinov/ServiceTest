package com.dev_marinov.myservicetest.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.dev_marinov.myservicetest.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ViewPager2 viewPager2;
    TabLayout tabLayout;
    List<ObjectTab> list = new ArrayList<>();
    ArrayList<Fragment> arrayList = new ArrayList<>(); // массив для хранения fragments
    LinearLayout ll_tab_1, ll_tab_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        TabLayout tabLayout = findViewById(R.id.tablayout);

        ll_tab_1 = findViewById(R.id.ll_tab_1);
        ll_tab_2 = findViewById(R.id.ll_tab_2);

        list.add(new ObjectTab("1","Async work")); // добавляем объет в массив
        list.add(new ObjectTab("2","Sync work"));  // добавляем объет в массив
        tabLayout.removeAllTabs();// удалить все закладки

// Таб карта
        TabLayout.Tab new_Tab_1 = tabLayout.newTab();
        new_Tab_1.setText("Async work \"Started\"");
        new_Tab_1.setTag("0");
        tabLayout.addTab(new_Tab_1);
// Таб список ресторанов
        TabLayout.Tab new_Tab_2 = tabLayout.newTab();
        new_Tab_2.setText("Sync work \"Bound\"");
        new_Tab_2.setTag("1");
        tabLayout.addTab(new_Tab_2);

        arrayList.add(new FragmentAsync());  // добавляем fragment в массив
        arrayList.add(new FragmentSync()); // добавляем fragment в массив

        // устанавливаем слушателя для tabLayout
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() { //
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // если я кликаю на закладку я получаю из тега ноль или еденицу
                String num_tab = tab.getTag().toString();
                Log.e("frag_fin","-num_tab-" + num_tab);
                if(num_tab.equals("0"))
                {
                    ll_tab_1.setVisibility(View.VISIBLE);
                    ll_tab_2.setVisibility(View.GONE);
                }
                if(num_tab.equals("1"))
                {
                    ll_tab_2.setVisibility(View.VISIBLE);
                    ll_tab_1.setVisibility(View.GONE);
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }


}