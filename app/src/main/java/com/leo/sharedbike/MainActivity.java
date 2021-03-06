package com.leo.sharedbike;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    private List<Bike> mBikeList;
    private QuickAdapter<Bike> mAdapter;
    private IWXAPI api;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        api = WXAPIFactory.createWXAPI(this, "wxf57b8e770be4cea4", false);
        api.registerApp("wxf57b8e770be4cea4");

        mBikeList = new ArrayList<>();

        getBikes();
        mAdapter = new QuickAdapter<Bike>(this, R.layout.list_item_bike) {
            @Override
            protected void convert(BaseAdapterHelper helper, Bike item) {
                helper.setText(R.id.item_bike_id, item.getId());
                helper.setText(R.id.item_bike_password, item.getPassword());
            }
        };
        ListView listView = (ListView) findViewById(R.id.main_list);
        listView.setAdapter(mAdapter);

        final EditText etSearch = (EditText) findViewById(R.id.main_search);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String search = etSearch.getText().toString();
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                intent.putExtra(TAG, search);
                startActivity(intent);
            }
        });

        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) findViewById(R.id.main_refresh);
        refreshLayout.setColorSchemeColors(Color.BLUE, Color.GRAY, Color.WHITE);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBikes();
                refreshLayout.setRefreshing(false);
            }
        });

        final ImageView ivClear = (ImageView) findViewById(R.id.main_search_clear);

        ivClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch.setText("");
                ivClear.setVisibility(View.GONE);
            }
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etSearch.setGravity(Gravity.CENTER_VERTICAL);
                String str = s.toString();
                List<Bike> searchList = new ArrayList<>();
                System.out.println(str);
                if (TextUtils.isEmpty(str)) {
                    searchList = mBikeList;
                } else {
                    searchList.clear();
                    for (Bike bike:mBikeList) {
                        if (bike.getId().startsWith(str)) {
                            searchList.add(bike);
                        }
                    }
                }
                mAdapter.replaceAll(searchList);


            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etSearch.getText().length() > 0) {
                    ivClear.setVisibility(View.VISIBLE);
                } else {
                    ivClear.setVisibility(View.GONE);
                    etSearch.setGravity(Gravity.CENTER);
                }
            }
        });

    }

    @NonNull
    private BmobQuery<Bike> getBikes() {
        final BmobQuery<Bike> query = new BmobQuery<>();
        query.findObjects(new FindListener<Bike>() {
            @Override
            public void done(List<Bike> list, BmobException e) {
                if (e == null) {
                    mBikeList = list;
                    mAdapter.replaceAll(mBikeList);
                } else {
                    Toast.makeText(MainActivity.this, "刷新失败！", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
        return query;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            StringBuilder builder = new StringBuilder();
            for (Bike bike:mBikeList) {
                builder.append(bike.getId()+":"+bike.getPassword()+"\n");
            }
            WXTextObject textObject = new WXTextObject();
            textObject.text = builder.toString();
            WXMediaMessage message = new WXMediaMessage();
            message.mediaObject = textObject;
            message.description = "共享数据单";
            // 构造一个Req
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
            req.message = message;
            req.scene = SendMessageToWX.Req.WXSceneSession;
            // 调用api接口发送数据到微信
            api.sendReq(req);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
