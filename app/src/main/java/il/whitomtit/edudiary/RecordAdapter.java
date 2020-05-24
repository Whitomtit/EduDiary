package il.whitomtit.edudiary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import il.whitomtit.edudiary.model.Category;
import il.whitomtit.edudiary.model.Record;
import il.whitomtit.edudiary.model.Item;
import il.whitomtit.edudiary.model.Subject;
import il.whitomtit.edudiary.util.RecordDbManager;
import il.whitomtit.edudiary.util.Subjects;
import il.whitomtit.edudiary.util.Utils;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordHolder> {
    private RecordDbManager dbManager;
    private List<Record> recordList;
    private Context context;
    private ImageView noRecordsImage;

    RecordAdapter(Context context, List<Record> recordList, RecordDbManager dbManager, ImageView noRecordsImage) {
        this.context = context;
        this.recordList = recordList;
        this.dbManager = dbManager;
        this.noRecordsImage = noRecordsImage;
        updateImage();
    }

    private void updateImage() {
        if (getItemCount() > 0)
            noRecordsImage.setVisibility(View.INVISIBLE);
        else
            noRecordsImage.setVisibility(View.VISIBLE);
    }

    @Override
    @NonNull
    public RecordHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Creates new and empty record card
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_record_card, parent, false);

        return new RecordHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecordHolder holder, final int position) {
        final Record record = recordList.get(position);

        //Set base record data in the view
        Subject subject = Subjects.getInstance().getSubjectByName(record.getSubject());
        holder.subjectName.setText(record.getSubject());
        holder.image.setImageResource(subject.getImage());
        holder.imageLayout.setBackgroundColor(subject.getColor());
        holder.date.setText(record.getDateAsString());

        if (holder.categoryHolders.size() == 0) {
            //Add all categories into the record view
            for (Category itemGroup : record.getCategoryList()) {
                View groupView = LayoutInflater.from(holder.groupData.getContext()).inflate(R.layout.main_record_category,
                        holder.groupData, false);

                CategoryHolder group = new CategoryHolder(groupView);
                group.groupName.setText(itemGroup.getName());

                //Add all items into categories
                for (final Item item : itemGroup.getItemList()) {
                    Chip chip = (Chip) LayoutInflater.from(groupView.getContext())
                            .inflate(R.layout.main_category_item, group.chipGroup, false);
                    chip.setChecked(item.isDone());
                    chip.setText(item.getContent());
                    //If item's view status changed, change it also in the database
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
        //On long click show a card menu
        holder.card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PopupMenu menu = Utils.showMenu(holder.date);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_delete:
                                dbManager.deleteRecord(record);
                                int position = recordList.indexOf(record);
                                recordList.remove(position);
                                notifyItemRemoved(position);
                                updateImage();
                                return true;
                            case R.id.menu_edit:
                                Intent intent = new Intent(context, EditActivity.class);
                                intent.putExtra("record", record);
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
        return recordList.size();
    }

    static class RecordHolder extends RecyclerView.ViewHolder {
        private MaterialCardView card;
        private TextView subjectName;
        private TextView date;
        private ImageView image;
        private LinearLayout imageLayout;
        private LinearLayout groupData;
        private List<CategoryHolder> categoryHolders;

        private RecordHolder(View v) {
            super(v);
            card = v.findViewById(R.id.card);
            subjectName = card.findViewById(R.id.subjectName);
            date = card.findViewById(R.id.deadlineDate);
            image = card.findViewById(R.id.cardImage);
            imageLayout = (LinearLayout) image.getParent();
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
