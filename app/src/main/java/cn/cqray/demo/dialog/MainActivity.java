package cn.cqray.demo.dialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import cn.cqray.android.dialog.BottomAlterDialog;
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



//        new ViewComponent<View>();

        DialogFragment fragment = GetDialog.builder(this)
                .setContentView(R.layout.activity_main)
                .getDialogDelegate().getFragment();
//        fragment.setShowsDialog(false);

        ViewPager2 pager2 = findViewById(R.id.content);
        FragmentStateAdapter adapter = new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return fragment;
            }

            @Override
            public int getItemCount() {
                return 1;
            }
        };
        pager2.setAdapter(adapter);

//        Log.e("数据", "1111" + (fragment == null));
//        FragmentManager fm = getSupportFragmentManager();
//        FragmentTransaction ft = fm.beginTransaction();
//        ft.add(R.id.content, fragment);
//        ft.commit();

//        cn.cqray.android.dialog.internal.DialogFragment


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

                GetDialog.builder(MainActivity.this)
                        .setContentView(R.layout.activity_main)
                        .show();

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