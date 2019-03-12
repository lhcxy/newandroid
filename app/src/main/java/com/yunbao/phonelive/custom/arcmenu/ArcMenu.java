package com.yunbao.phonelive.custom.arcmenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.yunbao.phonelive.R;

/**
 * Created by Saurabh on 14/12/15.
 */
@CoordinatorLayout.DefaultBehavior(MoveUpwardBehaviour.class)
public class ArcMenu extends ViewGroup implements View.OnClickListener {
    //设置常量,标识成枚举
    private static final int POS_LEFT_TOP = 0;
    private static final int POS_LEFT_BOTTOM = 1;
    private static final int POS_RIGHT_TOP = 2;
    private static final int POS_RIGHT_BOTTOM = 3;

    //以下5个成员变量是所需要的.
    //声明两个属性 位置 还有半径
    private Position mPosition = Position.RIGHT_BOTTOM;
    private int mRadius;

    /**
     * 菜单的状态
     */
    private Status mCurrentStatus = Status.CLOSE;

    /**
     * 菜单的主按钮
     */
    private View mCButton;

    //子菜单的回调按钮
    private OnMenuItemClickListener mMenuItemClickListener;


    /**
     * 菜单的位置枚举类,4个位置
     */
    public enum Position {
        LEFT_TOP, LEFT_BOTTOM, RIGHT_TOP, RIGHT_BOTTOM
    }

    public enum Status {
        OPEN, CLOSE
    }

    /**
     * 点击子菜单项,顺便把位置传递过去
     */
    public interface OnMenuItemClickListener {
        void onClick(View view, int pos);

        void onMenuClick();
    }

    //3个构造方法,相互传递.
    //注意别写错误.
    public ArcMenu(Context context) {
        this(context, null);
    }

    public ArcMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //TypedValue.applyDimension是转变标准尺寸的方法 参数一:单位  参数二:默认值 参数三:可以获取当前屏幕的分辨率信息.
//        mRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP
//                , 100, getResources().getDisplayMetrics());

        //获取自定义属性的值
        //参数1:attrs AttributeSet是节点的属性集合
        //参数2:attrs的一个数组集
        //参数3:指向当前theme 某个item 描述的style 该style指定了一些默认值为这个TypedArray
        //参数4;当defStyleAttr 找不到或者为0， 可以直接指定某个style
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.ArcMenu, defStyleAttr, 0);
        int pos = a.getInt(R.styleable.ArcMenu_position, POS_RIGHT_BOTTOM);
        switch (pos) {
            case POS_LEFT_TOP:
                mPosition = Position.LEFT_TOP;
                break;
            case POS_LEFT_BOTTOM:
                mPosition = Position.LEFT_BOTTOM;
                break;
            case POS_RIGHT_TOP:
                mPosition = Position.RIGHT_TOP;
                break;
            case POS_RIGHT_BOTTOM:
                mPosition = Position.RIGHT_BOTTOM;
                break;
        }

        mRadius = (int) a.getDimension(R.styleable.ArcMenu_radius,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP
                        , 100, getResources().getDisplayMetrics()));

