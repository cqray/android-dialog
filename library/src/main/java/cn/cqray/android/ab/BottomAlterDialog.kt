package cn.cqray.android.ab

import android.app.Activity
import android.view.Gravity
import cn.cqray.android.dialog.amin.BottomIn
import cn.cqray.android.dialog.amin.BottomOut

class BottomAlterDialog(activity: Activity) : AlterDialog<BottomAlterDialog>(activity) {

    init {
        // 宽度
        widthScale(1F)
        // 位置
        gravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)
        // 圆角
        backgroundRadii(arrayOf(6F, 6F, 6F, 6F, 0F, 0F, 0F, 0F).toFloatArray())
        // 动画
        showAnimator(BottomIn())
        dismissAnimator(BottomOut())
    }
}