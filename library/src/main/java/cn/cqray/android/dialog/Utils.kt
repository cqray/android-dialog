package cn.cqray.android.dialog

import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.viewbinding.ViewBinding

@Suppress("Unchecked_cast")
internal object Utils {

    @Throws(Exception::class)
    fun <T : ViewBinding> getViewBinding(clazz: Class<T>, inflater: LayoutInflater): T {
        val method = clazz.getMethod("inflate", LayoutInflater::class.java)
        return method.invoke(null, inflater) as T
//        runCatching {
//            return method.invoke(null, inflater) as T
//        }.onFailure { throw it }
//        try {
//            return method.invoke(null, inflater) as T
//        } catch (exc: Exception) {
//            throw exc
//        }
//        runCatching { return method.invoke(null, inflater) as T }
    }

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

    @Suppress("Deprecation")
    fun getAppScreenWidth(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return Point().also { wm.defaultDisplay.getSize(it) }.x
    }

    @Suppress("Deprecation")
    fun getAppScreenHeight(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return Point().also { wm.defaultDisplay.getSize(it) }.y
    }
}