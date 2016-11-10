package com.blingbling.refreshlayout.simple;


import android.content.Context;
import android.util.AttributeSet;

import com.blingbling.refreshlayout.BaseFooterView;
import com.blingbling.refreshlayout.BaseHeaderView;
import com.blingbling.refreshlayout.BaseRefreshLayout;


/**
 * Created by zhouweilong on 2016/10/19.
 */

public class SimpleRefreshLayout extends BaseRefreshLayout {

    public SimpleRefreshLayout(Context context) {
        super(context);
    }

    public SimpleRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override protected BaseHeaderView onCreateHeaderView() {
        return new SimpleHeaderView(getContext());
    }

    @Override protected BaseFooterView onCreateFooterView() {
        return new SimpleFooterView(getContext());
    }
}
