package cn.cqray.android.ab


import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import cn.cqray.android.ab.BaseDialog

class Te {

    companion object {

        @JvmStatic
        fun <BaseDialog> show(activity: FragmentActivity) {
//            BaseDialog<T>2(activity)
////                .widthScale(0.5F)
////
//////                .width(300F)
////                .heightScale(0.5F)
//                .show()

            BaseDialog()
                .widthScale(0.5F)
                .heightScale(0.5F)
                .show(activity)
        }
//         @JvmStatic
//        fun <T: BaseDialog<T>> get(activity: FragmentActivity): DialogFragment {
//            val tv = TextView(activity)
//            tv.setText("777777777777777")
//            return BaseDialog<T>(activity).also { it.setContentView(tv) }
//        }
    }
}