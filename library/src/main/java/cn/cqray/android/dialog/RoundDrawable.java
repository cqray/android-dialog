package cn.cqray.android.dialog;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import com.blankj.utilcode.util.SizeUtils;

import org.jetbrains.annotations.Nullable;

import lombok.NonNull;

/**
 * 圆角图片
 * @author Cqray
 */
public class RoundDrawable extends Drawable {
    /** 圆角数量 **/
    private static final int RADII_LENGTH = 8;
    /** 圆角数据 **/
    private float[] mRadii = new float[8];
    /** 图片画笔 **/
    private Paint mPaint;
    /** 创建一个矩形，将图片绘制到该矩形上 **/
    private RectF mRectF;
    /** 需要绘制的图片 **/
    private Bitmap mBitmap;

    public RoundDrawable(Bitmap bitmap) {
        mBitmap = bitmap;
        initPaint();
    }

    public RoundDrawable(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            mBitmap = bd.getBitmap();
            initPaint();
            return;
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ALPHA_8);
        // 将Bitmap对象放到画布里面
        Canvas canvas = new Canvas(mBitmap);
        drawable.setBounds(0, 0, w, h);
        // 将drawable画到画布上
        drawable.draw(canvas);
        // 初始化画笔
        initPaint();
    }

    private void initPaint() {
        // 获取图片的着色器
        BitmapShader shader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        // 创建画笔
        mPaint = new Paint();
        // 画笔防锯齿
        mPaint.setAntiAlias(true);
        // 将图片设置到画笔上
        mPaint.setShader(shader);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        mRectF = new RectF(left, top, right, bottom);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Path path = new Path();
        path.addRoundRect(mRectF, mRadii, Path.Direction.CW);
        canvas.drawPath(path, mPaint); }

    @Override
    public int getIntrinsicWidth() {
        return mBitmap.getWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return mBitmap.getHeight();
    }

    @Override
    public void setAlpha(int i) {
        mPaint.setAlpha(i);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    /**
     * 设置圆角大小，每个圆角都有两个半径值[X，Y]。圆角按左上、右上、右下、左下排列
     * <p>默认单位DP</p>
     * @param radii 8个值的数组，4对[X，Y]半径
     */
    public void setRadii(float [] radii) {
        setRadii(radii, TypedValue.COMPLEX_UNIT_DIP);
    }

    /**
     * 设置圆角大小，每个圆角都有两个半径值[X，Y]。圆角按左上、右上、右下、左下排列
     * @param radii 8个值的数组，4对[X，Y]半径
     * @param unit  值单位
     */
    public void setRadii(float [] radii, int unit) {
        if (radii == null || radii.length < RADII_LENGTH) {
            throw new IllegalArgumentException("Radii array length must >= " + RADII_LENGTH);
        }
        for (int i = 0; i < RADII_LENGTH; i++) {
            mRadii[i] = SizeUtils.applyDimension(radii[i], unit);
        }
        invalidateSelf();
    }

    /**
     * 设置圆角大小
     * <p>默认单位DP</p>
     * @param radius 圆角半径
     */
    public void setRadius(float radius) {
        setRadius(radius, TypedValue.COMPLEX_UNIT_DIP);
    }

    /**
     * 设置圆角大小
     * @param radius 圆角半径
     * @param unit 值单位
     */
    public void setRadius(float radius, int unit) {
        float [] radii = new float[RADII_LENGTH];
        for (int i = 0; i < RADII_LENGTH; i++) {
            radii[i] = radius;
        }
        setRadii(radii, unit);
    }
}
