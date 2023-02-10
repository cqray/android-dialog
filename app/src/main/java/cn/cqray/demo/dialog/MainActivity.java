package cn.cqray.demo.dialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;


import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.Locale;

import cn.cqray.android.dialog.module2.BaseDialog;
import cn.cqray.android.dialog.BottomAlterDialog;

import cn.cqray.android.dialog.listener.OnCancelListener;
import cn.cqray.android.dialog.listener.OnDismissListener;
import cn.cqray.android.dialog.module2.BaseDialog2;

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

                    dialog = new BaseDialog2(MainActivity.this);
                   // dialog.setContentView(R.layout.activity_content);
//                            .width(200)
//                            .height(100)
//                            .title("车哈哈还是算法还是算法还是算法还是算法")
//                            .startVisible(false)
//                            .gravity(GravityCompat.END)
//                            //.showAnimator(new BounceIn())
//                            .offset(-20, 20)
//                            .cornerRadius(10);
                }
                dialog.show();
            }
        });
    }
    BaseDialog2 dialog ;

}