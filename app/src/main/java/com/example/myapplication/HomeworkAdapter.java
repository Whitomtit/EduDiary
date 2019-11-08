package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

class HomeworkAdapter extends RecyclerView.Adapter<HomeworkAdapter.HomeworkHolder> {
    private List<Homework> homeworkList;

    public HomeworkAdapter(List<Homework> homeworkList) {
        this.homeworkList = homeworkList;
    }

    @Override
    public HomeworkHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.homework_card, parent, false);

        return new HomeworkHolder(v);
    }

    @Override
    public void onBindViewHolder(HomeworkHolder holder, int position) {
        Homework homework = homeworkList.get(position);
        holder.subjectName.setText(homework.getSubject());
        holder.deadlineDate.setText(homework.getDateAsString());
        if (holder.groupHolders.size() == 0) {
            for (ItemGroup itemGroup : homework.getGroupList()) {
                View groupView = LayoutInflater.from(holder.groupData.getContext()).inflate(R.layout.homework_group,
                        holder.groupData, true);

                GroupHolder group = new GroupHolder(groupView, position);
                group.groupName.setText(itemGroup.getName());

                for (Item item : itemGroup.getItemList()) {
                    Chip chip = LayoutInflater.from(groupView.getContext())
                            .inflate(R.layout.item_chip, group.chipGroup, true).findViewById(R.id.chip);
                    chip.setChecked(item.isDone());
                    chip.setText(item.getContent());
                    group.addChip(chip, position);
                }

                holder.addGroupHolder(group);
            }
        }
    }

    @Override
    public int getItemCount() {
        return homeworkList.size();
    }

    public static class HomeworkHolder extends RecyclerView.ViewHolder {
        public MaterialCardView card;
        public TextView subjectName;
        public TextView deadlineDate;
        public LinearLayout groupData;
        public List<GroupHolder> groupHolders;

        public HomeworkHolder(View v) {
            super(v);
            card = v.findViewById(R.id.card);
            subjectName = card.findViewById(R.id.subjectName);
            deadlineDate = card.findViewById(R.id.deadlineDate);
            groupData = card.findViewById(R.id.groupData);
            groupHolders = new ArrayList<>();
        }

        public void addGroupHolder(GroupHolder g) {
            groupHolders.add(g);
        }
    }

    public static class GroupHolder {
        public TextView groupName;
        public ChipGroup chipGroup;
        public List<Chip> chips;
        public GroupHolder(View v, int id) {
            groupName = v.findViewById(R.id.groupName);
            groupName.setId(id);
            chipGroup = v.findViewById(R.id.chipsGroup);
            chipGroup.setId(id);
            chips = new ArrayList<>();
        }

        public void addChip(Chip c, int id) {
            c.setId(id);
            chips.add(c);
        }
    }
}
