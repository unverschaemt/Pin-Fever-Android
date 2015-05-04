package net.unverschaemt.pinFever;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;

/**
 * Created by D060338 on 04.05.2015.
 */
public class RoundImageButton extends ImageButton {

    public RoundImageButton(Context context) {
        super(context);
    }

    public RoundImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RoundImageButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean clicked = super.onTouchEvent(event);
        if(clicked){
            int rad = super.getWidth()/2;
            int centerX = super.getLeft()+rad;
            int centerY = super.getTop()+rad;
            double eventX = event.getRawX();
            double eventY = event.getRawY();
            double distance = Math.sqrt(Math.pow(centerX - eventX,2)+Math.pow(centerY-eventY,2));
            if(distance > rad){
                clicked = false;
            }
        }
        return clicked;
    }
}
