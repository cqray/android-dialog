package cn.cqray.android.dialog.module;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.lifecycle.LifecycleOwner;

import cn.cqray.android.dialog.DialogLiveData;
import cn.cqray.android.dialog.Utils;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * 文本控件相关操作模块
 * @author Cqray
 */
@Accessors(prefix = "m")
public class TextViewModule extends ViewModule<TextView> {

    /** 文本 **/
    @Getter
    protected final DialogLiveData<CharSequence> mText = new DialogLiveData<>();
    /** 文本资源 **/
    @Getter
    protected final DialogLiveData<Integer> mTextRes = new DialogLiveData<>();
    /** 文本颜色 **/
    @Getter
    protected final DialogLiveData<ColorStateList> mTextColor = new DialogLiveData<>();
    /** 文本大小 **/
    @Getter
    protected final DialogLiveData<Integer> mTextSize = new DialogLiveData<>();
    /** 文本加粗 **/
    @Getter
    protected final DialogLiveData<Integer> mTextStyle = new DialogLiveData<>();
    /** 文本位置 **/
    @Getter
    protected final DialogLiveData<Integer> mGravity = new DialogLiveData<>();

    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull TextView view) {
        super.observe(owner, view);
        // 设置文本监听
        mText.observe(owner, view::setText);
        // 设置文本资源监听
        mTextRes.observe(owner, view::setText);
        // 设置文本颜色变化监听
        mTextColor.observe(owner, view::setTextColor);
        // 设置文本大小变化监听
        mTextSize.observe(owner, aInt -> view.setTextSize(TypedValue.COMPLEX_UNIT_PX, aInt));
        // 设置文本样式变化监听
        mTextStyle.observe(owner, aBoolean -> view.setTypeface(Typeface.defaultFromStyle(aBoolean)));
        // 设置文本位置变化监听
        mGravity.observe(owner, view::setGravity);
    }

    public void setText(@StringRes int resId) {
        mTextRes.setValue(resId);
    }

    public void setText(CharSequence content) {
        mText.setValue(content);
    }

    public void setTextColor(@ColorInt int color) {
        mTextColor.setValue(ColorStateList.valueOf(color));
    }

    public void setTextColor(String color) {
        mTextColor.setValue(ColorStateList.valueOf(Color.parseColor(color)));
    }

    public void setTextColor(ColorStateList colorStateList) {
        mTextColor.setValue(colorStateList);
    }

    public void setTextSize(float size) {
        mTextSize.setValue((int) Utils.applyDimension(size, TypedValue.COMPLEX_UNIT_DIP));
    }

    public void setTextSize(float size, int unit) {
        mTextSize.setValue((int) Utils.applyDimension(size, unit));
    }

    public void setTextBold(boolean bold) {
        mTextStyle.setValue(bold ? Typeface.BOLD : Typeface.NORMAL);
    }

    public void setTextStyle(int style) {
        mTextStyle.setValue(style);
    }

    public void setGravity(int gravity) {
        mGravity.setValue(gravity);
    }
}
