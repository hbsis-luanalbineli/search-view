package com.ambev.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ambev.searchviewlib.ISearchViewDataBinder;
import com.ambev.searchviewlib.ISearchViewDataSource;
import com.ambev.searchviewlib.SearchViewComponent;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SearchViewComponent<ProductModel, ProductViewHolder> searchViewComponent = (SearchViewComponent<ProductModel, ProductViewHolder>) findViewById(R.id.search_view_component);

        searchViewComponent.setDataSource(new ISearchViewDataSource<ProductModel>() {
            @Override
            public Observable<List<ProductModel>> onSearch(final String input) {
                return Observable.create(new Observable.OnSubscribe<List<ProductModel>>() {
                    @Override
                    public void call(Subscriber<? super List<ProductModel>> subscriber) {
                        List<ProductModel> result = new ArrayList<>();
                        for (int index = 0; index < 10; index++) {
                            result.add(new ProductModel(index, "Produto: " + index + " | Search: " + input));
                        }
                        subscriber.onNext(result);
                        subscriber.onCompleted();
                    }
                });
            }
        });

        searchViewComponent.setViewHolder(new ISearchViewDataBinder<ProductModel, ProductViewHolder>() {
            @Override
            public ProductViewHolder getViewHolder(View itemView) {
                return new ProductViewHolder(itemView);
            }

            @Override
            public int getLayoutResourceId() {
                return R.layout.search_item;
            }

            @Override
            public void bindViewHolder(ProductViewHolder holder, ProductModel productModel) {
                holder.mIdTextView.setText(String.valueOf(productModel.id));
                holder.mDescriptionTextView.setText(productModel.description);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static class ProductViewHolder extends RecyclerView.ViewHolder {
        private TextView mIdTextView;
        private TextView mDescriptionTextView;

        public ProductViewHolder(View itemView) {
            super(itemView);

            mIdTextView = (TextView) itemView.findViewById(R.id.search_item_id_text_view);
            mDescriptionTextView = (TextView) itemView.findViewById(R.id.search_item_description_text_view);
        }
    }

}
