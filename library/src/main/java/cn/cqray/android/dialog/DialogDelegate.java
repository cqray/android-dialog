package cn.cqray.android.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;

import cn.cqray.android.code.lifecycle.SimpleLiveData;
import cn.cqray.android.code.util.SizeUnit;
import cn.cqray.android.code.util.SizeUtils;
import cn.cqray.android.dialog.delegate.PanelDelegate;

public class DialogDelegate extends BaseDelegate {

    private FrameLayout mPanelLayout;
    private Dialog mDialog;
    private OnCreatingCallback mCallback;

    protected final PanelDelegate mPanelDelegate;

    private final MutableLiveData<View> mContentView = new SimpleLiveData<>();

    protected final SimpleLiveData<Integer> mContentResId = new SimpleLiveData<>();
    protected final SimpleLiveData<Boolean> mCancelable = new SimpleLiveData<>();
    protected final SimpleLiveData<Boolean> mCancelableOutsize = new SimpleLiveData<>();
    protected final SimpleLiveData<Boolean> mBlackStatusBar = new SimpleLiveData<>();

    public DialogDelegate(Fragment fragment, OnCreatingCallback callback) {
        super(fragment);
        mCallback = callback;
        mPanelDelegate = new PanelDelegate(fragment);
        initPanelDelegate();
    }

    public DialogDelegate(FragmentActivity activity, OnCreatingCallback callback) {
        super(activity);
        mCallback = callback;
        mPanelDelegate = new PanelDelegate(activity);
        initPanelDelegate();
    }

    protected void initPanelDelegate() {
        mPanelDelegate.setBackgroundColor(Color.WHITE);
        mPanelDelegate.setRadius(4);
        mPanelDelegate.setWidthScale(0.5f);
        mPanelDelegate.setHeightScale(0.5f);
        mPanelDelegate.setGravity(Gravity.CENTER);
    }

    protected void initDialog() {
        mDialog = new Dialog(requireContext(), R.style.DialogFullTheme) {
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                Window window = getWindow();
                assert window != null;
                window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                window.setDimAmount(0.3f);
                setContentView(R.layout.__dialog_layout);
                mPanelLayout = findViewById(R.id.__dialog_panel);
                mPanelLayout.setVisibility(View.GONE);
                mPanelLayout.postDelayed(() -> mPanelLayout.setVisibility(View.VISIBLE), 50);
                initLiveData();
                mPanelDelegate.setView(mPanelLayout);
                mBlackStatusBar.setValue(false);
                if (mCallback != null) {
                    mCallback.onCreating(savedInstanceState);
                }
            }

            @Override
            public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
//                if (BaseDialog.this.dispatchTouchEvent(ev) || mDialogModule.isDialogAnimRunning()) {
//                    return true;
//                }
                return super.dispatchTouchEvent(ev);
            }

            @Override
            public void onBackPressed() {
                super.onBackPressed();
//                if (!BaseDialog.this.onBackPressed()
//                        && mDialogModule.isCancelable()
//                        && !mDismissing) {
//                    mCancel = true;
//                    BaseDialog.this.dismiss();
//                }
            }

            @Override
            public void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                mContentView.removeObservers(getLifecycleOwner());
            }
        };
        mDialog.setOwnerActivity((Activity) requireContext());
    }

    public void setContentView(@LayoutRes int layoutRes) {
        mContentResId.postValue(layoutRes);
    }

    public void setContentView(View view) {
        mContentView.postValue(view);
    }

    public void setCancelable(boolean cancelable) {
        mCancelable.setValue(cancelable);
    }

    public void setCancelableOutsize(boolean cancelable) {
        mCancelableOutsize.setValue(cancelable);
    }

    public void setBlackStatusBar(boolean black) {
        mBlackStatusBar.setValue(black);
    }

    protected void show() {
        initDialog();
        mDialog.show();
    }

    protected void initLiveData() {
        mContentView.observe(getLifecycleOwner(), view -> {
            mPanelLayout.removeAllViews();
            mPanelLayout.addView(view);
        });
        mContentResId.observe(getLifecycleOwner(), aInteger -> {
            View view = LayoutInflater.from(requireContext()).inflate(aInteger, mPanelLayout, false);
            mContentView.setValue(view);
        });
        mCancelable.observe(getLifecycleOwner(), aBoolean -> mDialog.setCancelable(aBoolean));
        mCancelableOutsize.observe(getLifecycleOwner(), aBoolean -> mDialog.setCanceledOnTouchOutside(aBoolean));
        mBlackStatusBar.observe(getLifecycleOwner(), aBoolean -> {
            Window window = mDialog.getWindow();
            assert window != null;
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = -1;
            params.height = aBoolean ? -1 : -2;
            window.setAttributes(params);
        });
    }



//    protected void initModule() {
//        panelModule = new PanelModule(getLifecycleOwner());
//        panelModule.observe(getLifecycleOwner(), mPanelLayout);
//        panelModule.setWidthScale(0.5f);
//        panelModule.setHeightScale(0.5f);
//        panelModule.setBackgroundColor(Color.WHITE);
//    }

    public interface OnCreatingCallback {

        void onCreating(Bundle savedInstanceState);
    }
}
