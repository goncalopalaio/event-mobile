package com.gplio.event_mobile.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gplio.event_mobile.R;
import com.gplio.event_mobile.ReportingApi;
import com.gplio.event_mobile.models.Category;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet with categories</p>
 * <p>Loads categories in the background</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     CategoryListDialogFragment.newInstance().show(getSupportFragmentManager(), "dialog");
 * </pre>
 * <p>You activity (or fragment) needs to implement {@link CategoryListDialogFragment.Listener}.</p>
 */
public class CategoryListDialogFragment extends BottomSheetDialogFragment {
    private static String TAG = "CategoryListDialogFragment";

    private Listener mListener;
    private CategoryAdapter categoryAdapter;

    public static CategoryListDialogFragment newInstance() {
        return new CategoryListDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category_list_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        categoryAdapter = new CategoryAdapter(new ArrayList<Category>());
        recyclerView.setAdapter(categoryAdapter);

        final Handler mainHandler = new Handler();

        ReportingApi.getEventInstance(getContext()).listAllCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(@NonNull Call<List<Category>> call, @NonNull Response<List<Category>> response) {
                final List<Category> categoryList = response.body();
                if (categoryList == null) {
                    Log.e(TAG, "onResponse: Empty body");
                    return;
                }
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        categoryAdapter.data = categoryList;
                        categoryAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<List<Category>> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t);
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        categoryAdapter.data.clear();
                        categoryAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final Fragment parent = getParentFragment();
        if (parent != null) {
            mListener = (Listener) parent;
        } else {
            mListener = (Listener) context;
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    public interface Listener {
        void onCategoryClicked(int position);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView description;
        final View wrapper;

        ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.fragment_category_list_dialog_item, parent, false));
            wrapper = itemView.findViewById(R.id.wrapper);
            wrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onCategoryClicked(getAdapterPosition());
                        dismiss();
                    }
                }
            });
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
        }

    }

    private class CategoryAdapter extends RecyclerView.Adapter<ViewHolder> {
        private List<Category> data;

        CategoryAdapter(ArrayList<Category> data) {
            this.data = data;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Category category = data.get(position);
            holder.title.setText(category.title);
            holder.description.setText(category.description);
        }

        @Override
        public int getItemCount() {
            if (data == null ) {
                return 0;
            }
            return data.size();
        }

    }

}
