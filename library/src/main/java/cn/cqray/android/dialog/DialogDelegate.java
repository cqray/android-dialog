package cn.cqray.android.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;

public class DialogDelegate extends BaseDelegate {

    private Dialog mDialog;
    private OnCreatingCallback mCallback;
    private final MutableLiveData<Integer> mContentResId = new MutableLiveData<>();
    private final MutableLiveData<View> mContentView = new MutableLiveData<>();

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
                //params.height = -1;
                window.setAttributes(params);
                mContentView.observe(getLifecycleOwner(), this::setContentView);
                mContentResId.observe(getLifecycleOwner(), this::setContentView);
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
                Log.e("数据", "对话框销户");
                mContentView.removeObservers(getLifecycleOwner());
            }
        };
    }

    public void setContentView(@LayoutRes int layoutRes) {
        mContentResId.setValue(layoutRes);
    }

    public void setContentView(View view) {
        mContentView.setValue(view);
    }

    protected void show() {
        initDialog();
        mDialog.show();
    }

    public interface OnCreatingCallback {

        void onCreating(Bundle savedInstanceState);
    }
}
