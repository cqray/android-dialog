package cn.cqray.android.dialog

import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import butterknife.ButterKnife
import butterknife.Unbinder
import java.util.concurrent.atomic.AtomicBoolean

@Suppress("Unchecked_cast")
internal object Utils {

    /** ButterKnife是否可用  */
    private val bkSupported = AtomicBoolean(true)

    /**
     * ButterKnife绑定控件
     * @param target 目标
     * @param source 来源
     * @return 绑定实例
     */
    @JvmStatic
    fun bindButterKnife(target: Any?, source: View): Any? {
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

    /**
     * 获取相应的尺寸
     * @param value 值
     * @param unit 值单位[TypedValue]
     */
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
}