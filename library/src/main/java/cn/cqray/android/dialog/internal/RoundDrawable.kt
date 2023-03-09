package cn.cqray.android.dialog.internal

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable

/**
 * 圆角图片
 * @author Cqray
 */
internal class RoundDrawable(drawable: Drawable) : Drawable() {
    /** 圆角数据  */
    private val radii = FloatArray(8)

    /** 图片画笔  */
    private val paint: Paint

    /** 创建一个矩形，将图片绘制到该矩形上  */
    private var rectF: RectF? = null

    /** 需要绘制的图片  */
    private var bitmap: Bitmap

    init {
        if (drawable is BitmapDrawable) {
            bitmap = drawable.bitmap
        } else {
            val w = drawable.intrinsicWidth
            val h = drawable.intrinsicHeight
            bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ALPHA_8)
            // 将Bitmap对象放到画布里面
            Canvas(bitmap).let {
                // 设置图形大小
                drawable.setBounds(0, 0, w, h)
                // 将drawable画到画布上
                drawable.draw(it)
            }
        }
        // 获取图片的着色器
        val shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        // 创建画笔
        paint = Paint()
        // 画笔防锯齿
        paint.isAntiAlias = true
        // 将图片设置到画笔上
        paint.shader = shader
    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setBounds(left, top, right, bottom)
        rectF = RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
    }

    override fun draw(canvas: Canvas) {
        val path = Path().also { it.addRoundRect(rectF!!, radii, Path.Direction.CW) }
        canvas.drawPath(path, paint)
    }

    override fun getIntrinsicWidth() = bitmap.width

    override fun getIntrinsicHeight() = bitmap.height

    override fun setAlpha(i: Int) = run { paint.alpha = i }

    override fun setColorFilter(colorFilter: ColorFilter?) = run { paint.colorFilter = colorFilter }

    override fun getOpacity() = PixelFormat.TRANSLUCENT

    /**
     * 设置圆角大小，每个圆角都有两个半径值[X，Y]。圆角按左上、右上、右下、左下排列
     * @param radii 8个值的数组，4对[X，Y]半径
     */
    fun setRadii(radii: FloatArray) {
        this.radii.forEachIndexed { i, _ ->
            this.radii[i] = radii.getOrNull(i) ?: 0F
        }
        invalidateSelf()
    }
}