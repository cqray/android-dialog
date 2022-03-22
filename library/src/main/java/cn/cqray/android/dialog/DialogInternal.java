package cn.cqray.android.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

/**
 * DialogFragment的复制简化实现
 * @author Cqray
 */
class DialogInternal extends Fragment {

    private Dialog mDialog;
    private Handler mHandler;
    private boolean mViewDestroyed;
    private boolean mDismissed;
    private boolean mShownByMe;

    private DialogViewModel mDialogViewModel;
    private MutableLiveData<Boolean> mDismiss = new MutableLiveData<>();

    public DialogInternal() {}

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
        mDialogViewModel = new ViewModelProvider(this).get(DialogViewModel.class);
        Log.e("数据", "222222");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e("数据", "333333");
        assert mDialog != null;
        View view = getView();
        if (view != null) {
            if (view.getParent() != null) {
                throw new IllegalStateException(
                        "DialogFragment can not be attached to a container view");
            }
            mDialog.setContentView(view);
        }
        mDialog.setOwnerActivity(requireActivity());
        mDialog.setOnDismissListener(this::onDismiss);
        if (mDialogViewModel.getDialogState() != null) {
            mDialog.onRestoreInstanceState(mDialogViewModel.getDialogState());
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
            // 清空监听，保证回调在OnDestroy之前
            mDialog.setOnDismissListener(null);
            // 消除对话框
            mDialog.dismiss();
            // 移除控件和对话框的绑定
            ((ViewGroup) requireView().getParent()).removeView(requireView());
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
            mDialogViewModel.setDialogState(dialogState);
        }
    }

    @Override
    @NonNull
    public LayoutInflater onGetLayoutInflater(@Nullable Bundle savedInstanceState) {
        mDialog = onCreateDialog(savedInstanceState);
        Log.e("数据", "1111111111111111");

        return (LayoutInflater) mDialog.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new Dialog(requireContext(), R.style.DialogFullTheme);
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
        ft.commitAllowingStateLoss();
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
                    mHandler.post(() -> {
                        if (mDialog != null) {
                            onDismiss(mDialog);
                        }
                    });
                }
            }
        }
        mViewDestroyed = true;

//        FragmentManager fm = getParentFragmentManager();
//        if (mDialogViewModel.getBackStackId() >= 0) {
//            fm.popBackStack(mDialogViewModel.getBackStackId(),
//                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
//            mDialogViewModel.setBackStackId(-1);
//        } else {
//            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
//            ft.remove(this);
//            if (allowStateLoss) {
//                ft.commitAllowingStateLoss();
//            } else {
//                ft.commit();
//            }
//        }
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.remove(this);
        ft.commitAllowingStateLoss();
//        if (allowStateLoss) {
//            ft.commitAllowingStateLoss();
//        } else {
//            ft.commit();
//        }
    }

}
