package com.example.alan.myapplication;

//import android.animation.ObjectAnimator;
import com.nineoldandroids.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Linjf on 2016/3/4 0004.
 */
public class TestPassWordDialog implements View.OnClickListener {
    private String Tag = TestPassWordDialog.class.getName();
    private Context context;
    private Dialog mDialog;
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
//    private float dip_density;
    /**
     * 累积  密码输入
     */
    private StringBuilder pwdBuilder = new StringBuilder(6);
    /**
     * 布局 键盘 部分
     */
    private View keyBoardPart;

    private boolean keyboardIsHide = false;
    /**
     * 键盘部分 初始 Y值
     */
    private float keyBoardPartY;
    /**
     * 键盘部分 初始 高
     */
    private float keyBoardPartHeight;
    /**
     *  确认 回调
     * */
    private OnOkButtonClickedListener onOkButtonClickedListener;
    /**
     *  要显示的金额 ，可有可无
     * */
    private String Amt;

    public TestPassWordDialog(Context context, String amt,OnOkButtonClickedListener onOkButtonClickedListener) {
        this.onOkButtonClickedListener = onOkButtonClickedListener;
        this.context = context;
        if(amt==null){
            Amt="";
        }else
        Amt = amt;
    }
//    @TargetApi(21)
//    public void create() {
//        if (context != null) {
//            mAlertDialog=new AlertDialog.Builder(context).setView(R.layout.common_dialog_password).create();
////            mAlertDialog = new AlertDialog.Builder(context).create();
//        }
//    }

    public void createAndShow() {
             /*
          默认 布局资源文件
         */

        mDialog = new Dialog(context, R.style.add_dialog);
        initView();
        setDialogWitdh();
        initKeyboardGridView();
        mDialog.show();
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

    private void initView() {
        int LAYOUT_ID = R.layout.common_dialog_password;
        View myLayout = mDialog.getLayoutInflater().inflate(LAYOUT_ID, null, false);
        blackPointParent = (LinearLayout) myLayout.findViewById(R.id.ll_black_point_series);
        blackPointParent.setOnClickListener(this);
        keyboardGridView = (GridView) myLayout.findViewById(R.id.gv_keyboard);
        keyboardGridView.setOnItemClickListener(myOnItemClickListener);
        TextView tv_FinishEdit_btn = (TextView) myLayout.findViewById(R.id.tv_finish);
        tv_FinishEdit_btn.setOnClickListener(this);
        keyBoardPart = myLayout.findViewById(R.id.ll_keyboard_part);
        mDialog.setContentView(myLayout);
        TextView tv_confirm_btn= (TextView) myLayout.findViewById(R.id.tv_confirm_btn);
        tv_confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwdResult=pwdBuilder.toString();
                if (pwdResult.length()==6){
                    onOkButtonClickedListener.onClicked(mDialog, pwdResult);
                }

            }
        });
        TextView tv_Amt= (TextView) myLayout.findViewById(R.id.tv_amt);
        tv_Amt.setText("¥" + Amt);
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

    private AdapterView.OnItemClickListener myOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.i(Tag, "onItemClick at pos->" + position);
            if (position == 11) {
                int length = pwdBuilder.length();
                if (length > 0) {
                    pwdBuilder = pwdBuilder.deleteCharAt(length - 1);
                    updateBlackPoints(pwdBuilder.length());
                } else
                    Log.i(Tag, "StringBuild.length should bigger than 0");
            } else if (position == 9) {

            } else if (position > 11) {
                Log.i(Tag, "Dose not exist this item");
            } else {
                if (pwdBuilder.length() >= 6)
                    return;
                pwdBuilder.append(keyboardNum.get(position));
                int realNum = Integer.parseInt(keyboardNum.get(position));
                updateBlackPoints(pwdBuilder.length());
                Log.i(Tag, "RealNum=" + realNum);
            }
            Log.i(Tag, "build now=" + pwdBuilder.toString());
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_finish:
                hideKeyboard();
                break;
            case R.id.ll_black_point_series:
                showKeyboard();
                break;
        }
    }

    private void showKeyboard() {
        Log.i(Tag, "ll_black_point_series pressed");
        if (keyboardIsHide) {
            ObjectAnimator objAnimShow =
                    ObjectAnimator.ofFloat(keyBoardPart, "y", keyBoardPartY + keyBoardPartHeight, keyBoardPartY);
            objAnimShow.setDuration(400);
            objAnimShow.start();
            keyboardIsHide = false;
        }
    }

    private void hideKeyboard() {
        Log.i(Tag, "tv_finish clicked");
        keyBoardPartY = keyBoardPart.getY();
        keyBoardPartHeight = keyBoardPart.getHeight();
        ObjectAnimator objAnimHide =
                ObjectAnimator.ofFloat(keyBoardPart, "y", keyBoardPartY, keyBoardPartY + keyBoardPartHeight);
        objAnimHide.setDuration(400);
        objAnimHide.start();
        keyboardIsHide = true;
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
     * @param ctx     上下文
     * @param dpValue dp 值
     * @return 像素值
     */
    private int dip2px(Context ctx, float dpValue) {

        return (int) (dpValue * ctx.getResources().getDisplayMetrics().density + 0.5f);
    }

    /**
     *  确定按钮回调，传回相关信息
     * */
    public interface OnOkButtonClickedListener{

        void onClicked(DialogInterface dialog,String pwd);
    }
}
