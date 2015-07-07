/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package retrofacebook.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;

import rx.Observable;
import rx.functions.*;

import rx.android.app.*;

import retrofacebook.*;

public class ListFragment extends Fragment {
    RecyclerView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        listView = (RecyclerView) inflater.inflate(R.layout.fragment_list, container, false);

        listAdapter = ListRecyclerAdapter.create();
        listAdapter.createViewHolder(new Func2<ViewGroup, Integer, ItemViewHolder>() {
            @Override
            public ItemViewHolder call(ViewGroup parent, Integer position) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

                /*
                TypedValue typedValue = new TypedValue();
                parent.getContext().getTheme().resolveAttribute(R.attr.selectableItemBackground, typedValue, true);
                view.setBackgroundResource(typedValue.resourceId);
                */

                return new ItemViewHolder(view);
            }
        });
        listView.setLayoutManager(new LinearLayoutManager(listView.getContext()));
        listView.setAdapter(listAdapter);

        return listView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            items.toList().subscribe(list -> {
                listAdapter.getList().clear();
                listAdapter.getList().addAll(list);
                listAdapter.notifyDataSetChanged();
            });
        }
    }

    private ListRecyclerAdapter<Item, ItemViewHolder> listAdapter;
    Observable<Item> items;

    public ListFragment items(Observable<Item> items) {
        this.items = items;
        return this;
    }

    public static ListFragment create() {
        return new ListFragment();
    }

    public static class ItemViewHolder extends BindViewHolder<Item> {
        @InjectView(R.id.icon)
        ImageView icon;
        @InjectView(R.id.text1)
        TextView text1;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        };

        @Override
        public void onBind(int position, Item item) {
            if (!android.text.TextUtils.isEmpty(item.text1())) text1.setText(item.text1());

            if (!android.text.TextUtils.isEmpty(item.icon())) {
                Glide.with(itemView.getContext())
                    .load(item.icon())
                    .fitCenter()
                    .into(icon);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, CheeseDetailActivity.class);
                    if (!android.text.TextUtils.isEmpty(item.text1())) intent.putExtra(CheeseDetailActivity.EXTRA_NAME, item.text1());

                    context.startActivity(intent);
                }
            });
        }
    }
}
