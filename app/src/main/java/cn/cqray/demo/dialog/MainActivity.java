package cn.cqray.demo.dialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.Locale;

import cn.cqray.android.dialog.BaseDialog;
import cn.cqray.android.dialog.BottomAlterDialog;

import cn.cqray.android.dialog.listener.OnCancelListener;
import cn.cqray.android.dialog.listener.OnDismissListener;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int [] location = new int[2];
                v.getLocationOnScreen(location);

                if (dialog == null) {

                    dialog = new BottomAlterDialog<>(MainActivity.this)
                            .width(200)
                            .height(100)
                            .title("车哈哈还是算法还是算法还是算法还是算法")
//                            .titleVisible(true)
//                            .titlePadding(16)
//                            .titleCenter()
                            .startVisible(false)
//                            .offset(0, -50)

                            //.endVisible(false)
                            //.startBackgroundResource(R.color.colorAccent)
                            //.endBackgroundResource(R.color.colorAccent)
                            //.content("是噶好尬gsa")
                            //.contentTextSize(16)
                            //.contentTextColor(Color.RED)
                            //.buttonTextSize(16)
                            //.buttonBackgroundResource(R.color.colorAccent)
//                            .observeShow(MainActivity.this, new Observer<BaseDialog<?>>() {
//                                @Override
//                                public void onChanged(BaseDialog<?> t) {
//
//                                }
//                            })
                            .addEndClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.getDelegate().showTip("66666666");
                                    //dialog.dismiss();
                                }
                            })
                            .addOnDismissListener(new OnDismissListener() {
                                @Override
                                public void onDismiss() {
                                    Toast.makeText(MainActivity.this, "哈哈", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnCancelListener(new OnCancelListener() {
                                @Override
                                public void onCancel() {
                                    Toast.makeText(MainActivity.this, "哈哈2", Toast.LENGTH_SHORT).show();
                                }
                            })
                            //.showAnimator(new NoAnimator())
                            //.nativeDimAmount(0.5f)
                            //.blackStatusBar(true)
                            .gravity(GravityCompat.END)
                            //.showAnimator(new BounceIn())
                            .offset(-20, 20)
                            .cornerRadius(10);
                }
                new Thread(() -> dialog.show()).start();

            }
        });
    }
    BaseDialog dialog ;

    static boolean isFull(Activity act) {
        if (act == null || act.getWindow() == null) {
            return false;
        }
        Window window = act.getWindow();
        return (window.getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN)
                == WindowManager.LayoutParams.FLAG_FULLSCREEN;
    }

    private boolean isRtl() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == View.LAYOUT_DIRECTION_RTL;
        }
        return false;
    }

    static int getStatusBarHeight() {
        int statusBarHeight = 0;
        int resourceId = Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = Resources.getSystem().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    public int getNavigationBarHeight() {
        int navigationBarHeight = 0;
        int resourceId = Resources.getSystem().getIdentifier("navigation_bar_height","dimen","android");
        if (resourceId > 0) {
            navigationBarHeight = Resources.getSystem().getDimensionPixelSize(resourceId);
        }
        return navigationBarHeight;
    }
}