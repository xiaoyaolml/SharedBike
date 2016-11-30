package com.leo.sharedbike;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    private List<Bike> mBikeList;
    private QuickAdapter<Bike> mAdapter;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBikeList = new ArrayList<>();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,AddActivity.class));
            }
        });


        mListView = (ListView) findViewById(R.id.main_list);
        mAdapter = new QuickAdapter<Bike>(this, R.layout.list_item_bike) {
            @Override
            protected void convert(BaseAdapterHelper helper, Bike item) {
                helper.setText(R.id.item_bike_id,item.getId());
                helper.setText(R.id.item_bike_password,item.getPassword());
            }
        };
        mListView.setAdapter(mAdapter);



        BmobQuery<Bike> query = new BmobQuery<>();
        query.findObjects(new FindListener<Bike>() {
            @Override
            public void done(List<Bike> list, BmobException e) {
                if (e == null) {
                    mBikeList = list;
                    mAdapter.addAll(mBikeList);
                } else {
                    Log.i(TAG,"失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });







    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
