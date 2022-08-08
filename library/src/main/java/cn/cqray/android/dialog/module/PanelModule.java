package cn.cqray.android.dialog.module;

import android.animation.Animator;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.SizeUtils;

import cn.cqray.android.anim.AnimatorListener;
import cn.cqray.android.anim.ViewAnimator;
import cn.cqray.android.dialog.BaseDialog;
import cn.cqray.android.dialog.DialogLiveData;
import cn.cqray.android.dialog.amin.BounceIn;
import cn.cqray.android.dialog.amin.BounceOut;
import cn.cqray.android.dialog.amin.DialogAnimator;

/**
 * 对话框面板委托
 * @author Cqray
 */
public final class PanelModule extends ViewModule<FrameLayout> {

    /** 对话框根界面 **/
    private View mRootView;
    /** 对话框实例 **/
    private final BaseDialog<?> mDialog;
    /** 面板相关尺寸, 依次为宽度dp值、宽度比例、宽度最小值, 宽度最大值、高度dp值、高度比例值、高度最小值，高度最大值。 **/
    private final Float[] mSizeArray = new Float[8];
    /** 对话框显示、消除动画，提示显示、消失动画 **/
    private final DialogAnimator[] mAnimators = new DialogAnimator[2];
    /** 对话框位置 **/
    private final DialogLiveData<Integer> mGravity = new DialogLiveData<>();
    /** 对话框偏移 **/
    private final DialogLiveData<float[]> mOffset = new DialogLiveData<>();
    /** 面板宽、高 **/
    private final DialogLiveData<int[]> mSize = new DialogLiveData<>();
    /** 面板大小请求 **/
    private final DialogLiveData<Object> mRequestSize = new DialogLiveData<>();
    /** 父级生命周期监听 **/
    private final LifecycleEventObserver mParentObserver;

    public PanelModule(BaseDialog<?> dialog) {
        mDialog = dialog;
        setBackgroundColor(Color.WHITE);
        mParentObserver = (LifecycleEventObserver) (source, event) -> {
            if (event == Lifecycle.Event.ON_DESTROY) {
                if (dialog.getLifecycle()
                        .getCurrentState()
                        .isAtLeast(Lifecycle.State.INITIALIZED)) {
                    mDialog.quickDismiss();
                }
            }
        };
        mDialog.getParentLifecycleOwner()
                .getLifecycle()
                .addObserver(mParentObserver);
    }

