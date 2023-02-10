package cn.cqray.android.dialog.module2

import android.app.Dialog
import android.content.ComponentCallbacks
import android.content.res.Configuration
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.atomic.AtomicReference
import java.util.logging.Handler

class BaseDialog2 : DialogProvider {

    private val lifecycleOwner: LifecycleOwner

    constructor(activity: ComponentActivity) {
        lifecycleOwner = activity
    }

    constructor(fragment: Fragment) {
        lifecycleOwner = fragment
    }

    private val atomicDialog = AtomicReference<Dialog>()

//    private val lifecycleRegistry by lazy { LifecycleRegistry(this) }

    private val dialogDelegate by lazy { DialogDelegate3(this) }

//    override fun getLifecycle() = lifecycleRegistry

    override fun requireActivity(): ComponentActivity {
        return when(lifecycleOwner) {
            is Fragment -> lifecycleOwner.requireActivity()
            else -> lifecycleOwner as ComponentActivity
        }
    }

    override fun show() {
        // 取消对话框
        atomicDialog.get()?.let {
            if (it.isShowing) {
                it.cancel()
            }
        }
        // 显示对话框
        val dialog = atomicDialog.get() ?: dialogDelegate.onCreateDialog()
        dialog.show()
//        // 显示新的对话框
//        atomicDialog.set(dialogDelegate.onCreateDialog().also {
//            it.show()
//        })
        requireActivity().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                // 取消旧的对话框
                atomicDialog.get()?.let {
                    if (it.isShowing) {
                        it.cancel()
                    }
                }
            }
        })

        requireActivity().registerComponentCallbacks(object : ComponentCallbacks {
            override fun onConfigurationChanged(newConfig: Configuration) {
                Log.e("数据", "配置发生了变化");
                android.os.Handler().postDelayed({show()}, 500)
                //show()
            }

            override fun onLowMemory() {
                //TODO("Not yet implemented")
            }

        })
    }

    override fun dismiss() {
        //TODO("Not yet implemented")
        atomicDialog.get()?.dismiss()
    }

    override fun quickDismiss() {
        //TODO("Not yet implemented")
        atomicDialog.get()?.dismiss()
    }
}