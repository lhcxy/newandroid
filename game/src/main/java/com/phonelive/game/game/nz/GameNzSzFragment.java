package com.phonelive.game.game.nz;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.phonelive.game.game.LastCoinEvent;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2017/10/19.
 * 上庄列表的弹窗
 */

public class GameNzSzFragment extends DialogFragment implements View.OnClickListener {

    private Context mContext;
    private View mRootView;
    private ListView mListView;
    private TextView mBtnApplySz;
    private GameNzSzAdapter mAdapter;
    private ProgressBar mProgressBar;
    private String mStream;
    private String mBankerLimit;
    private static final int SZ_NOT_APPLY = 0;//没有申请上庄
    private static final int SZ_APPLY = 1;//已经申请上庄
    private static final int SZ_UNKONOW = 2;//未知申请上庄
    private int mStauts = SZ_UNKONOW;//是否申请上庄了

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();
        Dialog dialog = new Dialog(mContext, R.style.dialog2);
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.game_fragment_nz_sz, null);
        dialog.setContentView(mRootView);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = DpUtil.dp2px(300);
        params.height = DpUtil.dp2px(400);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.progressbar);
        mListView = (ListView) mRootView.findViewById(R.id.listView);
        mBtnApplySz = (TextView) mRootView.findViewById(R.id.btn_apply_sz);
        mBtnApplySz.setOnClickListener(this);
        mRootView.findViewById(R.id.btn_close).setOnClickListener(this);
        Bundle bundle = getArguments();
        mStream = bundle.getString("stream");
        mBankerLimit = bundle.getString("bankerLimit");
    }

    private void initData() {
        HttpUtil.gameNiuGetBanker(mStream, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<GameNzSzBean> list = JSON.parseArray(Arrays.toString(info), GameNzSzBean.class);
                    if (list.size() >= 2) {
                        for (int i = 0; i < list.size(); i++) {
                            if ("0".equals(list.get(i).getId())) {
                                list.remove(i);
                                break;
                            }
                        }
                    }
                    if (list.size() > 0) {
                        if (mAdapter == null) {
                            mAdapter = new GameNzSzAdapter(list);
                        } else {
                            mAdapter.setList(list);
                        }
                        mListView.setAdapter(mAdapter);
                    }
                    mStauts = isSz(list);
                    if (mStauts == SZ_NOT_APPLY) {
                        mBtnApplySz.setText(WordUtil.getString(R.string.game_nz_apply_sz_1));
                    } else if (mStauts == SZ_APPLY) {
                        mBtnApplySz.setText(WordUtil.getString(R.string.game_nz_apply_sz_2));
                    }
                } else {
                    ToastUtil.show(msg);
                }
            }

            @Override
            public void onFinish() {
                if (mProgressBar.getVisibility() == View.VISIBLE) {
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 判断自己是否上庄了
     */
    private int isSz(List<GameNzSzBean> list) {
        int result = SZ_NOT_APPLY;
        for (GameNzSzBean bean : list) {
            if (bean.getId().equals(AppConfig.getInstance().getUid())) {
                result = SZ_APPLY;
                break;
            }
        }
        return result;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_apply_sz:
                if (mStauts == SZ_NOT_APPLY) {
                    applySz();
                } else if (mStauts == SZ_APPLY) {
                    applyXz();
                }
                break;
            case R.id.btn_close:
                dismiss();
                break;
        }
    }

    /**
     * 申请上庄
     */
    private void applySz() {
        DialogUitl.inputDialog(mContext, WordUtil.getString(R.string.game_nz_apply_sz_yajin),
                WordUtil.getString(R.string.game_nz_apply_sz_yajin2) + mBankerLimit + AppConfig.getInstance().getConfig().getName_coin()
                , "", "", new DialogUitl.Callback3() {
                    @Override
                    public void confirm(Dialog dialog, String text) {
                        if (!"".equals(text)) {
                            dialog.dismiss();
                            HttpUtil.gameNiuSetBanker(mStream, text, new HttpCallback() {
                                @Override
                                public void onSuccess(int code, String msg, String[] info) {
                                    if (code == 0) {
                                        if (info.length > 0) {
                                            JSONObject obj = JSON.parseObject(info[0]);
                                            EventBus.getDefault().post(new LastCoinEvent(obj.getString("coin")));
                                            ToastUtil.show(obj.getString("msg"));
                                            dismiss();
                                        }
                                    } else {
                                        ToastUtil.show(msg);
                                    }
                                }

                                @Override
                                public void onStart() {
                                    if (mProgressBar.getVisibility() == View.GONE) {
                                        mProgressBar.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onFinish() {
                                    if (mProgressBar.getVisibility() == View.VISIBLE) {
                                        mProgressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        } else {
                            ToastUtil.show(WordUtil.getString(R.string.game_nz_apply_sz_yajin_empty));
                        }
                    }
                }).show();
    }


    /**
     * 申请下庄
     */
    private void applyXz() {
        HttpUtil.gameNiuQuitBanker(mStream, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (info.length > 0) {
                        ToastUtil.show(JSON.parseObject(info[0]).getString("msg"));
                        dismiss();
                    }
                } else {
                    ToastUtil.show(msg);
                }
            }

            @Override
            public void onStart() {
                if (mProgressBar.getVisibility() == View.GONE) {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFinish() {
                if (mProgressBar.getVisibility() == View.VISIBLE) {
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        HttpUtil.cancel(HttpUtil.GAME_NIU_GET_BANKER);
        HttpUtil.cancel(HttpUtil.GAME_NIU_SET_BANKER);
        HttpUtil.cancel(HttpUtil.GAME_NIU_QUIT_BANKER);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
