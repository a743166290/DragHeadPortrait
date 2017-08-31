package com.dragheadportrait;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jkdrag.DragImageView;
import com.jkdrag.DragLayout;
import com.jkdrag.listener.DragViewListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private  final String TAG = getClass().getSimpleName();
    private RecyclerView recyclerview;
    private List<String> mDatas;
    private DragLayout layout;
//    private GestrueDetectorHelper gestrueDetectorHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
//        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        initData();
        recyclerview.setAdapter(new mAdapter());
        layout = (DragLayout) findViewById(R.id.draglayout);
        //手势监听
//        gestrueDetectorHelper = new GestrueDetectorHelper().init(this)
//                .setmListener(new GestrueDetectorHelper.GestrueDetectorHelperListener() {
//                    @Override
//                    public void swipeRight() {
//
//                    }
//
//                    @Override
//                    public void swipeLeft() {
//
//                    }
//
//                    @Override
//                    public void swipeTop() {
//
//                    }
//
//                    @Override
//                    public void swipeBottom() {
//
//                    }
//                });
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//
//        return gestrueDetectorHelper.getmDetector().onTouchEvent(event);
//
//    }

    protected void initData()
    {
        mDatas = new ArrayList<String>();
        for (int i = 0; i < 50; i++)
        {
            mDatas.add("" + i);
        }
    }
    class mAdapter extends RecyclerView.Adapter<mAdapter.mViewHolder>{
        @Override
        public mViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mViewHolder holder = new mViewHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.item,parent,false));
            return holder;
        }

        @Override
        public void onBindViewHolder(mViewHolder holder,final int position) {
            final DragImageView dragImageView = holder.dragImageView;
            holder.number.setText(mDatas.get(position));
            if(position % 2 == 0){
                dragImageView.setBackgroundColor(Color.parseColor("#303F9F"));
            }else {
                dragImageView.setBackgroundColor(Color.parseColor("#FF4081"));
            }
            dragImageView.setVisibility(View.VISIBLE);
            dragImageView.setDragViewListener(new DragViewListener() {
                @Override
                public void LongclickListener(View view) {
                    //长按出现view
                    layout.addMovedView(view);
                    //设置当前view隐藏
                    view.setVisibility(View.INVISIBLE);
                    //去掉recylerview的touch分发事件
                    dragImageView.disallowInterceptTouchEvent(true);
                    //需要设置recylerview 的长度已判断出没出屏幕
                    layout.setMaxRecylerViewWidth(recyclerview.getMeasuredWidth());
                }

                @Override
                public void move(int x, int y) {
                    //view的移动
                    layout.moveView(x,y);
                }


                @Override
                public void up(DragImageView view) {
                    //抬起手事件 移除view
                    layout.removeMovedView();
                    //判断移除范围有没有出 recylerview
                    if(layout.isMovedOutSize()){
                        Log.d(TAG,"delete");
                        mDatas.remove(position);
                        notifyDataSetChanged();
                    }else{
                        Log.d(TAG,"not delete");
                        view.setVisibility(View.VISIBLE);
                    }

                }
            });

        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        class mViewHolder extends RecyclerView.ViewHolder {
            DragImageView dragImageView;
            TextView number;
            public mViewHolder(View itemView) {
                super(itemView);
                dragImageView = (DragImageView) itemView.findViewById(R.id.item_img);
                number = (TextView) itemView.findViewById(R.id.number);
            }
        }
    }
}
