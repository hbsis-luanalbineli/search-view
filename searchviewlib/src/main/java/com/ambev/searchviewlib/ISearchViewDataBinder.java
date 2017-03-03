package com.ambev.searchviewlib;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public interface ISearchViewDataBinder<TItem, THolder extends RecyclerView.ViewHolder> {
    THolder getViewHolder(View itemView);

    @LayoutRes int getLayoutResourceId();

    void bindViewHolder(THolder holder, TItem tItem);
}
