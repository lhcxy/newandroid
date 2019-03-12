package com.yunbao.phonelive.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yunbao.phonelive.R;

public class UserLevelView extends LinearLayout {
    private static final char ZERO = '0';
    private static final char ONE = '1';
    private static final char TWO = '2';
    private static final char THREE = '3';
    private static final char FOUR = '4';
    private static final char FIVE = '5';
    private static final char SIX = '6';
    private static final char SEVEN = '7';
    private static final char EIGHT = '8';
    private static final char NINE = '9';

    public UserLevelView(Context context) {
        this(context, null, 0);
    }

    public UserLevelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public UserLevelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(LinearLayout.HORIZONTAL);
        setLevelBg();
    }

    public UserLevelView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs, defStyleAttr);
    }

    public void setLevel(int level) {
        this.level = level;
        setLevelInfo();
    }

    public void setLevel(int level, int levelType) {
        this.levelType = levelType;
        setLevel(level);
    }

    private int level = 0;
    private int levelType = 0;  //0 = 普通用户  1=主播

    private void setLevelInfo() {
        removeAllViews();
        setLevelBg();
        setLevelIcon();
    }

    private void setLevelBg() {
        Drawable drawable;
        if (levelType == 0) {
            if (level >= 15 && level <= 29) {
                drawable = getResources().getDrawable(R.drawable.bg_user_level_15_29);
            } else if (level >= 30 && level <= 39) {
                drawable = getResources().getDrawable(R.drawable.bg_user_level_30_39);
            } else if (level >= 40 && level <= 49) {
                drawable = getResources().getDrawable(R.drawable.bg_user_level_40_49);
            } else if (level >= 50 && level <= 59) {
                drawable = getResources().getDrawable(R.drawable.bg_user_level_50_59);
            } else if (level >= 60 && level <= 69) {
                drawable = getResources().getDrawable(R.drawable.bg_user_level_60_69);
            } else if (level >= 70 && level <= 79) {
                drawable = getResources().getDrawable(R.drawable.bg_user_level_70_79);
            } else if (level >= 80 && level <= 89) {
                drawable = getResources().getDrawable(R.drawable.bg_user_level_80_89);
            } else if (level >= 90 && level <= 99) {
                drawable = getResources().getDrawable(R.drawable.bg_user_level_90_99);
            } else if (level >= 100 && level <= 109) {
                drawable = getResources().getDrawable(R.drawable.bg_user_level_100_109);
            } else if (level >= 110 && level <= 119) {
                drawable = getResources().getDrawable(R.drawable.bg_user_level_110_119);
            } else if (level >= 120) {
                drawable = getResources().getDrawable(R.drawable.bg_user_level_120);
            } else {
                drawable = getResources().getDrawable(R.drawable.bg_user_level_0_14);
            }
        } else {
            if (level >= 15 && level <= 29) {
                drawable = getResources().getDrawable(R.drawable.bg_anchor_level_15_29);
            } else if (level >= 30 && level <= 39) {
                drawable = getResources().getDrawable(R.drawable.bg_anchor_level_30_39);
            } else if (level >= 40 && level <= 49) {
                drawable = getResources().getDrawable(R.drawable.bg_anchor_level_40_49);
            } else if (level >= 50 && level <= 59) {
                drawable = getResources().getDrawable(R.drawable.bg_anchor_level_50_59);
            } else if (level >= 60 && level <= 69) {
                drawable = getResources().getDrawable(R.drawable.bg_anchor_level_60_69);
            } else if (level >= 70 && level <= 79) {
                drawable = getResources().getDrawable(R.drawable.bg_anchor_level_70_79);
            } else if (level >= 80 && level <= 89) {
                drawable = getResources().getDrawable(R.drawable.bg_anchor_level_80_89);
            } else if (level >= 90 && level <= 94) {
                drawable = getResources().getDrawable(R.drawable.bg_anchor_level_90_94);
            } else if (level >= 95 && level <= 100) {
                drawable = getResources().getDrawable(R.drawable.bg_anchor_level_95_100);
            } else {
                drawable = getResources().getDrawable(R.drawable.bg_anchor_level_1_14);
            }
        }
        setBackground(drawable);
    }

    private void setLevelIcon() {
        char[] chars = String.valueOf(level).toCharArray();
        if (chars.length <= 0) {
            return;
        }
        for (char aChar : chars) {
            ImageView imageView = new ImageView(getContext());
            switch (aChar) {
                case ZERO:
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_level_fonts_0));
                    break;
                case ONE:
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_level_fonts_1));
                    break;
                case TWO:
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_level_fonts_2));
                    break;
                case THREE:
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_level_fonts_3));
                    break;
                case FOUR:
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_level_fonts_4));
                    break;
                case FIVE:
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_level_fonts_5));
                    break;
                case SIX:
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_level_fonts_6));
                    break;
                case SEVEN:
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_level_fonts_7));
                    break;
                case EIGHT:
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_level_fonts_8));
                    break;
                case NINE:
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_level_fonts_9));
                    break;
            }
            addView(imageView);
        }
    }

}
