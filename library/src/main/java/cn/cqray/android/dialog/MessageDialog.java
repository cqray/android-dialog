package cn.cqray.android.dialog;

import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import cn.cqray.android.dialog.module.TextViewModule;

/**
 * 消息对话框
 * @author Cqray
 */
public class MessageDialog extends AlterDialog<MessageDialog> {

    private final TextViewModule mContentModule;

    public MessageDialog(FragmentActivity activity) {
        super(activity);
        mContentModule = new TextViewModule();
    }

    public MessageDialog(Fragment fragment) {
        super(fragment);
        mContentModule = new TextViewModule();
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

    public MessageDialog content(@StringRes int resId) {
        mContentModule.setText(resId);
        return this;
    }

    public MessageDialog content(CharSequence text) {
        mContentModule.setText(text);
        return this;
    }

    public MessageDialog contentTextColor(int color) {
        mContentModule.setTextColor(color);
        return this;
    }

    public MessageDialog contentTextColor(String color) {
        mContentModule.setTextColor(color);
        return this;
    }

    public MessageDialog contentTextSize(float size) {
        mContentModule.setTextSize(size);
        return this;
    }

}
