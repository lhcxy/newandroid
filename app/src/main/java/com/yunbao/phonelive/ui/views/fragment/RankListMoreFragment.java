package com.yunbao.phonelive.ui.views.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxAdapter;
import com.jakewharton.rxbinding2.widget.RxAdapterView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.LiveAudienceActivity;
import com.yunbao.phonelive.bean.LiveBean;
import com.yunbao.phonelive.bean.RankItemBean;
import com.yunbao.phonelive.custom.UserLevelView;
import com.yunbao.phonelive.event.AttentionEvent;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.ui.base.BaseLazyFragment;
import com.yunbao.phonelive.ui.tools.StringUtil;
import com.yunbao.phonelive.ui.views.LiveWatcherActivity;
import com.yunbao.phonelive.ui.views.adapter.RankHomeListAdapter;
import com.yunbao.phonelive.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Timed;

public class RankListMoreFragment extends BaseLazyFragment implements OnItemClickListener<String> {
    private int ranklistType = 0; //榜单类型  0= 收益 1=消费
    private int dataType = 0; //数据类型 0 = 周榜 1 = 月榜
    private RankHomeListAdapter rankHomeListAdapter;

    /**
     * 更多榜单列表界面
     *
     * @param dataType 0 = 明星主播  1 = 人气主播  2= 富豪实力  3=游戏大人 4=牛牛排行榜
     * @return
     */
    public static RankListMoreFragment newInstance(int dataType) {
        Bundle args = new Bundle();
        args.putInt("ranklistType", dataType);
        RankListMoreFragment fragment = new RankListMoreFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private RecyclerView ranklistMoreRv;
    private TextView ranklistMoreHintTv;
    private NestedScrollView ranklistMoreNsv;
    private TextView nameOne, nameTwo, nameThree;
    private RoundedImageView avatarOne, avatarTwo, avatarThree;
    private ImageView livingOne, livingTwo, livingThree;
    private LinearLayout oneLl, twoLl, threeLl, rankTitleLl;
    private UserLevelView oneLevel, twoLevel, threeLevel;
    private TextView attentionOne, attentionTwo, attentionThree;

    private TextView contributionOne, contributionTwo, contributionThree;
    private TextView awardOne, awardTwo, awardThree;
    private RankItemBean oneBean, twoBean, threeBean;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ui_ranklist_more;
    }

    @Override
    protected void initView() {

        EventBus.getDefault().register(this);

        if (getArguments() != null) {
            ranklistType = getArguments().getInt("ranklistType");
        }
        initRankTitleView();
        ranklistMoreRv = findView(R.id.ranklist_more_rv);
        ranklistMoreHintTv = findView(R.id.ranklist_more_hint_tv);
        ranklistMoreNsv = findView(R.id.ranklist_more_nsv);
        ranklistMoreRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rankHomeListAdapter = new RankHomeListAdapter(ranklistType);
        rankHomeListAdapter.setListener(this);
        ranklistMoreRv.setAdapter(rankHomeListAdapter);

        setHintText();
    }

