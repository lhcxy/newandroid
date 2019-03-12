package com.yunbao.phonelive.activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewAfterTextChangeEvent;
import com.lzy.okgo.model.Response;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.adapter.SharedSdkAdapter;
import com.yunbao.phonelive.bean.ConfigBean;
import com.yunbao.phonelive.bean.SharedSdkBean;
import com.yunbao.phonelive.event.LoginSuccessEvent;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.http.JsonBean;
import com.yunbao.phonelive.interfaces.CommonCallback;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.ui.helper.CommonItemDecoration;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.L;
import com.yunbao.phonelive.utils.LoginUtil;
import com.yunbao.phonelive.utils.SharedSdkUitl;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.utils.ValidateUitl;
import com.yunbao.phonelive.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import io.reactivex.functions.Consumer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by cxf on 2017/8/5.
 * 登录页面
 */

public class LoginActivity extends AbsActivity implements OnItemClickListener<SharedSdkBean> {

    private String mType;
    private Dialog mLoginAuthDialog;
    private RecyclerView mRecyclerView;
    private View mOtherLogin;
    private static final int REGISTER_CODE = 100;
    public static final String LOGIN_PHONE_NUM = "phoneNum";
    public static final String LOGIN_PWD = "pwd";
    private EditText accountEt, pwdEt;
    private ImageView cleanIv, showPwdIv;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void main() {
        initView();
        getConfig();
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        accountEt = findViewById(R.id.login_account_et);
        pwdEt = findViewById(R.id.login_pwd_et);
        cleanIv = findViewById(R.id.login_account_clean_iv);
        showPwdIv = findViewById(R.id.login_pwd_show_iv);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.addItemDecoration(new CommonItemDecoration(DpUtil.dp2px(32), DpUtil.dp2px(2)));
        mOtherLogin = findViewById(R.id.other_login);
        //登录即代表你同意《服务和隐私协议》
        TextView textView = (TextView) findViewById(R.id.login_text);
        textView.setText(Html.fromHtml(WordUtil.getString(R.string.login_text)
                + "<font color='#ffd350'>" + WordUtil.getString(R.string.login_text_2) + "</font>"
        ));
        EventBus.getDefault().register(this);

        disposable.add(RxView.clicks(findViewById(R.id.login_forget_pwd)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            startActivity(new Intent(this, FindPwdActivity.class));
        }));
        disposable.add(RxView.clicks(findViewById(R.id.login_regiest_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            startActivity(new Intent(this, RegisterActivity.class));
        }));
        disposable.add(RxView.clicks(cleanIv).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            accountEt.setText("");
        }));
        disposable.add(RxTextView.afterTextChangeEvents(accountEt).subscribe(textViewAfterTextChangeEvent -> {
            if (textViewAfterTextChangeEvent.editable().length() > 0) {
                cleanIv.setVisibility(View.VISIBLE);
            } else {
                cleanIv.setVisibility(View.INVISIBLE);
            }
        }));
        disposable.add(RxView.clicks(showPwdIv).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            showPwd();
        }));

        disposable.add(RxView.clicks(findViewById(R.id.login_commit_tv)).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o ->
                login()
        ));
    }

    private boolean isShowPwd = false;

    private void showPwd() {
        if (isShowPwd) {
            //如果选中，显示密码
            pwdEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            showPwdIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_login_pwd_eye_close));
        } else {
            //否则隐藏密码
            pwdEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
            showPwdIv.setImageDrawable(getResources().getDrawable(R.mipmap.ic_login_pwd_eye_open));
        }
        isShowPwd = !isShowPwd;
    }

    private void getConfig() {
        HttpUtil.getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean bean) {
                String[] loginTypes = bean.getLogin_type();
                if (loginTypes.length > 0) {
                    SharedSdkAdapter adapter = new SharedSdkAdapter(loginTypes, false, false, true);
                    adapter.setOnItemClickListener(LoginActivity.this);
                    mRecyclerView.setAdapter(adapter);
                } else {
                    mOtherLogin.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void loginClick(View view) {
        switch (view.getId()) {
            case R.id.login_text:
                forwardHtml();
                break;
//            case R.id.btn_phone_login:
//                phoneLoginActivity();
//                break;
        }
    }

    private void forwardHtml() {
        String url = AppConfig.HOST + "/index.php?g=portal&m=page&a=index&id=4";
        Intent intent = new Intent(mContext, WebActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    private void phoneLoginActivity() {
        startActivity(new Intent(mContext, LoginActivityEx.class));
    }


    //登录成功！
    private void loginSuccess(int code, String msg, String[] info) {
        if (code == 0 && info.length > 0) {
            JSONObject obj = JSON.parseObject(info[0]);
            AppConfig.getInstance().saveUserInfo(info[0]);
            String uid = obj.getString("id");
            String token = obj.getString("token");
            LoginUtil.login(uid, token);
            EventBus.getDefault().post(new LoginSuccessEvent());
            finish();
        } else {
            ToastUtil.show(msg);
        }
    }

    private void thirdLogin(String openid, PlatformDb platDB) {
        HttpUtil.loginByThird(openid, platDB.getUserName(), mType, platDB.getUserIcon(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                loginSuccess(code, msg, info);
            }

            @Override
            public void onFinish() {
                if (mLoginAuthDialog != null) {
                    mLoginAuthDialog.dismiss();
                }
            }

            @Override
            public void onError(Response<JsonBean> response) {
                if (mLoginAuthDialog != null) {
                    mLoginAuthDialog.dismiss();
                }
                super.onError(response);
            }
        });
    }


    //第三方登录回调
    private SharedSdkUitl.ShareListener mShareListener = new SharedSdkUitl.ShareListener() {
        @Override
        public void onSuccess(Platform platform) {
            ToastUtil.show(getString(R.string.auth_success));
            final PlatformDb platDB = platform.getDb();
            if (platDB.getPlatformNname().equals(Wechat.NAME)) {

                String openid = platDB.get("unionid");
                thirdLogin(openid, platDB);
            } else if (platDB.getPlatformNname().equals(QQ.NAME)) {
                //需要数据打通的时候用
//                HttpUtil.getQQLoginOpenid(platDB.getToken(), new StringCallback() {
//                    @Override
//                    public void onSuccess(Response<String> response) {
//                        String res = response.body();
//                        String openid = res.trim().substring(res.indexOf("unionid") + 10, res.indexOf("}") - 1);
//                        thirdLogin(openid, platDB);
//                    }
//                });

                //没有数据打通的时候时候用
                getQQUnionid(platform);
            }
        }

        @Override
        public void onError(Platform platform) {
            if (mLoginAuthDialog != null) {
                mLoginAuthDialog.dismiss();
            }
            ToastUtil.show(getString(R.string.auth_failure));
        }

        @Override
        public void onCancel(Platform platform) {
            if (mLoginAuthDialog != null) {
                mLoginAuthDialog.dismiss();
            }
            ToastUtil.show(getString(R.string.auth_cancle));
        }
    };

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onLoginSuccessEvent(LoginSuccessEvent e) {
//        finish();
//    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        SharedSdkUitl.getInstance().releaseShareListener();
        HttpUtil.cancel(HttpUtil.GET_CONFIG);
        HttpUtil.cancel(HttpUtil.LOGIN_BY_THIRD);
        HttpUtil.cancel(HttpUtil.GET_QQLOGIN_OPENID);
        HttpUtil.cancel(HttpUtil.GET_RECOMMEND);
        super.onDestroy();
    }

    @Override
    public void onItemClick(SharedSdkBean item, int position) {
        mType = item.getType();
        if (mLoginAuthDialog == null) {
            mLoginAuthDialog = DialogUitl.loginAuthDialog(this);
        }
        mLoginAuthDialog.show();
        SharedSdkUitl.getInstance().login(mType, mShareListener);
    }

    private void getQQUnionid(Platform plat) {
        Log.e("//", "getQQUnionid: " + plat.getDb().getToken());
        OkHttpClient okHttpClient = new OkHttpClient();
        //2构造Request,
        //builder.get()代表的是get请求，url方法里面放的参数是一个网络地址
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url("https://graph.qq.com/oauth2.0/me?access_token=" + plat.getDb().getToken() + "&unionid=1").build();

        //3将Request封装成call
        Call call = okHttpClient.newCall(request);

        //4，执行call，这个方法是异步请求数据
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //失败调用
                Log.e("MainActivity", "onFailure: ");
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                //获取网络访问返回的字符串
                String sd = response.body().string();
                sd = sd.replace("callback(", "");
                sd = sd.replace(");", "").trim();
                JSONObject jsonObject = JSON.parseObject(sd);
                if (jsonObject != null) {
                    if (jsonObject.containsKey("unionid")) {
                        thirdLogin(jsonObject.getString("unionid"), plat.getDb());
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REGISTER_CODE && resultCode == RESULT_OK) {
            String phoneNum = intent.getStringExtra(LOGIN_PHONE_NUM);
            String pwd = intent.getStringExtra(LOGIN_PWD);
            accountEt.setText(phoneNum);
            pwdEt.setText(pwd);
        }
    }

    private void login() {
        String phoneNum = accountEt.getText().toString();
        if (TextUtils.isEmpty(phoneNum)) {
            accountEt.requestFocus();
            ToastUtil.show("请输入登录账号");
            return;
        }
        String pwd = pwdEt.getText().toString();
        if (TextUtils.isEmpty(pwd)) {
            pwdEt.requestFocus();
            ToastUtil.show("请输入登录密码");
            return;
        }
        HttpUtil.login(phoneNum, pwd, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                loginSuccess(code, msg, info);
            }

            @Override
            public boolean showLoadingDialog() {
                return true;
            }

            @Override
            public Dialog createLoadingDialog() {
                return DialogUitl.loadingDialog(mContext, getString(R.string.login_ing));
            }
        });
    }

}
