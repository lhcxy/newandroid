package com.yunbao.phonelive.ui.views.pop;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.interfaces.ChoseCallback;
import com.yunbao.phonelive.utils.DpUtil;

public class RankTypePop extends PopupWindow {

    private Context context;
    private RadioGroup radioGroup;

    public RankTypePop(Context context) {
        super(context);
        this.context = context;
        initalize();
        setOutsideTouchable(true);
        setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x00000000);
//        设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
//        backgroundAlpha(context, 0);
    }


    private void initalize() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.pop_rank_list_menus, null);
        radioGroup = view.findViewById(R.id.rank_list_rg);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.pop_live_share_tv) {
                if (callback != null)
                    callback.onChose(0);
            } else if (checkedId == R.id.pop_live_report_tv) {
                if (callback != null)
                    callback.onChose(1);
            }
            dismiss();
        });
        setContentView(view);
        initWindow();
        setOnDismissListener(() -> backgroundAlpha(view.getContext(), 1f));
    }

    private void initWindow() {
//        this.setFocusable(false);
//        this.setOutsideTouchable(false);
    }

    //设置添加屏幕的背景透明度
    public void backgroundAlpha(Context context, float bgAlpha) {

        WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();
        lp.alpha = bgAlpha;
        ((Activity) context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        ((Activity) context).getWindow().setAttributes(lp);
    }

    public void showAtBottom(View view, int index) {
        //弹窗位置设置
        if (radioGroup != null)
            if (index == 0) {
                radioGroup.check(R.id.pop_live_share_tv);
            } else {
                radioGroup.check(R.id.pop_live_report_tv);
            }
        backgroundAlpha(view.getContext(), 0.7f);
        showAsDropDown(view, Math.abs((view.getWidth() - getWidth()) / 2) - 160, 0);
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
