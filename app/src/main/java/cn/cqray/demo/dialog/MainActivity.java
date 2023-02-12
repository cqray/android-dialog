package cn.cqray.demo.dialog;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.view.View;

import cn.cqray.android.ab.Te;

public class MainActivity extends AppCompatActivity {

//    ViewComponent<View> viewViewC = new ViewComponent<View>(this, {
//            DialogUtils.INSTANCE.getViewBinding(ActivityMainBinding.class,this.getLayoutInflater())
//    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ViewC<View> viewViewC = new ViewC<View>(this, ActivityMainBinding.class);

        setContentView(R.layout.activity_main);



//        new ViewComponent<View>();

//        FragmentManager fm = getSupportFragmentManager();
//        FragmentTransaction ft = fm.beginTransaction();
//        ft.add(R.id.content, Te.get(this));
//        ft.commit();


        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int [] location = new int[2];
                v.getLocationOnScreen(location);

                Te.show(MainActivity.this);

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

}