package cn.cqray.demo.dialog;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import cn.cqray.android.dialog.BottomAlterDialog;
import cn.cqray.android.dialog.GetAlterDialog;
import cn.cqray.android.dialog.GetDialog;

public class MainActivity extends AppCompatActivity {

//    ViewComponent<View> viewViewC = new ViewComponent<View>(this, {
//            DialogUtils.INSTANCE.getViewBinding(ActivityMainBinding.class,this.getLayoutInflater())
//    });

    BottomAlterDialog dialog = new BottomAlterDialog(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ViewC<View> viewViewC = new ViewC<View>(this, ActivityMainBinding.class);

        setContentView(R.layout.activity_main);


        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int [] location = new int[2];
                v.getLocationOnScreen(location);

//                Te.show(MainActivity.this);

//                dialog.btnTextSizes(1, 2,3, SizeUnit.DIP);
//                dialog.setContentView(R.layout.activity_main);
//                dialog.show();

//                GetAlterDialog.builder(MainActivity.this)
//                        .buttonTexts("12", "56")
//                        .setContentView(R.layout.activity_main)
//                        .show();

                GetAlterDialog.builder(MainActivity.this)
                        .setContentView(R.layout.activity_main)
                        .show();

//
//                GetDialog.builder(MainActivity.this)
//                        .setContentView(R.layout.activity_main)
//                        .show();

//
//                if (dialog == null) {
//
//                    dialog = new BaseDialog(MainActivity.this);
//                    dialog.setContentView(R.layout.activity_content);
////                            .width(200)
////                            .height(100)
////                            .title("车哈哈还是算法还是算法还是算法还是算法")
////                            .startVisible(false)
////                            .gravity(GravityCompat.END)
////                            //.showAnimator(new BounceIn())
////                            .offset(-20, 20)
////                            .cornerRadius(10);
//                }
//                dialog.show();
            }
        });


//        ViewC<View> viewViewC = new ViewC<View>(this, ActivityMainBinding.class);

//        TextView tv = viewViewC.getView().findViewById(R.id.tv);
//        tv.setText("666666666666");

//        viewViewC.setBackgroundColor(Color.BLACK);
    }
//    BaseDialog dialog ;


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