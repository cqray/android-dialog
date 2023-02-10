package cn.cqray.android.dialog.module2

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.*
import androidx.lifecycle.LifecycleOwner

import cn.cqray.android.dialog.listener.OnCancelListener
import cn.cqray.android.dialog.listener.OnDismissListener
import cn.cqray.android.dialog.listener.OnShowListener

/**
 * 内部实现Dialog
 * @author Cqray
 */
class BaseDialog<T : BaseDialog<T>> : DialogFragment {

    /** 持有对话框的Fragment  */
    private val mOwnerFragment: Fragment?

    /** 持有对话框的Activity  */
    private val mOwnerActivity: FragmentActivity?

    /** 取消监听  */
    private val mCancelListeners: MutableList<OnCancelListener> = ArrayList()

    /** 消除监听  */
    private val mDismissListeners: MutableList<OnDismissListener> = ArrayList()

    /** 显示监听  */
    private val mShowListeners: MutableList<OnShowListener> = ArrayList()

//    /** 提示模块  */
//    @Getter
//    private val mTipModule: TipModule

//    /** 面板模块  */
//    private val mPanelModule: PanelModule

    /** 对话框委托  */
    private val mDelegate: DialogDelegate

    constructor(activity: FragmentActivity) {
        mOwnerActivity = activity
        mOwnerFragment = null
        mDelegate = DialogDelegate(this)
//        mTipModule = mDelegate.tipModule
//        mPanelModule = mDelegate.panelModule
    }

//    constructor(fragment: Fragment?) {
//        mOwnerActivity = null
//        mOwnerFragment = fragment
//        mDelegate = DialogDelegate(this)
//        mTipModule = mDelegate.tipModule
//        mPanelModule = mDelegate.panelModule
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDelegate.observe(this)
    }

    override fun onResume() {
        super.onResume()
        Log.e("数据", "onResume")
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        for (listener in mCancelListeners) {
            listener.onCancel()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        for (listener in mDismissListeners) {
            listener.onDismiss()
        }
    }

    fun onShow(dialog: DialogInterface) {
        for (listener in mShowListeners) {
            listener.onShow()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        onCreating(savedInstanceState)
        return mDelegate.baseBinding.root
    }

    fun onCreating(savedInstanceState: Bundle?) {}
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return mDelegate.onCreateDialog()
    }

    fun show() {
        val fm: FragmentManager
        val act: Activity?
        if (mOwnerFragment == null) {
            assert(mOwnerActivity != null)
            fm = mOwnerActivity!!.supportFragmentManager
            act = mOwnerActivity
        } else {
            fm = mOwnerFragment.parentFragmentManager
            act = mOwnerFragment.requireActivity()
        }
        act.runOnUiThread {
//            super.show(fm, null)
            try {
                val fragment = fm.findFragmentByTag(javaClass.name)
                fragment?.let {
                    val ft = fm.beginTransaction()
                    ft.remove(it)
                    ft.commitNow()
                }
                super.showNow(fm, javaClass.name)
            } catch (ignore: IllegalStateException) {
                ignore.printStackTrace()
            }
        }
    }

//    @Deprecated("")
    override fun show(manager: FragmentManager, tag: String?) {
    }

//    @Deprecated("")
    override fun showNow(manager: FragmentManager, tag: String?) {
    }

//    @Deprecated("")
    override fun show(transaction: FragmentTransaction, tag: String?): Int {
        return 0
    }

    @Deprecated("")
    override fun setShowsDialog(showsDialog: Boolean) {
    }

    val parentLifecycleOwner: LifecycleOwner?
        get() = mOwnerFragment ?: mOwnerActivity

    override fun dismiss() {
        mDelegate.dismiss()
    }

    /**
     * 快速消除，无视动画。本质是原始的消除方式
     */
    fun quickDismiss() {
        try {
            val fm = parentFragmentManager
            if (!fm.isStateSaved && !fm.isDestroyed && !isStateSaved) {
                // 保证Fragment及其父类状态存活
                super.dismiss()
            } else {
                super.dismissAllowingStateLoss()
            }
        } catch (ignore: IllegalStateException) {
        }
    }

    fun onBackPressed(): Boolean {
        return false
    }

    fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return false
    }

    fun setContentView(view: View) = mDelegate.setContentView(view)

    fun setContentView(@LayoutRes layoutResId: Int) = mDelegate.setContentView(layoutResId)

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

