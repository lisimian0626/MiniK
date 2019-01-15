package com.beidousat.karaoke.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.beidousat.karaoke.R;
import com.beidousat.karaoke.interf.KeyboardListener;
import com.beidousat.libbns.model.Common;
import com.beidousat.libbns.util.DensityUtil;
import com.beidousat.libbns.util.FragmentUtil;
import com.beidousat.libbns.util.ListUtil;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libwidget.recycler.HorizontalDividerItemDecoration;
import com.beidousat.libwidget.recycler.VerticalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J Wong on 2015/10/9 08:56.
 */
public class WidgetKeyboard extends LinearLayout implements View.OnClickListener, View.OnTouchListener, EditTextEx.OnEditTextIconClickListener {

    private View mRootView;
    private EditTextEx mEditText;
    private SelfAbsoluteLayout tablet;
    private View mTypeKeyboard, mTypeHandWriting;
    private RecyclerView mRvKeyboardLine1, mRvKeyboardLine2, mRvKeyboardLine3,mRvKeyboardLine4, mRvNumber, mRvWords, mRvWordCount;
    private WidgetTopTabs mTabInputType;

    private AdapterWord mAdtWord;
    private AdapterKeyboard AdtKeyboardLine1, AdtKeyboardLine2, AdtKeyboardLine3, AdtNumber;
    private AdapterSign AdtKeyboardLine4;
    private KeyboardListener mKeyboardListener;

    private String[] texts = new String[]{"", "", "", "", "", "", "", ""};

    private String mEnableText;

    private String mTextWord;

    private boolean tabletHaveWord = false;

    private HorizontalDividerItemDecoration horDivider;
    private VerticalDividerItemDecoration verDivider;

    private final static String TAG = WidgetKeyboard.class.getSimpleName();

    public WidgetKeyboard(Context context) {
        super(context);
        initView();
    }

