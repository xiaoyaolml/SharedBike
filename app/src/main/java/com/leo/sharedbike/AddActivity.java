package com.leo.sharedbike;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class AddActivity extends AppCompatActivity {
    public static final String TAG = "AddActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String search = getIntent().getStringExtra(MainActivity.TAG);
        final EditText etId = (EditText) findViewById(R.id.add_id);
        final EditText etPassword = (EditText) findViewById(R.id.add_password);
        etId.setText(search);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_save);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String id = etId.getText().toString();
                String password = etPassword.getText().toString();
                if (TextUtils.isEmpty(id) || TextUtils.isEmpty(password)) {
                    Toast.makeText(AddActivity.this, "内容不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }

                Bike bike = new Bike();
                bike.setId(id);
                bike.setPassword(password);
                bike.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            etId.setText("");
                            etPassword.setText("");
                            Snackbar.make(view, "保存成功！", Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(view, "保存失败！", Snackbar.LENGTH_LONG).show();
                            Log.i(TAG,"失败："+e.getMessage()+","+e.getErrorCode());
                        }
                    }
                });
            }
        });
    }

}