//    fun blackStatusBar(black: Boolean): T {
//        mDelegate.setBlackStatusBar(black)
//        return this as T
//    }
//
//    fun cancelable(cancelable: Boolean): T {
//        mDelegate.setCancelable(cancelable)
//        return this as T
//    }
//
//    fun cancelableOutsize(cancelable: Boolean): T {
//        mDelegate.setCancelableOutsize(cancelable)
//        return this as T
//    }
//
//    fun customDimMargin(l: Float, t: Float, r: Float, b: Float): T {
//        return this as T
//    }
//
//    fun customDimAmount(@FloatRange(from = 0, to = 1) amount: Float): T {
//        mDelegate.setCustomAmountCount(amount)
//        return this as T
//    }
//
//    fun nativeDimAmount(@FloatRange(from = 0, to = 1) amount: Float): T {
//        mDelegate.setNativeAmountCount(amount)
//        return this as T
//    }
//
//    fun cornerRadii(radii: FloatArray?): T {
//        mPanelModule.backgroundRadii = radii
//        return this as T
//    }
//
//    fun cornerRadii(radii: FloatArray?, unit: Int): T {
//        mPanelModule.setBackgroundRadii(radii, unit)
//        return this as T
//    }
//
//    fun cornerRadius(radius: Float): T {
//        mPanelModule.setBackgroundRadius(radius)
//        return this as T
//    }
//
//    fun cornerRadius(radius: Float, unit: Int): T {
//        mPanelModule.setBackgroundRadius(radius, unit)
//        return this as T
//    }
//
//    fun background(drawable: Drawable?): T {
//        mPanelModule.setBackground(drawable)
//        return this as T
//    }
//
//    fun backgroundColor(color: Int): T {
//        mPanelModule.setBackgroundColor(color)
//        return this as T
//    }
//
//    fun backgroundResource(@DrawableRes resId: Int): T {
//        mPanelModule.setBackgroundResource(resId)
//        return this as T
//    }
//
//    fun gravity(gravity: Int): T {
//        mPanelModule.setGravity(gravity)
//        return this as T
//    }
//
//    fun offset(offsetX: Float, offsetY: Float): T {
//        mPanelModule.setOffset(offsetX, offsetY)
//        return this as T
//    }
//
//    fun width(width: Float): T {
//        mPanelModule.setWidth(width)
//        return this as T
//    }
//
//    fun width(width: Float, unit: Int): T {
//        mPanelModule.setWidth(width, unit)
//        return this as T
//    }
//
//    fun widthScale(@FloatRange(from = 0, to = 1) scale: Float): T {
//        mPanelModule.setWidthScale(scale)
//        return this as T
//    }
//
//    fun widthMin(min: Float): T {
//        mPanelModule.setWidthMin(min, TypedValue.COMPLEX_UNIT_DIP)
//        return this as T
//    }
//
//    fun widthMin(min: Float, unit: Int): T {
//        mPanelModule.setWidthMin(min, unit)
//        return this as T
//    }
//
//    fun widthMax(max: Float): T {
//        mPanelModule.setWidthMax(max, TypedValue.COMPLEX_UNIT_DIP)
//        return this as T
//    }
//
//    fun widthMax(max: Float, unit: Int): T {
//        mPanelModule.setWidthMax(max, unit)
//        return this as T
//    }
//
//    fun height(height: Float): T {
//        mPanelModule.setHeight(height)
//        return this as T
//    }
//
//    fun height(height: Float, unit: Int): T {
//        mPanelModule.setHeight(height, unit)
//        return this as T
//    }
//
//    fun heightScale(@FloatRange(from = 0, to = 1) scale: Float): T {
//        mPanelModule.setHeightScale(scale)
//        return this as T
//    }
//
//    fun heightMin(min: Float): T {
//        mPanelModule.setHeightMin(min, TypedValue.COMPLEX_UNIT_DIP)
//        return this as T
//    }
//
//    fun heightMin(min: Float, unit: Int): T {
//        mPanelModule.setHeightMin(min, unit)
//        return this as T
//    }
//
//    fun heightMax(max: Float): T {
//        mPanelModule.setHeightMax(max, TypedValue.COMPLEX_UNIT_DIP)
//        return this as T
//    }
//
//    fun heightMax(max: Float, unit: Int): T {
//        mPanelModule.setHeightMax(max, unit)
//        return this as T
//    }
//
//    fun showAnimator(animator: DialogAnimator?): T {
//        mPanelModule.setShowAnimator(animator)
//        return this as T
//    }
//
//    fun dismissAnimator(animator: DialogAnimator?): T {
//        mPanelModule.setDismissAnimator(animator)
//        return this as T
//    }
//
//    fun addOnCancelListener(listener: OnCancelListener): T {
//        synchronized(BaseDialog::class.java) { mCancelListeners.add(listener) }
//        return this as T
//    }
//
//    fun addOnDismissListener(listener: OnDismissListener): T {
//        synchronized(BaseDialog::class.java) { mDismissListeners.add(listener) }
//        return this as T
//    }
//
//    fun addOnShowListener(listener: OnShowListener): T {
//        synchronized(BaseDialog::class.java) { mShowListeners.add(listener) }
//        return this as T
//    }
//
//    fun <V : View?> findViewById(@IdRes resId: Int): V {
//        return mDelegate.findViewById(resId)
//    }
//
//    fun showTip(tip: String?) {
//        mDelegate.showTip(tip)
//    }
//
//    fun showTip(tip: String?, duration: Int) {
//        mDelegate.showTip(tip, duration)
//    }
}