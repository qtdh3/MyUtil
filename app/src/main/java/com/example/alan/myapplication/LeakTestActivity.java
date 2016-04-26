package com.example.alan.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Linjf on 2016/4/13 0013.
 */
public class LeakTestActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leak_test);
        findViewById(R.id.textView2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        MyApplication.getActivityList().add(this);
        makegarbage();

    }

    private void makegarbage() {
        List<ImageView> imageViewList=new ArrayList<ImageView>();
        for (int i=0;i<100;i++){
            ImageView imageView=new ImageView(this);
            imageView.setImageResource(R.drawable.backdelete);
            imageViewList.add(imageView);
        }
    }
}