    public WidgetKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        readAttr(attrs);
    }

    public WidgetKeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        readAttr(attrs);
    }


    public void setWords(final String texts) {
        mTextWord = texts;
        tablet.postDelayed(runnableTexts, 1000);
//        if (!TextUtils.isEmpty(texts)) {
//        } else {
//            resetWordsList();
//        }
    }

    private Runnable runnableTexts = new Runnable() {
        @Override
        public void run() {
            if (!TextUtils.isEmpty(mTextWord)) {
                if (!mTextWord.contains(",")) {
                    String[] text = new String[]{mTextWord};
                    setWords(text);
                } else {
                    String[] words = mTextWord.split(",");
                    setWords(words);
                }
            } else {
                resetWordsList();
            }
        }
    };

    private void setWords(String[] words) {
        if (words != null && words.length > 0) {
            String[] texts = new String[8];
            for (int i = 0; i < texts.length; i++) {
                texts[i] = words.length > i ? words[i] : "";
            }
            mAdtWord.setData(ListUtil.array2List(texts));
            mAdtWord.notifyDataSetChanged();
        } else {
            resetWordsList();
        }
    }

    public void setKeyboardKeyEnableText(String enableText) {
        mEnableText = enableText;
        Logger.i(getClass().getSimpleName(), "setKeyboardKeyEnableText :" + mEnableText);
        AdtKeyboardLine1.notifyDataSetChanged();
        AdtKeyboardLine2.notifyDataSetChanged();
        AdtKeyboardLine3.notifyDataSetChanged();
        AdtNumber.notifyDataSetChanged();
    }


    private void initView() {

        mRootView = LayoutInflater.from(getContext()).inflate(R.layout.widget_keyboard, this);

        mEditText = (EditTextEx) mRootView.findViewById(android.R.id.edit);
        mEditText.setOnEditTextIconClickListener(this);

        mTypeKeyboard = this.findViewById(R.id.type_keyboard);
        mTypeHandWriting = this.findViewById(R.id.type_handwriting);
//        mTypeNumber = this.findViewById(R.id.type_number);

        tablet = (SelfAbsoluteLayout) this.findViewById(R.id.tablet);
        findViewById(R.id.iv_del).setOnClickListener(this);
        findViewById(R.id.tv_key_del).setOnClickListener(this);

        mRvWordCount = (RecyclerView) this.findViewById(R.id.rv_words_count);
        mTabInputType = (WidgetTopTabs) this.findViewById(R.id.wtt_input_type);

        mRvKeyboardLine1 = (RecyclerView) this.findViewById(R.id.rv_keyboard_line1);
        mRvKeyboardLine2 = (RecyclerView) this.findViewById(R.id.rv_keyboard_line2);
        mRvKeyboardLine3 = (RecyclerView) this.findViewById(R.id.rv_keyboard_line3);
        mRvKeyboardLine4 = (RecyclerView) this.findViewById(R.id.rv_keyboard_line4);
        mRvNumber = (RecyclerView) this.findViewById(R.id.rv_number);

//        mRvNumberLine2 = (RecyclerView) this.findViewById(R.id.rv_number_line2);

        mRvWords = (RecyclerView) this.findViewById(R.id.rv_words);

//        findViewById(R.id.tv_del).setOnClickListener(this);
//        findViewById(R.id.tv_del_num).setOnClickListener(this);
//        findViewById(R.id.tv_del_write).setOnClickListener(this);

        int dividerWidth = DensityUtil.dip2px(getContext(), 2);
        int dividerWidthH = DensityUtil.dip2px(getContext(), 4);

        horDivider = new HorizontalDividerItemDecoration.Builder(getContext())
                .color(Color.TRANSPARENT).size(dividerWidthH).margin(dividerWidthH, dividerWidthH)
                .build();

        verDivider = new VerticalDividerItemDecoration.Builder(getContext())
                .color(Color.TRANSPARENT).size(dividerWidth).margin(dividerWidth, dividerWidth)
                .build();

        initKeyboard();

        initWordCount();

        initInputType();

//        initNumber();

        initHandWritingWord();

        resetWordsList();

        tablet.setOnWriteListener(new SelfAbsoluteLayout.WhiteListner() {
            @Override
            public void onWrite(String[] texts) {
                tabletHaveWord = true;
                if (texts != null && texts.length > 0) {
                    setWords(texts);
                } else {
                    resetWordsList();
                }
            }
        });
        tablet.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Logger.i(TAG, "onTouch : " + event.getAction());
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
//                        tablet.removeCallbacks(runnableAutoSelectWord);
                        break;
                    case MotionEvent.ACTION_MOVE:
//                        tablet.removeCallbacks(runnableAutoSelectWord);
                        break;
                    case MotionEvent.ACTION_UP:
//                        tablet.postDelayed(runnableAutoSelectWord, 3000);
                        break;
                }
                return false;
            }
        });


        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mEditText.setSelection(s.length());
                if (mKeyboardListener != null) {
                    if (!(mIsCleanText && TextUtils.isEmpty(s)))
                        mKeyboardListener.onInputTextChanged(s.toString());
                }
            }
        });

        mEditText.setOnClickListener(this);
        mEditText.setOnTouchListener(this);
        mEditText.setInputType(mEditText.getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

    }


