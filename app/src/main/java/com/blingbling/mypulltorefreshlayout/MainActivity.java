package com.blingbling.mypulltorefreshlayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.blingbling.refreshlayout.BaseRefreshLayout;
import com.blingbling.refreshlayout.simple.SimpleRefreshLayout;


public class MainActivity extends AppCompatActivity {

    private SimpleRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        refreshLayout= (SimpleRefreshLayout) findViewById(R.id.swipe);
        refreshLayout.setOnRefreshListener(new BaseRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                Log.e("TAG","refreshLayout-------------onRefresh");
            }
        });

    }


    public void refresh(View view) {
        refreshLayout.setRefreshing(true);
    }

    public void complete(View view) {
        refreshLayout.setRefreshing(false,true);
    }

    public void enableTrue(View view) {
        refreshLayout.setEnabled(true);
    }

    public void enableFalse(View view) {
        refreshLayout.setEnabled(false);
    }

}
