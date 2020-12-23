package com.xing.scanble;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements  ScrlViewItemClickListener {

    private TextView mTextView;
    private RecyclerView mBlueDevList;
    private RecycleViewAdapter mAdapter;

    ArrayList<HashMap<String,Object>> mListItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text_info);

        initData();
        initView();
    }

    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);//使用线性布局
        mBlueDevList = (RecyclerView) findViewById(R.id.dev_list);
        mAdapter = new RecycleViewAdapter(this, mListItem);
        mAdapter.setOnItemClickListener(this);
        // 设置分割线
        mBlueDevList.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));
        mBlueDevList.setLayoutManager(layoutManager);
        mBlueDevList.setHasFixedSize(true);
        mBlueDevList.setAdapter(mAdapter);
    }

    private void initData() {
        mListItem = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < 5; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemTitle", "第" + i + "行");
            map.put("ItemText", "这是第" + i + "行");
            map.put("ItemImage", R.mipmap.ic_launcher);
            mListItem.add(map);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        mTextView.setText((String) mListItem.get(position).get("ItemText"));
    }
}