package com.wyf.daily;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

/**
 * DebugFragment用来做APP各种后台的调试
 *
 * @author wifi9984
 * @date 2017/9/3
 */

public class DebugFragment extends android.app.Fragment implements View.OnClickListener {

    protected View mView;
    protected Context mContext;
    private Button btnRead;
    private Button btnClean;
    private EventsDBHelper mHelper;
    private TextView tvLog;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        mView = inflater.inflate(R.layout.debug_fragment,container,false);
        init();
        return mView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = EventsDBHelper.getInstance(this.getActivity(),1);
        mHelper.openReadLink();
    }

    void init(){
        btnRead = (Button)mView.findViewById(R.id.btn_debug_read);
        btnClean = (Button)mView.findViewById(R.id.btn_debug_clean);
        btnRead.setOnClickListener(this);
        btnClean.setOnClickListener(this);
        tvLog = (TextView)mView.findViewById(R.id.tv_debug_log);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_debug_read){
            // 读取数据库数据，代码我也是抄来的，改好了2333
            if (mHelper == null){
                tvLog.setText("查找失败");
                return;
            }
            ArrayList<Event> allEvents = mHelper.allEvents(mHelper.getReadableDatabase());
            String desc = String.format(Locale.getDefault(),"数据库查询到%d条记录，详情如下：", allEvents.size());
            for (int i=0; i<allEvents.size(); i++) {
                Event event = allEvents.get(i);
                desc = String.format(Locale.getDefault(),"%s\n第%d条记录信息如下：", desc, i+1);
                desc = String.format("%s\n　事件：%s", desc, event.getEvent());
                desc = String.format("%s\n　日期：%s", desc, event.getDate());
                desc = String.format("%s\n　开始时间：%s", desc, event.getTimeStart());
                desc = String.format("%s\n　结束时间：%s", desc, event.getTimeEnd());
                desc = String.format(Locale.getDefault(),"%s\n　提醒方式：%d", desc, event.getPattern());
            }
            tvLog.setText(desc);
        }else if(v.getId() == R.id.btn_debug_clean){
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
            builder.setTitle("令人窒息的操作！");
            builder.setMessage("您确定要删除数据库全部内容吗？");
            builder.setPositiveButton("不了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.setNegativeButton("删tm的", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mHelper.deleteAll();
                    tvLog.setText("删了...真没了...");
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
