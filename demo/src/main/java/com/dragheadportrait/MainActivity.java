package com.dragheadportrait;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.jkdrag.DragImageView;
import com.jkdrag.DragLayout;
import com.jkdrag.listener.DragViewListener;
import java.util.ArrayList;
import java.util.List;

import tyrantgit.explosionfield.ExplosionField;

public class MainActivity extends AppCompatActivity {
    private  final String TAG = getClass().getSimpleName();
    private RecyclerView recyclerview;
    private List<String> mDatas;
    private DragLayout layout;
    private ExplosionField mExplosionField;
    private String [] mData = new String[]{
            "http://imgsrc.baidu.com/forum/pic/item/53f082025aafa40f95261179a164034f79f019b6.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1504176867338&di=37d89903c924100e53cd12c862cd0372&imgtype=0&src=http%3A%2F%2Fimg1.2345.com%2Fduoteimg%2FqqTxImg%2F11%2F2012091910313510745.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1504176917510&di=754f6e710e6cde0beae80fe86972abea&imgtype=0&src=http%3A%2F%2Fp.3761.com%2Fpic%2F43701399945993.png",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1504176917510&di=ad9e9bbaaa5fc9a518665d84e329d334&imgtype=0&src=http%3A%2F%2Fwww.qqzhi.com%2Fuploadpic%2F2015-01-30%2F155756909.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1504176917510&di=7b1e4aadb1fe726df927334a8bf917ca&imgtype=0&src=http%3A%2F%2Ffile.popoho.com%2F2016-07-22%2Fa648063b73893b4b2940e124b97b3238.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1504176917510&di=74b604cc2a26a28aca2e07b8c133532d&imgtype=0&src=http%3A%2F%2Fimg5.duitang.com%2Fuploads%2Fitem%2F201507%2F18%2F20150718211806_eB2K3.jpeg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1504176917510&di=4db11c2ec2ebeef906c7738db493a4e1&imgtype=0&src=http%3A%2F%2Fup.qqya.com%2Fallimg%2F201710-t%2F17-101802_79867.jpg",
            "http://up.qqjia.com/z/01/tu3945_9.jpg",
            "http://diy.qqjay.com/u/files/2015/0120/10f1ee2b68229aef44f6d0748a1c6de7.jpg",
            "http://img1.imgtn.bdimg.com/it/u=3259547394,1635760435&fm=214&gp=0.jpg"
    };
//    private GestrueDetectorHelper gestrueDetectorHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
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
        for (int i = 0; i < mData.length; i++)
        {
            mDatas.add("" + mData[i]);
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
            Glide.with(MainActivity.this).load(mDatas.get(position)).centerCrop().into(dragImageView);
            holder.number.setText(position+"");
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
                public void move(int x, int y,MotionEvent ev) {
                    //view的移动
                    layout.moveView(x,y,ev);
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
