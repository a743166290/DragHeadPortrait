package com.jkdrag;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ScrollView;

import com.jkdrag.listener.DragViewListener;

/**
 * 拖拽的ImageView
 * Created by zengyan on 2017/8/28.
 */

public class DragImageView extends AppCompatImageView implements View.OnLongClickListener{
    private float mLastX ;
    private float mLastY ;
    private boolean lockUp = true;
    private DragViewListener dragViewListener;
    public DragViewListener getDragViewListener() {
        return dragViewListener;
    }

    public void setDragViewListener(DragViewListener dragViewListener) {
        this.dragViewListener = dragViewListener;
    }

    public void setLockUp(boolean lockUp) {
        this.lockUp = lockUp;
    }

    public DragImageView(Context context) {
        super(context);
    }

    public DragImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DragImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setOnLongClickListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastX = event.getRawX();
                mLastY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float touchX = event.getX();
                float touchY = event.getY();
                int dx = (int) (touchX-mLastX);
                int dy = (int) (touchY-mLastY);
                mLastX = touchX;
                mLastY = touchY;
                dragViewListener.move(dx,dy);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if(!lockUp){
                    dragViewListener.up(this);
                }
                disallowInterceptTouchEvent(false);
                break;
        }

        return super.onTouchEvent(event);
    }
    //拦截事件分发
    public void disallowInterceptTouchEvent(boolean disable) {
        if(disable){
            lockUp = false;
        }else{
            lockUp = true;
        }
        ViewGroup parent = (ViewGroup) getParent();
        parent.requestDisallowInterceptTouchEvent(disable);
        while (true) {
            if (parent == null) {
                return;
            }

            if (parent instanceof RecyclerView || parent instanceof ListView || parent instanceof GridView ||
                    parent instanceof ScrollView) {
                parent.requestDisallowInterceptTouchEvent(disable);
                return;
            }

            ViewParent vp = parent.getParent();
            if (vp instanceof ViewGroup) {
                parent = (ViewGroup) parent.getParent();
            } else {
                return; // DecorView
            }
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if(dragViewListener != null){
            dragViewListener.LongclickListener(view);
        }
        return false;
    }

}
