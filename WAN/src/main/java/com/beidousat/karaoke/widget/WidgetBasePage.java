package com.beidousat.karaoke.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.beidousat.karaoke.R;

/**
 * Created by J Wong on 2017/5/8.
 */

public class WidgetBasePage extends RelativeLayout implements View.OnClickListener {

    private View mRootView;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private Button mBtnRetry;

    public WidgetBasePage(Context context) {
        super(context);
        initView();
    }

    public WidgetBasePage(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public WidgetBasePage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mRootView = LayoutInflater.from(getContext()).inflate(R.layout.widget_base_page, this);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler);
        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.progress);
        mBtnRetry = (Button) mRootView.findViewById(R.id.btn_retry);
        mBtnRetry.setOnClickListener(this);
    }

    /**
     * loading 状态
     *
     * @param status 0:loading ; -1:fail; 1:s
     */
    public void setProgressStatus(int status) {
        switch (status) {
            case 0://
                mRecyclerView.setVisibility(GONE);
                mBtnRetry.setVisibility(GONE);
                mProgressBar.setVisibility(VISIBLE);
                break;
            case -1://fail
                mRecyclerView.setVisibility(GONE);
                mBtnRetry.setVisibility(VISIBLE);
                mProgressBar.setVisibility(INVISIBLE);
                break;
            case 1://success
                mRecyclerView.setVisibility(VISIBLE);
                mBtnRetry.setVisibility(GONE);
                mProgressBar.setVisibility(GONE);
                break;
        }
    }

    public void setLayoutManager(RecyclerView.LayoutManager layout) {
        mRecyclerView.setLayoutManager(layout);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration decor) {
        mRecyclerView.addItemDecoration(decor);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_retry:
                onRetry();
                break;
        }
    }

    public void onRetry() {

    }
}
