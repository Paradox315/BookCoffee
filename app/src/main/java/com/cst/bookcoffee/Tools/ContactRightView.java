package com.cst.bookcoffee.Tools;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;


import androidx.core.content.ContextCompat;

import com.cst.bookcoffee.R;

public class ContactRightView extends View {

    private String[] letters = new String[]{
            "","A", "B", "C", "D", "E",
            "F", "G", "H", "I", "J", "K",
            "L", "M", "N", "O", "P", "Q",
            "R", "S", "T", "U", "V", "W",
            "X", "Y", "Z", "#"};

    /**
     * 每一个字母的高度
     */
    private int singleHeight;
    /**
     * 控件的高度
     */
    private int height;
    private float arc = 0;
    /**
     * 最高的高度
     */
    private float maxHeight;

    private float normalWidth;
    private float selectedWidth;
    /**
     * 是否是选择状态，默认为false
     */
    private boolean iSelectedState = false;

    private float textSize;

    private int startPos = -1;
    private int endPos = -1;
    private float measureTextSize = -1;
    /**
     * 接口变量，该接口主要用来实现当手指在右边的滑动控件上滑动时ListView能够跟着滚动
     */
    private UpdateListView updateListView;
    Typeface plain;
    String arrow;
    private Context context;

    public ContactRightView(Context context) {
        this(context, null);
    }

    public ContactRightView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContactRightView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context=context;
        plain = Typeface.createFromAsset(context.getAssets(), "iconfont.ttf");
        arrow = context.getResources().getString(R.string.icon);
        init();
    }

    private void init() {
        textSize = 14;
        normalWidth = getResources().getDimensionPixelSize(R.dimen.normal_width);
        selectedWidth = getResources().getDimensionPixelSize(R.dimen.selected_width);
        maxHeight = selectedWidth-getResources().getDimension(R.dimen.activity_vertical_margin);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float y = event.getY();
        int oldChoose = choose;

        int selectIndex = (int) (y / (height / letters.length));
        if (selectIndex > -1 && selectIndex < letters.length) {
            String key = letters[selectIndex];
            if (updateListView != null) {
                updateListView.updateListView(key);
            }
            if (!iSelectedState) {
                iSelectedState = true;
                setLayoutParams((int) selectedWidth);
            }
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (oldChoose != selectIndex) {
                    if (selectIndex >= 0 && selectIndex < letters.length) {
                        choose = selectIndex;
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (oldChoose != selectIndex) {
                    if (selectIndex > -1 && selectIndex < letters.length) {
                        choose = selectIndex;
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                resetView();
                break;
            case MotionEvent.ACTION_CANCEL:
                resetView();
                break;
            default:
                break;
        }

        return true;
    }


    private void resetView() {
        choose = -1;
        iSelectedState = false;
        setLayoutParams((int) normalWidth);
    }


    private void setLayoutParams(int width) {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        setLayoutParams(lp);
    }

    Paint paint = new Paint();
    /**
     * 当前选中的位置
     */
    int choose = -1;


    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        height = getHeight();
        singleHeight = height / letters.length;
        getStartAndEndPosFromArg();

        for (int i = 0; i < letters.length; i++) {
            paint.setColor(ContextCompat.getColor(context, R.color.DimGrey));
            setPaintSize(textSize);
            Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
            paint.setTypeface(font);
            paint.setAntiAlias(true);
            if(i==0){
                measureTextSize = paint.measureText(letters[1]);
            }else{
                measureTextSize = paint.measureText(letters[i]);
            }
            float xPos, yPos;
            if (iSelectedState) {
                //如果选中状态，所有的字母的x数值会比没选中之前变大
                xPos = selectedWidth - normalWidth / 2 - measureTextSize / 2;
            } else {
                xPos = normalWidth / 2 - measureTextSize / 2;
            }

            yPos = singleHeight * i + singleHeight;

            if (i == choose) {
                paint.setFakeBoldText(true);
            }

            if ((i >= startPos && i <= endPos) && choose != -1 && iSelectedState) {
                getArcFromCurrentPos(i);
                xPos = getXFormArg();
                float size = getLetterTextSize(i);
                setPaintSize(size);
            }
            if (i == 0) {
                paint.setTypeface(plain);
                canvas.drawText(arrow, xPos - 3, yPos, paint);
            } else {
                canvas.drawText(letters[i], xPos, yPos, paint);
            }

            paint.reset();
        }
    }

    private void setPaintSize(float size) {
        float realSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, size, getResources().getDisplayMetrics());
        if (realSize != paint.getTextSize()) {
            paint.setTextSize(realSize);
        }

    }


    /**
     * 由当前位置获取角度
     */
    private void getArcFromCurrentPos(int i) {
        int value = i - choose;
        if (value == 0) {
            arc = 90;
        } else {
            arc = 90 - value * 18;
        }
        if (Math.abs(value) == 1) {
            paint.setAlpha(77);
        } else if (Math.abs(value) == 2) {
            paint.setAlpha(128);
        } else if (Math.abs(value) == 3) {
            paint.setAlpha(153);
        } else if (Math.abs(value) == 4) {
            paint.setAlpha(204);
        }
    }

    /**
     * 得到开始绘图位置和结束绘图位置
     */
    private void getStartAndEndPosFromArg() {
        if (choose != -1) {
            if (choose <= 4) {
                startPos = 0;
            } else {
                startPos = choose - 4;
            }
            if (choose - letters.length + 5 <= 0) {
                endPos = choose + 4;
            } else {
                endPos = letters.length - 1;
            }
        }
    }

    /**
     * 不同位置的字母不同的字体大小
     * @param i
     * @return
     */
    private float getLetterTextSize(int i) {
        textSize = 14;
        if (i == choose) {
            return textSize + 18;
        } else if (i + 1 == choose || choose + 1 == i) {
            return textSize + 14;
        } else if (i + 2 == choose || choose + 2 == i) {
            return textSize + 10;
        } else if (i + 3 == choose || choose + 3 == i) {
            return textSize + 4;
        }
        return textSize;
    }


    /**
     * 由当前的角度得到x值
     * @return
     */
    private float getXFormArg() {
        float x = (float) (maxHeight * (1 - Math.sin(arc * Math.PI / 180)));
        return x;
    }

    public void setUpdateListView(UpdateListView updateListView) {
        this.updateListView = updateListView;
    }

    public interface UpdateListView {
        void updateListView(String currentChar);
    }

}

