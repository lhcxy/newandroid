package com.yunbao.phonelive.ui.views.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.MsgCenterDetailBean;
import com.yunbao.phonelive.utils.DateUtil;

public class ItemMsgCenterDetail extends RecyclerView.ViewHolder {
    private TextView contentTv, timeTv;
//    private String test = "<span style=\"color: rgb(51, 51, 51); font-family: &quot;Microsoft Yahei&quot;, 微软雅黑, arial, 宋体, sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: normal; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; background-color: rgb(255, 255, 255); text-decoration-style: initial; text-decoration-color: initial; display: inline !important; float: none;\">在使用strip_tags函数会<a href=\"http://testzhibo.2yx.cm\" target=\"_self\">传回错误</a>，而htmlspecialchars不会有错误出现，依然后转换为HTML实体。</span>";

    public ItemMsgCenterDetail(@NonNull ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg_center_detail, parent, false));
        contentTv = itemView.findViewById(R.id.item_content_tv);
        timeTv = itemView.findViewById(R.id.item_time_tv);
        contentTv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void onBindData(MsgCenterDetailBean data) {
        if (data != null) {
            if (!TextUtils.isEmpty(data.getContent())) {
                contentTv.setText(Html.fromHtml(data.getContent().replaceAll("<p>", "").replaceAll("</p>", "").replaceAll("<br/>", "")));
            }
            timeTv.setText(DateUtil.getYMDHMByTimeStamp(data.getCreate_time()));
        }
    }
}
