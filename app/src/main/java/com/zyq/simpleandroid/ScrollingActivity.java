package com.zyq.simpleandroid;

import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.jfz.wealth.R;
import com.zyq.ui.recyler.SuspensionManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ScrollingActivity extends AppCompatActivity {
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        init();
    }

    private void init() {
//        NestedScrollView mNestedScrollView = findViewById(R.id.nestedscrollview);
//        mNestedScrollView.setNestedScrollingEnabled(false);
        recyclerView = findViewById(R.id.testList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(new Adapter());
        new SuspensionManager().setRecyclerView(recyclerView);
    }

    static class Adapter extends RecyclerView.Adapter<Main3Activity.MyViewHolder> implements SuspensionManager.SuspensionAdapter {

        @NonNull
        @Override
        public Main3Activity.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            TextView textView = new Button(parent.getContext());
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_test, parent, false);
            return new Main3Activity.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Main3Activity.MyViewHolder holder, int position) {

            holder.textView.setText("------" + position);
            Log.i("qing========", "position=" + position);
            holder.itemView.setBackgroundColor(isSuspension(position) ? Color.RED : Color.GRAY);
        }

        @Override
        public int getItemCount() {
            return 100;
        }

        @Override
        public boolean isSuspension(int index) {
            return (index % 8 == 0 && index != 0) || index == 11;
        }
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.item_text);
            this.itemView.setOnClickListener(view -> {
                Toast.makeText(this.textView.getContext(), "item" + SuspensionManager.getAdapterPosition(this), Toast.LENGTH_SHORT).show();
            });
        }

    }
}
