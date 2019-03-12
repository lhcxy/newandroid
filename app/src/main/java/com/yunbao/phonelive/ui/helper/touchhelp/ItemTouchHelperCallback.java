package com.yunbao.phonelive.ui.helper.touchhelp;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_IDLE;

public class ItemTouchHelperCallback<TA extends RVTouchSwapInterface> extends ItemTouchHelper.Callback{
    public ItemTouchHelperCallback(TA adapter) {
        this.adapter = adapter;
    }
    private TA adapter;
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        //首先回调的方法 返回int表示是否监听该方向
        int dragFlags = ItemTouchHelper.UP|ItemTouchHelper.DOWN| ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT;//拖拽
        return makeMovementFlags(dragFlags,ACTION_STATE_IDLE);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//        Collections.swap(adapter.getDatas(),viewHolder.getAdapterPosition(),target.getAdapterPosition());
//        adapter.notifyItemMoved(viewHolder.getAdapterPosition(),target.getAdapterPosition());
//
        adapter.onDataSwap(viewHolder.getAdapterPosition(),target.getAdapterPosition());

        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }
}