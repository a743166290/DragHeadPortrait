package com.jkdrag;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * 拖拽布局
 * Created by zengyan on 2017/8/28.
 */

public class DragLayout extends RelativeLayout{
    private float mDownX ;
    private float mDownY ;
    private LayoutInflater minflater;
    private Context context;
    private View moveHead;
    private int _xDelta;
    private int _yDelta;
    private boolean movedOutSize;
    private int maxRecylerViewWidth;
    private int maxWindowWidth;

    //设置控件的最长长度
    public void setMaxRecylerViewWidth(int maxRecylerViewWidth) {
        this.maxRecylerViewWidth = maxRecylerViewWidth;
    }
    public boolean isMovedOutSize() {
        return movedOutSize;
    }
    public void setMovedOutSize(boolean movedOutSize) {
        this.movedOutSize = movedOutSize;
    }
    public DragLayout(Context context) {
        super(context);
        init(context);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context c){
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        maxWindowWidth = dm.widthPixels;
        minflater = LayoutInflater.from(c);
        this.context = c;

    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if(action == MotionEvent.ACTION_DOWN) {
            mDownX = ev.getX();
            mDownY = ev.getY();
        }
        return false;
    }
    public void moveView(int dx,int dy){
        if(moveHead == null){
            return;
        }
        LayoutParams layoutParams = (LayoutParams) moveHead
                .getLayoutParams();
        _xDelta = layoutParams.leftMargin;
        _yDelta = layoutParams.topMargin;
        layoutParams.leftMargin = _xDelta + dx;
        layoutParams.topMargin = _yDelta + dy;
        moveHead.setLayoutParams(layoutParams);
    }

    public void removeMovedView(){
        if(moveHead != null) {
            int moveX = maxWindowWidth - maxRecylerViewWidth - moveHead.getMeasuredWidth();
            int left = moveHead.getLeft() - moveHead.getMeasuredWidth() / 2;
            if(left  < moveX){
                setMovedOutSize(true);
            }else{
                setMovedOutSize(false);
            }
            this.removeView(moveHead);
            moveHead = null;
        }
    }
    public void addMovedView(View headView){
        if(moveHead == null) {
            headView.setDrawingCacheEnabled(true);
            headView.buildDrawingCache();  //启用DrawingCache并创建位图
            Bitmap bitmap = Bitmap.createBitmap(headView.getDrawingCache()); //创建一个DrawingCache的拷贝，因为DrawingCache得到的位图在禁用后会被回收
            headView.setDrawingCacheEnabled(false);  //禁用DrawingCahce否则会影响性能
            LayoutParams layoutParms =
                    new LayoutParams(headView.getMeasuredWidth(), headView.getMeasuredHeight());
            layoutParms.setMargins((int)mDownX - headView.getMeasuredWidth() / 2,
                    (int)mDownY - headView.getMeasuredHeight() / 2, 0, 0);
            ImageView head = new ImageView(context);
            head.setLayoutParams(layoutParms);
            head.setImageBitmap(bitmap);
            moveHead = head;
            addView(head);
        }
    }
}
