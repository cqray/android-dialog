package cn.cqray.android.dialog.module2

import android.view.MotionEvent
import androidx.activity.ComponentActivity

@JvmDefaultWithoutCompatibility
interface DialogProvider {

    fun requireActivity() : ComponentActivity

    fun show()

    fun dismiss()

    fun quickDismiss()

    fun onBackPressed() = false

    fun dispatchTouchEvent(ev: MotionEvent) = false

}