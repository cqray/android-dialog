package cn.cqray.android.ab

import android.app.Dialog
import android.os.Handler
import android.view.Gravity
import androidx.fragment.app.FragmentActivity
import cn.cqray.android.dialog.BottomMessageDialog
import cn.cqray.android.dialog.amin.BottomIn

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

            val dialog = BottomAlterDialog(activity)


                .widthScale(0.5F)
                .heightScale(0.5F)
//                .offset(10F, 200F)
//                .gravity(Gravity.BOTTOM)
//                .backgroundColor(Color.BLUE)
//                .backgroundRadius(10F)
                .nativeDimAccount(0.2f)
//                .customDimAccount(0.5F)

//                .showAnimator(BottomIn())
//                .marginLR(50F);
                dialog.show()


                dialog.show()

            Handler().postDelayed({
//                dialog.customDimAccount(0.1f)
            }, 1500)

        }
//         @JvmStatic
//        fun <T: BaseDialog<T>> get(activity: FragmentActivity): DialogFragment {
//            val tv = TextView(activity)
//            tv.setText("777777777777777")
//            return BaseDialog<T>(activity).also { it.setContentView(tv) }
//        }
    }
}