    private void initRankTitleView() {
        rankTitleLl = findView(R.id.rank_ll);
        oneLl = findView(R.id.rank_one_ll);
        twoLl = findView(R.id.rank_two_ll);
        threeLl = findView(R.id.rank_three_ll);
        nameOne = findView(R.id.rank_name_tv_one);
        nameTwo = findView(R.id.rank_name_tv_two);
        nameThree = findView(R.id.rank_name_tv_three);
        avatarOne = findView(R.id.rank_avatar_one);
        avatarTwo = findView(R.id.rank_avatar_two);
        avatarThree = findView(R.id.rank_avatar_three);
        livingOne = findView(R.id.rank_living_one);
        livingTwo = findView(R.id.rank_living_two);
        livingThree = findView(R.id.rank_living_three);
        oneLevel = findView(R.id.rank_level_ulv_one);
        twoLevel = findView(R.id.rank_level_ulv_two);
        threeLevel = findView(R.id.rank_level_ulv_three);
        attentionOne = findView(R.id.rank_attention_tv_one);
        attentionTwo = findView(R.id.rank_attention_tv_two);
        attentionThree = findView(R.id.rank_attention_tv_three);
        contributionOne = findView(R.id.rank_contribution_one);
        contributionTwo = findView(R.id.rank_contribution_two);
        contributionThree = findView(R.id.rank_contribution_three);
        awardOne = findView(R.id.rank_award_tv_one);
        awardTwo = findView(R.id.rank_award_tv_two);
        awardThree = findView(R.id.rank_award_tv_three);

        disposable.add(RxView.clicks(attentionOne).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            if (oneBean != null) {
                onItemClick(oneBean.getId(), -1);
            }
        }));
        disposable.add(RxView.clicks(attentionTwo).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            if (twoBean != null) {
                onItemClick(twoBean.getId(), -1);
            }
        }));
        disposable.add(RxView.clicks(attentionThree).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            if (threeBean != null) {
                onItemClick(threeBean.getId(), -1);
            }
        }));
        disposable.add(RxView.clicks(avatarOne).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            if (ranklistType==0 && oneBean!=null) {
                toLive(oneBean.getLiveInfo());
            }
        }));
        disposable.add(RxView.clicks(avatarTwo).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            if (ranklistType==0 && twoBean!=null) {
                toLive(twoBean.getLiveInfo());
            }
        }));
        disposable.add(RxView.clicks(avatarThree).throttleFirst(AppConfig.CLICK_DURATION, TimeUnit.MILLISECONDS).subscribe(o -> {
            if (ranklistType==0 && threeBean!=null) {
                toLive(threeBean.getLiveInfo());
            }
        }));
    }

    private void toLive(LiveBean liveBean) {
        if (liveBean == null || liveBean.getIslive() == 0) {
            ToastUtil.show("啊哦，主播当前不在家呢！");
        } else {
            Intent intent;
            if ("0".equals(liveBean.getAnyway())) {
                intent = new Intent(getContext(), LiveAudienceActivity.class);
            } else {
                intent = new Intent(getContext(), LiveWatcherActivity.class);
            }
            intent.putExtra("liveBean", liveBean);
            getContext().startActivity(intent);
        }
    }

    public void setWeekType(int weekType) {
        if (this.dataType != weekType) {
            this.dataType = weekType;
            setHintText();
            initData();
            ranklistMoreNsv.scrollTo(0, 0);
        }
    }

    private void selectMonthData() {
        dataType = 1;
        setHintText();
        initData();
    }

    private void setHintText() {
        switch (ranklistType) {
            case 0:
                if (dataType == 0) {
                    ranklistMoreHintTv.setText(getResources().getString(R.string.ranklist_more_remind_star_week));
                } else {
                    ranklistMoreHintTv.setText(getResources().getString(R.string.ranklist_more_remind_star_month));
                }
                break;
//            case 1:
//                if (dataType == 0) {
//                    ranklistMoreHintTv.setText(getResources().getString(R.string.ranklist_more_remind_popularity_week));
//                } else {
//                    ranklistMoreHintTv.setText(getResources().getString(R.string.ranklist_more_remind_popularity_month));
//                }
//                break;
            case 1:
                if (dataType == 0) {
                    ranklistMoreHintTv.setText(getResources().getString(R.string.ranklist_more_remind_rich_week));
                } else {
                    ranklistMoreHintTv.setText(getResources().getString(R.string.ranklist_more_remind_rich_month));
                }
                break;
            case 2:
                if (dataType == 0) {
                    ranklistMoreHintTv.setText(getResources().getString(R.string.ranklist_more_remind_game_week));
                } else {
                    ranklistMoreHintTv.setText(getResources().getString(R.string.ranklist_more_remind_game_month));
                }
                break;
            case 4:
                if (dataType == 0) {
                    ranklistMoreHintTv.setText(getResources().getString(R.string.ranklist_more_remind_niuniu_week));
                } else {
                    ranklistMoreHintTv.setText(getResources().getString(R.string.ranklist_more_remind_niuniu_month));
                }
                break;
        }
    }


    private void selectWeekData() {
        dataType = 0;
        setHintText();
        initData();
    }

    private List<RankItemBean> weekDatas, monthDatas;

    @Override
    protected void initData() {
        if (dataType == 0) {
            if (weekDatas == null || weekDatas.size() == 0) {
                HttpUtil.getMoreRankList(dataType, ranklistType, networkCallback);
            } else {
                rankHomeListAdapter.setDatas(weekDatas);
                if (weekDatas == null || weekDatas.size() == 0) {
                    rankTitleLl.setVisibility(View.GONE);
                } else {
                    rankTitleLl.setVisibility(View.VISIBLE);
                    bindRank();
                }
            }
        } else {
            if (monthDatas == null || monthDatas.size() == 0) {
                HttpUtil.getMoreRankList(dataType, ranklistType, networkCallback);
            } else {
                rankHomeListAdapter.setDatas(monthDatas);
                if (monthDatas == null || monthDatas.size() == 0) {
                    rankTitleLl.setVisibility(View.GONE);
                } else {
                    rankTitleLl.setVisibility(View.VISIBLE);
                    bindRank();
                }
            }
        }
    }

    /**
     * 收益榜 接口回调
     */
    private HttpCallback networkCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                List<RankItemBean> list = JSON.parseArray(Arrays.toString(info), RankItemBean.class);

                if (dataType == 0) {
                    weekDatas = list;
                    rankHomeListAdapter.setDatas(weekDatas);
                } else {
                    monthDatas = list;
                    rankHomeListAdapter.setDatas(monthDatas);
                }
                if (list == null || list.size() == 0) {
                    rankTitleLl.setVisibility(View.GONE);
                } else {
                    rankTitleLl.setVisibility(View.VISIBLE);
                    bindRank();
                }
            } else {
                rankHomeListAdapter.cleanDatas();
                ToastUtil.show(msg);
            }
        }

        @Override
        public void onFinish() {
        }
    };

    private void bindRank() {
        if (dataType == 0) {
            if (weekDatas == null || weekDatas.size() <= 0) {
                rankTitleLl.setVisibility(View.GONE);
            } else {
                rankTitleLl.setVisibility(View.VISIBLE);
            }
        } else {
            if (monthDatas == null || monthDatas.size() <= 0) {
                rankTitleLl.setVisibility(View.GONE);
            } else {
                rankTitleLl.setVisibility(View.VISIBLE);
            }
        }
        bindOne();
        bindTwo();
        bindThree();
    }

    private void bindOne() {
        if (dataType == 0) {
            if (weekDatas.size() > 0) {
                oneBean = weekDatas.get(0);
            } else {
                rankTitleLl.setVisibility(View.INVISIBLE);
                return;
            }
        } else {
            if (monthDatas.size() > 0) {
                oneBean = monthDatas.get(0);
            } else {
                rankTitleLl.setVisibility(View.INVISIBLE);
                return;
            }
        }
        if (oneBean != null) {
            rankTitleLl.setVisibility(View.VISIBLE);
            ImgLoader.display(oneBean.getAvatar_thumb(), avatarOne);
            nameOne.setText(oneBean.getUser_nicename());
            if (ranklistType == 0) {
                if (oneBean.getLiveInfo() != null) {
                    oneLevel.setLevel(oneBean.getLiveInfo().getLevel(), 1);
                    if (oneBean.getLiveInfo().getIslive() == 0) {
                        livingOne.setVisibility(View.VISIBLE);
                    } else {
                        livingOne.setVisibility(View.INVISIBLE);
                    }
                }
                contributionOne.setVisibility(View.GONE);
                awardOne.setText(oneBean.getJiangli());
                initOneAttention();
            } else {
                livingOne.setVisibility(View.INVISIBLE);
                attentionOne.setVisibility(View.GONE);
                contributionOne.setVisibility(View.VISIBLE);
                if (ranklistType == 1) {
                    contributionOne.setText(StringUtil.getContributionStr(oneBean.getTotal()) + " 贡献");
                } else {
                    contributionOne.setText(StringUtil.getContributionStr(oneBean.getTotal()) + " 爆米花");
                }
                oneLevel.setLevel(oneBean.getLevel());
                awardOne.setText(oneBean.getJiangli());
            }
        } else rankTitleLl.setVisibility(View.INVISIBLE);
    }

    private void bindTwo() {
        if (dataType == 0) {
            if (weekDatas.size() > 1) {
                twoBean = weekDatas.get(1);
            } else {
                twoLl.setVisibility(View.INVISIBLE);
                return;
            }
        } else {
            if (monthDatas.size() > 1) {
                twoBean = monthDatas.get(1);
            } else {
                twoLl.setVisibility(View.INVISIBLE);
                return;
            }
        }
        if (twoBean != null) {
            twoLl.setVisibility(View.VISIBLE);
            ImgLoader.display(twoBean.getAvatar_thumb(), avatarTwo);
            nameTwo.setText(twoBean.getUser_nicename());
            if (ranklistType == 0) {
                if (twoBean.getLiveInfo() != null) {
                    twoLevel.setLevel(twoBean.getLiveInfo().getLevel(), 1);
                    if (twoBean.getLiveInfo().getIslive() == 0) {
                        livingTwo.setVisibility(View.VISIBLE);
                    } else {
                        livingTwo.setVisibility(View.INVISIBLE);
                    }
                }
                awardTwo.setText(twoBean.getJiangli());
                contributionTwo.setVisibility(View.GONE);
                initTwoAttention();
            } else {
                livingTwo.setVisibility(View.INVISIBLE);
                attentionTwo.setVisibility(View.GONE);
                contributionTwo.setVisibility(View.VISIBLE);
                if (ranklistType == 1) {
                    contributionTwo.setText(StringUtil.getContributionStr(twoBean.getTotal()) + " 贡献");
                } else {
                    contributionTwo.setText(StringUtil.getContributionStr(twoBean.getTotal()) + " 爆米花");
                }
                twoLevel.setLevel(twoBean.getLevel());
                awardTwo.setText(twoBean.getJiangli());
            }
        } else twoLl.setVisibility(View.INVISIBLE);
    }

    private void bindThree() {
        if (dataType == 0) {
            if (weekDatas.size() > 2) {
                threeBean = weekDatas.get(2);
            } else {
                threeLl.setVisibility(View.INVISIBLE);
                return;
            }
        } else {
            if (monthDatas.size() > 2) {
                threeBean = monthDatas.get(2);
            } else {
                threeLl.setVisibility(View.INVISIBLE);
                return;
            }
        }
        if (threeBean != null) {
            threeLl.setVisibility(View.VISIBLE);
            ImgLoader.display(threeBean.getAvatar_thumb(), avatarThree);
            nameThree.setText(threeBean.getUser_nicename());
            if (ranklistType == 0) {
                if (threeBean.getLiveInfo() != null) {
                    threeLevel.setLevel(threeBean.getLiveInfo().getLevel(), 1);
                    if (threeBean.getLiveInfo().getIslive() == 0) {
                        livingThree.setVisibility(View.VISIBLE);
                    } else {
                        livingThree.setVisibility(View.INVISIBLE);
                    }
                }
                contributionThree.setVisibility(View.GONE);
                awardThree.setText(threeBean.getJiangli());
                initThreeAttention();
            } else {
                livingThree.setVisibility(View.INVISIBLE);
                attentionThree.setVisibility(View.GONE);
                contributionThree.setVisibility(View.VISIBLE);
                if (ranklistType == 1) {
                    contributionThree.setText(StringUtil.getContributionStr(threeBean.getTotal()) + " 贡献");
                } else {
                    contributionThree.setText(StringUtil.getContributionStr(threeBean.getTotal()) + " 爆米花");
                }
                threeLevel.setLevel(threeBean.getLevel());
                awardThree.setText(threeBean.getJiangli());
            }
        } else threeLl.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onItemClick(String uId, int position) {
        HttpUtil.setAttention(uId, null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessage(AttentionEvent message) {
        if (oneBean != null && oneBean.getId().equals(message.getTouid())) {
            if (oneBean.getLiveInfo() != null) {
                oneBean.getLiveInfo().setIsAttention(message.getIsAttention());
            }
            initOneAttention();
            return;
        }
        if (twoBean != null && twoBean.getId().equals(message.getTouid())) {
            if (twoBean.getLiveInfo() != null) {
                twoBean.getLiveInfo().setIsAttention(message.getIsAttention());
            }
            initTwoAttention();
            return;
        }
        if (threeBean != null && threeBean.getId().equals(message.getTouid())) {
            if (threeBean.getLiveInfo() != null) {
                threeBean.getLiveInfo().setIsAttention(message.getIsAttention());
            }
            initThreeAttention();
            return;
        }
        if (rankHomeListAdapter != null) {
            rankHomeListAdapter.setAttention(message.getTouid(), message.getIsAttention());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    private void initOneAttention() {
        if (oneBean.getLiveInfo() == null || oneBean.getLiveInfo().getIsAttention() == 0) {
            attentionOne.setText(getResources().getString(R.string.attention3));
            attentionOne.setBackground(getResources().getDrawable(R.drawable.bg_ui_item_unattention));
            attentionOne.setTextColor(getResources().getColor(R.color.app_selected_color));
        } else {
            attentionOne.setText(getResources().getString(R.string.attention));
            attentionOne.setBackground(getResources().getDrawable(R.drawable.bg_circle_line_cc));
            attentionOne.setTextColor(getResources().getColor(R.color.text_color_cc));
        }
    }

    private void initTwoAttention() {
        if (twoBean.getLiveInfo() == null || twoBean.getLiveInfo().getIsAttention() == 0) {
            attentionTwo.setText(getResources().getString(R.string.attention3));
            attentionTwo.setBackground(getResources().getDrawable(R.drawable.bg_ui_item_unattention));
            attentionTwo.setTextColor(getResources().getColor(R.color.app_selected_color));
        } else {
            attentionTwo.setText(getResources().getString(R.string.attention));
            attentionTwo.setBackground(getResources().getDrawable(R.drawable.bg_circle_line_cc));
            attentionTwo.setTextColor(getResources().getColor(R.color.text_color_cc));
        }
    }

    private void initThreeAttention() {
        if (threeBean.getLiveInfo() == null || threeBean.getLiveInfo().getIsAttention() == 0) {
            attentionThree.setText(getResources().getString(R.string.attention3));
            attentionThree.setBackground(getResources().getDrawable(R.drawable.bg_ui_item_unattention));
            attentionThree.setTextColor(getResources().getColor(R.color.app_selected_color));
        } else {
            attentionThree.setText(getResources().getString(R.string.attention));
            attentionThree.setBackground(getResources().getDrawable(R.drawable.bg_circle_line_cc));
            attentionThree.setTextColor(getResources().getColor(R.color.text_color_cc));
        }
    }
}
