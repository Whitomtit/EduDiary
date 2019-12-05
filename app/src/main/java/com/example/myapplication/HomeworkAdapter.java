package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.object.Category;
import com.example.myapplication.object.Homework;
import com.example.myapplication.object.Item;
import com.example.myapplication.utils.HomeworkDbManager;
import com.example.myapplication.utils.Utils;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

class HomeworkAdapter extends RecyclerView.Adapter<HomeworkAdapter.HomeworkHolder> {
    private HomeworkDbManager dbManager;
    private List<Homework> homeworkList;
    private Context context;

    HomeworkAdapter(Context context, List<Homework> homeworkList, HomeworkDbManager dbManager) {
        this.context = context;
        this.homeworkList = homeworkList;
        this.dbManager = dbManager;
    }

    @Override
    public HomeworkHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.homework_card, parent, false);

        return new HomeworkHolder(v);
    }

    @Override
    public void onBindViewHolder(final HomeworkHolder holder, final int position) {
        final Homework homework = homeworkList.get(position);
        holder.subjectName.setText(homework.getSubject());
        holder.date.setText(homework.getDateAsString());
        if (holder.categoryHolders.size() == 0) {
            for (Category itemGroup : homework.getCategoryList()) {
                View groupView = LayoutInflater.from(holder.groupData.getContext()).inflate(R.layout.homework_group,
                        holder.groupData, false);

                CategoryHolder group = new CategoryHolder(groupView);
                group.groupName.setText(itemGroup.getName());

                for (final Item item : itemGroup.getItemList()) {
                    Chip chip = (Chip) LayoutInflater.from(groupView.getContext())
                            .inflate(R.layout.item_chip, group.chipGroup, false);
                    chip.setChecked(item.isDone());
                    chip.setText(item.getContent());
                    chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                            item.setDone(checked);
                            dbManager.updateItem(item);
                        }
                    });
                    group.chipGroup.addView(chip);
                }
                holder.groupData.addView(groupView);
                holder.addGroupHolder(group);
            }
        }
        holder.card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PopupMenu menu = Utils.showMenu(holder.date);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_delete:
                                dbManager.deleteHomework(homework);
                                int position = homeworkList.indexOf(homework);
                                homeworkList.remove(position);
                                notifyItemRemoved(position);
                                return true;
                            case R.id.menu_edit:
                                Intent intent = new Intent(context, EditActivity.class);
                                intent.putExtra("homeworkRecycler", homework);
                                ((Activity)context).startActivityForResult(intent, 2);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return homeworkList.size();
    }

    static class HomeworkHolder extends RecyclerView.ViewHolder {
        private MaterialCardView card;
        private TextView subjectName;
        private TextView date;
        private LinearLayout groupData;
        private List<CategoryHolder> categoryHolders;

        private HomeworkHolder(View v) {
            super(v);
            card = v.findViewById(R.id.card);
            subjectName = card.findViewById(R.id.subjectName);
            date = card.findViewById(R.id.deadlineDate);
            groupData = card.findViewById(R.id.groupData);
            categoryHolders = new ArrayList<>();
        }

        private void addGroupHolder(CategoryHolder g) {
            categoryHolders.add(g);
        }
    }

    private static class CategoryHolder {
        private TextView groupName;
        private ChipGroup chipGroup;
        private CategoryHolder(View v) {
            groupName = v.findViewById(R.id.groupName);
            chipGroup = v.findViewById(R.id.chipsGroup);
        }
    }
}
