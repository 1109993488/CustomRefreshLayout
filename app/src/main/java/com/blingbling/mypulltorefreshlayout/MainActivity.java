package com.blingbling.mypulltorefreshlayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blingbling.refreshlayout.listener.OnRefreshListener;
import com.blingbling.refreshlayout.simple.SimpleRefreshLayout;


public class MainActivity extends AppCompatActivity implements OnRefreshListener {

    private SimpleRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        refreshLayout = (SimpleRefreshLayout) findViewById(R.id.swipe);

        refreshLayout.setOnRefreshListener(this);

        RecyclerView list = (RecyclerView) findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this));

        list.setAdapter(new RecyclerAdapter());

    }


    public void refresh(View view) {
        refreshLayout.autoRefresh();
    }

    public void complete(View view) {
        refreshLayout.stopRefresh(true);
    }

    public void enableTrue(View view) {
        refreshLayout.setCanRefresh(true);
    }

    public void enableFalse(View view) {
        refreshLayout.setCanRefresh(false);
    }

    public void simple(View view) {
        Class cls = com.blingbling.mypulltorefreshlayout.widget.SimpleRefreshLayout.class;
        Intent intent = new Intent(this, SimpleActivity.class);
        intent.putExtra(SimpleActivity.REFRESH_LAYOUT_CLASS, cls);
        startActivity(intent);
    }


    @Override public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                refreshLayout.stopRefresh(true);
            }
        }, 1000);
    }

    @Override public void onLoadMore() {
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                refreshLayout.stopLoadMore(false);
            }
        }, 1000);
    }
}
