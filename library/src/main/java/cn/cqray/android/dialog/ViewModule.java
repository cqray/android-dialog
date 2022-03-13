package cn.cqray.android.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

/**
 * View控件模块
 * @author Cqray
 */
public class ViewModule<T extends View> {
    /** 圆角数量 **/
    private static final int RADII_LENGTH = 8;
    /** LifeOwner **/
    private LifecycleOwner mLifecycleOwner;
    /** 圆角 **/
    private float[] mBackgroundRadii;
    /** 颜色 **/
    private int mBackgroundColor = Color.WHITE;
    /** 间隔 **/
    private final MutableLiveData<float[]> mPadding = new MutableLiveData<>();
    /** 显示 **/
    private final MutableLiveData<Integer> mVisibility = new MutableLiveData<>();
    /** 高度 **/
    private final MutableLiveData<Float> mHeight = new MutableLiveData<>();
    /** 高度 **/
    private final MutableLiveData<Float> mWidth = new MutableLiveData<>();
    /** 背景 **/
    private final MutableLiveData<Drawable> mBackground = new MutableLiveData<>();
    /** 背景资源 **/
    private final MutableLiveData<Integer> mBackgroundResource = new MutableLiveData<>();

    public ViewModule(LifecycleOwner owner) {
        if (owner instanceof Activity || owner instanceof Fragment) {
            mLifecycleOwner = owner;
            return;
        }
        throw new IllegalArgumentException("LifecycleOwner must implements on FragmentActivity or Fragment.");
    }

    public void observe(LifecycleOwner owner, final T view) {
        observePadding(owner, new Observer<float[]>() {
            @Override
            public void onChanged(float[] ints) {
                view.setPadding(toPix(ints[0]), toPix(ints[1]), toPix(ints[2]), toPix(ints[3]));
            }
        });
        observeVisibility(owner, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                view.setVisibility(integer);
            }
        });
        observeWidth(owner, new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.width = toPix(aFloat);
                view.requestLayout();
            }
        });
        observeHeight(owner, new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.height = toPix(aFloat);
                view.requestLayout();
            }
        });
        observeBackground(owner, new Observer<Drawable>() {
            @Override
            public void onChanged(Drawable drawable) {
                ViewCompat.setBackground(view, drawable);
            }
        });
        observeBackgroundResource(owner, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                Drawable drawable = ContextCompat.getDrawable(requireContext(), integer);
                requestBackground(drawable);
            }
        });
    }

    public void observeVisibility(LifecycleOwner owner, Observer<Integer> observer) {
        mVisibility.removeObservers(owner);
        mVisibility.observe(owner, observer);
    }

    public void observePadding(LifecycleOwner owner, Observer<float[]> observer) {
        mPadding.removeObservers(owner);
        mPadding.observe(owner, observer);
    }

    public void observeWidth(LifecycleOwner owner, Observer<Float> observer) {
        mWidth.removeObservers(owner);
        mWidth.observe(owner, observer);
    }

    public void observeHeight(LifecycleOwner owner, Observer<Float> observer) {
        mHeight.removeObservers(owner);
        mHeight.observe(owner, observer);
    }

    public void observeBackground(LifecycleOwner owner, Observer<Drawable> observer) {
        mBackground.removeObservers(owner);
        mBackground.observe(owner, observer);
    }

    public void observeBackgroundResource(LifecycleOwner owner, Observer<Integer> observer) {
        mBackgroundResource.removeObservers(owner);
        mBackgroundResource.observe(owner, observer);
    }

    public void setPadding(float l, float t, float r, float b) {
        mPadding.postValue(new float[] {l, t, r, b});
    }

    public void setVisibility(int visibility) {
        mVisibility.postValue(visibility);
    }

    public void setWidth(float width) {
        mWidth.postValue(width);
    }

    public void setHeight(float height) {
        mHeight.postValue(height);
    }

    public void setRadii(float [] radii) {
        if (radii == null || radii.length < RADII_LENGTH) {
            throw new IllegalArgumentException("Radii array length must >= " + RADII_LENGTH);
        }
        mBackgroundRadii = new float[RADII_LENGTH];
        for (int i = 0; i < RADII_LENGTH; i++) {
            mBackgroundRadii[i] = toPix(radii[i]);
        }
        requestBackground();
    }

    public void setRadius(float radius) {
        float [] radii = new float[RADII_LENGTH];
        for (int i = 0; i < RADII_LENGTH; i++) {
            radii[i] = radius;
        }
        setRadii(radii);
    }

    public void setBackground(Drawable drawable) {
        requestBackground(drawable);
    }

    public void setBackgroundColor(int color) {
        mBackgroundColor = color;
        requestBackground();
    }

    public void setBackgroundColor(String color) {
        mBackgroundColor = Color.parseColor(color);
        requestBackground();
    }

    public void setBackgroundResource(@DrawableRes int resId) {
        mBackgroundResource.postValue(resId);
    }

    protected FragmentActivity requireActivity() {
        if (mLifecycleOwner instanceof FragmentActivity) {
            return (FragmentActivity) mLifecycleOwner;
        } else {
            return ((Fragment) mLifecycleOwner).requireActivity();
        }
    }

    protected Context requireContext() {
        return requireActivity();
    }

    protected String getString(@StringRes int resId) {
        return requireContext().getString(resId);
    }

    protected int toPix(float dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return (int) (density * dp + 0.5f);
    }

    private void requestBackground() {
        if (mBackgroundRadii == null) {
            mBackground.postValue(new ColorDrawable(mBackgroundColor));
        } else {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(mBackgroundColor);
            drawable.setCornerRadii(mBackgroundRadii);
            mBackground.postValue(drawable);
        }
    }

    private void requestBackground(Drawable drawable) {
        if (drawable == null) {
            mBackground.postValue(null);
            return;
        }
        if (mBackgroundRadii == null) {
            mBackground.postValue(drawable);
            return;
        }
        boolean equal = true;
        float value = mBackgroundRadii[0];
        for (float v : mBackgroundRadii) {
            if (Math.abs(v - value) < 0.01f) {
                equal = false;
                break;
            }
        }
        if (equal) {
            Bitmap bitmap = Bitmap.createBitmap(
                    drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            drawable.draw(canvas);

            RoundedBitmapDrawable newDrawable = RoundedBitmapDrawableFactory.create(requireContext().getResources(), bitmap);
            newDrawable.setCornerRadius(value);
            newDrawable.setAntiAlias(true);
            mBackground.postValue(newDrawable);
        } else {
            mBackground.postValue(drawable);
        }
    }
}
