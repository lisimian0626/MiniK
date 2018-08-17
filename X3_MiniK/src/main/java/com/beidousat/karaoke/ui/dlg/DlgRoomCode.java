package com.beidousat.karaoke.ui.dlg;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.data.PrefData;
import com.beidousat.karaoke.widget.EditTextEx;
import com.beidousat.libbns.net.socket.KBoxSocketHeart;
import com.beidousat.libbns.util.ListUtil;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libwidget.recycler.HorizontalDividerItemDecoration;
import com.beidousat.libwidget.recycler.VerticalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J Wong on 2017/05/08 11:52.
 */
public class DlgRoomCode extends BaseDialog implements View.OnClickListener {

    private TextView mTvRoomCode;
    private RecyclerView mLvItems;
    private EditTextEx mEtPwd;
    private Activity mContext;

    public DlgRoomCode(Activity context) {
        super(context, R.style.MyDialog);
        init(context);
    }

    public void init(Activity context) {
        mContext = context;
        mContext = context;
        this.setContentView(R.layout.dlg_room_code);

        WindowManager.LayoutParams lp = getWindow().getAttributes();

        lp.width = 450;
        lp.height = 450;
        lp.gravity = Gravity.CENTER;

        getWindow().setAttributes(lp);
        mLvItems = (RecyclerView) findViewById(R.id.rv_items);
        mEtPwd = (EditTextEx) findViewById(android.R.id.input);
        mTvRoomCode = (TextView) findViewById(R.id.tv_room_no);
        findViewById(R.id.iv_close).setOnClickListener(this);
        init();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                dismiss();
                break;
        }
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


        String roomCode = PrefData.getRoomCode(getContext().getApplicationContext());
        if (TextUtils.isEmpty(roomCode)) {
            mTvRoomCode.setText(getContext().getString(R.string.room_code_x, "未设置"));
        } else {
            mTvRoomCode.setText(getContext().getString(R.string.room_code_x, roomCode));
        }
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
                            String pwd = mEtPwd.getText().toString().trim();
                            if (!TextUtils.isEmpty(pwd)) {
                                PrefData.setRoomCode(getContext(), "B" + pwd);
                                String roomCode = PrefData.getRoomCode(getContext().getApplicationContext());
                                mTvRoomCode.setText(getContext().getString(R.string.room_code_x, roomCode));
                                dismiss();
                            } else {
                                Toast.makeText(getContext().getApplicationContext(), "请输入房间编号", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            if (!TextUtils.isEmpty(keyText)) {
                                if (keyText.equals(mContext.getString(R.string.delete))) {
                                    String text = mEtPwd.getText().toString();
                                    if (!TextUtils.isEmpty(text)) {
                                        String txt = text.substring(0, text.length() - 1);
                                        mEtPwd.setText(txt);
                                    }
                                } else {
                                    mEtPwd.setText(mEtPwd.getText() + keyText);
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
