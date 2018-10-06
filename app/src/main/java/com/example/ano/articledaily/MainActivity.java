package com.example.ano.articledaily;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.support.v7.widget.Toolbar;

import com.example.ano.articledaily.Fragment.ArticleFra;
import com.example.ano.articledaily.Fragment.BooksFrag;
import com.example.ano.articledaily.Fragment.MusicFrag;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String[] mtitle=new  String[3];
    private List<Fragment> fragments=new ArrayList<Fragment>();
    private DrawerLayout mDrawer;
    TabLayout mTab;
    ViewPager mViewPager;
    NavigationView navigationView;
    Context context=this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * init the viewpager date
         */
        InitView();
        LitePal.initialize(this);

        //set the drawerlayout
       Toolbar toolbar=(Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        mDrawer=(DrawerLayout)findViewById(R.id.drawer_layout);
        navigationView=(NavigationView)findViewById(R.id.nav_view);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.booked:
                        Intent intent=new Intent(context,watched.class);
                        intent.putExtra("booked",true);
                        startActivity(intent);
                        break;
                    case R.id.nav_watched:
                        Intent intent1=new Intent(context,watched.class);
                        startActivity(intent1);
                        break;
                }
                return true;
            }
        });
    }

    //init view page
    private void InitView()
    {
        mtitle= new String[]{"文章", "声音", "书架"};
        mTab=(TabLayout)findViewById(R.id.tab);
        mViewPager=(ViewPager)findViewById(R.id.view);
        ArticleFra articleFragment=new ArticleFra();
        MusicFrag musicFrag=new MusicFrag();
        BooksFrag booksFragment=new BooksFrag();
        fragments.add(articleFragment);
        fragments.add(musicFrag);
        fragments.add(booksFragment);
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            //此方法用来显示tab上的名字
            @Override
            public CharSequence getPageTitle(int position) {
                return mtitle[position];
            }

            @Override
            public Fragment getItem(int position) {
                //创建Fragment并返回
                return  fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });

        mViewPager.setOffscreenPageLimit(3);
        mTab.setupWithViewPager(mViewPager);
        for (int i=0;i<3;i++){
            mTab.getTabAt(i).setText(mtitle[i]);
        }

    }

    //click to open the drawer
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId()){
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                break;
        }
        return true;
    }
}
