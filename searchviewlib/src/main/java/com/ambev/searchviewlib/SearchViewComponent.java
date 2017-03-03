package com.ambev.searchviewlib;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.security.InvalidParameterException;
import java.util.List;

import rx.Subscription;
import rx.functions.Action1;

public class SearchViewComponent<TItem, THolder extends RecyclerView.ViewHolder> extends LinearLayout {
    private static final long WAIT_TIME = 250;
    private RecyclerView mResultRecyclerView;
    private SearchViewAdapter mSearchViewAdapter;
    private TextView mSearchTextView;

    private ISearchViewDataSource<TItem> mSearchViewDataSource;
    private ISearchViewDataBinder<TItem, THolder> mSearchViewDataBinder;

    private Subscription mOnSearchObservable;

    private final LayoutInflater mLayoutInflater;

    private final Handler mHandler = new Handler();
    private Runnable mSearchRunnable;

    private final int mMinLength = 3;

    public SearchViewComponent(Context context) {
        super(context);

        mLayoutInflater = LayoutInflater.from(context);

        initializeChildViews();
    }

    public SearchViewComponent(Context context, AttributeSet attrs) {
        super(context, attrs);

        mLayoutInflater = LayoutInflater.from(context);

        initializeChildViews();
    }

    private void initializeChildViews() {
        mLayoutInflater.inflate(R.layout.search_view, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mSearchTextView = (TextView) findViewById(R.id.search_text_view);
        mResultRecyclerView = (RecyclerView) findViewById(R.id.search_result_recycler_view);

        mSearchTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence input, int start, int before, int count) {
                final String inputText = mSearchTextView.getText().toString();
                if (inputText.length() < mMinLength) {
                    return;
                }

                if (mOnSearchObservable != null) {
                    mOnSearchObservable.unsubscribe();
                }

                if (mSearchRunnable != null) {
                    mHandler.removeCallbacks(mSearchRunnable);
                }

                mSearchRunnable = new Runnable() {
                    @Override
                    public void run() {
                        mOnSearchObservable = mSearchViewDataSource.onSearch(inputText)
                                .subscribe(new Action1<List<TItem>>() {
                                    @Override
                                    public void call(List<TItem> resultList) {
                                        fillResultList(resultList);
                                    }
                                });
                    }
                };

                mHandler.postDelayed(mSearchRunnable, WAIT_TIME);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void fillResultList(List<TItem> resultList) {
        if (mSearchViewAdapter == null) {
            mSearchViewAdapter = configureRecyclerViewAdapter(resultList);
        } else {
            mSearchViewAdapter.clear();
            mSearchViewAdapter.addAll(resultList);
        }
    }

    private SearchViewAdapter configureRecyclerViewAdapter(List<TItem> resultList) {
        if (mSearchViewDataBinder == null) {
            throw new InvalidParameterException("Missing mSearchViewDataBinder");
        }

        mResultRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mResultRecyclerView.addItemDecoration(new DividerDecorationRecyclerView(getContext()));

        final SearchViewAdapter searchViewAdapter = new SearchViewAdapter(resultList, mSearchViewDataBinder);
        mResultRecyclerView.setAdapter(searchViewAdapter);
        return searchViewAdapter;
    }

    public void setDataSource(ISearchViewDataSource<TItem> searchViewDataSource) {
        this.mSearchViewDataSource = searchViewDataSource;
    }

    public void setViewHolder(ISearchViewDataBinder<TItem, THolder> searchViewDataBinder) {
        mSearchViewDataBinder = searchViewDataBinder;
    }

    private class SearchViewAdapter extends RecyclerView.Adapter<THolder> {

        private final ISearchViewDataBinder<TItem, THolder> mSearchViewDataBinder;
        private final List<TItem> mResultList;

        SearchViewAdapter(List<TItem> resultList, ISearchViewDataBinder<TItem, THolder> searchViewDataBinder) {
            mSearchViewDataBinder = searchViewDataBinder;

            mResultList = resultList;
        }


        @Override
        public THolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return mSearchViewDataBinder.getViewHolder(mLayoutInflater.inflate(mSearchViewDataBinder.getLayoutResourceId(), parent, false));
        }

        @Override
        public void onBindViewHolder(THolder holder, int position) {
            mSearchViewDataBinder.bindViewHolder(holder, mResultList.get(position));
        }

        @Override
        public int getItemCount() {
            return mResultList.size();
        }

        void clear() {
            notifyItemRangeRemoved(0, getItemCount());
            mResultList.clear();
        }

        void addAll(List<TItem> itemList) {
            mResultList.addAll(itemList);
            notifyItemRangeInserted(0, getItemCount());
        }
    }
}
