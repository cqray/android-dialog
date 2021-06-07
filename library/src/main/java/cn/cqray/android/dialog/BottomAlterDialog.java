package cn.cqray.android.dialog;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;

import cn.cqray.android.dialog.amin.SlideBottomIn;
import cn.cqray.android.dialog.amin.SlideBottomOut;

/**
 * 消息对话框
 * @author Cqray
 */
@SuppressWarnings("unchecked")
public class BottomAlterDialog<T extends BottomAlterDialog<T>> extends BaseDialog<T> {

    /** 文字缩放比例，用来计算间隔 **/
    private static final float SIZE_SCALE = 1.8f;

    private View mDividerView;
    private TextView mTitleView;
    private TextView mStartView;
    private TextView mEndView;
    private FrameLayout mContentView;
    private boolean mTitleCenter;
    private boolean mDefaultStartBackground = true;
    private boolean mDefaultEndBackground = true;
    private List<OnBtnClickListener> mLeftListeners = new ArrayList<>();
    private List<OnBtnClickListener> mRightListeners = new ArrayList<>();

    /** 标题间隔 **/
    private final MutableLiveData<float[]> mTitlePadding = new MutableLiveData<>();
    /** 内容内部间隔 **/
    private final MutableLiveData<float[]> mContentPadding = new MutableLiveData<>();
    /** 标题文本代码块 **/
    protected final TextViewModule mTitleModule;
    /** 标题左边边按钮代码块 **/
    protected final TextViewModule mStartModule;
    /** 标题右边按钮代码块 **/
    protected final TextViewModule mEndModule;
    /** 分割线代码块 **/
    protected final ViewModule<View> mDividerModule;

    public BottomAlterDialog(FragmentActivity act) {
        super(act);
        mTitleModule = new TextViewModule(act);
        mStartModule = new TextViewModule(act);
        mEndModule = new TextViewModule(act);
        mDividerModule = new ViewModule<>(act);
        gravityBottom().showAnimator(new SlideBottomIn()).dismissAnimator(new SlideBottomOut());
    }

    public BottomAlterDialog(Fragment fragment) {
        super(fragment);
        mTitleModule = new TextViewModule(fragment);
        mStartModule = new TextViewModule(fragment);
        mEndModule = new TextViewModule(fragment);
        mDividerModule = new ViewModule<>(fragment);
        gravityBottom().showAnimator(new SlideBottomIn()).dismissAnimator(new SlideBottomOut());
    }

