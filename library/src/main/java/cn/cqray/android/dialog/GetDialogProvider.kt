package cn.cqray.android.dialog

import android.content.res.Configuration
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.annotation.FloatRange
import androidx.annotation.LayoutRes
import cn.cqray.android.dialog.amin.DialogAnimator
import cn.cqray.java.tool.SizeUnit

/**
 * 对话框相关功能提供器
 * @author Cqray
 */
@Suppress(
    "Deprecation",
    "MemberVisibilityCanBePrivate",
    "Unchecked_cast",
    "Unused"
)
@JvmDefaultWithoutCompatibility
interface GetDialogProvider<T : GetDialogProvider<T>> {

    /** 对话框委托实例 **/
    val dialogDelegate: DialogDelegate

    /**
     * 显示对画框
     */
    @JvmDefault
    fun show() = dialogDelegate.show()

    /**
     * 隐藏对话框
     */
    @JvmDefault
    fun dismiss() = dialogDelegate.dismiss()

    /**
     * 快速隐藏对话框（无动画）
     */
    @JvmDefault
    fun quickDismiss() = dialogDelegate.quickDismiss()

//    fun autoDismiss()

    /**
     * 设置内容视图
     * @param view 视图
     */
    @JvmDefault
    fun setContentView(view: View) = also { dialogDelegate.setContentView(view) } as T

    /**
     * 设置内容视图
     * @param id 视图资源ID
     */
    @JvmDefault
    fun setContentView(@LayoutRes id: Int) = also { dialogDelegate.setContentView(id) } as T

    /**
     * 对话框是否可取消
     * @param cancelable 是否可取消
     */
    @JvmDefault
    fun cancelable(cancelable: Boolean) = also { dialogDelegate.setCancelable(cancelable) } as T

    /**
     * 对话框点击外部是否可取消
     * @param cancelable 是否可取消
     */
    @JvmDefault
    fun cancelableOutsize(cancelable: Boolean) = also { dialogDelegate.setCancelableOutsize(cancelable) } as T

    /**
     * 原始遮罩透明度
     * @param account 透明度
     */
    @JvmDefault
    fun nativeDimAccount(@FloatRange(from = 0.0, to = 1.0) account: Float) = also {
        dialogDelegate.setNativeDimAccount(account)
    } as T

    /**
     * 自定义遮罩透明度
     * @param account 透明度
     */
    @JvmDefault
    fun customDimAccount(@FloatRange(from = 0.0, to = 1.0) account: Float) = also {
        dialogDelegate.setCustomDimAccount(account)
    } as T

    /**
     * 显示动画
     * @param animator 动画
     */
    @JvmDefault
    fun showAnimator(animator: DialogAnimator) = also { dialogDelegate.setShowAnimator(animator) } as T

    /**
     * 消失动画
     * @param animator 动画
     */
    @JvmDefault
    fun dismissAnimator(animator: DialogAnimator) = also { dialogDelegate.setDismissAnimator(animator) } as T

    /**
     * 对话框位置
     * @param gravity 位置
     */
    @JvmDefault
    fun gravity(gravity: Int) = also { dialogDelegate.setGravity(gravity) } as T

    /**
     * 对话框偏移量，默认单位DIP
     * @param offsetX 横向偏移量
     * @param offsetY 纵向向偏移量
     */
    @JvmDefault
    fun offset(offsetX: Float, offsetY: Float) = also { dialogDelegate.setOffset(offsetX, offsetY) } as T

    /**
     * 对话框偏移量
     * @param offsetX 横向偏移量
     * @param offsetY 纵向向偏移量
     * @param unit 值单位[SizeUnit]
     */
    @JvmDefault
    fun offset(offsetX: Float, offsetY: Float, unit: SizeUnit) = also {
        dialogDelegate.setOffset(offsetX, offsetY, unit)
    } as T

    /**
     * 生命周期处理
     */
    fun onCreating(savedInstanceState: Bundle?) {}

    /**
     * 回退拦截
     */
    @JvmDefault
    fun onBackPressed() = false

    /**
     * 分发触摸事件
     * @param ev 事件
     */
    @JvmDefault
    fun dispatchTouchEvent(ev: MotionEvent) = false

    /**
     * 配置发生变化
     * @param newConfig 新配置
     */
    @JvmDefault
    fun onConfigurationChanged(newConfig: Configuration) {}
}