//        Log.d("TAG", "Position = " + mPosition + ", radius" + mRadius);
        //使用完必须回收.
        a.recycle();

    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener mMenuItemClickListener) {
        this.mMenuItemClickListener = mMenuItemClickListener;
    }

    /**
     * 测量方法
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            //测量child的各个属性.
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            layoutCButton();
            //获得容器内组件的个数,并且包括这个主的组件(大按钮)
            int count = getChildCount();
            for (int i = 0; i < count - 1; i++) {
                //这里直接获取第一个,是因为getChildAt(0)是红色的按钮.
                View child = getChildAt(i + 1);
                //正常来说,如果设置按钮动画,移动出去后,是不能点击的,这里给按钮设置一个隐藏的属性.等卫星菜单飞过去,在让它们显示出来.
                child.setVisibility(View.GONE);
                /**
                 * 根据画图分析,得出每个子卫星按钮的夹角 a = 90°/(菜单的个数-1)
                 * 假设menu总数为4,那么从左侧数menu1的坐标为(0,R);
                 * menu2的坐标为(R*sin(a),R*cos(a));
                 * menu3的坐标为(R*sin(2a),R*cos(2a));
                 * ...
                 * menuN的坐标为(R,0);
                 * 另:PI为π
                 * */
                //测量每个子卫星组件的在屏幕上面的坐标距离
                //这里count-2,是因为count包含了主按钮
                //每个组件的坐标为(cl,ct);
                int cl = (int) (mRadius * Math.sin(Math.PI / 2 / (count - 2) * i));
                int ct = (int) (mRadius * Math.cos(Math.PI / 2 / (count - 2) * i));

                int cWidth = child.getMeasuredWidth();
                int cHeight = child.getMeasuredHeight();

                //如果卫星菜单存在于底部,那么坐标位置的计算方法,就完全相反.
                /**
                 * 如果菜单位置在底部 左下 ,右下.坐标会发生变化
                 * */
                if (mPosition == Position.LEFT_BOTTOM || mPosition == Position.RIGHT_BOTTOM) {
                    ct = getMeasuredHeight() - cHeight - ct;
                }

                /**
                 * 右上,右下
                 * */
                if (mPosition == Position.RIGHT_TOP || mPosition == Position.RIGHT_BOTTOM) {
                    cl = getMeasuredWidth() - cWidth - cl;
                }


                //子布局的测量坐标;
                child.layout(cl, ct, cl + cWidth, ct + cHeight);


            }
        }

    }

    /**
     * 定位主菜单按钮
     */
    private void layoutCButton() {
        // 给主按钮设置监听
        if (getChildCount() > 0) {
            mCButton = getChildAt(0);
            mCButton.setOnClickListener(this);
        }

        //分别代表控件所处离左侧和上侧得距离
        int l = 0;
        int t = 0;
        int width = mCButton.getMeasuredWidth();
        int height = mCButton.getMeasuredHeight();

        /**
         * getMeasuredHeight()如果前面没有对象调用,那么这个控件继承ViewGroup,就意味着这是获取容器的总高度.
         * getMeasuredWidth()也是同理.
         * 那么就可以判断出控件在四个位置(根据坐标系判断.)
         * */
        switch (mPosition) {
            case LEFT_TOP:
                l = 0;
                t = 0;
                break;
            case LEFT_BOTTOM:
                l = 0;
                t = getMeasuredHeight() - height;
                break;
            case RIGHT_TOP:
                l = getMeasuredWidth() - width;
                t = 0;
                break;
            case RIGHT_BOTTOM:
                l = getMeasuredWidth() - width;
                t = getMeasuredHeight() - height;
                break;
        }

        //layout的四个属性.分别代表主按钮在不同位置距离屏幕左侧和上侧
        mCButton.layout(l, t, l + width, t + height);

    }


    @Override
    public void onClick(View v) {
        //主要确定mCButton的值
//        mCButton = findViewById(R.id.id_button);
        if (mCButton == null) {
            mCButton = getChildAt(0);
        }
        if (mMenuItemClickListener != null) {
            mMenuItemClickListener.onMenuClick();
        }
        //旋转动画
        rotateCButton(v, 0f, 720f);
        //判断菜单是否关闭,如果菜单关闭需要给菜单展开,如果菜单是展开的需要给菜单关闭.
        toggleMenu();
    }

    /**
     * 切换菜单
     * 参数:切换菜单的时间是可控的.
     */
    public void toggleMenu() {
        //为所有子菜单添加动画. :平移动画丶旋转动画
        int count = getChildCount();
        for (int i = 0; i < count - 1; i++) {
            /**
             * 默认位置左上的话,子菜单起始坐标点为(-cl,-ct);
             *   位置右上的话,子菜单起始坐标点为(+cl,-ct);
             *   位置左下的话,子菜单起始坐标点为(-cl,+ct);
             *   位置右下的话,子菜单起始坐标点为(+cl,+ct);**
             * */
            final View childView = getChildAt(i + 1);
            //不管按钮是开还是关,子菜单必须显示才能出现动画效果.
            childView.setVisibility(View.VISIBLE);
            //平移 结束为止 0,0(以子菜单按钮当前位置,为坐标系.)
            int cl = (int) (mRadius * Math.sin(Math.PI / 2 / (count - 2) * i));
            int ct = (int) (mRadius * Math.cos(Math.PI / 2 / (count - 2) * i));

            //创建两个判断变量,判别起始位置.
            int xflag = 1;
            int yflag = 1;
            if (mPosition == Position.LEFT_TOP
                    || mPosition == Position.LEFT_BOTTOM) {
                xflag = -1;
            }
            if (mPosition == Position.LEFT_TOP
                    || mPosition == Position.RIGHT_TOP) {
                yflag = -1;
            }
            //多个动画同时使用使用,用到AnimationSet
            AnimationSet animset = new AnimationSet(true);
            Animation tranAnim = null;

            //to open 打开的情况下
            if (mCurrentStatus == Status.CLOSE) {
                tranAnim = new TranslateAnimation(xflag * cl, 0, yflag * ct, 0);
                //当卫星菜单打开的时候,按钮就可以进行点击.
                childView.setClickable(true);
                childView.setFocusable(true);
            } else {//to close
                tranAnim = new TranslateAnimation(0, xflag * cl, 0, yflag * ct);
                //当卫星菜单关闭的时候,按钮也不能随之点击.
                childView.setClickable(false);
                childView.setFocusable(false);
            }
            tranAnim.setFillAfter(true);
            tranAnim.setDuration(ANIMATION_DURATION);
            //设置弹出速度.
            tranAnim.setStartOffset((i * 100) / count);
            //为动画设置监听 如果需要关闭的话,在动画结束的同时,需要将子菜单的按钮全部隐藏.
            tranAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                //在动画结束时,进行设置.
                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mCurrentStatus == Status.CLOSE) {
//            Log.d("动画结束状态",mCurrentStatus +"");
                        childView.setVisibility(View.GONE);
                        if (getChildAt(0) instanceof ImageView) {
                            ((ImageView) getChildAt(0)).setImageDrawable(getResources().getDrawable(R.mipmap.ic_live_menus_add));
                        }
                    } else if (mCurrentStatus == Status.OPEN) {
                        if (getChildAt(0) instanceof ImageView) {
                            ((ImageView) getChildAt(0)).setImageDrawable(getResources().getDrawable(R.mipmap.ic_live_menus_close));
                        }
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            //设置旋转动画(转两圈)
            RotateAnimation rotateAnim = new RotateAnimation(0, 720,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnim.setDuration(ANIMATION_DURATION);
            rotateAnim.setFillAfter(true);

            //把两个动画放到动画集里面
            //注意动画顺序.先增加旋转/在增加移动./
            animset.addAnimation(rotateAnim);
            animset.addAnimation(tranAnim);
            childView.startAnimation(animset);

            final int pos = i + 1;
            //设置子菜单的点击事件
            childView.setOnClickListener(v -> {
                if (mMenuItemClickListener != null) {
                    mMenuItemClickListener.onClick(childView, pos);
                }
//                menuItemAnim(pos - 1);
                //切换菜单状态
//                changeStatus();
            });
        }
        /**
         * 当所有子菜单切换完成后,那么菜单的状态也发生了改变.
         * 所以changeStatus()必须放在循环外,
         * */
        //切换菜单状态
        changeStatus();

    }


    /**
     * 切换菜单状态
     */
    private void changeStatus() {
        //在执行一个操作之后,如果按钮是打开的在次点击就会切换状态.
        mCurrentStatus = (mCurrentStatus == Status.CLOSE ? Status.OPEN :
                Status.CLOSE);
        Log.d("动画结束状态", mCurrentStatus + "");
    }

    public boolean isOpen() {
        return mCurrentStatus == Status.OPEN;
    }


    //设置旋转动画绕自身旋转一圈 然后持续时间为300
    private void rotateCButton(View v, float start, float end) {
        RotateAnimation anim = new RotateAnimation(start, end, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(ANIMATION_DURATION);
        //保持动画旋转后的状态.
        anim.setFillAfter(true);
        v.startAnimation(anim);
    }

    /**
     * 添加menuItem的点击动画
     */
    private void menuItemAnim(int pos) {

        for (int i = 0; i < getChildCount() - 1; i++) {
            View childView = getChildAt(i + 1);
            //在判断条件下,写入动画
            //当其中一个子菜单被点击后,自身变大并且消失
            //其他子菜单则变小消失.
//            if (i == pos) {
//                childView.startAnimation(scaleBigAnim());
//            } else {
//                childView.startAnimation(scaleSmallAnim());
//            }
//            int cl = (int) (mRadius * Math.sin(Math.PI / 2 / (count - 2) * i));
//            int ct = (int) (mRadius * Math.cos(Math.PI / 2 / (count - 2) * i));
            //当子菜单被点击之后,其他子菜单就要变成不可被点击和获得焦点的状态,
            childView.setClickable(false);
            childView.setFocusable(false);
        }
    }

    public static final int ANIMATION_DURATION = 240;

    /**
     * 为当前点击的Item设置变大和透明度降低的动画
     *
     * @return
     */
    private Animation scaleBigAnim() {
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 4.0f, 1.0f, 4.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0.0f);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);

        animationSet.setDuration(ANIMATION_DURATION);
        animationSet.setFillAfter(true);
        return animationSet;
    }

    private Animation scaleSmallAnim() {
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0.0f);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setDuration(ANIMATION_DURATION);
        animationSet.setFillAfter(true);
        return animationSet;
    }
}
