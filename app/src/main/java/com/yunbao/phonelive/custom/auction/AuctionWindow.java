package com.yunbao.phonelive.custom.auction;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.ChargeActivity;
import com.yunbao.phonelive.activity.WebActivity;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.socket.SocketUtil;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.L;
import com.yunbao.phonelive.utils.ToastUtil;

/**
 * Created by cxf on 2017/9/26.
 * 竞拍窗口
 */

public class AuctionWindow implements View.OnClickListener {

    private Context mContext;
    private ViewGroup mParent;
    private RelativeLayout mView;
    private TextView mTime;
    private TextView mPrice;
    private String mAuctionId;
    private String mAuctionThumb;
    private String mAuctionTitle;
    private String mAuctionPrice;
    private int mDuration;//竞拍总时长
    private int mCurTime;    //已经进行的时长
    private Handler mHandler;
    private boolean mIsAnchor;//是否是主播
    private View mMostUserView;//出价最高的人
    private ImageView mHeadImg;
    private TextView mName;
    private TextView mMostPrice;
    private View mAuctionEndView;
    private View mPayView;
    private TextView mPayLastTime;
    private int mLastTime = 15 * 1000 * 60;//15分钟

    public AuctionWindow(Context context, ViewGroup parent, boolean isAnchor) {
        mContext = context;
        mParent = parent;
        mIsAnchor = isAnchor;
        mView = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.view_auction, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(DpUtil.dp2px(150), DpUtil.dp2px(240));
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.setMargins(0, DpUtil.dp2px(70), 0, 0);
        mView.setLayoutParams(params);
        mTime = (TextView) mView.findViewById(R.id.time);
        View addGroup = mView.findViewById(R.id.add_group);
        if (isAnchor) {
            addGroup.setVisibility(View.GONE);
        } else {
            mPrice = (TextView) addGroup.findViewById(R.id.price);
            addGroup.findViewById(R.id.btn_add).setOnClickListener(this);
            addGroup.findViewById(R.id.btn_detail).setOnClickListener(this);

        }
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        mCurTime += 1000;
                        getLastTime();
                        break;
                    case 1:
                        hideResultView();
                        break;
                    case 2:
                        setPayLastTime();
                        break;
                }

            }
        };
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_detail:
                forwardHtml();
                break;
            case R.id.btn_add:
                addPrice();
                break;
            case R.id.btn_to_pay://打开付款窗口
                addPayView();
                break;
            case R.id.btn_pay://执行付款
                doPay();
                break;
            case R.id.btn_cancel://取消付款 关闭付款窗口
                hidePayView();
                break;
            case R.id.coin://跳转到充值页面
                mContext.startActivity(new Intent(mContext, ChargeActivity.class));
                break;
        }
    }

    /**
     * 跳转到竞拍详情页面
     */
    private void forwardHtml() {
        Intent intent = new Intent(mContext, WebActivity.class);
        String url = AppConfig.HOST + "/index.php?g=Appapi&m=Auction&a=detail&id=" + mAuctionId + "&uid=" + AppConfig.getInstance().getUid() + "&token=" + AppConfig.getInstance().getToken();
        intent.putExtra("url", url);
        mContext.startActivity(intent);
    }

    /**
     * 加价
     */
    private void addPrice() {
        HttpUtil.setAuction(mAuctionId, mAddCallback);
    }

    private HttpCallback mAddCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                JSONObject obj = JSON.parseObject(info[0]);
                String mostPrice = obj.getString("bid_price");
                SocketUtil.getInstance().auctionAddMoney(mostPrice);
                ToastUtil.show(mContext.getString(R.string.auction_success));
            } else if (code == 1003) {
                DialogUitl.confirmDialog(mContext, mContext.getString(R.string.tip), msg, new DialogUitl.Callback() {
                    @Override
                    public void confirm(Dialog dialog) {
                        dialog.dismiss();
                       // forwardHtml();
                    }

                    @Override
                    public void cancel(Dialog dialog) {
                        dialog.dismiss();
                    }
                }).show();
            } else {
                ToastUtil.show(msg);
            }
        }
    };


    /**
     * 显示出价最高的那个人
     */
    public void showMostUser(String uhead, String uname, String price) {
        mAuctionPrice = price;
        if (mMostUserView == null) {
            mMostUserView = LayoutInflater.from(mContext).inflate(R.layout.view_auction_most_user, null);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(DpUtil.dp2px(150), DpUtil.dp2px(40));
            params.setMargins(0, DpUtil.dp2px(40), 0, 0);
            mMostUserView.setLayoutParams(params);
            mView.addView(mMostUserView);
            mHeadImg = (ImageView) mMostUserView.findViewById(R.id.uhead);
            mName = (TextView) mMostUserView.findViewById(R.id.uname);
            mMostPrice = (TextView) mMostUserView.findViewById(R.id.most_price);
        }
        ImgLoader.displayCircleOrangeBorder(uhead, mHeadImg);
        mName.setText(uname);
        mMostPrice.setText(mAuctionPrice);
        if (!mIsAnchor) {
            mPrice.setText(mAuctionPrice);
        }
    }

    /**
     * 设置竞拍的各种参数
     */
    public AuctionWindow setData(String auctionId, String auctionThumb, String title, String startPrice, int duration) {
        mAuctionId = auctionId;
        mAuctionThumb = auctionThumb;
        mAuctionTitle = title;
        mAuctionPrice = startPrice;
        mDuration = duration * 1000;
        if (mPrice != null) {
            mPrice.setText(mAuctionPrice);
        }
        return this;
    }

    public void show() {
        mParent.addView(mView);
        getLastTime();
    }

    private void getLastTime() {
        int lastTime = mDuration - mCurTime;
        if (lastTime < 1000) {
            mTime.setText(getDurationText(0));
            if (mIsAnchor) {
                HttpUtil.auctionEnd(mAuctionId, new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            JSONObject obj = JSON.parseObject(info[0]);
                            String bidUid = obj.getString("bid_uid");
                            if ("0".equals(bidUid)) {
                                SocketUtil.getInstance().auctionEnd(3, "", "", "", "");
                            } else {
                                SocketUtil.getInstance().auctionEnd(4, bidUid, obj.getString("user_nicename"), obj.getString("avatar"), obj.getString("bid_price"));
                            }
                        } else {
                            ToastUtil.show(msg);
                        }
                    }
                });
            }
            stopAuction();
        } else {
            mTime.setText(getDurationText(lastTime));
            mHandler.sendEmptyMessageDelayed(0, 1000);
        }
    }

    private void stopAuction() {
        if (mHandler != null) {
            mHandler.removeMessages(0);
        }
        if (mView.getParent() != null) {
            mParent.removeView(mView);
        }
    }


    /**
     * 把一个int类型的总毫秒数转成时长
     */
    private String getDurationText(int mms) {
        int hours = mms / (1000 * 60 * 60);
        int minutes = (mms % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (mms % (1000 * 60)) / 1000;
        String h = mContext.getString(R.string.hour) + ":";
        String m = mContext.getString(R.string.min) + ":";
        String s = mContext.getString(R.string.sec);
        String res = "";
        if (hours > 0) {
            if (hours < 10) {
                res += "0" + hours + h;
            } else {
                res += hours + h;
            }
        } else {
            res += "00" + h;
        }
        if (minutes > 0) {
            if (minutes < 10) {
                res += "0" + minutes + m;
            } else {
                res += minutes + m;
            }
        } else {
            res += "00" + m;
        }
        if (seconds > 0) {
            if (seconds < 10) {
                res += "0" + seconds + s;
            } else {
                res += seconds + s;
            }
        } else {
            res += "00" + s;
        }
        return res;
    }

    public void auctionSuccess(String bidUid, String uhead, String uname, String mostPrice) {
        setAuctionEndView(true);
        if (mIsAnchor) {
            View group = mAuctionEndView.findViewById(R.id.group);
            group.setVisibility(View.VISIBLE);
            TextView nameTextView = (TextView) group.findViewById(R.id.uname);
            nameTextView.setText(uname);
            TextView priceTextView = (TextView) group.findViewById(R.id.most_price);
            priceTextView.setText(mostPrice);
            mHandler.sendEmptyMessageDelayed(1, 5000);
        } else {
            if (bidUid.equals(AppConfig.getInstance().getUid())) {//拍到的人是自己
                View btnPay = mAuctionEndView.findViewById(R.id.btn_to_pay);
                btnPay.setVisibility(View.VISIBLE);
                btnPay.setOnClickListener(this);
            } else {
                mHandler.sendEmptyMessageDelayed(1, 5000);
            }
        }
    }

    public void auctionFailure() {
        setAuctionEndView(false);
        mHandler.sendEmptyMessageDelayed(1, 3000);
    }

    private void setAuctionEndView(boolean success) {
        mAuctionEndView = LayoutInflater.from(mContext).inflate(R.layout.view_auction_result, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(DpUtil.dp2px(120), ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mAuctionEndView.setLayoutParams(params);
        if (!success) {
            ImageView imageView = (ImageView) mAuctionEndView.findViewById(R.id.img);
            imageView.setImageResource(R.mipmap.icon_auction_failure);
        }
        mParent.addView(mAuctionEndView);
    }

    private void hideResultView() {
        if (mAuctionEndView != null) {
            mAuctionEndView.animate()
                    .y(mParent.getHeight())
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (mAuctionEndView != null && mAuctionEndView.getParent() != null) {
                                mParent.removeView(mAuctionEndView);
                            }
                        }
                    })
                    .start();
        }
    }

    private void addPayView() {
        hideResultView();
        mPayView = LayoutInflater.from(mContext).inflate(R.layout.view_auction_pay, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mPayView.setLayoutParams(params);
        mPayView.measure(0, 0);
        float h = mPayView.getMeasuredHeight();
        mParent.addView(mPayView);
        mPayLastTime = (TextView) mPayView.findViewById(R.id.last_time);
        mPayView.findViewById(R.id.btn_pay).setOnClickListener(this);
        mPayView.findViewById(R.id.btn_cancel).setOnClickListener(this);
        final TextView coin = (TextView) mPayView.findViewById(R.id.coin);
        coin.setOnClickListener(this);
        ImageView img = (ImageView) mPayView.findViewById(R.id.img);
        ImgLoader.display(mAuctionThumb, img);
        ((TextView) mPayView.findViewById(R.id.title)).setText(mAuctionTitle);
        ((TextView) mPayView.findViewById(R.id.most_price)).setText(mAuctionPrice);
        HttpUtil.getCoin(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    coin.setText(obj.getString("coin"));
                }
            }
        });
        ObjectAnimator animator = ObjectAnimator.ofFloat(mPayView, "translationY", h, 0);
        animator.setDuration(300);
        animator.start();
        setPayLastTime();
    }

    /**
     * 付款剩余时间
     */
    private void setPayLastTime() {
        mLastTime -= 1000;
        if (mLastTime > 1000) {
            mPayLastTime.setText(mContext.getString(R.string.last) + getDurationText(mLastTime) + mContext.getString(R.string.auto_close));
            mHandler.sendEmptyMessageDelayed(2, 1000);
        } else {
            ToastUtil.show(mContext.getString(R.string.pay_failure));
            hidePayView();
        }
    }

    //取消付款
    public void hidePayView() {
        mHandler.removeMessages(2);
        if (mPayView != null) {
            mPayView.animate()
                    .translationY(mPayView.getHeight())
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (mPayView != null && mPayView.getParent() != null) {
                                mParent.removeView(mPayView);
                            }
                        }
                    })
                    .start();
        }
    }

    //执行付款
    public void doPay() {
        HttpUtil.setBidPrice(mAuctionId, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    AppConfig.getInstance().getUserBean().setLevel(obj.getIntValue("level"));
                    ToastUtil.show(mContext.getString(R.string.pay_success));
                } else {
                    ToastUtil.show(msg);
                }
                hidePayView();
            }

            @Override
            public boolean showLoadingDialog() {
                return true;
            }

            @Override
            public Dialog createLoadingDialog() {
                return DialogUitl.loadingDialog(mContext, mContext.getString(R.string.waiting));
            }
        });
    }

}
