package com.beidousat.karaoke.ui.dlg;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.data.PrefData;
import com.beidousat.karaoke.widget.EditTextEx;
import com.beidousat.libbns.util.ListUtil;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libwidget.recycler.HorizontalDividerItemDecoration;
import com.beidousat.libwidget.recycler.VerticalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J Wong on 2017/05/08 15:52.
 */
public class DlgPassword extends BaseDialog implements View.OnTouchListener, View.OnClickListener {

    private RecyclerView mLvItems;
    private EditTextEx mEtPwdO, mEtPwd, mEtPwdC;
    private EditTextEx mEtFocus;
    private Activity mContext;

    public DlgPassword(Activity context) {
        super(context, R.style.MyDialog);
        init(context);
    }

    public void init(Activity context) {
        mContext = context;
        mContext = context;
        this.setContentView(R.layout.dlg_password);

        WindowManager.LayoutParams lp = getWindow().getAttributes();

        lp.width = 450;
        lp.height = 500;
        lp.gravity = Gravity.CENTER;

        getWindow().setAttributes(lp);

        mLvItems = (RecyclerView) findViewById(R.id.rv_items);
        mEtPwdO = (EditTextEx) findViewById(android.R.id.edit);

        mEtPwd = (EditTextEx) findViewById(android.R.id.input);
        mEtPwdC = (EditTextEx) findViewById(android.R.id.inputArea);
        findViewById(R.id.iv_close).setOnClickListener(this);

        mEtPwdO.setOnTouchListener(this);
        mEtPwd.setOnTouchListener(this);
        mEtPwdC.setOnTouchListener(this);

        init();

        mEtFocus = mEtPwdO;
        mEtFocus.setSelected(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                dismiss();
                break;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        mEtPwdO.setSelected(false);
        mEtPwd.setSelected(false);
        mEtPwdC.setSelected(false);

        if (view == mEtPwdO) {
            mEtFocus = mEtPwdO;
        } else if (view == mEtPwd) {
            mEtFocus = mEtPwd;
        } else if (view == mEtPwdC) {
            mEtFocus = mEtPwdC;
        }
        mEtFocus.setSelected(true);
        return false;
    }

    private void init() {

        HorizontalDividerItemDecoration horDivider = new HorizontalDividerItemDecoration.Builder(mContext.getApplicationContext())
                .color(Color.TRANSPARENT).size(12).margin(12, 12)
                .build();

        VerticalDividerItemDecoration verDivider = new VerticalDividerItemDecoration.Builder(mContext.getApplicationContext())
                .color(Color.TRANSPARENT).size(12).margin(12, 12)
                .build();

        mLvItems.setLayoutManager(new GridLayoutManager(mContext.getApplicationContext(), 3));
        mLvItems.addItemDecoration(verDivider);
        mLvItems.addItemDecoration(horDivider);

        AdapterNumber adapter = new AdapterNumber();
        mLvItems.setAdapter(adapter);

        adapter.setData(ListUtil.array2List(mContext.getResources().getStringArray(R.array.mng_pwd_texts)));


    }


    public class AdapterNumber extends RecyclerView.Adapter<AdapterNumber.ViewHolder> {

        private LayoutInflater mInflater;
        private List<String> mData = new ArrayList<String>();

        public AdapterNumber() {
            mInflater = LayoutInflater.from(mContext);
        }

        public void setData(List<String> data) {
            this.mData = data;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView tvKey;

            public ViewHolder(View view) {
                super(view);
            }
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = mInflater.inflate(R.layout.list_item_pop_keyboard, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(view);
            viewHolder.tvKey = (TextView) view.findViewById(android.R.id.button1);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final String keyText = mData.get(position);
            holder.tvKey.setText(keyText);
            holder.tvKey.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (position == getItemCount() - 1) {
                            String pwdO = mEtPwdO.getText().toString().trim();
                            String pwd = mEtPwd.getText().toString().trim();
                            String pwdC = mEtPwdC.getText().toString().trim();
                            String prefPwd = PrefData.getPassword(getContext());

                            if (TextUtils.isEmpty(pwdO)) {
                                Toast.makeText(getContext(), "请输入原密码！", Toast.LENGTH_LONG).show();
                                return;
                            }

                            if (TextUtils.isEmpty(pwd)) {
                                Toast.makeText(getContext(), "请输入密码！", Toast.LENGTH_LONG).show();
                                return;
                            }
                            if (TextUtils.isEmpty(pwdC)) {
                                Toast.makeText(getContext(), "请确认密码！", Toast.LENGTH_LONG).show();
                                return;
                            }
                            if (!prefPwd.equals(pwdO)) {
                                Toast.makeText(getContext(), "原密码不对！", Toast.LENGTH_LONG).show();
                                return;
                            }
                            if (!pwd.equals(pwdC)) {
                                Toast.makeText(getContext(), "设置密码和确认密码不一致！", Toast.LENGTH_LONG).show();
                                return;
                            }

                            PrefData.setPassword(getContext(), pwd);
                            Toast.makeText(getContext(), "设置密码成功！", Toast.LENGTH_LONG).show();

                            dismiss();

                        } else {
                            if (!TextUtils.isEmpty(keyText)) {
                                if (keyText.equals(mContext.getString(R.string.delete))) {
                                    String text = mEtFocus.getText().toString();
                                    if (!TextUtils.isEmpty(text)) {
                                        String txt = text.substring(0, text.length() - 1);
                                        mEtFocus.setText(txt);
                                    }
                                } else {
                                    mEtFocus.setText(mEtFocus.getText() + keyText);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Logger.e(getClass().getSimpleName(), e.toString());
                    }
                }
            });
        }
    }
}
