package cn.cqray.demo.dialog;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import cn.cqray.android.dialog.BottomAlterDialog;
import cn.cqray.android.dialog.GetAlterDialog;
import cn.cqray.android.dialog.GetDialog;

public class MainActivity extends AppCompatActivity {

    BottomAlterDialog dialog = new BottomAlterDialog(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ViewC<View> viewViewC = new ViewC<View>(this, ActivityMainBinding.class);

        setContentView(R.layout.activity_main);


        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] location = new int[2];
                v.getLocationOnScreen(location);

//                Te.show(MainActivity.this);

//                dialog.btnTextSizes(1, 2,3, SizeUnit.DIP);
//                dialog.setContentView(R.layout.activity_main);
//                dialog.show();

//                GetAlterDialog.builder(MainActivity.this)
//                        .buttonTexts("12", "56")
//                        .setContentView(R.layout.activity_main)
//                        .show();

                GetDialog.builder(MainActivity.this)
                        .asBottomAlter()
                        //.setContentView(R.layout.activity_main)
                        .titleText("提示")
                        .contentText("这是内容")
                        .buttonTexts("知道了")
                        .show();

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("数据", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("数据", "onStop");
    }
}