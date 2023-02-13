package cn.cqray.android.ab

import android.view.MotionEvent
import android.view.View
import android.util.TypedValue
import androidx.annotation.FloatRange
import androidx.annotation.LayoutRes
import cn.cqray.android.dialog.amin.DialogAnimator

/**
 * 对话框相关功能提供器
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unchecked_cast",
    "Unused"
)
@JvmDefaultWithoutCompatibility
interface DialogProvider<T : DialogProvider<T>> {

    /** 对话框Fragment实例 **/
    val dialogFragment: DialogFragment

    /**
     * 设置内容视图
     * @param view 视图
     */
    fun setContentView(view: View) = also { dialogFragment.setContentView(view) } as T

    /**
     * 设置内容视图
     * @param id 视图资源ID
     */
    fun setContentView(@LayoutRes id: Int) = also { dialogFragment.setContentView(id) } as T

    /**
     * 对话框是否可取消
     * @param cancelable 是否可取消
     */
    fun cancelable(cancelable: Boolean) = also { dialogFragment.isCancelable = cancelable } as T

    /**
     * 对话框点击外部是否可取消
     * @param cancelable 是否可取消
     */
    fun cancelableOutsize(cancelable: Boolean) = also { dialogFragment.setCancelableOutsize(cancelable) } as T

    fun nativeDimAccount(@FloatRange(from = 0.0, to = 1.0) account: Float) = also {
        dialogFragment.setNativeDimAccount(account)
    } as T

    fun customDimAccount(@FloatRange(from = 0.0, to = 1.0) account: Float) = also {
        dialogFragment.setCustomDimAccount(account)
    } as T

    /**
     * 显示动画
     * @param animator 动画
     */
    fun showAnimator(animator: DialogAnimator) = also { dialogFragment.setShowAnimator(animator) } as T

    /**
     * 消失动画
     * @param animator 动画
     */
    fun dismissAnimator(animator: DialogAnimator) = also { dialogFragment.setDismissAnimator(animator) } as T

    /**
     * 对话框位置
     * @param gravity 位置
     */
    fun gravity(gravity: Int) = also { dialogFragment.setGravity(gravity) } as T

    /**
     * 对话框偏移量，默认单位DIP
     * @param offsetX 横向偏移量
     * @param offsetY 纵向向偏移量
     */
    fun offset(offsetX: Float, offsetY: Float) = also { dialogFragment.setOffset(offsetX, offsetY) } as T

    /**
     * 对话框偏移量
     * @param offsetX 横向偏移量
     * @param offsetY 纵向向偏移量
     * @param unit 值单位[TypedValue]
     */
    fun offset(offsetX: Float, offsetY: Float, unit: Int) = also {
        dialogFragment.setOffset(offsetX, offsetY, unit)
    } as T

    /**
     * 回退拦截
     */
    fun onBackPressed() = false

    /**
     * 分发触摸事件
     * @param ev 事件
     */
    fun dispatchTouchEvent(ev: MotionEvent) = false
}