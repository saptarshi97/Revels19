package in.mitrev.revels19.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class SwipeScrollView extends NestedScrollView {

    GestureDetector gestureDetector;

    public SwipeScrollView(@NonNull Context context) {
        super(context);
    }

    public SwipeScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setGestureDetector(GestureDetector gestureDetector) {
        this.gestureDetector = gestureDetector;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (gestureDetector.onTouchEvent(ev))
            return true;
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (gestureDetector.onTouchEvent(ev))
            return true;
        return super.onInterceptTouchEvent(ev);
    }
}
