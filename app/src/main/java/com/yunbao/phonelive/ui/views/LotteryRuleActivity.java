package com.yunbao.phonelive.ui.views;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.AbsActivity;

public class LotteryRuleActivity extends AbsActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_lottery_rule;
    }

    @Override
    protected void main() {
        setTitle("活动规则");
    }
}
