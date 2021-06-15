package cn.cqray.android.dialog;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

/**
 * @author Cqray
 * @date 2021/6/6 22:35
 */
public class TextViewModule extends ViewModule<TextView> {

    /** 文本 **/
    private final MutableLiveData<CharSequence> mText = new MutableLiveData<>();
    /** 文本颜色 **/
    private final MutableLiveData<ColorStateList> mTextColor = new MutableLiveData<>();
    /** 文本大小 **/
    private final MutableLiveData<Integer> mTextSize = new MutableLiveData<>();
    /** 文本加粗 **/
    private final MutableLiveData<Boolean> mTextBold = new MutableLiveData<>();
    /** 文本位置 **/
    private final MutableLiveData<Integer> mGravity = new MutableLiveData<>();

    public TextViewModule(FragmentActivity act) {
        super(act);
    }

    public TextViewModule(Fragment fragment) {
        super(fragment);
    }

    @Override
    public void observe(LifecycleOwner owner, final TextView tv) {
        super.observe(owner, tv);
        observeText(owner, new Observer<CharSequence>() {
            @Override
            public void onChanged(CharSequence charSequence) {
                tv.setText(charSequence);
            }
        });
        observeTextColor(owner, new Observer<ColorStateList>() {
            @Override
            public void onChanged(ColorStateList colorStateList) {
                tv.setTextColor(colorStateList);
            }
        });
        observeTextSize(owner, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, integer);
            }
        });
        observeTextBold(owner, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                tv.setTypeface(Typeface.defaultFromStyle(aBoolean ? Typeface.BOLD : Typeface.NORMAL));
            }
        });
        observeGravity(owner, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                tv.setGravity(integer);
            }
        });
    }

    public void observeText(LifecycleOwner owner, Observer<CharSequence> observer) {
        mText.removeObservers(owner);
        mText.observe(owner, observer);
    }

    public void observeTextColor(LifecycleOwner owner, Observer<ColorStateList> observer) {
        mTextColor.removeObservers(owner);
        mTextColor.observe(owner, observer);
    }

    public void observeTextSize(LifecycleOwner owner, Observer<Integer> observer) {
        mTextSize.removeObservers(owner);
        mTextSize.observe(owner, observer);
    }

    public void observeTextBold(LifecycleOwner owner, Observer<Boolean> observer) {
        mTextBold.removeObservers(owner);
        mTextBold.observe(owner, observer);
    }

    public void observeGravity(LifecycleOwner owner, Observer<Integer> observer) {
        mGravity.removeObservers(owner);
        mGravity.observe(owner, observer);
    }

    public void setText(@StringRes int resId) {
        mText.postValue(getString(resId));
    }

    public void setText(CharSequence content) {
        mText.postValue(content);
    }

    public void setTextColor(int color) {
        mTextColor.postValue(ColorStateList.valueOf(color));
    }

    public void setTextColor(String color) {
        mTextColor.postValue(ColorStateList.valueOf(Color.parseColor(color)));
    }

    public void setTextColor(ColorStateList colorStateList) {
        mTextColor.postValue(colorStateList);
    }

    public void setTextSize(float size) {
        mTextSize.postValue(toPix(size));
    }

    public void setTextBold(boolean bold) {
        mTextBold.postValue(bold);
    }

    public void setGravity(int gravity) {
        mGravity.postValue(gravity);
    }
}
