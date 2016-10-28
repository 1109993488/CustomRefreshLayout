package com.blingbling.refreshlayout.simple;

import android.content.Context;
import android.util.AttributeSet;

import com.blingbling.refreshlayout.BaseHeaderView;
import com.blingbling.refreshlayout.BaseRefreshLayout;


/**
 * Created by BlingBling on 2016/10/28.
 */

public class SimpleRefreshLayout extends BaseRefreshLayout {

    public SimpleRefreshLayout(Context context) {
        super(context);
    }

    public SimpleRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override protected boolean scrollHeaderAndContent() {
        return true;
    }

    @Override protected BaseHeaderView onCreateHeaderView() {
        return new SimpleHeaderView(getContext());
    }
}
