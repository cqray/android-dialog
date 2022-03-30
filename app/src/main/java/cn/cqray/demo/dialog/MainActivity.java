package cn.cqray.demo.dialog;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
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
import cn.cqray.android.dialog.PanelModule;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout panel = findViewById(R.id.panel);

        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int [] location = new int[2];
                v.getLocationOnScreen(location);
                Log.e("数据", location[0] + "|" + location[1]);
//                new BottomMessageDialog()
//                        .title("我爱你66666666666666666666666")
////                        .titleCenter()
////                        .titleHeight(60)
//                        .content("55555555555555555")
//                        .dividerColor(Color.BLACK)
//                        .cornerRadius(4)
////                        .heightMax(400)
////                        .height(200)
////                        .heightMin(300)
////                        .titleCenter()
////                        .leftVisible(false)
////                        .btnTextColor(Color.GRAY, Color.GREEN)
//                        .contentPadding(16)
//                        .widthMax(300)
//                        .widthScale(0.8f)
//                        .backgroundColor(Color.WHITE)
//                        .show(MainActivity.this);
                if (dialog == null) {

                    dialog = new BottomAlterDialog<>(MainActivity.this)
                            .width(300)
                            .height(200)
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
                                    dialog.dismiss();
                                }
                            })
                            .addDismissListener(new OnDismissListener() {
                                @Override
                                public void onDismiss() {
                                    Toast.makeText(MainActivity.this, "哈哈", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addCancelListener(new OnCancelListener() {
                                @Override
                                public void onCancel() {
                                    Toast.makeText(MainActivity.this, "哈哈2", Toast.LENGTH_SHORT).show();
                                }
                            })
                            //.showAnimator(new NoAnimator())
                            //.nativeDimAmount(0.15f)
                            //.blackStatusBar(true)
                            .cornerRadius(10);
                }
                dialog.show();

//                new BaseDialog(MainActivity.this) {
//                    @Override
//                    public void onCreating(Bundle savedInstanceState) {
//                        super.onCreating(savedInstanceState);
//                        setContentView(R.layout.activity_content);
//                    }
//                }
//                .offset(20, 20)
////                .blackStatusBar(true)
//                .show();
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