package cn.cqray.android.dialog;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelStoreOwner;

/**
 * 基础委托实现
 * @author Cqray
 */
public class BaseDelegate implements LifecycleEventObserver {

    private Fragment mFragment;
    private FragmentActivity mActivity;
    private LifecycleOwner mLifecycleOwner;
    private ViewModelStoreOwner mViewModelStoreOwner;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public BaseDelegate(Fragment fragment) {
        mFragment = fragment;
        mLifecycleOwner = mFragment;
        mViewModelStoreOwner = mFragment;
        mHandler.post(() -> mFragment.getLifecycle().addObserver(this));
    }

    public BaseDelegate(FragmentActivity activity) {
        mActivity = activity;
        mLifecycleOwner = mActivity;
        mViewModelStoreOwner = mActivity;
        mHandler.post(() -> mActivity.getLifecycle().addObserver(this));
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner owner, @NonNull Lifecycle.Event event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            mHandler.removeCallbacksAndMessages(null);
        }
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

    public ViewModelStoreOwner getViewModelStoreOwner() {
        return mViewModelStoreOwner;
    }

}
