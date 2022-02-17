package cn.cqray.android.dialog;

import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

/**
 * 底部消息
 * @author Cqray
 */
public class BottomMessageDialog extends BottomAlterDialog<BottomMessageDialog> {

    private final TextViewModule mContentModule;

    public BottomMessageDialog(FragmentActivity activity) {
        super(activity);
        mContentModule = new TextViewModule(activity);
        startVisible(false).endVisible(false);
    }

    public BottomMessageDialog(Fragment fragment) {
        super(fragment);
        mContentModule = new TextViewModule(fragment);
        startVisible(false).endVisible(false);
    }

    @Override
    public void onCreating(@Nullable Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(-1, -1);
        TextView tv = new TextView(requireContext());
        tv.setLayoutParams(params);
        tv.setTextColor(Color.parseColor("#333333"));
        tv.setTextSize(14);
        setContentView(tv);
        mContentModule.observe(this, tv);
    }

    public BottomMessageDialog content(@StringRes int resId) {
        mContentModule.setText(resId);
        return this;
    }

    public BottomMessageDialog content(CharSequence text) {
        mContentModule.setText(text);
        return this;
    }

    public BottomMessageDialog contentTextColor(int color) {
        mContentModule.setTextColor(color);
        return this;
    }

    public BottomMessageDialog contentTextColor(String color) {
        mContentModule.setTextColor(color);
        return this;
    }

    public BottomMessageDialog contentTextSize(float size) {
        mContentModule.setTextSize(size);
        return this;
    }
}
