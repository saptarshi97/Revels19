package in.mitrev.revels19.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.view.GestureDetector;

public class SwipeScrollView extends NestedScrollView {

    GestureDetector gestureDetector;

    public SwipeScrollView(@NonNull Context context) {
        super(context);
    }


}
