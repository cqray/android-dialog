package cn.cqray.android.dialog;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

/**
 * 消息对话框
 * @author Cqray
 */
@SuppressWarnings("unchecked")
public class AlterDialog<T extends AlterDialog<T>> extends BaseDialog<T> {

    private View mDividerView;
    private TextView mTitleView;
    private FrameLayout mContentView;
    private LinearLayout mBottomView;
    /** 是否是默认背景 **/
    private boolean mDefaultButtonBackground = true;
    /** 按钮监听事件 **/
    private OnBtnClickListener[] mButtonListeners;
    /** 内容间隔 **/
    private final MutableLiveData<Float> mContentPadding = new MutableLiveData<>();
    /** 按钮文本 **/
    private final MutableLiveData<String[]> mButtonTexts = new MutableLiveData<>();
    /** 按钮文本大小 **/
    private final MutableLiveData<Float> mButtonTextSize = new MutableLiveData<>();
    /** 按钮文本常规颜色 **/
    private final MutableLiveData<int[]> mButtonTextColors = new MutableLiveData<>();
    /** 按钮文本按下颜色 **/
    private final MutableLiveData<int[]> mButtonTextColors2 = new MutableLiveData<>();
    /** 按钮背景资源 **/
    private final MutableLiveData<int[]> mButtonDrawableResIds = new MutableLiveData<>();
    /** 按钮背景资源 **/
    private final MutableLiveData<Drawable[]> mButtonDrawables = new MutableLiveData<>();
    /** 标题程序块 **/
    protected final TextViewModule mTitleModule;
    /** 分割线程序块 **/
    protected final ViewModule<View> mDividerModule;

    public AlterDialog(FragmentActivity act) {
        super(act);
        mTitleModule = new TextViewModule(act);
        mDividerModule = new ViewModule<>(act);
        gravityCenter();
    }

    public AlterDialog(Fragment fragment) {
        super(fragment);
        mTitleModule = new TextViewModule(fragment);
        mDividerModule = new ViewModule<>(fragment);
        gravityCenter();
    }

