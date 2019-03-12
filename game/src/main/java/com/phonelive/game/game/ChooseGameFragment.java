package com.phonelive.game.game;

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
import android.widget.AdapterView;
import android.widget.GridView;

import com.alibaba.fastjson.JSON;
import com.yunbao.phonelive.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by cxf on 2017/10/6.
 */

public class ChooseGameFragment extends DialogFragment implements AdapterView.OnItemClickListener {

    private Context mContext;
    private View mRootView;
    private GridView mGridView;
    private ChooseGameAdapter mAdapter;
    private GameManager mGameManager;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.game_fragment_choose, null);
        Dialog dialog = new Dialog(mContext, R.style.dialog2);
        dialog.setContentView(mRootView);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        window.setWindowAnimations(R.style.bottomToTopAnim);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int[] gameSwitchs = JSON.parseObject(getArguments().getString("game_switch"), int[].class);
        List<ChooseGameBean> gameList = new ArrayList<>();
        for (int gameSwtich : gameSwitchs) {
            gameList.add(ChooseGameBean.createCameBean(gameSwtich));
        }
        //对list进行排序
        Collections.sort(gameList, mComparator);
        mGridView = (GridView) mRootView.findViewById(R.id.gridView);
        mAdapter = new ChooseGameAdapter(gameList);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        dismiss();
        mGameManager.openGame(mAdapter.getItem(position).getMethod());
    }

    public void setGameManager(GameManager gameManager) {
        mGameManager = gameManager;
    }

    private Comparator<ChooseGameBean> mComparator = new Comparator<ChooseGameBean>() {
        @Override
        public int compare(ChooseGameBean bean1, ChooseGameBean bean2) {
            return bean1.getOrder() - bean2.getOrder();
        }
    };
}
