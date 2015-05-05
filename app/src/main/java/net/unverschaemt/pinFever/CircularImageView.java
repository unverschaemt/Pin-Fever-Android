package net.unverschaemt.pinFever;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * Created by D060338 on 04.05.2015.
 */
public class CircularImageView extends ImageView {

    public CircularImageView(Context context) {
        super(context);
    }

    public CircularImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircularImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CircularImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean clicked = super.onTouchEvent(event);
        if(clicked){
            int rad = getRadius();
            int centerX = getCenterX();
            int centerY = getCenterY();
            double eventX = event.getRawX();
            double eventY = event.getRawY();
            double distance = Math.sqrt(Math.pow(centerX - eventX,2)+Math.pow(centerY-eventY,2));
            if(distance > rad){
                clicked = false;
            }
        }
        return clicked;
    }

    private int getRadius(){
        return super.getWidth()/2;
    }
    private int getCenterX(){
        int rad = getRadius();
        return super.getLeft()+rad;
    }
    private int getCenterY(){
        int rad = getRadius();
        return super.getTop()+rad;
    }

    @Override
    protected void onDraw(Canvas canvas){
        Path clipPath = new Path();
        RectF rect = new RectF(0,0,this.getWidth(), this.getHeight());
        clipPath.addRoundRect(rect, getRadius(), getRadius(), Path.Direction.CW);
        canvas.clipPath(clipPath);
        super.onDraw(canvas);
    }
}
