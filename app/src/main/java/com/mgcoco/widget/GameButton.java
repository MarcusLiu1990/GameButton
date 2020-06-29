package com.mgcoco.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

public class GameButton extends FrameLayout {

    private View mView;

    public GameButton(Context context) {
        super(context);
        init(context, null);
    }

    public GameButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public GameButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public GameButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        mView = new GameButtonView(context, attrs);
        addView(mView);
        mView.getLayoutParams().width = LayoutParams.MATCH_PARENT;
        mView.getLayoutParams().height = LayoutParams.MATCH_PARENT;
    }

}
