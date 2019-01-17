package in.mitrev.revels19.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;

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
