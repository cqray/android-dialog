package cn.cqray.demo.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;

import cn.cqray.android.dialog.BaseDialog;

public class Dialog33 extends BaseDialog<Dialog33> {


    public Dialog33(FragmentActivity act) {
        super(act);
    }

    public Dialog33(Fragment fragment) {
        super(fragment);
    }

    @Override
    public void onCreating(@Nullable Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);
        setContentView(R.layout.activity_main);

//        observeShow(requireActivity(), new Observer<Dialog33>() {
//            @Override
//            public void onChanged(Dialog33 dialog33) {
//
//            }
//        });
    }

    @Override
    public void onCancel() {
        super.onCancel();
    }

    @Override
    public void onDismiss() {
        super.onDismiss();
    }

    @Override
    public void onShow() {
        super.onShow();
    }
}
