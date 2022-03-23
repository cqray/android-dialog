package cn.cqray.android.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;

public class BaseDialog2 implements DialogDelegate.OnCreatingCallback {

    private final Fragment mFragment;
    private final FragmentActivity mActivity;
    private final LifecycleOwner mLifecycleOwner;
    private final DialogDelegate mDialogDelegate;

    public BaseDialog2(Fragment fragment) {
        mFragment = fragment;
        mActivity = null;
        mLifecycleOwner = mFragment;
        mDialogDelegate = new DialogDelegate(fragment, this);
    }

    public BaseDialog2(FragmentActivity activity) {
        mActivity = activity;
        mFragment = null;
        mLifecycleOwner = mActivity;
        mDialogDelegate = new DialogDelegate(activity, this);
    }

    @Override
    public void onCreating(Bundle savedInstanceState) {

    }

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


}
