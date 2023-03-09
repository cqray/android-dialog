package cn.cqray.android.dialog

import android.app.Activity
import android.view.Gravity

/**
 * 对话框工厂
 */
@Suppress("UPPER_BOUND_VIOLATED_WARNING")
class GetDialogBuilder(private val activity: Activity) {

    fun asAlter(): GetAlterDialog<*> {
        return GetAlterDialog<GetAlterDialog<*>>(activity)
    }

    fun asBottomAlter() = asAlter().also {
        // 底部居中
        it.gravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)
        // 动画
        it.showAnimator { va -> va.bottomIn() }
        it.dismissAnimator { va -> va.bottomOut() }
    }
}