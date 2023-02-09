package cn.cqray.android.dialog.module

import android.view.View
import butterknife.ButterKnife
import butterknife.Unbinder
import java.util.concurrent.atomic.AtomicBoolean

/**
 * ButterKnife工具类
 * @author Cqray
 */
object ButterKnifeUtils {

    /** ButterKnife是否可用  */
    private val supported = AtomicBoolean(true)

    /**
     * ButterKnife绑定控件
     * @param target 目标
     * @param source 来源
     * @return 绑定实例
     */
    @JvmStatic
    fun bind(target: Any?, source: View): Any? {
        if (target == null || !supported.get()) return null
        val unBinder: Unbinder?
        try {
            unBinder = ButterKnife.bind(target, source)
            supported.set(true)
        } catch (t: NoClassDefFoundError) {
            supported.set(false)
            return null
        }
        return unBinder
    }

    /**
     * 解除ButterKnife绑定
     * @param unBinder 绑定实例[Unbinder]
     */
    @JvmStatic
    fun unbind(unBinder: Any?) {
        if (unBinder == null || !supported.get()) return
        if (unBinder is Unbinder) unBinder.unbind()
        supported.set(
            try {
                if (unBinder is Unbinder) unBinder.unbind()
                true
            } catch (e: NoClassDefFoundError) {
                false
            }
        )
    }
}