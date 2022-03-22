package cn.cqray.android.dialog;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

public class BaseDelegate implements LifecycleEventObserver {

    public BaseDelegate(Fragment fragment) {

    }

    public BaseDelegate(FragmentActivity activity) {

    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {

    }
}
