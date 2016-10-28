package com.blingbling.mypulltorefreshlayout.swipe;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by BlingBling on 2016/10/28.
 */

public class SimpleRefreshLayout extends SwipeRefreshLayout{
    public SimpleRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override protected View getHeaderView() {
        return new HeaderView(getContext());
    }
}
