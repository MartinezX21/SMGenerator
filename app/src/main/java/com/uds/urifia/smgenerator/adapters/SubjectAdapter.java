package com.uds.urifia.smgenerator.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.uds.urifia.smgenerator.R;
import com.uds.urifia.smgenerator.smanet.model.Subject;
import com.uds.urifia.smgenerator.utils.OnSubjectItemChanged;

import java.util.ArrayList;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {
    private ArrayList<Subject> dataset;
    private OnSubjectItemChanged subjectItemChangeHandler;
    private boolean isMainActivity;

    public void setDataset(ArrayList<Subject> dataset) {
        this.dataset = dataset;
    }

    public static class SubjectViewHolder extends RecyclerView.ViewHolder {
        public ImageView subjectImage;
        public CheckBox interestedIndicator;
        public TextView subjectIndicator, subjectName, subjectDescription, subjectHierarchy;
        public SubjectViewHolder(final View itemView) {
            super(itemView);

            subjectImage = itemView.findViewById(R.id.subject_view_image);
            subjectImage.setImageResource(selectImage());
            interestedIndicator = itemView.findViewById(R.id.subject_view_interested_indicator);
            subjectIndicator = itemView.findViewById(R.id.subject_view_indicator);
            subjectName = itemView.findViewById(R.id.subject_view_name);
            subjectDescription = itemView.findViewById(R.id.subject_view_description);
            subjectHierarchy = itemView.findViewById(R.id.subject_view_hierarchy);

        }

        private int selectImage() {
            double r = Math.random();
            if(r < 0.125) return R.color.colorAccent;
            else if(r < 0.25) return R.color.colorBlack;
            else if(r < 0.375) return R.color.colorBlue;
            else if(r < 0.5) return R.color.colorGray;
            else if(r < 0.625) return R.color.colorGreen;
            else if(r < 0.75) return R.color.colorPrimary;
            else if(r < 0.875) return R.color.colorPrimaryDark;
            else return R.color.colorPurple;
        }
    }

    // Provide a suitable constructor to initialize the set of subjects
    public SubjectAdapter(boolean isMainActivity, ArrayList<Subject> dataset, OnSubjectItemChanged handler) {
        this.dataset = dataset;
        this.subjectItemChangeHandler = handler;
        this.isMainActivity = isMainActivity;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SubjectAdapter.SubjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View subjectView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subject_view, parent, false);
        SubjectViewHolder vh = new SubjectViewHolder(subjectView);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final SubjectViewHolder holder, final int position) {
        // - get element from your dataset at this position
        final Subject subject = this.dataset.get(position);

        // - replace the contents of the view with that element
        holder.subjectIndicator.setText(subject.getName().substring(0, 1));
        holder.subjectName.setText(subject.getName() + "(" + subject.getCode() + ")");
        holder.subjectDescription.setText(subject.getDescription());
        holder.subjectHierarchy.setText(subject.getId().replaceAll("\\.", " > "));

        if (isMainActivity) {
            holder.subjectName.setTextColor(Color.BLACK);
            holder.interestedIndicator.setVisibility(View.INVISIBLE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    subjectItemChangeHandler.onSubjectPressed(subject);
                }
            });
        } else {
            if(subject.isInterested()){
                holder.subjectName.setTextColor(Color.BLACK);
                holder.interestedIndicator.setChecked(true);
            }
            holder.interestedIndicator.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        subjectItemChangeHandler.onSubscribe(subject);
                        holder.subjectName.setTextColor(Color.BLACK);
                    } else {
                        subjectItemChangeHandler.onUnsubscribe(subject);
                        holder.subjectName.setTextColor(Color.GRAY);
                    }
                }
            });
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.dataset.size();
    }
}
