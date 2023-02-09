package cn.cqray.android.dialog.module2

import android.animation.ValueAnimator
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import cn.cqray.android.dialog.ButterKnifeUtils.bind
import cn.cqray.android.dialog.ButterKnifeUtils.unbind
import cn.cqray.android.dialog.DialogLiveData
import cn.cqray.android.dialog.DialogUtils
import cn.cqray.android.dialog.databinding.AndroidDlgLayoutBaseBinding
import cn.cqray.android.dialog.module.PanelModule
import lombok.Setter
import java.util.concurrent.atomic.AtomicReference

/**
 * 对话框委托实现
 * @author Cqray
 */
class DialogDelegate(
    /** 对话框实例  */
    private val mDialog: BaseDialog<*>
) {

    /** 内容视图 **/
    private val atomicContent = AtomicReference<View>()

    /** ButterKnife绑定实例  */
    private val atomicUnBinder = AtomicReference<Any>()

    /** 界面是否能够取消  */
    @Setter
    private val mCancelable = true

    /** 点击外部取消  */
    @Setter
    private val mCancelableOutsize = true

    /** 自定义遮罩透明度  */
    @Setter
    private val mCustomAmountCount = 0f

    /** 原始遮罩透明度  */
    @Setter
    private val mNativeAmountCount = 0.4f

    /** 遮罩动画  */
    private val mDimAnimator = ValueAnimator()

    /** 是否使用黑色状态栏  */
    private val mBlackStatusBar = DialogLiveData(false)

//    /** 提示模块实现  */
//    @Getter
//    private val mTipModule: TipModule

    /** 面板实现模块  */
//    @Getter
    val panelModule: PanelModule

    val context by lazy { mDialog.context!! }

    val baseBinding by lazy { AndroidDlgLayoutBaseBinding.inflate(mDialog.layoutInflater) }

    init {
//        mTipModule = TipModule()
        panelModule = PanelModule(mDialog)
    }

    fun observe(owner: LifecycleOwner) {
        owner.lifecycle.addObserver(LifecycleEventObserver { source: LifecycleOwner?, event: Lifecycle.Event ->
            if (event == Lifecycle.Event.ON_CREATE) {
                onCreate()
            } else if (event == Lifecycle.Event.ON_START) {
                onStart()
            } else if (event == Lifecycle.Event.ON_DESTROY) {
                onDestroy()
            }
        })
    }

    private fun onCreate() {
        val context = mDialog.requireContext()
        // 根布局点击事件
        baseBinding.dlgRoot.setOnClickListener {
            // 可点击外部取消、可取消、且面板没有被消除
            if (mCancelableOutsize && mCancelable && !panelModule.isDismissing) {
                // 消除对话框
                dismiss()
            }
        }


//        // 面板布局
//        mPanelLayout = FrameLayout(context)
//        mPanelLayout!!.layoutParams = ViewGroup.LayoutParams(-1, -1)
//        mPanelLayout!!.isClickable = true
//        mPanelLayout!!.isFocusable = true
//        // 遮罩布局
//        mDimView = View(context)
//        mDimView!!.layoutParams = ViewGroup.LayoutParams(-1, -1)
//        // 提示控件
//        val tipView = TextView(context)
//        // 位置布局
//        val siteLayout = FrameLayout(context)
//        siteLayout.layoutParams = ViewGroup.LayoutParams(-2, -2)
//        siteLayout.addView(mPanelLayout)
//        siteLayout.addView(tipView)
//        // 添加布局
//        mRootView!!.addView(mDimView)
//        mRootView!!.addView(siteLayout)
//        mTipModule.observe(mDialog, tipView)
//        panelModule.setRootView(mRootView)
//        panelModule.observe(mDialog, mPanelLayout!!)
//        // 是否显示黑色状态栏监听
//        mBlackStatusBar.observe(mDialog) { aBoolean: Boolean ->
//            val window = mDialog.requireDialog().window!!
//            val portrait = ScreenUtils.isPortrait()
//            val lp = window.attributes
//            // lp.width和lp.height均为-1，则会出现黑色状态栏
//            // 竖屏时，宽度设置为屏幕宽度。横屏是设置为-1（考虑到刘海屏，不能直接取宽度）
//            lp.width = if (aBoolean) -1 else if (portrait) ScreenUtils.getScreenWidth() else -1
//            // 锁屏时，高度设置为-1（考虑到刘海屏，不能直接取高度）。横屏是设置为屏幕高度
//            lp.height = if (aBoolean) -1 else if (portrait) -1 else ScreenUtils.getScreenHeight()
//            window.attributes = lp
//        }
    }

    private fun onStart() {
        // 执行面板动画
        panelModule.show { duration: Int ->
            // 执行遮罩动画
            doDimAnimator(duration, true)
        }
    }

    private fun onDestroy() {
        // 取消绑定
        unbind(atomicUnBinder.get())
        // 销毁动画
        if (mDimAnimator.isRunning) {
            mDimAnimator.cancel()
        }
    }

    fun onCreateDialog(): Dialog {
        // 初始化对话框
        val dialog: Dialog = object : Dialog(mDialog.requireActivity()) {
            override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
                // 事件是否被消费掉
                val dispatch = mDialog.dispatchTouchEvent(ev)
                // 面板动画正在进行
                val running = false//panelModule.isPanelAnimatorRunning
                // 任意一个条件满足则不继续
                return if (dispatch || running) {
                    true
                } else super.dispatchTouchEvent(ev)
            }

            override fun onBackPressed() {
                // 回退事件是否被拦截
                val backPressed = mDialog.onBackPressed()
                // 如果可以被取消、且没有正在消除面板、回退事件未被拦截
                if (mCancelable && !panelModule.isDismissing && !backPressed) {
                    // 消除对话框
                    this@DialogDelegate.dismiss()
                }
            }
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.window?.let {
            val width = DialogUtils.getAppScreenWidth(context)
            val height = DialogUtils.getAppScreenHeight(context)
            it.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            it.setDimAmount(mNativeAmountCount)
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
            it.decorView.minimumWidth = width
            it.decorView.minimumHeight = height
            it.attributes.let { lp ->
                it.attributes = lp
                it.setGravity(Gravity.TOP)
            }
        }
        return dialog
    }

    /**
     * 消除对话框
     */
    fun dismiss() {
        // 消除面板，并执行遮罩动画
        panelModule.dismiss { doDimAnimator(it, false) }
    }

    fun setContentView(view: View) {
        checkLifecycleState()
        baseBinding.dlgPanel.let {
            it.removeAllViews()
            it.addView(view)
            atomicContent.set(it.getChildAt(0))
            atomicUnBinder.set(bind(mDialog, atomicContent.get()))
        }
    }

    fun setContentView(@LayoutRes layoutResId: Int) {
        checkLifecycleState()
        baseBinding.dlgPanel.let {
            it.removeAllViews()
            View.inflate(context, layoutResId, it)
            atomicContent.set(it.getChildAt(0))
            atomicUnBinder.set(bind(mDialog, atomicContent.get()))
        }
//        val view = LayoutInflater.from(mDialog.requireContext()).inflate(
//            layoutResId,
//            baseBinding.dlgPanel,
//            true
//        )
//        mPanelLayout!!.removeAllViews()
//        mPanelLayout!!.addView(view)
//        mContentView = view
//        mUnBinder = bind(mDialog, view)
    }

    fun setBlackStatusBar(black: Boolean) {
        mBlackStatusBar.value = black
    }

//    fun showTip(tip: String?) {
//        mTipModule.setText(tip)
//        mTipModule.show()
//    }
//
//    fun showTip(tip: String?, duration: Int) {
//        mTipModule.setText(tip)
//        mTipModule.setDuration(duration)
//        mTipModule.show()
//    }

    fun <V : View> findViewById(@IdRes id: Int) = atomicContent.get()?.findViewById<V>(id)!!

    /**
     * 检查生命周期状态
     */
    private fun checkLifecycleState() {
        check(
            mDialog.lifecycle
                .currentState
                .isAtLeast(Lifecycle.State.CREATED)
        ) { "Please call setContentView in onCreating()." }
    }

    /***
     * 执行遮罩动画
     * @param duration 显示时长
     * @param show true显示对话框，false关闭对话框
     */
    private fun doDimAnimator(duration: Int, show: Boolean) {
        if (mDimAnimator.isRunning) {
            // 取消正在进行的动画
            mDimAnimator.cancel()
        }
        if (mCustomAmountCount != 0f) {
            // 开始值
            val start = if (show) 0 else (255 * mCustomAmountCount).toInt()
            // 结束值
            val end = if (show) (255 * mCustomAmountCount).toInt() else 0
            // 临时时间值
            val tmp = if (duration <= 0) 300 else duration
            // 设置值
            mDimAnimator.setIntValues(start, end)
            // 进度监听
            mDimAnimator.addUpdateListener { animation: ValueAnimator ->
                val value = animation.animatedValue as Int
                val alpha = String.format("%02X", value)
                val color = "#" + alpha + "000000"
                //mDimView!!.setBackgroundColor(Color.parseColor(color))
            }
            // 开始动画
            mDimAnimator.setDuration(tmp.toLong()).start()
        }
    }
}