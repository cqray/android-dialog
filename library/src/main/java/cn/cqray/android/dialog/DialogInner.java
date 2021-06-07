package cn.cqray.android.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * DialogFragment的复制简化实现
 * @author Cqray
 */
class DialogInner extends Fragment {

    private static final String SAVED_DIALOG_STATE_TAG = "android:savedDialogState";
    private static final String SAVED_THEME = "android:theme";
    private static final String SAVED_BACK_STACK_ID = "android:backStackId";

    @StyleRes
    private int mTheme = 0;
    private int mBackStackId = -1;
    private Dialog mDialog;
    private Handler mHandler;
    private boolean mViewDestroyed;
    private boolean mDismissed;
    private boolean mShownByMe;

    public DialogInner() {}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!mShownByMe) {
            mDismissed = false;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (!mShownByMe && !mDismissed) {
            mDismissed = true;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler(Looper.getMainLooper());
        if (savedInstanceState != null) {
            mTheme = savedInstanceState.getInt(SAVED_THEME, 0);
            mBackStackId = savedInstanceState.getInt(SAVED_BACK_STACK_ID, -1);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        assert mDialog != null;
        View view = getView();
        if (view != null) {
            if (view.getParent() != null) {
                throw new IllegalStateException(
                        "DialogFragment can not be attached to a container view");
            }
            mDialog.setContentView(view);
        }
        final Activity activity = getActivity();
        if (activity != null) {
            mDialog.setOwnerActivity(activity);
        }
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                DialogInner.this.onDismiss(dialog);
            }
        });
        if (savedInstanceState != null) {
            Bundle dialogState = savedInstanceState.getBundle(SAVED_DIALOG_STATE_TAG);
            if (dialogState != null) {
                mDialog.onRestoreInstanceState(dialogState);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mDialog != null) {
            mViewDestroyed = false;
            mDialog.show();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mDialog != null) {
            mDialog.hide();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mDialog != null) {
            // 在这里设置removed，因为这只是为了隐藏对话框,我们不希望这会导致片段被实际删除。
            mViewDestroyed = true;
            // 不要等待已发布的onDismiss()，而要清空侦听器并手动调用onDismiss()，以确保回调发生在onDestroy()之前
            mDialog.setOnDismissListener(null);
            mDialog.dismiss();
            // 移除RootView和Dialog的绑定
            View view = requireView();
            ViewGroup parent = (ViewGroup) view.getParent();
            parent.removeView(view);
            if (!mDismissed) {
                // 如果我们已经在dismissInternal中手动取消对话框，请不要发送第二个onDismiss()回调
                onDismiss(mDialog);
            }
            mDialog = null;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mDialog != null) {
            Bundle dialogState = mDialog.onSaveInstanceState();
            outState.putBundle(SAVED_DIALOG_STATE_TAG, dialogState);
        }
        if (mTheme != 0) {
            outState.putInt(SAVED_THEME, mTheme);
        }
        if (mBackStackId != -1) {
            outState.putInt(SAVED_BACK_STACK_ID, mBackStackId);
        }
    }

    @Override
    @NonNull
    public LayoutInflater onGetLayoutInflater(@Nullable Bundle savedInstanceState) {
        mDialog = onCreateDialog(savedInstanceState);
        return (LayoutInflater) mDialog.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new Dialog(requireContext(), mTheme);
    }

    public Dialog getDialog() {
        return mDialog;
    }

    void show(@NonNull FragmentActivity activity) {
        show(activity.getSupportFragmentManager(), getClass().getName());
    }

    void show(@NonNull Fragment fragment) {
        show(fragment.getChildFragmentManager(), getClass().getName());
    }

    void dismiss() {
        dismissInternal(false, false);
    }

    void onDismiss(DialogInterface dialog) {
        if (!mViewDestroyed) {
            dismissInternal(true, true);
        }
    }

    private void show(@NonNull FragmentManager manager, @Nullable String tag) {
        mDismissed = false;
        mShownByMe = true;
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commit();
    }

    private void dismissInternal(boolean allowStateLoss, boolean fromOnDismiss) {
        if (mDismissed) {
            return;
        }
        mDismissed = true;
        mShownByMe = false;
        if (mDialog != null) {
            mDialog.setOnDismissListener(null);
            mDialog.dismiss();
            if (!fromOnDismiss) {
                if (Looper.myLooper() == mHandler.getLooper()) {
                    onDismiss(mDialog);
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mDialog != null) {
                                onDismiss(mDialog);
                            }
                        }
                    });
                }
            }
        }
        mViewDestroyed = true;
        if (mBackStackId >= 0) {
            requireFragmentManager().popBackStack(mBackStackId,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
            mBackStackId = -1;
        } else {
            FragmentTransaction ft = requireFragmentManager().beginTransaction();
            ft.remove(this);
            if (allowStateLoss) {
                ft.commitAllowingStateLoss();
            } else {
                ft.commit();
            }
        }
    }

}
