package com.jkdrag;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jkdrag.listener.BmAnimationListener;

/**
 * 拖拽布局
 * Created by zengyan on 2017/8/28.
 */

public class DragLayout extends RelativeLayout{
    private final static int INVALIDATE = 0x001;
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
    private Bitmap movedBitmap;
    private int[] explosionResIds = new int[]{
            R.drawable.explosion_one,
            R.drawable.explosion_two,
            R.drawable.explosion_three,
            R.drawable.explosion_four,
            R.drawable.explosion_five
    };
    private float curX; // 当前手指x坐标
    private float curY; // 当前手指y坐标
    private static final int EXPLOSION_ANIM_FRAME_INTERVAL = 50; // 爆裂动画帧之间的间隔
    private Bitmap[] explosionAnim; // 爆裂动画位图
    private boolean explosionAnimStart; // 爆裂动画是否开始
    private int explosionAnimNumber; // 爆裂动画帧的个数
    private int curExplosionAnimIndex; // 爆裂动画当前帧
    private int explosionAnimWidth; // 爆裂动画帧的宽度
    private int explosionAnimHeight; // 爆裂动画帧的高度
    private BmAnimationListener bmAnimationListener;

    public void setBmAnimationListener(BmAnimationListener bmAnimationListener) {
        this.bmAnimationListener = bmAnimationListener;
    }

    //设置控件的最长长度
    public void bindRecylerView(final RecyclerView recyclerView){
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                maxRecylerViewWidth = recyclerView.getMeasuredWidth();
                recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

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
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (explosionAnimStart) {
            if(bmAnimationListener != null){
                bmAnimationListener.startAnimation();
            }
            drawExplosionAnimation(canvas);
        }
    }
    private void drawExplosionAnimation(Canvas canvas) {
        if (!explosionAnimStart) {
            return;
        }

        if (curExplosionAnimIndex < explosionAnimNumber) {
            canvas.drawBitmap(explosionAnim[curExplosionAnimIndex],
                    curX - explosionAnimWidth / 2, curY - explosionAnimHeight / 2, null);
            curExplosionAnimIndex++;
            // 每隔固定时间执行
            postInvalidateDelayed(EXPLOSION_ANIM_FRAME_INTERVAL);
        } else {
            // 动画结束
            explosionAnimStart = false;
            curExplosionAnimIndex = 0;
            curX = 0;
            curY = 0;
            if(bmAnimationListener != null){
                bmAnimationListener.endAnimation();
            }
            recycleBitmap();
        }
    }
    /**
     * ************************* 爆炸动画(帧动画) *************************
     */
    private void initExplosionAnimation() {
        if (explosionAnim == null) {
            explosionAnimNumber = explosionResIds.length;
            explosionAnim = new Bitmap[explosionAnimNumber];
            for (int i = 0; i < explosionAnimNumber; i++) {
                explosionAnim[i] = BitmapFactory.decodeResource(getResources(), explosionResIds[i]);
            }

            explosionAnimHeight = explosionAnimWidth = explosionAnim[0].getWidth(); // 每帧长宽都一致
        }
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
    public void moveView(int dx,int dy,MotionEvent ev){

        if(moveHead == null){
            return;
        }
        curX = moveHead.getX();
        curY = moveHead.getY();
        LayoutParams layoutParams = (LayoutParams) moveHead
                .getLayoutParams();
        _xDelta = layoutParams.leftMargin;
        _yDelta = layoutParams.topMargin;
        layoutParams.leftMargin = _xDelta + dx;
        layoutParams.topMargin = _yDelta + dy;
        moveHead.setLayoutParams(layoutParams);
    }

    public void calculationMoved(){
        if(moveHead != null) {
            int moveX = maxWindowWidth - maxRecylerViewWidth - moveHead.getMeasuredWidth();
            int left = moveHead.getLeft() - moveHead.getMeasuredWidth() / 2;
            if(left  < moveX){
                if(bmAnimationListener != null){
                    initExplosionAnimation();
                    explosionAnimStart = true;
                    invalidate();
                }
                setMovedOutSize(true);
            }else{

                setMovedOutSize(false);
                if(bmAnimationListener != null){
                    bmAnimationListener.endAnimation();
                }
            }
            this.removeView(moveHead);
            moveHead = null;
            movedBitmap.recycle();
        }
    }
    public void addMovedView(View headView){
        if(moveHead == null) {
            headView.setDrawingCacheEnabled(true);
            headView.buildDrawingCache();  //启用DrawingCache并创建位图
            movedBitmap = Bitmap.createBitmap(headView.getDrawingCache()); //创建一个DrawingCache的拷贝，因为DrawingCache得到的位图在禁用后会被回收
            headView.setDrawingCacheEnabled(false);  //禁用DrawingCahce否则会影响性能
            LayoutParams layoutParms =
                    new LayoutParams(headView.getMeasuredWidth(), headView.getMeasuredHeight());
            layoutParms.setMargins((int)mDownX - headView.getMeasuredWidth() / 2,
                    (int)mDownY - headView.getMeasuredHeight() / 2, 0, 0);
            ImageView head = new ImageView(context);
            head.setLayoutParams(layoutParms);
            head.setImageBitmap(movedBitmap);
            moveHead = head;
            addView(head);
        }
    }
    private void recycleBitmap() {
        if (explosionAnim != null && explosionAnim.length != 0) {
            for (int i = 0; i < explosionAnim.length; i++) {
                if (explosionAnim[i] != null && !explosionAnim[i].isRecycled()) {
                    explosionAnim[i].recycle();
                    explosionAnim[i] = null;
                }
            }
            setMovedOutSize(true);
            movedBitmap.recycle();
            explosionAnim = null;
        }
    }

}