    @Override
    public void onCreating(@Nullable Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);
        super.setContentView(R.layout._dlg_bottom_alter_layout);
        mTitleView = findViewById(R.id._dlg_title);
        mStartView = findViewById(R.id._dlg_left);
        mEndView = findViewById(R.id._dlg_right);
        mDividerView = findViewById(R.id._dlg_divider);
        mContentView = findViewById(R.id._dlg_content);
        mStartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (OnBtnClickListener listener : mLeftListeners) {
                    listener.onClick();
                }
            }
        });
        mEndView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (OnBtnClickListener listener : mRightListeners) {
                    listener.onClick();
                }
            }
        });
        mContentPadding.observe(this, new Observer<float[]>() {
            @Override
            public void onChanged(float[] floats) {
                mContentView.setPadding(toPix(floats[0]), toPix(floats[1]), toPix(floats[2]), toPix(floats[3]));
            }
        });
        initTitleModule();
    }

    /**
     * 初始化标题相关代码块
     */
    private void initTitleModule() {
        mTitleModule.observe(this, mTitleView);
        mStartModule.observe(this, mStartView);
        mEndModule.observe(this, mEndView);
        mDividerModule.observe(this, mDividerView);
        mTitleModule.observeVisibility(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                View parent = (View) mTitleView.getParent();
                parent.setVisibility(integer);
                mDividerView.setVisibility(integer);
            }
        });
        mTitleModule.observeGravity(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                setTitleCenter(integer);
                setTitlePadding();
            }
        });
        mStartModule.observeGravity(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                mStartView.setVisibility(integer);
                setTitlePadding();
            }
        });
        mEndModule.observeGravity(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                mEndView.setVisibility(integer);
                setTitlePadding();
            }
        });
        mTitlePadding.observe(this, new Observer<float[]>() {
            @Override
            public void onChanged(float[] floats) {
                setTitlePadding();
            }
        });
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

    public T title(CharSequence title) {
        mTitleModule.setText(title);
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
        mContentPadding.postValue(new float[] { padding, padding, padding, padding });
        return (T) this;
    }

    public T contentPadding(float l, float t, float r, float b) {
        mContentPadding.postValue(new float[] { l, t, r, b });
        return (T) this;
    }

    public T startVisible(boolean visible) {
        mStartModule.setVisibility(visible ? View.VISIBLE : View.GONE);
        return (T) this;
    }

    public T startTextColor(int color) {
        mStartModule.setTextColor(color);
        return (T) this;
    }

    public T startTextColor(String color) {
        mStartModule.setTextColor(color);
        return (T) this;
    }

    public T startTextColor(ColorStateList color) {
        mStartModule.setTextColor(color);
        return (T) this;
    }

    public T startTextSize(float size) {
        mStartModule.setTextSize(size);
        return (T) this;
    }

    public T startBackground(Drawable drawable) {
        mDefaultStartBackground = false;
        mStartModule.setBackground(drawable);
        return (T) this;
    }

    public T startBackgroundResource(@DrawableRes int resId) {
        mDefaultStartBackground = resId == R.drawable.dialog_button_background;
        mStartModule.setBackgroundResource(resId);
        return (T) this;
    }

    public T endVisible(boolean visible) {
        mEndModule.setVisibility(visible ? View.VISIBLE : View.GONE);
        return (T) this;
    }

    public T endTextColor(int color) {
        mEndModule.setTextColor(color);
        return (T) this;
    }

    public T endTextColor(String color) {
        mEndModule.setTextColor(color);
        return (T) this;
    }

    public T endTextColor(ColorStateList color) {
        mEndModule.setTextColor(color);
        return (T) this;
    }

    public T endTextSize(float size) {
        mEndModule.setTextSize(size);
        return (T) this;
    }

    public T endBackground(Drawable drawable) {
        mDefaultEndBackground = false;
        mEndModule.setBackground(drawable);
        return (T) this;
    }

    public T endBackgroundResource(@DrawableRes int resId) {
        mDefaultEndBackground = resId == R.drawable.dialog_button_background;
        mEndModule.setBackgroundResource(resId);
        return (T) this;
    }

    public T addStartClickListener(OnBtnClickListener listener) {
        mLeftListeners.add(listener);
        return (T) this;
    }

    public T addEndClickListener(OnBtnClickListener listener) {
        mRightListeners.add(listener);
        return (T) this;
    }

    /**
     * 设置标题间隔
     */
    private void setTitlePadding() {
        boolean visible1 = mStartView.getVisibility() == View.VISIBLE;
        boolean visible2 = mEndView.getVisibility() == View.VISIBLE;
        boolean isDefault = mDefaultStartBackground && mDefaultEndBackground;
        int padding = getDefaultPadding();
        int maxTemp = (int) Math.max(mStartView.getTextSize() / SIZE_SCALE, mEndView.getTextSize() / SIZE_SCALE);
        ViewGroup.MarginLayoutParams params;
        // 左边部分
        if (visible1) {
            params = (ViewGroup.MarginLayoutParams) mStartView.getLayoutParams();
            params.rightMargin = isDefault ? 0 : padding / 2;
            params.leftMargin = isDefault ? padding - maxTemp : padding;
            mStartView.requestLayout();
        }
        // 右边部分
        if (visible2) {
            params = (ViewGroup.MarginLayoutParams) mEndView.getLayoutParams();
            params.leftMargin = isDefault ? 0 : padding / 2;
            params.rightMargin = isDefault ? padding - maxTemp : padding;
            mEndView.requestLayout();
        }
        setTitleMargin();
    }


    private void setTitleMargin() {
        mTitleView.post(new Runnable() {
            @Override
            public void run() {
                boolean visible1 = mStartView.getVisibility() == View.VISIBLE;
                boolean visible2 = mEndView.getVisibility() == View.VISIBLE;
                boolean isDefault = mDefaultStartBackground && mDefaultEndBackground;
                int padding = getDefaultPadding();
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mTitleView.getLayoutParams();
                if (mTitleCenter) {
                    // 设置左右间隔
                    int width = Math.max(mStartView.getMeasuredWidth(), mEndView.getMeasuredWidth());
                    int tTemp = (int) (mTitleView.getTextSize() / SIZE_SCALE);
                    // 最大间隔
                    int maxTemp = (int) Math.max(mStartView.getTextSize() / SIZE_SCALE, mEndView.getTextSize() / SIZE_SCALE);
                    // 获取间隔
                    int margin = width == 0 ? padding : isDefault ? (padding + width - maxTemp) : (padding + width + tTemp);
                    // 设置左右间隔
                    params.leftMargin = margin;
                    params.rightMargin = margin;
                } else {
                    // 设置左右间隔
                    params.leftMargin = visible1 ? 0 : padding;
                    params.rightMargin = visible2 ? 0 : padding;
                }
                mTitleView.requestLayout();
            }
        });
    }

    /**
     * 设置标题居中
     */
    private void setTitleCenter(int gravity) {
        if (gravity == Gravity.CENTER) {
            mTitleCenter = true;
            mTitleView.setGravity(Gravity.CENTER);
            // 清除布局规制
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mTitleView.getLayoutParams();
            params.addRule(RelativeLayout.RIGHT_OF, 0);
            params.addRule(RelativeLayout.LEFT_OF, 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                params.addRule(RelativeLayout.END_OF, 0);
                params.addRule(RelativeLayout.START_OF, 0);
            }
        }
    }

    private int getDefaultPadding() {
        float[] padding = mTitlePadding.getValue();
        if (padding == null) {
            return requireContext().getResources().getDimensionPixelSize(R.dimen.content);
        }
        return toPix(padding[0]);
    }
}
