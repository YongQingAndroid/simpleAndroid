package com.zyq.simpleandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.QLinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jfz.wealth.R;
import com.zyq.ui.recyler.SuspensionManager;

public class Main3Activity extends AppCompatActivity {
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        init();
    }

    private void init() {
        recyclerView = findViewById(R.id.testList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(new Adapter());
        new SuspensionManager().setRecyclerView(recyclerView);
    }

    static class Adapter extends RecyclerView.Adapter<MyViewHolder> implements SuspensionManager.SuspensionAdapter {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            TextView textView = new Button(parent.getContext());
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_test, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

            holder.textView.setText("------" + position);
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
