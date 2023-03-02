package cn.cqray.android.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.os.Build
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.Lifecycle
import butterknife.ButterKnife
import butterknife.Unbinder
import cn.cqray.java.tool.SizeUnit
import java.util.concurrent.atomic.AtomicBoolean

@Suppress("Unchecked_cast")
internal object DialogUtils {

    private val logTag = GetDialog::class.java.simpleName

    /** ButterKnife是否可用  */
    private val bkSupported = AtomicBoolean(true)

    /**
     * ButterKnife绑定控件
     * @param target 目标
     * @param source 来源
     * @return 绑定实例
     */
    @JvmStatic
    fun bindButterKnife(target: Dialog?, source: View): Any? {
        if (target == null || !bkSupported.get()) return null
        val unBinder: Unbinder?
        try {
            unBinder = ButterKnife.bind(target, source)
            bkSupported.set(true)
        } catch (t: NoClassDefFoundError) {
            bkSupported.set(false)
            return null
        }
        return unBinder
    }

    /**
     * 解除ButterKnife绑定
     * @param unBinder 绑定实例[Unbinder]
     */
    @JvmStatic
    fun unbindButterKnife(unBinder: Any?) {
        if (unBinder == null || !bkSupported.get()) return
        if (unBinder is Unbinder) unBinder.unbind()
        bkSupported.set(
            try {
                if (unBinder is Unbinder) unBinder.unbind()
                true
            } catch (e: NoClassDefFoundError) {
                false
            }
        )
    }

    fun setRippleBackground(view: View) {
        val a = TypedValue()
        view.context.theme.resolveAttribute(android.R.attr.selectableItemBackground, a, true)
        // 设置水波纹
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 前景
            view.foreground = ContextCompat.getDrawable(view.context, a.resourceId)
        } else {
            // 背景
            ViewCompat.setBackground(view, ContextCompat.getDrawable(view.context, a.resourceId))
        }
    }

    /**
     * 获取相应的尺寸
     * @param value 值
     * @param unit 值单位[SizeUnit]
     */
    @JvmStatic
    fun applyDimension(value: Float, unit: Int): Float {
        val metrics = Resources.getSystem().displayMetrics
        when (unit) {
            TypedValue.COMPLEX_UNIT_PX -> return value
            TypedValue.COMPLEX_UNIT_DIP -> return value * metrics.density
            TypedValue.COMPLEX_UNIT_SP -> return value * metrics.scaledDensity
            TypedValue.COMPLEX_UNIT_PT -> return value * metrics.xdpi * (1.0f / 72)
            TypedValue.COMPLEX_UNIT_IN -> return value * metrics.xdpi
            TypedValue.COMPLEX_UNIT_MM -> return value * metrics.xdpi * (1.0f / 25.4f)
        }
        return 0F
    }

    /**
     * 获取相应的尺寸
     * @param value 值
     * @param unit 值单位[SizeUnit]
     */
    @JvmStatic
    fun applyDimension(value: Float, unit: SizeUnit): Float {
        val metrics = Resources.getSystem().displayMetrics
        when (unit.type) {
            TypedValue.COMPLEX_UNIT_PX -> return value
            TypedValue.COMPLEX_UNIT_DIP -> return value * metrics.density
            TypedValue.COMPLEX_UNIT_SP -> return value * metrics.scaledDensity
            TypedValue.COMPLEX_UNIT_PT -> return value * metrics.xdpi * (1.0f / 72)
            TypedValue.COMPLEX_UNIT_IN -> return value * metrics.xdpi
            TypedValue.COMPLEX_UNIT_MM -> return value * metrics.xdpi * (1.0f / 25.4f)
        }
        return 0F
    }

    /**
     * 获取APP可用屏幕宽度
     * @param context 上下文
     */
    @Suppress("Deprecation")
    fun getAppScreenWidth(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return Point().also { wm.defaultDisplay.getSize(it) }.x
    }

    /**
     * 获取APP可用屏幕高度
     * @param context 上下文
     */
    @Suppress("Deprecation")
    fun getAppScreenHeight(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return Point().also { wm.defaultDisplay.getSize(it) }.y
    }

    fun runOnUiThread(activity: Activity, runnable: Runnable) {
        // 主线程，直接运行
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run()
            return
        }
        // 其他线程，需要判定Activity是否正在销毁或已销毁
        val isFinishing = activity.isFinishing
        val isDestroyed = when {
            activity is ComponentActivity -> activity.lifecycle.currentState == Lifecycle.State.DESTROYED
            Build.VERSION.SDK_INT >= 17 -> activity.isDestroyed
            else -> activity.isFinishing
        }
        if (isFinishing || isDestroyed) {
            logE("Nothing can be done because Activity is finished.", null)
            return
        }
        // 跳转主线程运行
        activity.runOnUiThread(runnable)
    }

    fun logD(message: String) {
        Log.d(logTag, message)
    }

    fun logI(message: String) {
        Log.i(logTag, message)
    }

    fun logE(message: String, throwable: Throwable?) {
        Log.e(logTag, message, throwable)
    }
}