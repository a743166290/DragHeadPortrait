package com.jkdrag.listener;

import android.view.MotionEvent;
import android.view.View;

import com.jkdrag.DragImageView;

/**
 * Created by zengyan on 2017/8/31.
 */

public interface DragViewListener {
    //长按事件
    void LongclickListener(View view);
    //移动距离
    void move(int x, int y,MotionEvent ev);
    //弹起事件
    void up(DragImageView view);
}
