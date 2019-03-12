package com.yunbao.phonelive.ui;

import android.text.Html;
import android.webkit.WebView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.AbsActivity;

public class TestActivity extends AbsActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.a;
    }

    private String content = "<p><span style=\\\"color: rgb(51, 51, 51); font-family: &quot;Microsoft Yahei&quot;, 微软雅黑, arial, 宋体, sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: normal; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; background-color: rgb(255, 255, 255); text-decoration-style: initial; text-decoration-color: initial; display: inline !important; float: none;\\\">在使用strip_tags函数会<a href=\\\"http://testzhibo.2yx.cm\\\" target=\\\"_self\\\">传回错误</a>，而htmlspecialchars不会有错误出现，依然后转换为HTML实体。</span></p>";

    @Override
    protected void main() {
        WebView test = findViewById(R.id.test);

        test.loadData(Html.fromHtml(content).toString(), "text/html", "UTF-8");
    }
}
