package com.example.musicplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class SquareRelativeLayout extends RelativeLayout {

    public SquareRelativeLayout(Context context) {
        super(context);
    }

    public SquareRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int size = 0;

        if (width == MeasureSpec.UNSPECIFIED && height == MeasureSpec.UNSPECIFIED) {
            size = getContext().getResources()
                    .getDimensionPixelSize(R.dimen.default_music_player_album_cover_size);
        } else if (width == MeasureSpec.UNSPECIFIED || height == MeasureSpec.UNSPECIFIED) {
            size = width > height ? width : height;
        } else {
            size = width > height ? height : width;
        }

        setMeasuredDimension(size, size);
    }
}
