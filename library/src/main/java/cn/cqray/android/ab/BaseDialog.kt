package cn.cqray.android.ab

import android.app.Activity
import cn.cqray.android.dialog.component.PanelComponent

open class BaseDialog<T : BaseDialog<T>>(activity: Activity) :
    DialogProvider<BaseDialog<T>>,
    PanelProvider<BaseDialog<T>> {

    override val dialogDelegate: DialogDelegate by lazy { DialogDelegate(activity, this) }

    override val panelComponent by lazy { PanelComponent(dialogDelegate.lifecycleOwner) { dialogDelegate.binding.dlgPanel } }

}