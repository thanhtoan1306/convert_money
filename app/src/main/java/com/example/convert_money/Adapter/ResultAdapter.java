package com.example.convert_money.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.convert_money.Model.Result;
import com.example.convert_money.R;

import java.util.ArrayList;
import java.util.List;
public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder> {
        Context context;
        List<Result> results;

        public ResultAdapter(Context context,  List<Result> results) {
            this.context = context;
            this.results=new ArrayList<>();
            for (int i=results.size()-1;i>=0;i--)
            {
                this.results.add(results.get(i));
            }
        }

        public static final class ResultViewHolder extends RecyclerView.ViewHolder{
            TextView valueView,resultView;
            TextView currencyFrom, currencyTo;

            public ResultViewHolder(@NonNull View itemView) {
                super(itemView);
                valueView=itemView.findViewById(R.id.valueView);
                resultView=itemView.findViewById(R.id.resultView);
                currencyFrom=itemView.findViewById(R.id.CurrencySource);
                currencyTo=itemView.findViewById(R.id.CurrencyTarget);

            }
        }

        @NonNull
        @Override
        public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_result,parent,false);
            return new ResultViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
            holder.valueView.setText(results.get(position).getValueView());
            holder.resultView.setText(results.get(position).getResultView());
            holder.currencyFrom.setText(results.get(position).getCurrencyFrom());
            holder.currencyTo.setText(results.get(position).getCurrencyTo());
        }

        @Override
        public int getItemCount() {
            return results.size();
        }
}


