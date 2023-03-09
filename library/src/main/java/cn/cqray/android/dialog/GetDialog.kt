package cn.cqray.android.dialog

import android.app.Activity
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import cn.cqray.android.dialog.component.PanelComponent

/**
 * 基础对话框
 * @author Cqray
 */
open class GetDialog<T : GetDialog<T>>(val activity: Activity) :
    LifecycleOwner,
    GetDialogProvider<GetDialog<T>>,
    GetPanelProvider<GetDialog<T>> {

    /** 对话框委托 **/
    override val dialogDelegate: GetDialogDelegate by lazy { GetDialogDelegate(activity, this) }

    /** 面板组件 **/
    override val panelComponent by lazy { PanelComponent(this) { dialogDelegate.binding.dlgPanel } }

    /** 对话框生命周期 **/
    override fun getLifecycle() =  dialogDelegate.lifecycleOwner.lifecycle

    override fun onCreating(savedInstanceState: Bundle?) {
        super.onCreating(savedInstanceState)
        // 调用下PanelComponent，不然有可能未被使用，
        // 则一些配置项将不会生效
        panelComponent.hashCode()
    }

    companion object {

        @JvmStatic
        @Suppress("UPPER_BOUND_VIOLATED_WARNING")
        fun builder2(activity: Activity) = GetDialog<GetDialog<*>>(activity)


        @JvmStatic
        fun builder(activity: Activity) = GetDialogBuilder(activity)
    }
}