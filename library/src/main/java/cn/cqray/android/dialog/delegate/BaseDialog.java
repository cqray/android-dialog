package cn.cqray.android.dialog.delegate;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;

import cn.cqray.android.dialog.listener.OnCancelListener;
import cn.cqray.android.dialog.listener.OnDismissListener;
import cn.cqray.android.dialog.listener.OnShowListener;

/**
 * 基础对话框实现
 * @author Cqray
 */
public class BaseDialog {

    private final Fragment mFragment;
    private final FragmentActivity mActivity;
    private final LifecycleOwner mLifecycleOwner;
    private final DialogDelegate mDialogDelegate;

    public BaseDialog(Fragment fragment) {
        mFragment = fragment;
        mActivity = null;
        mLifecycleOwner = mFragment;
        mDialogDelegate = new DialogDelegate(fragment, this);
    }

    public BaseDialog(FragmentActivity activity) {
        mActivity = activity;
        mFragment = null;
        mLifecycleOwner = mActivity;
        mDialogDelegate = new DialogDelegate(activity, this);
    }

    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        return false;
    }

    public boolean onBackPressed() {
        return false;
    }

    public void onCancel() {}

    public void onShow() {}

    public void onDismiss() {}

    public void onCreating(Bundle savedInstanceState) {}

    public void setContentView(@LayoutRes int layoutRes) {
        mDialogDelegate.setContentView(layoutRes);
    }

    public void setContentView(@NonNull View view) {
        mDialogDelegate.setContentView(view);
    }

    public Context requireContext() {
        if (mFragment != null) {
            return mFragment.requireContext();
        }
        return mActivity;
    }

    public LifecycleOwner getLifecycleOwner() {
        return mLifecycleOwner;
    }

    public void show() {
        mDialogDelegate.show();
    }

    public void addOnCancelListener(OnCancelListener listener) {
        mDialogDelegate.addOnCancelListener(listener);
    }

    public void addOnDismissListener(OnDismissListener listener) {
        mDialogDelegate.addOnDismissListener(listener);
    }

    public void addOnShowListener(OnShowListener listener) {
        mDialogDelegate.addOnShowListener(listener);
    }
}
