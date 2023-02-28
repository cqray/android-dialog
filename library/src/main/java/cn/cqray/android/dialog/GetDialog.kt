package cn.cqray.android.dialog

import android.app.Activity
import android.os.Bundle
import cn.cqray.android.dialog.component.PanelComponent

/**
 * 基础对话框
 * @author Cqray
 */
open class GetDialog<T : GetDialog<T>>(val activity: Activity) :
    GetDialogProvider<GetDialog<T>>,
    GetPanelProvider<GetDialog<T>> {

    /** 对话框委托 **/
    override val dialogDelegate: DialogDelegate by lazy { DialogDelegate(activity, this) }

    /** 面板主键 **/
    override val panelComponent by lazy { PanelComponent(dialogLifecycleOwner!!) { dialogDelegate.binding.dlgPanel } }

    /** 对话框生命周期 **/
    val dialogLifecycleOwner by lazy { dialogDelegate.lifecycleOwner }

    override fun onCreating(savedInstanceState: Bundle?) {
        super.onCreating(savedInstanceState)
        // 调用下PanelComponent，不然有可能未被使用，
        // 则一些配置项将不会生效
        panelComponent.hashCode()
    }

    companion object {

        @JvmStatic
        @Suppress("UPPER_BOUND_VIOLATED_WARNING")
        fun builder(activity: Activity) = GetDialog<GetDialog<*>>(activity)
    }
}