    @Override
    public void onCreating(@Nullable Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);
        super.setContentView(R.layout._dlg_alter_layout);
        mTitleView = findViewById(R.id._dlg_title);
        mDividerView = findViewById(R.id._dlg_divider);
        mContentView = findViewById(R.id._dlg_content);
        mBottomView = findViewById(R.id._dlg_bottom);
        // 订阅标题、分割线属性
        mTitleModule.observe(this, mTitleView);
        mDividerModule.observe(this, mDividerView);
        mTitleModule.observeVisibility(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                mTitleView.setVisibility(integer);
                mDividerView.setVisibility(integer);
            }
        });
        // 设置内容间隔
        mContentPadding.observe(this, new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                setContentPadding();
            }
        });
        // 设置内容间隔
        setContentPadding();
        // 初始化按钮相关的LiveData
        initButtonLiveData();
    }

    @Override
    public void setContentView(View view) {
        mContentView.removeAllViews();
        mContentView.addView(view);
    }

    @Override
    public void setContentView(int layoutResId) {
        View view = LayoutInflater.from(requireContext()).inflate(layoutResId, mContentView, false);
        mContentView.removeAllViews();
        mContentView.addView(view);
    }

    public T title(@StringRes int resId) {
        mTitleModule.setText(resId);
        return (T) this;
    }

    public T title(CharSequence text) {
        mTitleModule.setText(text);
        return (T) this;
    }

    public T titleHeight(float height) {
        mTitleModule.setHeight(height);
        return (T) this;
    }

    public T titleCenter() {
        mTitleModule.setGravity(Gravity.CENTER);
        return (T) this;
    }

    public T titleVisible(boolean visible) {
        mTitleModule.setVisibility(visible ? View.VISIBLE : View.GONE);
        return (T) this;
    }

    public T titlePadding(float padding) {
        mTitleModule.setPadding(padding, 0, padding, 0);
        return (T) this;
    }

    public T titleTextColor(int color) {
        mTitleModule.setTextColor(color);
        return (T) this;
    }

    public T titleTextColor(String color) {
        mTitleModule.setTextColor(color);
        return (T) this;
    }

    public T titleTextSize(float size) {
        mTitleModule.setTextSize(size);
        return (T) this;
    }

    public T titleTextBold(boolean bold) {
        mTitleModule.setTextBold(bold);
        return (T) this;
    }

    public T dividerHeight(float height) {
        mDividerModule.setHeight(height);
        return (T) this;
    }

    public T dividerColor(int color) {
        mDividerModule.setBackgroundColor(color);
        return (T) this;
    }

    public T dividerColor(String color) {
        mDividerModule.setBackgroundColor(color);
        return (T) this;
    }

    public T contentPadding(float padding) {
        mContentPadding.postValue(padding);
        return (T) this;
    }

    public T buttonText(String... texts) {
        mButtonTexts.postValue(texts);
        return (T) this;
    }

    public T buttonBackground(Drawable... drawables) {
        mDefaultButtonBackground = false;
        mButtonDrawables.postValue(drawables == null ? new Drawable[0] : drawables);
        return (T) this;
    }

    public T buttonBackgroundResource(@DrawableRes int... ids) {
        mDefaultButtonBackground = (ids == null || ids.length == 0)
                || ids[ids.length - 1] == R.drawable.dialog_button_background;
        mButtonDrawableResIds.postValue(ids == null ? new int[0] : ids);
        return (T) this;
    }

    public T buttonClick(OnBtnClickListener... listeners) {
        mButtonListeners = listeners;
        return (T) this;
    }

    public T buttonTextColor(int... colors) {
        mButtonTextColors.postValue(colors == null ? new int[0] : colors);
        return (T) this;
    }

    public T buttonTextPressColor(int... colors) {
        mButtonTextColors2.postValue(colors == null ? new int[0] : colors);
        return (T) this;
    }

    public T buttonTextSize(float size) {
        mButtonTextSize.postValue(size);
        return (T) this;
    }

    /**
     * 初始化按钮相关LiveData
     */
    private void initButtonLiveData() {
        mButtonTexts.observe(this, new Observer<String[]>() {
            @Override
            public void onChanged(String[] texts) {
                setButtonText();
            }
        });
        mButtonTextSize.observe(this, new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                setButtonTextSize();
            }
        });
        mButtonTextColors.observe(this, new Observer<int[]>() {
            @Override
            public void onChanged(int[] ints) {
                setButtonTextColor();
            }
        });
        mButtonTextColors2.observe(this, new Observer<int[]>() {
            @Override
            public void onChanged(int[] ints) {
                setButtonTextColor();
            }
        });
        mButtonDrawableResIds.observe(this, new Observer<int[]>() {
            @Override
            public void onChanged(int[] ints) {
                Drawable[] drawables = new Drawable[ints.length];
                for (int i = 0; i < ints.length; i++) {
                    drawables[i] = ContextCompat.getDrawable(requireContext(), ints[i]);
                }
                mButtonDrawables.setValue(drawables);
            }
        });
        mButtonDrawables.observe(this, new Observer<Drawable[]>() {
            @Override
            public void onChanged(Drawable[] drawables) {
                setButtonBackground();
                setContentPadding();
            }
        });
    }

    /**
     * 设置内容间隔
     */
    private void setContentPadding() {
        Float value = mContentPadding.getValue();
        int padding = value == null ? getResources().getDimensionPixelSize(R.dimen.content) : toPix(value);
        int count = mBottomView.getChildCount();
        int half = mDefaultButtonBackground ? padding / 2 : padding;
        mContentView.setPadding(padding, padding, padding, count > 0 ? padding : half);
        mBottomView.setPadding(padding, 0, padding, half);
    }

    /**
     * 设置按钮文本
     */
    private void setButtonText() {
        // 清空控件
        mBottomView.removeAllViews();
        String [] texts = mButtonTexts.getValue();
        if (texts == null || texts.length == 0) {
            return;
        }
        // 监听事件
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 控件索引
                int index = mBottomView.indexOfChild(v);
                // 获取对应的监听事件
                OnBtnClickListener listener1 = mButtonListeners == null ||
                        mButtonListeners.length <= index ?
                        null : mButtonListeners[index];
                // 回调
                if (listener1 != null) {
                    listener1.onClick();
                }
            }
        };
        // 添加控件
        for (String text : texts) {
            TextView tv = new TextView(requireContext());
            tv.setText(text);
            tv.setFocusable(true);
            tv.setClickable(true);
            tv.setOnClickListener(listener);
            mBottomView.addView(tv);
        }
        // 设置按钮文本文字颜色
        setButtonTextColor();
        // 设置按钮文本文字大小
        setButtonTextSize();
        // 设置按钮文本背景
        setButtonBackground();
        // 设置内容间隔
        setContentPadding();
    }

    /**
     * 设置按钮文本文字大小
     */
    private void setButtonTextSize() {
        Float value = mButtonTextSize.getValue();
        int size = value == null ? getResources().getDimensionPixelSize(R.dimen.body) : toPix(value);
        int count = mBottomView.getChildCount();
        for (int i = 0; i < count; i++) {
            // 获取文本控件
            TextView tv = (TextView) mBottomView.getChildAt(i);
            // 设置文本大小
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
            // 获取间隔
            int p = (int) (size / 1.8f);
            // 设置间隔
            tv.setPadding(p, p / 2, p, p / 2);
            // 设置外部间隔
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -2);
            if (i == count - 1) {
                // 如果使用的默认背景，设置右间隔
                params.rightMargin = mDefaultButtonBackground ? -p : 0;
            } else {
                // 如果没有使用的默认背景，设置右间隔
                params.rightMargin = !mDefaultButtonBackground ? p : 0;
            }
            tv.setLayoutParams(params);
        }
    }

    /**
     * 设置按钮文本颜色
     */
    private void setButtonTextColor() {
        int count = mBottomView.getChildCount();
        for (int i = 0; i < count; i++) {
            TextView tv = (TextView) mBottomView.getChildAt(i);
            tv.setTextColor(getButtonTextColor(mBottomView.getContext(), i));
        }
    }

    /**
     * 设置按钮背景
     */
    private void setButtonBackground() {
        int count = mBottomView.getChildCount();
        for (int i = 0; i < count; i++) {
            TextView tv = (TextView) mBottomView.getChildAt(i);
            ViewCompat.setBackground(tv, getButtonBackground(i));
        }
    }

    /**
     * 获取按钮背景
     */
    private Drawable getButtonBackground(int index) {
        Drawable[] drawables = mButtonDrawables.getValue();
        int drawableCount = drawables == null ? 0 : drawables.length;
        Drawable last = drawableCount == 0 ? null : drawables[drawableCount - 1];
        return drawableCount == 0 || index >= drawableCount ? last : drawables[index];
    }

    /**
     * 获取按钮文字颜色
     */
    @NonNull
    private ColorStateList getButtonTextColor(@NonNull Context context, int index) {
        int[] defColors = mButtonTextColors.getValue();
        int[] pressColors = mButtonTextColors2.getValue();
        int defCount = defColors == null ? 0 : defColors.length;
        int pressCount = pressColors == null ? 0 : pressColors.length;
        // 最后的颜色
        int lastDef = defCount == 0 ? ContextCompat.getColor(context, R.color.text) : defColors[defCount - 1];
        // 最后的按下颜色
        int lastPress = pressCount == 0 ? 0 : pressColors[pressCount - 1];
        // 获取相应的颜色
        int defColor = defCount == 0 || index >= defCount ? lastDef : defColors[index];
        int pressColor = defCount == 0 || index >= pressCount ? lastPress : pressColors[index];
        // 获取颜色
        return getColorStateList(defColor, pressColor);
    }

    /**
     * 获取颜色
     */
    @NonNull
    private ColorStateList getColorStateList(int defColor, int pressColor) {
        if (pressColor == 0) {
            return ColorStateList.valueOf(defColor);
        }
        int[] colors = new int[]{pressColor, defColor};
        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_pressed};
        states[1] = new int[]{0};
        return new ColorStateList(states, colors);
    }

}
