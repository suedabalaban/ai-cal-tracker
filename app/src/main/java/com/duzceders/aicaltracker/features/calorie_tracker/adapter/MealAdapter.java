package com.duzceders.aicaltracker.features.calorie_tracker.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.duzceders.aicaltracker.R;
import com.duzceders.aicaltracker.features.food_detail.FoodDetailActivity;
import com.duzceders.aicaltracker.product.models.Meal;

import java.util.List;

import lombok.Getter;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.ViewHolder> {

    private List<Meal> mealList;
    private Context context;

    public MealAdapter(List<Meal> mealList, Context context) {
        this.mealList = mealList;
        this.context = context;
    }


    public void updateMeals(List<Meal> newMeals) {
        this.mealList = newMeals;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meal_card, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mealName.setText(mealList.get(position).getMeal_name());
        Glide.with(context).load(mealList.get(position).getImage_url()).into(holder.foodImage);

        holder.calorieInfo.setText(String.format(context.getString(R.string.total_kcal_number), (int) mealList.get(position).getCalorie_kcal()));
    }

    @Override
    public int getItemCount() {
        return mealList != null ? mealList.size() : 0;
    }

    @Getter
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mealName;
        private final ImageView foodImage;
        private final TextView calorieInfo;


        public ViewHolder(View view) {
            super(view);
            mealName = (TextView) view.findViewById(R.id.mealName);
            foodImage = (ImageView) view.findViewById(R.id.mealImage);
            calorieInfo = (TextView) view.findViewById(R.id.calorieInfo);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), FoodDetailActivity.class);
                    intent.putExtra("mealId", mealList.get(getAdapterPosition()).getId());
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
}
