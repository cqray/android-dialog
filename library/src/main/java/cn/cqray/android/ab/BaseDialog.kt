package cn.cqray.android.ab

//import androidx.fragment.app.Fragment
//import androidx.fragment.app.FragmentActivity
//import androidx.fragment.app.FragmentManager
//import androidx.lifecycle.LifecycleOwner
import androidx.fragment.app.FragmentActivity
import cn.cqray.android.dialog.component.PanelComponent

@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unchecked_cast",
    "Unused"
)
open class BaseDialog (
    val activity: FragmentActivity
) : DialogProvider<BaseDialog>,
    PanelProvider<BaseDialog> {

    override val dialogFragment: DialogFragment by lazy { DialogFragment(this) }

    override val panelComponent by lazy { PanelComponent(dialogFragment) { dialogFragment.viewBinding.dlgPanel } }

//    constructor(activity: FragmentActivity) : this()
//
//    constructor(fragment: Fragment) : this()

//    val fragmentManager: FragmentManager
//        get() {
//            return when(lifecycleOwner) {
//                is FragmentActivity -> lifecycleOwner.supportFragmentManager
//                else -> (lifecycleOwner as Fragment).childFragmentManager
//            }
//        }

//    constructor(activity: FragmentActivity)

//    constructor(fragment: Fragment?) {
//        mOwnerActivity = null
//        mOwnerFragment = fragment
//        mDelegate = DialogDelegate(this)
//        mTipModule = mDelegate.tipModule
//        mPanelModule = mDelegate.panelModule
//    }

    fun show() {
        dialogFragment.show(activity.supportFragmentManager, dialogFragment::javaClass.name)
//        val fm: FragmentManager
//        val act: Activity?
//        if (mOwnerFragment == null) {
//            assert(mOwnerActivity != null)
//            fm = mOwnerActivity!!.supportFragmentManager
//            act = mOwnerActivity
//        } else {
//            fm = mOwnerFragment.parentFragmentManager
//            act = mOwnerFragment.requireActivity()
//        }
//        act.runOnUiThread {
////            super.show(fm, null)
//            try {
//                val fragment = fm.findFragmentByTag(javaClass.name)
//                fragment?.let {
//                    val ft = fm.beginTransaction()
//                    ft.remove(it)
//                    //ft.commitNow()
//                }
//                super.showNow(fm, javaClass.name)
//            } catch (ignore: IllegalStateException) {
//                ignore.printStackTrace()
//            }
//        }
    }
}