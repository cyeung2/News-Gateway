package com.example.newsgateway;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String ARTICLE_BROADCAST = "";
    public static final String DATA_EXTRA = "DATA_EXTRA";
    private String source = "all";
    private ArrayList<story> storyList = new ArrayList<>();
    private ArrayList<com.example.newsgateway.source> sourceList = new ArrayList<>();
    private Menu menu;
    private ArrayList<String> catList = new ArrayList<>();
    private HashMap<String, ArrayList<String>> categoryData = new HashMap<>();
    private List<Fragment> fragments;
    private DrawerLayout drawerLayout;
    private ViewPager pageViewr;
    private ListView drawerList;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private final HashMap<String, ArrayList<com.example.newsgateway.source>> sourceData = new HashMap<>();
    private String currCategory = "";
    private ArrayAdapter<String> arrAdapt;
    private FragmentAdapter fragAdapt;
    ArrayList<String> allSources = new ArrayList<>();

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.DrawerLay);
        drawerList = findViewById(R.id.Drawer);
        drawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_item, sourceToName(sourceList)));
        drawerList.setOnItemClickListener(
                (parent, view, position, id) -> {
                    selectItem(position);
                    drawerLayout.closeDrawer(drawerList);
                }
        );
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        fragments = new ArrayList<>();
        fragAdapt = new FragmentAdapter(getSupportFragmentManager());
        pageViewr = findViewById(R.id.container);
        pageViewr.setAdapter(fragAdapt);
        sourceList.clear();
        if (sourceList.isEmpty()) {
            sourceRunnr sr = new sourceRunnr(this);
            new Thread(sr).start();
        }
        setTitle("News Gateway");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        setTitle(item.getTitle());
        catList.clear();
        ArrayList<String> lst = categoryData.get(item.getTitle().toString());
        if (lst != null) {
            catList.addAll(lst);
        }

        arrAdapt.notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
    }

    public void downloadFailedA() {
        storyList.clear();
    }

    public ArrayList<String> sourceToName(ArrayList<com.example.newsgateway.source> sourceLists) {
        ArrayList<String> temp = new ArrayList<>();
        for (int i=0;i<sourceLists.size();i++) {
            temp.add(sourceLists.get(i).getName());
        }
        return temp;
    }

    private boolean doNetCheck() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo aN = cm.getActiveNetworkInfo();
        boolean isConnected = aN != null && aN.isConnectedOrConnecting();

        if (isConnected) {
            return true;
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Data Cannot Be loaded Without A" + '\n' + "Network Connection");
            AlertDialog dialogs = builder.create();
            dialogs.show();
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        return true;
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);

        if (imm == null) {
            return;
        }
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void makeMenu(View v) {
        menu.clear();
        for (int i=0;i<catList.size();i++) {
            menu.add(catList.get(i));
        }
        hideKeyboard();
    }

    public void setupCategories(HashMap<String, HashSet<String>> categoryMapIn) {
        for(String s : categoryMapIn.keySet()) {
            HashSet<String> hSet = categoryMapIn.get(s);
            allSources.addAll(hSet);
        }
        Collections.sort(allSources);
        categoryData.put("all", allSources);

        for(String s : categoryMapIn.keySet()) {
            HashSet<String> hSet = categoryMapIn.get(s);
            if (hSet == null) {
                continue;
            }
            ArrayList<String> categories = new ArrayList<>(hSet);
            Collections.sort(categories);
            categoryData.put(s, categories);
        }
        ArrayList<String> temp = new ArrayList<>(categoryData.keySet());
        Collections.sort(temp);
        for(String s : temp) {
            menu.add(s);
        }
        ArrayList<String> lst = categoryData.get(temp.get(0));
        if (lst != null) {
            catList.addAll(lst);
        }
        arrAdapt = new ArrayAdapter<>(this, R.layout.drawer_item, catList);
        drawerList.setAdapter(arrAdapt);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    public void updateData(ArrayList<com.example.newsgateway.source> sourceArrayList) {
        for (com.example.newsgateway.source s : sourceArrayList) {
            if (!sourceData.containsKey(s.getCategory())) {
                sourceData.put(s.getCategory(), new ArrayList<>());
            }
            ArrayList<com.example.newsgateway.source> sList = sourceData.get(s.getCategory());
            if (sList != null) {
                sList.add(s);
            }

            sourceData.put("All", sourceArrayList);
            ArrayList<String> tmpList = new ArrayList<>(sourceData.keySet());
            Collections.sort(tmpList);
            for (String ss : tmpList) {
                menu.add(ss);
            }
            sourceList.addAll(sourceArrayList);
            ArrayAdapter arrayAdapter = new ArrayAdapter<>(this, R.layout.drawer_item, sourceToName(sourceList));
            drawerList.setAdapter(arrayAdapter);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
            }
        }
    }

    private void selectItem(int position) {
        pageViewr.setBackground(null);
        currCategory = catList.get(position);
        storyRunnr ar = new storyRunnr(this, currCategory);
        new Thread(ar).start();
        drawerLayout.closeDrawer(drawerList);
    }

    public void setArticles(ArrayList<story> articleList) {
        setTitle(currCategory);
        for(int i = 0; i< fragAdapt.getCount(); i++) {
            fragAdapt.notifyChangeInPosition(i);
        }
        fragments.clear();
        for(int i=0;i<articleList.size();i++) {
            fragments.add(storyFrag.newInstance(articleList.get(i), i+1, articleList.size()));
        }
        fragAdapt.notifyDataSetChanged();
        pageViewr.setCurrentItem(0);
    }

    private class FragmentAdapter extends FragmentPagerAdapter {
        private long baseID = 0;

        public FragmentAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public long getItemId(int position) {
            return baseID + position;
        }

        void notifyChangeInPosition(int n) {
            baseID += getCount() + n;
        }
    }
}