    public void setRootView(View view) {
        mRootView = view;
    }

    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull FrameLayout view) {
        super.observe(owner, view);
        // 设置位置情况
        mGravity.observe(owner, aInt -> {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent.getParent() instanceof FrameLayout) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) parent.getLayoutParams();
                params.gravity = aInt;
                parent.setLayoutParams(params);
            }
        });
        // 设置偏移位置监听
        mOffset.observe(owner, floats -> {
            View v = (View) view.getParent();
            v.setTranslationX(floats[0]);
            v.setTranslationY(floats[1]);
        });
        // 设置面板大小监听
        mSize.observe(owner, ints -> {
            View v = (View) view.getParent();
            ViewGroup.LayoutParams params = v.getLayoutParams();
            if (params.width != ints[0] || params.height != ints[1]) {
                params.width = ints[0];
                params.height = ints[1];
                v.requestLayout();
            }
        });
        // 设置请求面板大小监听
        mRequestSize.observe(owner, o -> requestNewSize());
        // 销毁资源
        owner.getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
            if (event == Lifecycle.Event.ON_DESTROY) {
                for (DialogAnimator animator : mAnimators) {
                    if (animator != null && animator.isRunning()) {
                        animator.cancel();
                    }
                }
            }
        });
    }

    /**
     * 是否面板动画正在进行中
     */
    public boolean isPanelAnimatorRunning() {
        // 是否正在显示（显示）动画
        boolean showing = mAnimators[0] != null && mAnimators[0].isRunning();
        // 是否正在显示（消除）动画
        boolean dismissing = mAnimators[1] != null && mAnimators[1].isRunning();
        // 两者满足其一，则动画进行中
        return showing || dismissing;
    }

    /**
     * 面板是否正在消除
     */
    public boolean isDismissing() {
        return mAnimators[1] != null && mAnimators[1].isRunning();
    }

    /**
     * 执行面板动画，返回动画时长
     * @param show 是否是显示动画
     */
    protected void doPanelAnimator(boolean show, ViewAnimator.Callback callback) {
        if (getView() != null) {
            // 获取对应动画
            DialogAnimator animator;
            if (show) {
                animator = mAnimators[0] == null ? new BounceIn() : mAnimators[0];
            } else {
                animator = mAnimators[1] == null ? new BounceOut() : mAnimators[1];
            }
            // 动画没有在运行，才继续操作
            if (!animator.isRunning()) {
                // 设置目标对象
                animator.setTarget(getView());
                // 设置监听
                animator.addAnimatorListener(new AnimatorListener() {

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (show) {
                            mDialog.onShow(mDialog.requireDialog());
                        } else {
                            mDialog.quickDismiss();
                            mDialog.getParentLifecycleOwner()
                                    .getLifecycle()
                                    .removeObserver(mParentObserver);
                        }
                    }

                });
                // 开始面板动画
                animator.start();
            }
            animator.getDuration(callback);
        }
    }

    /**
     * 显示面板
     */
    public void show(ViewAnimator.Callback callback) {
        doPanelAnimator(true, callback);
    }

    /**
     * 消除面板
     */
    public void dismiss(ViewAnimator.Callback callback) {
        doPanelAnimator(false, callback);
    }

    public void setShowAnimator(DialogAnimator animator) {
        mAnimators[0] = animator;
    }

    public void setDismissAnimator(DialogAnimator animator) {
        mAnimators[1] = animator;
    }

    public void setGravity(int gravity) {
        mGravity.setValue(gravity);
    }

    public void setOffset(float offsetX, float offsetY) {
        setOffset(offsetX, offsetY, TypedValue.COMPLEX_UNIT_DIP);
    }

    public void setOffset(float offsetX, float offsetY, int unit) {
        float [] array = new float[2];
        array[0] = SizeUtils.applyDimension(offsetX, unit);
        array[1] = SizeUtils.applyDimension(offsetY, unit);
        mOffset.setValue(array);
    }

    @Override
    public void setWidth(float width) {
        setWidth(width, TypedValue.COMPLEX_UNIT_DIP);
    }

    @Override
    public void setWidth(float width, int unit) {
        synchronized (PanelModule.class) {
            mSizeArray[0] = width <= 0 ? null : SizeUtils.applyDimension(width, unit);
            mSizeArray[1] = null;
        }
        mRequestSize.setValue(null);
    }

    public void setWidthScale(float scale) {
        synchronized (PanelModule.class) {
            mSizeArray[0] = null;
            mSizeArray[1] = scale <= 0 || scale > 1 ? null : scale;
        }
        mRequestSize.setValue(null);
    }

    public void setWidthMin(float min, int unit) {
        synchronized (PanelModule.class) {
            mSizeArray[2] = min <= 0 ? null : SizeUtils.applyDimension(min, unit);
        }
        mRequestSize.setValue(null);
    }

    public void setWidthMax(float max, int unit) {
        synchronized (PanelModule.class) {
            mSizeArray[3] = max <= 0 ? null : SizeUtils.applyDimension(max, unit);
        }
        mRequestSize.setValue(null);
    }

    @Override
    public void setHeight(float height) {
        setHeight(height, TypedValue.COMPLEX_UNIT_DIP);
    }

    @Override
    public void setHeight(float height, int unit) {
        synchronized (PanelModule.class) {
            mSizeArray[4] = height <= 0 ? null : SizeUtils.applyDimension(height, unit);
            mSizeArray[5] = null;
        }
        mRequestSize.setValue(null);
    }

    public void setHeightScale(float scale) {
        synchronized (PanelModule.class) {
            mSizeArray[4] = null;
            mSizeArray[5] = scale <= 0 || scale > 1 ? null : scale;
        }
        mRequestSize.setValue(null);
    }

    public void setHeightMin(float min, int unit) {
        synchronized (PanelModule.class) {
            mSizeArray[6] = min <= 0 ? null : SizeUtils.applyDimension(min, unit);
        }
        mRequestSize.setValue(null);
    }

    public void setHeightMax(float max, int unit) {
        synchronized (PanelModule.class) {
            mSizeArray[7] = max <= 0 ? null : SizeUtils.applyDimension(max, unit);
        }
        mRequestSize.setValue(null);
    }

    protected float getValue(float value, Float min, Float max) {
        float result = value;
        if (min != null) {
            result = Math.max(value, min);
        }
        if (max != null) {
            result = Math.min(result, max);
        }
        if (min != null && max != null && min > max) {
            result = value;
        }
        return result;
    }

    protected void requestNewSize() {
        if (mRootView != null) {
            mRootView.post(() -> {
                // 获取根布局的宽高
                int usableWidth = mRootView.getMeasuredWidth();
                int usableHeight = mRootView.getMeasuredHeight();
                // 宽高
                float w, h;
                // 面板相关尺寸,
                // 依次为宽度dp值、宽度比例、宽度最小值, 宽度最大值、
                // 高度dp值、高度比例值、高度最小值，高度最大值。
                Float widthDp = mSizeArray[0];
                Float widthScale = mSizeArray[1];
                Float minWidth = mSizeArray[2];
                Float maxWidth = mSizeArray[3];
                Float heightDp = mSizeArray[4];
                Float heightScale = mSizeArray[5];
                Float minHeight = mSizeArray[6];
                Float maxHeight = mSizeArray[7];
                int[] size = new int[2];
                // 宽度计算
                if (widthDp != null) {
                    w = getValue(widthDp, minWidth, maxWidth);
                } else if (widthScale != null) {
                    w = widthScale * usableWidth;
                    w = getValue(w, minWidth, maxWidth);
                } else {
                    w = minWidth != null ? minWidth : -2;
                }
                size[0] = (int) w;
                // 高度计算
                if (heightDp != null) {
                    h = getValue(heightDp, minHeight, maxHeight);
                } else if (heightScale != null) {
                    h = heightScale * usableHeight;
                    h = getValue(h, minHeight, maxHeight);
                } else {
                    h = minHeight != null ? minHeight : -2;
                }
                size[1] = (int) h;
                mSize.setValue(size);
            });
        }
    }
}
