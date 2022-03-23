package cn.cqray.android.dialog;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;

public class DialogDelegate extends BaseDelegate {

    private FrameLayout mPanelLayout;
    private Dialog mDialog;
    private OnCreatingCallback mCallback;
    /** 遮罩动画 **/
    private ValueAnimator mDimAnimator;
    /** 自定义遮罩透明度 **/
    private float mCustomAmountCount = 0.15f;

    private PanelModule panelModule;

    private final MutableLiveData<Integer> mContentResId = new MutableLiveData<>();
    private final MutableLiveData<View> mContentView = new MutableLiveData<>();

    private final MutableLiveData<Boolean> mCancelable = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mCancelableOutsize = new MutableLiveData<>();

    public DialogDelegate(Fragment fragment, OnCreatingCallback callback) {
        super(fragment);
        mCallback = callback;
    }

    public DialogDelegate(FragmentActivity activity, OnCreatingCallback callback) {
        super(activity);
        mCallback = callback;
    }

    protected void initDialog() {
        mDialog = new Dialog(requireContext(), R.style.DialogFullTheme) {
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                Window window = getWindow();
                assert window != null;
                window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                window.setDimAmount(0);
                //window.setDimAmount(mDialogModule.getNativeAmountCount());
                //window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = -1;
                window.setAttributes(params);
                setContentView(R.layout.__dialog_layout);
                mPanelLayout = findViewById(R.id.__dialog_panel);
                //params.height = -1;
                initLiveData();
                initModule();
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
    }

    public void setContentView(@LayoutRes int layoutRes) {
        mContentResId.postValue(layoutRes);
    }

    public void setContentView(View view) {
        mContentView.postValue(view);
    }

    public void setCancelable(boolean cancelable) {
        mCancelable.postValue(cancelable);
    }

    public void setCancelableOutsize(boolean cancelable) {
        mCancelableOutsize.postValue(cancelable);
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
    }

    protected void initModule() {
        panelModule = new PanelModule(getLifecycleOwner());
        panelModule.observe(getLifecycleOwner(), mPanelLayout);
        panelModule.setWidthScale(0.5f);
        panelModule.setHeightScale(0.5f);
        panelModule.setBackgroundColor(Color.WHITE);
    }

    @Nullable
    protected ValueAnimator getCustomDimAnimator(boolean show) {
        if (mDimAnimator != null) {
            mDimAnimator.cancel();
        }
        if (mCustomAmountCount == 0) {
            return null;
        }
        int start = show ? 0 : (int) (255 * mCustomAmountCount);
        int end = show ? (int) (255 * mCustomAmountCount) : 0;
        mDimAnimator = ValueAnimator.ofInt(start, end);
        return mDimAnimator;
    }

    public interface OnCreatingCallback {

        void onCreating(Bundle savedInstanceState);
    }
}
