package com.yunbao.phonelive.ui.views;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.AbsActivity;
import com.yunbao.phonelive.activity.EMChatRoomActivity;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.glide.ItemDecoration;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.ui.views.adapter.MsgSearchContentRvAda;
import com.yunbao.phonelive.ui.views.adapter.MsgSearchHistoryRvAda;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.SharedPreferencesUtil;
import com.yunbao.phonelive.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MsgSearchActivity extends AbsActivity implements OnItemClickListener<Integer> {
    private static final int MAX_COUNT = 3;  //搜索历史最多显示条数

    private EditText searchEt;
    private RecyclerView historyRv, contentRv;
    private List<String> historyDatas;
    private List<UserBean> contentDatas;
    private MsgSearchHistoryRvAda historyAdapter;
    private LinearLayout historyLl;
    private MsgSearchContentRvAda contentRvAda;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ui_msg_search;
    }


    @Override
    protected void main() {
        contentRv = findViewById(R.id.msg_search_content_rv);
        historyRv = findViewById(R.id.mgs_search_history_rv);
        searchEt = findViewById(R.id.msg_search_et);
        historyLl = findViewById(R.id.msg_search_history_ll);
        getHistory();
        historyAdapter = new MsgSearchHistoryRvAda(historyDatas);
        historyAdapter.setListener(this);
        historyRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        historyRv.setAdapter(historyAdapter);
        contentRv.addItemDecoration(new ItemDecoration(this, 0x00EBEBF2, 0, 1));
        contentRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        contentRvAda = new MsgSearchContentRvAda(contentDatas);
        contentRvAda.setListener(this::onItemClick);
        contentRv.setAdapter(contentRvAda);

        disposable.add(RxView.clicks(findViewById(R.id.subtitle_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(
                o -> {
                    String s = searchEt.getText().toString();
                    if (!TextUtils.isEmpty(s)) {
                        search(s);
                    }
                }
        ));
        disposable.add(RxTextView.afterTextChangeEvents(searchEt).subscribe(textViewAfterTextChangeEvent -> {
            if (textViewAfterTextChangeEvent != null) {
                if (textViewAfterTextChangeEvent.editable().toString().length() <= 0) {
                    historyLl.setVisibility(View.VISIBLE);
                    contentRv.setVisibility(View.GONE);
                } else historyLl.setVisibility(View.GONE);
            }
        }));

    }


    private void addSearchHistory(String content) {
        historyDatas.add(0, content);
        if (historyDatas.size() > MAX_COUNT) {
            historyDatas.remove(MAX_COUNT);
        }
        historyAdapter.notifyDataSetChanged();
    }

    private void saveHistory() {
        SharedPreferencesUtil.getInstance().saveString("searchHistory", JSON.toJSONString(historyDatas));
    }

    private void getHistory() {
        String searchHistory = SharedPreferencesUtil.getInstance().getString("searchHistory");
        if (!TextUtils.isEmpty(searchHistory)) {
            historyDatas = JSON.parseArray(searchHistory, String.class);
        } else historyDatas = new ArrayList<>();
    }


    private void search(String content) {
        addSearchHistory(content);
        HttpUtil.searchBody(content, callback);
    }

    private HttpCallback callback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0 && info != null && info.length > 0) {
                contentRv.setVisibility(View.VISIBLE);
                contentDatas = JSON.parseArray(Arrays.toString(info), UserBean.class);
                if (contentRvAda == null) {
                    contentRvAda = new MsgSearchContentRvAda(contentDatas);
                    contentRvAda.setListener(MsgSearchActivity.this::onItemClick);
                }
                contentRvAda.setDatas(contentDatas);
                contentRvAda.notifyDataSetChanged();
            } else {
                contentRv.setVisibility(View.GONE);
                ToastUtil.show("没有搜索到该用户");
            }
        }

        @Override
        public boolean showLoadingDialog() {
            return true;
        }

        @Override
        public Dialog createLoadingDialog() {
            return DialogUitl.loadingDialog(mContext, mContext.getString(R.string.waiting));
        }
    };

    @Override
    protected void onPause() {
        saveHistory();
        super.onPause();
    }

    @Override
    public void onItemClick(Integer item, int position) {
        if (item == 0) {
            //搜索历史
            searchEt.setText(historyDatas.get(position));
            search(historyDatas.get(position));
        } else if (item == 1) {
            //删除历史
            historyDatas.remove(position);
            historyAdapter.notifyDataSetChanged();
        } else if (item == 2) {
            //跳转聊天界面
            if (contentDatas.size() > position)
                openChatRoom(contentDatas.get(position));
        }
    }

    private void openChatRoom(UserBean chatUserBean) {
        if (AppConfig.isUnlogin()) {
            unloginHint(contentRv);
            return;
        } else if (chatUserBean == null || chatUserBean.getId().equals(AppConfig.getInstance().getUserBean().getId())) {
            ToastUtil.show("自己不能与自己进行私信");
            return;
        }

        Intent intent = new Intent(this, EMChatRoomActivity.class);
        intent.putExtra("from", 0);
        intent.putExtra("touser", chatUserBean);
        intent.putExtra("isAttention", 0);
        startActivity(intent);
    }
}
