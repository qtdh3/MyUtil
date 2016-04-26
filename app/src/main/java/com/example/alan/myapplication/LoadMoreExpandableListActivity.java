package com.example.alan.myapplication;

import android.app.Activity;
import android.content.ClipData;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.Toast;

import org.apache.http.entity.StringEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Linjf on 2016/4/21 0021.
 */
public class LoadMoreExpandableListActivity extends Activity {

    /**
     * 是否加载中
     */
    private boolean loading=false;
    /**
     * 是否已经滚动到底部
     */
    private boolean isBootom=false;
    /**
     * 滚动状态
     */
    private ScrollState currentScrollState=ScrollState.IDLE;
    private Handler myHandler;

    private View.OnTouchListener onTouchListener;
    /**
     * 上次 触摸位置
     */
    private float lastY;
    /**
     * 是否为上划 手势
     */
    private boolean isMovingUp=false;
    private MyLoadMoreExpandableListViewAdapter myLoadMoreExpandableListViewAdapter;
    private MyLoadMoreExpandableListViewAdapter.ExpandListDataSet expandListDataSet;

    private LoadMore mOnLoadMoreListener=new LoadMore();

    public enum WithdrawState{
        FINISHED,DOING;
    }

    public enum ScrollState{
        IDLE,
        TOUCH_SCROLL,
        FLING;
    }

    public static class ItemInfo{
        String Amt; //金额
        String time; //时间
        String date; //日期
        WithdrawState withdrawState=WithdrawState.FINISHED;

        public ItemInfo(String amt, String time, String date, WithdrawState withdrawState) {
            Amt = amt;
            this.time = time;
            this.date = date;
            this.withdrawState = withdrawState;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_more_expandable_list);

        myHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };
        initTouchListener();
        initView();

    }

    private void initView() {
        ExpandableListView expandableListView= (ExpandableListView) findViewById(R.id.elv_check);
        expandableListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        expandableListView.setOnScrollListener(new MyScrollListener());
        expandableListView.setOnTouchListener(onTouchListener);
        List<String> groupList=new ArrayList<String>();
        List<ItemInfo> itemInfoList=new ArrayList<ItemInfo>();
        initData(groupList, itemInfoList);
        myLoadMoreExpandableListViewAdapter
                =new MyLoadMoreExpandableListViewAdapter(this, LayoutInflater.from(this),expandListDataSet);
//                =new MyLoadMoreExpandableListViewAdapter(this,groupList,itemInfoList);
        expandableListView.setAdapter(myLoadMoreExpandableListViewAdapter);

    }

    private void initData(List<String> groupList, List<ItemInfo> itemInfoList) {
        //{"AccDate":"","AccTime":"","OrderID":"101412","RefID":"",     这个是订单查询返回的，后期可能包含金额。提现记录查询估计返回类似
          //      "TxnDate":"20160422","TxnTime":"101412"}
        for (int i=0;i<5;i++){
//            groupList.add("group"+i);
            for (int j=0;j<3;j++){
                ItemInfo itemInfo=new ItemInfo("-25","23:5"+j,"2014030"+i,WithdrawState.FINISHED);
                itemInfoList.add(itemInfo);
            }
        }
        processData(groupList, itemInfoList);
    }

    private void processData(List<String> groupList, List<ItemInfo> itemInfoList) {
        Map<String,Integer> checkPositionMap= new HashMap<String, Integer>();  // 用于位置 从二维到一位的转换
        List<Integer> sizeList=new ArrayList<Integer>();        // 用于存储每组的数量
        int size=itemInfoList.size();
        String dateTemp=itemInfoList.get(0).date;
        Integer groupIndexMark=0;   // 在循环中用于  组序号的标记
        Integer childItemIndexMark=0; //在循环中 用于  子项序号的标记
        // 添加第一个项
        groupList.clear();
//        Integer[] firstOne=new Integer[]{groupIndexMark,childItemIndexMark};
        String firstOne=groupIndexMark+"|"+childItemIndexMark;
        checkPositionMap.put(firstOne,0);
        groupList.add(dateTemp);
        for (int i=1;i<size;i++){
            childItemIndexMark++;
            String IndexTemp=  groupIndexMark+"|"+childItemIndexMark;
            String currentDate=itemInfoList.get(i).date;
            if (!currentDate.equals(dateTemp)){  //如果日期时间不同
                dateTemp=currentDate;
                groupList.add(currentDate);
                groupIndexMark++;
                sizeList.add(childItemIndexMark);
                childItemIndexMark=0;
                IndexTemp=  groupIndexMark+"|"+childItemIndexMark;
            }
//            Log.i("processData",groupIndexMark+";"+childItemIndexMark+";"+i);
            checkPositionMap.put(IndexTemp,i);
        }
        sizeList.add(childItemIndexMark+1);
//        Log.i("processData",sizeList.toString());
        expandListDataSet=new MyLoadMoreExpandableListViewAdapter.ExpandListDataSet(checkPositionMap,sizeList,groupList,itemInfoList);
    }

    public class MyScrollListener implements AbsListView.OnScrollListener{

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            currentScrollState=ScrollState.values()[scrollState];
            Log.i("OnScrollListener","State="+currentScrollState);
            switch (currentScrollState){

                case IDLE:
                    break;
                case TOUCH_SCROLL:
//                    if (!loading){
//                        Log.i("MainCondition","TOUCH_SCROLL");
//                        if (isBootom&&isMovingUp){
//                            if (mOnLoadMoreListener != null) {
//                                Log.i("LoadMore","条件成立");
//                                mOnLoadMoreListener.loadMore();
//                            }
//                            loading = true;
//                        }
//                    }
                    break;
                case FLING:
//                    if (!loading){
//                        Log.i("MainCondition","FLING");
//                        if (isBootom&&isMovingUp){
//                            if (mOnLoadMoreListener != null) {
//                                Log.i("LoadMore","条件成立");
//                                mOnLoadMoreListener.loadMore();
//                            }
//                            loading = true;
//                        }
//                    }
                    break;
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//            int visibleItemCount = mLayoutManager.getChildCount();
//            int totalItemCount = mLayoutManager.getItemCount();
//            int firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
//            Log.i("LoadMoreOnScroll",firstVisibleItem+";"+visibleItemCount+";"+totalItemCount+";");

            if (!loading) {
                if (totalItemCount <= visibleItemCount + firstVisibleItem) {
                    Log.i("MainCondition","Bottom");
//                        if (currentScrollState==ScrollState.TOUCH_SCROLL&&isMovingUp){
//                            if (mOnLoadMoreListener != null) {
//                                Log.i("LoadMore","条件成立");
//                                mOnLoadMoreListener.loadMore();
//                            }
//                            loading = true;
//                        }
                    isBootom=true;
                }
                else {
                    isBootom=false;
                }
            }
        }
    }

    public class LoadMore implements OnLoadMoreListener{

        @Override
        public void loadMore() {
            Toast.makeText(LoadMoreExpandableListActivity.this,"加载更多",Toast.LENGTH_SHORT).show();

//
            final List<String> groupList=expandListDataSet.getGroupList();
            groupList.add("LoadMore");
//            final int size=itemInfoList.size();
            myLoadMoreExpandableListViewAdapter.notifyDataSetChanged();
//            myLoadMoreExpandableListViewAdapter.notifyItemInserted(size-1);
            myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    groupList.remove(groupList.size()-1);
                    myLoadMoreExpandableListViewAdapter.notifyDataSetChanged();
//                    mTestAdapter.setLoaded();
                    loading=false;
                }
            }, 3 * 1000);
        }
    }


    private void initTouchListener() {

        onTouchListener=new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                Log.i("onTouch",event.getY()+"");
                switch (event.getAction()) {
//                    case MotionEvent.ACTION_SCROLL:
//                        Log.i("onTouch","ACTION_SCROLL");
//                        break;
                    case MotionEvent.ACTION_MOVE:
//                        Log.i("onTouch","ACTION_MOVE");
                        if (!loading){
                            if (event.getY()<lastY){
                                isMovingUp=true;
                                Log.i("MainCondition","MoveUp");
                                judgeshouldLoadMore();
                            }
                        }

                        break;
                    case MotionEvent.ACTION_UP:
//                        Log.i("onTouch","ACTION_UP");
//                        if (!loading){
//                            if (event.getY()<lastY){
//                                Log.i("MainCondition","MoveUp");
//                                isMovingUp=true;
//                            }
//                        }
                        break;
                    case MotionEvent.ACTION_DOWN:
                        lastY=event.getY();
                        isMovingUp=false;
                        break;
                }
                return false;
            }
        };
    }

    private void judgeshouldLoadMore() {
        if (currentScrollState==ScrollState.TOUCH_SCROLL&&isMovingUp&&isBootom){
                            if (mOnLoadMoreListener != null) {
                                Log.i("LoadMore","条件成立");
                                mOnLoadMoreListener.loadMore();
                            }
                            loading = true;
                        }
    }
}
