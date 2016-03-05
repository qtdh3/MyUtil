package com.example.alan.myapplication;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.nfc.Tag;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Linjf on 2016/3/4 0004.
 */
public class TestPassWordDialog implements View.OnClickListener{
    private String Tag = TestPassWordDialog.class.getName();
    private Context context;
    private Dialog mDialog;
    //    /**
//     * 输入密码的接收实体，
//     */
//    private EditText et_Input_Password;
    /**
     * 表面掩盖视图
     */
    private LinearLayout blackPointParent;
    /**
     * 密码安全键盘
     */
    private GridView keyboardGridView;
    /**
     * 存放 0~9 数字 数组
     */
    private List<String> keyboardNum;
    /**
     * dp 换算倍数  密度
     */
    private float dip_density;
    /**
     * 累积  密码输入
     */
    private StringBuilder builder = new StringBuilder(6);
    /**
     *  布局 键盘 部分
     * */
    private View keyBoardPart;

    private boolean keyboardIsHide=false;
    /**
     * 键盘部分 初始 Y值
     * */
    private float keyBoardPartY;
    /**
     * 键盘部分 初始 高
     * */
    private float keyBoardPartHeight;

    public TestPassWordDialog(Context context) {
        this.context = context;
    }
//    @TargetApi(21)
//    public void create() {
//        if (context != null) {
//            mAlertDialog=new AlertDialog.Builder(context).setView(R.layout.common_dialog_password).create();
////            mAlertDialog = new AlertDialog.Builder(context).create();
//        }
//    }

    public void create() {
             /*
          默认 布局资源文件
         */
        int LAYOUT_ID = R.layout.common_dialog_password;
        mDialog = new Dialog(context, R.style.add_dialog);
        View myLayout = mDialog.getLayoutInflater().inflate(LAYOUT_ID, null, false);
        blackPointParent = (LinearLayout) myLayout.findViewById(R.id.ll_black_point_series);
        blackPointParent.setOnClickListener(this);
        keyboardGridView = (GridView) myLayout.findViewById(R.id.gv_keyboard);
        keyboardGridView.setOnItemClickListener(myOnItemClickListener);
        TextView tv_Finish_btn= (TextView) myLayout.findViewById(R.id.tv_finish);
        tv_Finish_btn.setOnClickListener(this);
        keyBoardPart=myLayout.findViewById(R.id.ll_keyboard_part);
        mDialog.setContentView(myLayout);
        setDialogWitdh();
        initKeyboardGridView();

        //设置监听  用于自动开启默认输入法
//        mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(DialogInterface dialog) {
//                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//
//            }
//        });


    }

    private AdapterView.OnItemClickListener myOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.i(Tag, "onItemClick at pos->" + position);
            if (position == 11) {
                int length = builder.length();
                if (length > 0) {
                    builder = builder.deleteCharAt(length - 1);
                    updateBlackPoints(builder.length());
                } else
                    Log.i(Tag, "StringBuild.length should bigger than 0");
            } else if (position == 9) {

            } else if (position > 11) {
                Log.i(Tag, "Dose not exist this item");
            } else {
                if (builder.length() >= 6)
                    return;
                builder.append(keyboardNum.get(position));
                int realNum = Integer.parseInt(keyboardNum.get(position));
                updateBlackPoints(builder.length());
                Log.i(Tag, "RealNum=" + realNum);
            }
            Log.i(Tag, "build now=" + builder.toString());
        }
    };

    private void initKeyboardGridView() {
        keyboardNum = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            keyboardNum.add(String.valueOf(i));
        }
        Collections.shuffle(keyboardNum);
        for (int i = 0; i < 10; i++) {
            Log.i(Tag, "Nums after shuffle" + keyboardNum.get(i));
        }
        keyboardNum.add(9, "");
        keyboardNum.add("image");
        Log.i(Tag, "keyboardNum.size=" + keyboardNum.size());
        MyGridViewAdapter myAdapter = new MyGridViewAdapter(keyboardNum);
        keyboardGridView.setAdapter(myAdapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_finish:
                Log.i(Tag, "tv_finish clicked");
//                Animation anim_hide= AnimationUtils.loadAnimation(mDialog.getContext(),R.anim.anim_keyboard_hile);
//                anim_hide.setFillAfter(true);
//                keyBoardPart.startAnimation(anim_hide);

//                AnimatorSet animatorSet= (AnimatorSet) AnimatorInflater.loadAnimator(mDialog.getContext(),R.animator.animator_keyboard_hide);
//                animatorSet.setTarget(keyBoardPart);
//                animatorSet.start();
                 keyBoardPartY= keyBoardPart.getY();
                keyBoardPartHeight=keyBoardPart.getHeight();
                ObjectAnimator objAnimHide=
                ObjectAnimator.ofFloat(keyBoardPart, "y", keyBoardPartY, keyBoardPartY + keyBoardPartHeight);
                objAnimHide.setDuration(400);
                objAnimHide.start();
                keyboardIsHide=true;
                break;
            case R.id.ll_black_point_series:
                Log.i(Tag, "ll_black_point_series pressed");
                if (keyboardIsHide){
//                    Animation anim_show= AnimationUtils.loadAnimation(mDialog.getContext(),R.anim.anim_keyboard_show);
////                anim_show.setFillAfter(false);
//                    keyBoardPart.startAnimation(anim_show);
                    ObjectAnimator objAnimShow=
                            ObjectAnimator.ofFloat(keyBoardPart, "y",  keyBoardPartY + keyBoardPartHeight,keyBoardPartY);
                    objAnimShow.setDuration(400);
                    objAnimShow.start();
                    keyboardIsHide=false;
                }
                break;
        }
    }

    private class MyGridViewAdapter extends BaseAdapter {
        private List<String> dataList;

        public MyGridViewAdapter(List<String> dataList) {
            this.dataList = dataList;
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int position) {
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View item_view;
            if (position == 11) {
                item_view = new TextView(mDialog.getContext());
                item_view.setBackground(context.getResources().getDrawable(R.drawable.backdelete));
            } else if (position == 9) {
                item_view = new TextView(mDialog.getContext());
            } else {
                item_view = new TextView(mDialog.getContext());
                ((TextView) item_view).setText(dataList.get(position));
                item_view.setBackgroundColor(Color.WHITE);
                ((TextView) item_view).setGravity(Gravity.CENTER);
                ((TextView) item_view).setTextSize(20);
            }
            ((TextView) item_view).setHeight(dip2px(context, 40));
            return item_view;
        }
    }

    private void setDialogWitdh() {
        Window dialogwindow = mDialog.getWindow();
        WindowManager.LayoutParams layoutParams = dialogwindow.getAttributes();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point windowSize = new Point();
        windowManager.getDefaultDisplay().getSize(windowSize);
        layoutParams.width = windowSize.x;
        //不好用，还得扣去状态栏高度
//        dialogwindow.setLayout(windowSize.x,windowSize.y);
        dialogwindow.setAttributes(layoutParams);
    }

    public void show() {
        if (mDialog != null) {
            mDialog.show();
        }
    }

    private void updateBlackPoints(int length) {
        for (int i = 0; i < 6; i++) {
            View view = blackPointParent.getChildAt(i);
            if (i < length)
                view.setSelected(true);
            else
                view.setSelected(false);
        }
    }

    /**
     * Date:2014-7-22 </br> Desc:dip换算为px
     * <p/>
     * </br>
     *
     * @param ctx
     * @param dpValue
     * @return
     */
    private int dip2px(Context ctx, float dpValue) {

        return (int) (dpValue * ctx.getResources().getDisplayMetrics().density + 0.5f);
    }

}
