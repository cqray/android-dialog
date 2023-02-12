package cn.cqray.android.ab


import androidx.fragment.app.FragmentActivity

class BaseDialog : PanelProvider<BaseDialog> {

    val fragment: DialogFragment by lazy { DialogFragment() }

    override val panelComponent by lazy { PanelComponent(fragment) { fragment.viewBinding.dlgContent } }


//    constructor(activity: FragmentActivity) {
//        mOwnerActivity = activity
//        mOwnerFragment = null
//        mDelegate = DialogDelegate(this)
////        mTipModule = mDelegate.tipModule
////        mPanelModule = mDelegate.panelModule
//    }

//    constructor(fragment: Fragment?) {
//        mOwnerActivity = null
//        mOwnerFragment = fragment
//        mDelegate = DialogDelegate(this)
//        mTipModule = mDelegate.tipModule
//        mPanelModule = mDelegate.panelModule
//    }

    fun show(activity: FragmentActivity) {
        fragment.show(activity.supportFragmentManager, fragment::javaClass.name)
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