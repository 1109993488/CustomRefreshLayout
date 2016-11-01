package com.blingbling.mypulltorefreshlayout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.blingbling.customrefreshlayoutlibrary.BaseRefreshLayout;
import com.blingbling.customrefreshlayoutlibrary.simple.SimpleRefreshLayout;
import com.blingbling.mypulltorefreshlayout.widget.Simple1RefreshLayout;
import com.blingbling.mypulltorefreshlayout.widget.Simple2RefreshLayout;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

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

        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
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

    @Override public void onClick(View view) {
        Class cls=null;
        switch (view.getId()){
            case R.id.btn1:
                cls= Simple1RefreshLayout.class;
                break;
            case R.id.btn2:
                cls= Simple2RefreshLayout.class;
                break;
        }
        if(cls!=null){
            Intent intent=new Intent(this,SimpleActivity.class);
            intent.putExtra(SimpleActivity.REFRESH_LAYOUT_CLASS,cls);
            startActivity(intent);
        }
    }
}
