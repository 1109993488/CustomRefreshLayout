package com.blingbling.mypulltorefreshlayout;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;

import com.blingbling.customrefreshlayoutlibrary.BaseRefreshLayout;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by BlingBling on 2016/11/1.
 */

public class SimpleActivity extends AppCompatActivity implements BaseRefreshLayout.OnRefreshListener{

    public static final String REFRESH_LAYOUT_CLASS = "REFRESH_LAYOUT_CLASS";
    private BaseRefreshLayout refreshLayout;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Class cls= (Class) getIntent().getSerializableExtra(REFRESH_LAYOUT_CLASS);
        refreshLayout=create(cls,this);
        setContentView(refreshLayout);

        LayoutInflater.from(this).inflate(R.layout.view_simple_list,refreshLayout);
        refreshLayout.setOnRefreshListener(this);
    }

    @Override public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                refreshLayout.setRefreshing(false);
            }
        },1000);
    }

    private BaseRefreshLayout create(Class cls, Context context){
        try {
            return (BaseRefreshLayout) cls.getConstructor(Context.class).newInstance(this);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}
