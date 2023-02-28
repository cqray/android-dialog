package cn.cqray.android.dialog.internal

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import cn.cqray.android.dialog.Utils

internal open class InternalDialog(
    /** 所有者[Activity] **/
    ownerActivity: Activity,
    /** 伴生[Fragment] **/
    private val accompanyingFragment: Fragment?
) : Dialog(ownerActivity) {

    /**
     * 对话框生命周期管理注册器
     * 伴生的Fragment为空，则创建生命周期管理注册器
     */
    private val lifecycleRegistry by lazy { if (accompanyingFragment == null) LifecycleRegistry(lifecycleOwner) else null }

    /**
     * 对话框生命周期
     * 伴生[Fragment]不为空，则使用伴生[Fragment]的[LifecycleOwner]
     * 否则，则创建新的[LifecycleOwner]对象
     */
    val lifecycleOwner: LifecycleOwner by lazy { accompanyingFragment ?: LifecycleOwner { lifecycleRegistry!! } }

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
//        setCanceledOnTouchOutside(false)
//        setCancelable(false)
        window?.let {
            val width = Utils.getAppScreenWidth(ownerActivity)
            val height = Utils.getAppScreenHeight(ownerActivity)
            it.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
            it.decorView.minimumWidth = width
            it.decorView.minimumHeight = height
            it.attributes.let { lp ->
                it.attributes = lp
                it.setGravity(Gravity.TOP)
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        lifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        lifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }
}