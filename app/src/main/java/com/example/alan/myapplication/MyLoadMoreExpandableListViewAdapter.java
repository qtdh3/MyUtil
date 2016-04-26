package com.example.alan.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

//import com.google.common.collect.HashBasedTable;
//import com.google.common.collect.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Linjf on 2016/4/21 0021.
 */
public class MyLoadMoreExpandableListViewAdapter extends BaseExpandableListAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private ExpandListDataSet expandListDataSet;

//    private List<String> groupList;//=new ArrayList<String>();

    private List<LoadMoreExpandableListActivity.ItemInfo> itemInfoList;//=new ArrayList<LoadMoreExpandableListActivity.ItemInfo>();

//    public MyLoadMoreExpandableListViewAdapter(Context context, List<String> groupList, List<LoadMoreExpandableListActivity.ItemInfo> itemInfoList) {
//        this.context=context;
//        this.groupList = groupList;
//        this.itemInfoList = itemInfoList;
//        layoutInflater=LayoutInflater.from(context);
//    }


    public MyLoadMoreExpandableListViewAdapter(Context context, LayoutInflater layoutInflater, ExpandListDataSet expandListDataSet) {
        this.context = context;
        this.layoutInflater = layoutInflater;
        this.expandListDataSet = expandListDataSet;
    }

//    public List<String> getGroupList() {
//        return groupList;
//    }

//    public void setGroupList(List<String> groupList) {
//        this.groupList = groupList;
//    }

    public List<LoadMoreExpandableListActivity.ItemInfo> getItemInfoList() {
        return itemInfoList;
    }

    public void setItemInfoList(List<LoadMoreExpandableListActivity.ItemInfo> itemInfoList) {
        this.itemInfoList = itemInfoList;
    }

    public ExpandListDataSet getExpandListDataSet() {
        return expandListDataSet;
    }

    public void setExpandListDataSet(ExpandListDataSet expandListDataSet) {
        this.expandListDataSet = expandListDataSet;
    }

    @Override
    public int getGroupCount() {
//        return 0;
        return expandListDataSet.groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
//        return 3;
       return expandListDataSet.sizeList.get(groupPosition);
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Log.i("getGroupView",groupPosition+";");
        List<String> groupList = expandListDataSet.groupList;
        if (groupPosition==groupList.size()-1){
            if (groupList.get(groupList.size()-1).equals("LoadMore")){
                return layoutInflater.inflate(R.layout.progressbar_item,null);
            }
        }

        if (convertView==null||convertView.getTag()==null){
            convertView=layoutInflater.inflate(R.layout.list_item_withdraw_group_top_date,null);
            TextView topDateView = (TextView) convertView.findViewById(R.id.tv_withdraw_history_top_date);
            convertView.setTag(topDateView);
        }
//        TextView topDateView = (TextView) convertView.findViewById(R.id.tv_withdraw_history_top_date);
        TextView topDateView= (TextView) convertView.getTag();
        String topText=expandListDataSet.getGroupList().get(groupPosition);
        if (topDateView==null){
            Log.e("getGroupView","topDateView==null"+";"+convertView.toString());
            return null;
        }
        topDateView.setText(topText);
        ImageView state_arrow= (ImageView) convertView.findViewById(R.id.iv_arrow_listItem_expand_state);
        if (isExpanded){
            state_arrow.setImageResource(R.drawable.itemlist_sign_to_close);
        }else {
            state_arrow.setImageResource(R.drawable.itemlist_sign_to_open);
        }
        return convertView;
//        return null;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
//        return layoutInflater.inflate(R.layout.list_item_withdraw_normal_amout,null);

        if (convertView==null){
            convertView=layoutInflater.inflate(R.layout.list_item_withdraw_normal_amout,null);
            MyHolder myHolder=new MyHolder(convertView,ViewType.NORMAL.ordinal());
            convertView.setTag(myHolder);
        }
        MyHolder holder= (MyHolder) convertView.getTag();
        String integers=groupPosition+"|"+childPosition;
        Integer posInteger=expandListDataSet.checkPositionMap.get(integers);
//        int pos=expandListDataSet.checkPositionMap.get(new Integer[]{groupPosition,childPosition});
        LoadMoreExpandableListActivity.ItemInfo itemInfo=expandListDataSet.itemInfoList.get(posInteger);
        holder.item_amount.setText(itemInfo.Amt);
        holder.item_time.setText(itemInfo.time);
        if (itemInfo.withdrawState== LoadMoreExpandableListActivity.WithdrawState.FINISHED){
                    holder.item_state.setText("提现成功");
                }else {
                    holder.item_state.setText("处理中");
                    holder.item_state.setTextColor(Color.BLUE);
                }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public static class ExpandListDataSet{
//        private Table<Integer,Integer,Integer> table=HashBasedTable.create();
        private Map<String,Integer> checkPositionMap=new HashMap<String, Integer>();  // 用于位置 从二维到一位的转换
        private List<Integer> sizeList=new ArrayList<Integer>();        // 用于存储每组的数量
        private List<String> groupList=new ArrayList<String>();                 //组  或一级内容
        private List<LoadMoreExpandableListActivity.ItemInfo> itemInfoList
                =new ArrayList<LoadMoreExpandableListActivity.ItemInfo>();

        public ExpandListDataSet(Map<String,Integer> checkPositionMap, List<Integer> sizeList, List<String> groupList, List<LoadMoreExpandableListActivity.ItemInfo> itemInfoList) {
            this.checkPositionMap = checkPositionMap;
            this.sizeList = sizeList;
            this.groupList = groupList;
            this.itemInfoList = itemInfoList;
        }

        public List<String> getGroupList() {
            return groupList;
        }
    }

    public enum  ViewType{
        GROUPTOP,
        LOADMORE,
        NORMAL;
    }

    public class MyHolder {
        private TextView topDateView;
        private ProgressBar mProgressBar;
        private TextView item_time;
        private TextView item_amount;
        private TextView item_state;

        public MyHolder(View itemView, int viewType) {
            init(itemView, viewType);
        }

        private void init(View view, int viewType) {
            ViewType viewTypeEnum=ViewType.values()[viewType];
            switch (viewTypeEnum) {
                case GROUPTOP:
                    topDateView= (TextView) view.findViewById(R.id.tv_withdraw_history_top_date);
                    break;
                case LOADMORE:
                    mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);
                    break;
                case NORMAL:
                    item_time= (TextView) view.findViewById(R.id.tv_withdraw_history_item_time);
                    item_state= (TextView) view.findViewById(R.id.tv_withdraw_history_item_state);
                    item_amount= (TextView) view.findViewById(R.id.tv_withdraw_history_item_amount);
                    break;
            }
        }
    }
}