//    private Runnable runnableAutoSelectWord = new Runnable() {
//        @Override
//        public void run() {
//            try {
//                String text;
//                if (mAdtWord != null && mAdtWord.getItemCount() > 0 && (text = mAdtWord.getItem(0)) != null && !TextUtils.isEmpty(text)) {
//                    addText(text);
//                    tablet.reset_recognize();
//                    tabletHaveWord = false;
//                }
//            } catch (Exception e) {
//                Logger.i(TAG, "e:" + e.toString());
//            }
//        }
//    };

    private void initHandWritingWord() {

        HorizontalDividerItemDecoration horDivider = new HorizontalDividerItemDecoration.Builder(getContext())
                .color(Color.TRANSPARENT).size(4).margin(4, 4)
                .build();

        VerticalDividerItemDecoration verDivider = new VerticalDividerItemDecoration.Builder(getContext())
                .color(Color.TRANSPARENT).size(1).margin(1, 1)
                .build();

        mRvWords.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mRvWords.addItemDecoration(horDivider);
        mRvWords.addItemDecoration(verDivider);
        mAdtWord = new AdapterWord();
        mRvWords.setAdapter(mAdtWord);
    }


    private void initKeyboard() {
        LinearLayoutManager layoutManagerNum = new LinearLayoutManager(getContext());
        layoutManagerNum.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvNumber.setLayoutManager(layoutManagerNum);
        mRvNumber.addItemDecoration(verDivider);
        AdtNumber = new AdapterKeyboard();
        mRvNumber.setAdapter(AdtNumber);
        AdtNumber.setData(ListUtil.array2List(getResources().getStringArray(R.array.keyboard_number)));

        LinearLayoutManager layoutManager3 = new LinearLayoutManager(getContext());
        layoutManager3.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvKeyboardLine3.setLayoutManager(layoutManager3);
        mRvKeyboardLine3.addItemDecoration(verDivider);
        AdtKeyboardLine3 = new AdapterKeyboard();
        mRvKeyboardLine3.setAdapter(AdtKeyboardLine3);
        AdtKeyboardLine3.setData(ListUtil.array2List(getResources().getStringArray(R.array.keyboard_keys_line3)));
//        if (Common.isEn) {
//            AdtKeyboardLine3.setData(ListUtil.array2List(getResources().getStringArray(R.array.keyboard_keys_line3s)));
//        } else {
//            AdtKeyboardLine3.setData(ListUtil.array2List(getResources().getStringArray(R.array.keyboard_keys_line3)));
//        }


        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext());
        layoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvKeyboardLine2.setLayoutManager(layoutManager2);
        mRvKeyboardLine2.addItemDecoration(verDivider);
        AdtKeyboardLine2 = new AdapterKeyboard();
        mRvKeyboardLine2.setAdapter(AdtKeyboardLine2);
        AdtKeyboardLine2.setData(ListUtil.array2List(getResources().getStringArray(R.array.keyboard_keys_line2)));


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvKeyboardLine1.setLayoutManager(layoutManager);
        mRvKeyboardLine1.addItemDecoration(verDivider);
        AdtKeyboardLine1 = new AdapterKeyboard();
        mRvKeyboardLine1.setAdapter(AdtKeyboardLine1);
        AdtKeyboardLine1.setData(ListUtil.array2List(getResources().getStringArray(R.array.keyboard_keys_line1)));

        if(Common.isEn){
            mRvKeyboardLine4.setVisibility(VISIBLE);
            LinearLayoutManager layoutManager4 = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            mRvKeyboardLine4.setLayoutManager(layoutManager4);
            mRvKeyboardLine4.addItemDecoration(verDivider);
            AdtKeyboardLine4 = new AdapterSign();
            mRvKeyboardLine4.setAdapter(AdtKeyboardLine4);
            AdtKeyboardLine4.setData(ListUtil.array2List(getResources().getStringArray(R.array.keyboard_keys_line4)));
        }else{
            mRvKeyboardLine4.setVisibility(GONE);
        }

    }

    private void initNumber() {

        HorizontalDividerItemDecoration horDivider = new HorizontalDividerItemDecoration.Builder(getContext())
                .color(Color.TRANSPARENT).size(4).margin(4, 4)
                .build();
        VerticalDividerItemDecoration verDivider = new VerticalDividerItemDecoration.Builder(getContext())
                .color(Color.TRANSPARENT).size(12).margin(12, 12)
                .build();
        mRvNumber.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mRvNumber.addItemDecoration(verDivider);
        mRvNumber.addItemDecoration(horDivider);

        AdapterNumber adapter = new AdapterNumber();
        mRvNumber.setAdapter(adapter);
        adapter.setData(ListUtil.array2List(getResources().getStringArray(R.array.keyboard_numbers)));
    }


    private void initWordCount() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        mRvWordCount.setLayoutManager(layoutManager);
        mRvWordCount.addItemDecoration(verDivider);
        mRvWordCount.addItemDecoration(horDivider);
        AdapterWordCount adapter = new AdapterWordCount();
        mRvWordCount.setAdapter(adapter);
        adapter.setData(ListUtil.array2List(getResources().getStringArray(R.array.keyboard_word_count)));


    }

    private boolean mNeedDisableKey = true;

    public void needDisableKey(boolean needDisableKey) {
        mNeedDisableKey = needDisableKey;
    }

    public void showLackSongButton(boolean show) {
        mTabInputType.setRightTabShow(show);
    }

    private void initInputType() {

        mTabInputType.setRightClickSelect(false);
        if (Common.isEn) {
            mTabInputType.setLeftTabs(R.array.keyboard_input_types_en);
        } else {
            mTabInputType.setLeftTabs(R.array.keyboard_input_types);
        }

        mTabInputType.setLeftTabClickListener(new WidgetTopTabs.OnTabClickListener() {
            @Override
            public void onTabClick(int position) {
                setInputType(position);
            }
        });

//        mTabInputType.setRightTabs(new String[]{getContext().getString(R.string.lack_song)});
//        mTabInputType.setRightTabClickListener(new WidgetTopTabs.OnTabClickListener() {
//            @Override
//            public void onTabClick(int position) {
////                FragmentUtil.addFragment(new FmSongFeedback(), false);
//            }
//        });
    }

    private void readAttr(AttributeSet attrs) {

        boolean showInputType = true;
        boolean showWordCount = true;
        int focusType = 0;

        String mStrHint = null;

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.WidgetKeyboard);

            mStrHint = a.getString(R.styleable.WidgetKeyboard_keyboard_input_hint);

            showInputType = a.getBoolean(R.styleable.WidgetKeyboard_show_input_type, true);

            focusType = a.getInt(R.styleable.WidgetKeyboard_keyboard_focus_input_type, 0);

            showWordCount = a.getBoolean(R.styleable.WidgetKeyboard_show_word_count, true);

        }

        if (mStrHint != null) {
            setHintText(mStrHint);
        }


        if (Common.isEn) {
            showWordCount(false);
            showInputType(false);
        } else {
            showWordCount(showWordCount);
            showInputType(showInputType);
            setInputType(focusType);
        }


    }

    public void showWordCount(boolean show) {
        mRvWordCount.setVisibility(show ? VISIBLE : GONE);
    }

    private void showInputType(boolean show) {
        mTabInputType.setVisibility(show ? VISIBLE : INVISIBLE);
    }

    public void setHintText(String hint) {
        mEditText.setHint(hint);
    }

    public void setInputText(String text) {
        mEditText.setText(text);
    }


    private void resetWordsList() {
        mAdtWord.setData(ListUtil.array2List(texts));
        mAdtWord.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case android.R.id.edit:
                break;
            case R.id.tv_key_del:
            case R.id.iv_del:
                deleteChar();
//                tablet.removeCallbacks(runnableAutoSelectWord);
                tablet.removeCallbacks(runnableTexts);
                break;
        }
    }


    public void setInputTextChangedListener(KeyboardListener listener) {
        this.mKeyboardListener = listener;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view == mEditText) {
            EditText edittext = (EditText) view;
            int inType = edittext.getInputType();       // Backup the input type
            edittext.setInputType(InputType.TYPE_NULL); // Disable standard keyboard
            edittext.onTouchEvent(motionEvent);               // Call native handler
            edittext.setInputType(inType);              // Restore input type
            return true; // Consume touch event
        }
        return false;
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            deleteChar();
            startDelChar();
            super.handleMessage(msg);
        }
    };

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.sendEmptyMessage(0);
        }
    };

    private void startDelChar() {
        if (!TextUtils.isEmpty(mEditText.getText())) {
            handler.postDelayed(runnable, 200);
        }
    }

    private void stopDelChar() {
        handler.removeCallbacks(runnable);
    }

    public void deleteChar() {
        if (tabletHaveWord) {
            tablet.reset_recognize();
            tabletHaveWord = false;
        } else {
            try {
                String text = mEditText.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    String txt = text.substring(0, text.length() - 1);
                    mEditText.setText(txt);
                }
            } catch (Exception e) {
                Logger.e("WidgetKeyBoard", e.toString());
            }
        }
    }


    public void setEditTextVisible(boolean show) {
        mEditText.setVisibility(show ? VISIBLE : GONE);
    }


    public class BaseAdapter extends RecyclerView.Adapter<ViewHolder> {

        LayoutInflater mInflater;
        List<String> mData = new ArrayList<String>();

        public BaseAdapter() {
            mInflater = LayoutInflater.from(getContext());
        }

        public void setData(List<String> data) {
            this.mData = data;
        }

        public String getItem(int position) {
            if (mData == null) {
                return null;
            } else {
                return mData.get(position);
            }
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return null;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvKey;

        public ViewHolder(View view) {
            super(view);
        }
    }

    public class AdapterWord extends BaseAdapter {

        public AdapterWord() {
            super();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = mInflater.inflate(R.layout.list_item_handwriting, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(view);
            viewHolder.tvKey = (TextView) view.findViewById(android.R.id.text1);
            return viewHolder;
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final String keyText = mData.get(position);
            holder.tvKey.setText(keyText);
            holder.tvKey.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
//                    tablet.removeCallbacks(runnableAutoSelectWord);
                    tablet.removeCallbacks(runnableTexts);
                    return false;
                }
            });
            holder.tvKey.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (!TextUtils.isEmpty(keyText)) {
                            addText(keyText);
                            tablet.reset_recognize();
                            tabletHaveWord = false;
                        }
                    } catch (Exception e) {
                        Logger.e("WidgetKeyBoard", e.toString());
                    }
                }
            });
        }
    }


    public class AdapterKeyboard extends BaseAdapter {

        public AdapterKeyboard() {
            super();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = mInflater.inflate(R.layout.list_item_keyboard_key, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(view);
            viewHolder.tvKey = (TextView) view.findViewById(android.R.id.button1);
            return viewHolder;
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final String keyText = mData.get(position);
            holder.tvKey.setText(keyText);
            final boolean isEnable = mIsCleanText || TextUtils.isEmpty(mEditText.getText())
                    || keyText.equals(getContext().getString(R.string.delete))
                    || (!TextUtils.isEmpty(mEnableText) && (mEnableText.contains(keyText)
                    || mEnableText.contains(keyText.toLowerCase())));
            holder.tvKey.setEnabled(mNeedDisableKey ? isEnable : true);
            holder.tvKey.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (!TextUtils.isEmpty(keyText)) {
                            if (keyText.equals(getContext().getString(R.string.delete))) {
                                deleteChar();
                            }else {
                                addText(keyText);
                            }
                        }
                    } catch (Exception e) {
                        Logger.e("WidgetKeyBoard", e.toString());
                    }
                }
            });
        }
    }


    public class AdapterWordCount extends BaseAdapter {

        private int mFocusItemPs = 0;

        public AdapterWordCount() {
            super();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = mInflater.inflate(R.layout.list_item_keyboard_word_count, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(view);
            viewHolder.tvKey = (TextView) view.findViewById(android.R.id.button1);
            return viewHolder;
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final String keyText = mData.get(position);
            holder.tvKey.setText(keyText);
            holder.tvKey.setSelected(mFocusItemPs == position);
            holder.tvKey.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mFocusItemPs = position;
                    if (mKeyboardListener != null) {
                        mKeyboardListener.onWordCountChanged(mFocusItemPs);
                    }
                    notifyDataSetChanged();
                }
            });
        }
    }

    public class AdapterNumber extends BaseAdapter {

        public AdapterNumber() {
            super();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = mInflater.inflate(R.layout.list_item_keyboard_number, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(view);
            viewHolder.tvKey = (TextView) view.findViewById(android.R.id.button1);
            return viewHolder;
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final String keyText = mData.get(position);
            holder.tvKey.setText(keyText);
            holder.tvKey.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (keyText.equals(getContext().getString(R.string.delete))) {
                            deleteChar();
                        } else {
                            addText(keyText);
                        }
                    } catch (Exception e) {
                        Logger.e("WidgetKeyBoard", e.toString());
                    }
                }
            });
        }
    }

    public class AdapterSign extends BaseAdapter {

        public AdapterSign() {
            super();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = mInflater.inflate(R.layout.list_item_keyboard_sign, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(view);
            viewHolder.tvKey = (TextView) view.findViewById(android.R.id.button1);
            return viewHolder;
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final String keyText = mData.get(position);
            holder.tvKey.setText(keyText);
            final boolean isEnable = mIsCleanText || TextUtils.isEmpty(mEditText.getText())
                    || keyText.equals(getContext().getString(R.string.delete))
                    || (!TextUtils.isEmpty(mEnableText) && (mEnableText.contains(keyText)
                    || mEnableText.contains(keyText.toLowerCase())));
            holder.tvKey.setEnabled(mNeedDisableKey ? isEnable : true);
            holder.tvKey.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (!TextUtils.isEmpty(keyText)) {
                            if (keyText.toLowerCase().trim().equals("space")) {
                                addText(" ");
                            }
                        }
                    } catch (Exception e) {
                        Logger.e("WidgetKeyBoard", e.toString());
                    }
                }
            });
        }
    }
    /**
     * @param inputType 0:keyboard 1:write 2:number
     */
    public void setInputType(int inputType) {
        mTabInputType.setLeftTabFocus(inputType);

        switch (inputType) {
            case 0:
                mTypeKeyboard.setVisibility(VISIBLE);
                mTypeHandWriting.setVisibility(GONE);
                tablet.setVisibility(GONE);
//                mTypeNumber.setVisibility(GONE);
                break;
            case 1:
                mTypeKeyboard.setVisibility(GONE);
                mTypeHandWriting.setVisibility(VISIBLE);
                tablet.setVisibility(VISIBLE);
//                mTypeNumber.setVisibility(GONE);
                break;
            case 2:
                mTypeKeyboard.setVisibility(GONE);
                mTypeHandWriting.setVisibility(GONE);
                tablet.setVisibility(GONE);
//                mTypeNumber.setVisibility(VISIBLE);
                break;
            default:
                mTypeKeyboard.setVisibility(VISIBLE);
                mTypeHandWriting.setVisibility(GONE);
                tablet.setVisibility(GONE);
//                mTypeNumber.setVisibility(GONE);
                break;
        }
    }

    private boolean mIsCleanText = false;

    public void addText(String text) {
        mEditText.setText(mEditText.getText() + text);
    }

    public void setCleanText(boolean cleanText) {
        mIsCleanText = cleanText;
        if (mIsCleanText) {
            setWords("");
            setKeyboardKeyEnableText("");
            setText("");
        }
    }

    public void setText(CharSequence text) {
        if (!TextUtils.equals(text, mEditText.getText())) {
            mEditText.setText(text);
        }
    }

    @Override
    public void onIconClick(View view, MotionEvent event) {
        mIsCleanText = false;
        if (mKeyboardListener != null) {
            mKeyboardListener.onInputTextChanged("");
        }
    }
}
