package com.ambev.searchviewlib;

import java.util.List;

import rx.Observable;

public interface ISearchViewDataSource<TItem> {
    Observable<List<TItem>> onSearch(String input);
}
