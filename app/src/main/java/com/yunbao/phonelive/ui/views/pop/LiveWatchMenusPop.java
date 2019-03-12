package com.yunbao.phonelive.ui.views.pop;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.VerticalRangeSeekBar;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.interfaces.ChoseCallback;
import com.yunbao.phonelive.utils.DpUtil;

public class LiveWatchMenusPop extends PopupWindow {

    private Context context;

    public LiveWatchMenusPop(Context context) {
        super(context);
        this.context = context;
        initalize();
        setOutsideTouchable(true);
        setFocusable(true);
    }


    private void initalize() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.pop_live_watcher_menus, null);
        view.findViewById(R.id.pop_live_share_tv).setOnClickListener(v -> {
            dismiss();
            if (callback != null) {
                callback.onChose(0);
            }
        });
        view.findViewById(R.id.pop_live_report_tv).setOnClickListener(v -> {
            dismiss();
            if (callback != null) {
                callback.onChose(1);
            }
        });
        setContentView(view);
        initWindow();
    }

    private void initWindow() {
        this.setFocusable(false);
        this.setOutsideTouchable(false);
//        this.update();
        //实例化一个ColorDrawable颜色为半透明
//        ColorDrawable dw = new ColorDrawable(0x00000000);
        //设置SelectPicPopupWindow弹出窗体的背景
//        this.setBackgroundDrawable(dw);
//        backgroundAlpha((Activity) context, 0.9f);//0.0-1.0
//        this.setOnDismissListener(() -> {
//        });
    }

    //设置添加屏幕的背景透明度
    public void backgroundAlpha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

    public void showAtBottom(View view) {
        //弹窗位置设置
        showAsDropDown(view, Math.abs((view.getWidth() - getWidth()) / 2), 10);
        //showAtLocation(view, Gravity.TOP | Gravity.RIGHT, 10, 110);//有偏差
    }

    public void showAtTop(View view) {
        //弹窗位置设置
        showAtLocation(view, Gravity.RIGHT, DpUtil.dp2px(8), DpUtil.dp2px(40));//有偏差
    }

    public void showLeft(View view) {
        showAsDropDown(view, -(view.getWidth() + DpUtil.dp2px(18)), -DpUtil.dp2px(16));
    }

    private ChoseCallback callback;

    public void setCallback(ChoseCallback callback) {
        this.callback = callback;
    }

}
