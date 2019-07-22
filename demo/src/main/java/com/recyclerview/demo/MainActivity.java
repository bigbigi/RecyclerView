package com.recyclerview.demo;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    SmoothRecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recycler);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 12);
        mRecyclerView.setLayoutManager(layoutManager);
        MyAdapter adapter = new MyAdapter();
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, int position, RecyclerView parent) {
                int left = 0;
                int right = 0;
                switch (position % 7) {
                   /* case 4:
                        left=500;
                        break;
                    case 5:
                        break;
                    case 6:
                        break;*/
                    default:
                        left = 10;
                        right = 10;
                        break;
                }
                outRect.set(left, 10, right, 10);
            }
        });
        GridLayoutManager.SpanSizeLookup lookup = new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int size;
                switch (position % 7) {
                  /*  case 4:
                    case 5:
                    case 6:
                        size = 4;
                        break;*/
                    default:
                        size = 3;
                }
                return size;
            }

        };
        layoutManager.setSpanSizeLookup(lookup);

        for (int i = 0; i < /*40*/120; i++) {
            mList.add("test_" + i);
        }
        adapter.notifyDataSetChanged();
        ViewTreeObserver vo=mRecyclerView.getViewTreeObserver();
        vo.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mRecyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                if(mRecyclerView.getChildCount()>0){
                    mRecyclerView.getChildAt(0).requestFocus();
                }
            }
        });
    }

    private List<String> mList = new ArrayList<>();

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {
        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View content = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
            return new MyHolder(content);
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {
            holder.textView.setText(mList.get(position));
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (/*position % 7 == 0*/false) {
                return R.layout.item_big;
            } else {
                return R.layout.item_txt;
            }
        }

        class MyHolder extends RecyclerView.ViewHolder {
            TextView textView;

            public MyHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.item_txt);
                textView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if(b){
//                            Log.d("big","bottom:"+view.getBottom());
                        }
                    }
                });
                itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if(b){
                            Log.d("big","onFocusChange");
                            mRecyclerView.scroll(view,50);
                        }
                    }
                });
            }
        }
    }
}
