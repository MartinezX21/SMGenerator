package com.uds.urifia.smgenerator.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uds.urifia.smgenerator.R;
import com.uds.urifia.smgenerator.smanet.model.Event;
import com.uds.urifia.smgenerator.utils.BitmapWorkerTask;
import com.uds.urifia.smgenerator.utils.OnEventSelectionHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private final Context context;
    private ArrayList<Event> dataset;
    private OnEventSelectionHandler eventSelectionHandler;

    public void setDataset(ArrayList<Event> dataset) {
        this.dataset = dataset;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        public ImageView eventImage;
        public TextView eventSubject, eventDescription, eventDate, eventValidity;
        public EventViewHolder(final View itemView) {
            super(itemView);

            eventImage = itemView.findViewById(R.id.event_view_image);
            eventSubject = itemView.findViewById(R.id.event_view_subject);
            eventDescription = itemView.findViewById(R.id.event_view_descr);
            eventDate = itemView.findViewById(R.id.event_view_pubdate);
            eventValidity = itemView.findViewById(R.id.event_view_validity);
        }

    }

    // Provide a suitable constructor to initialize the set of events
    public EventAdapter(ArrayList<Event> dataset,
                        OnEventSelectionHandler eventSelectionHandler,
                        Context context) {
        this.dataset = dataset;
        this.eventSelectionHandler = eventSelectionHandler;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public EventAdapter.EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View subjectView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_view, parent, false);
        EventViewHolder vh = new EventViewHolder(subjectView);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final EventViewHolder holder, final int position) {
        // - get element from your dataset at this positionSimpleDateFormat formatter1 = new SimpleDateFormat("EEE, d MMM yyyy", new Locale("fr_FR"));
        final Event event= this.dataset.get(position);

        SimpleDateFormat formatter1 = new SimpleDateFormat("EEE, d MMM yyyy", new Locale("fr_FR"));
        SimpleDateFormat formatter2 = new SimpleDateFormat("d MMM yyyy", new Locale("fr_FR"));
        // - replace the contents of the view with that element
        holder.eventSubject.setText(event.getSubject().getName());
        holder.eventDescription.setText(event.getDescription());
        holder.eventDate.setText(formatter1.format(event.getEventDate()));
        holder.eventValidity.setText(formatter2.format(new Date(event.getValidity())));
        //holder.eventImage.setImageResource();
        loadBitmap(selectImage(event.getPath()), holder.eventImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventSelectionHandler.onEventSelected(event);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.dataset.size();
    }

    private int selectImage(String fileName) {
        if(fileName.endsWith(".gif")) {
            return R.drawable.gif;
        }
        else if(fileName.endsWith(".html")) {
            return R.drawable.html;
        }
        else if (fileName.endsWith(".jpg")) {
            return R.drawable.jpg;
        }
        else if (fileName.endsWith(".pdf")) {
            return R.drawable.pdf;
        }
        else if (fileName.endsWith(".png")) {
            return R.drawable.png;
        }
        else if (fileName.endsWith(".svg")) {
            return R.drawable.svg;
        }
        else if (fileName.endsWith(".txt")) {
            return R.drawable.txt;
        }
        else {
            return R.drawable.unknown;
        }
    }

    public void loadBitmap(int resId, ImageView imageView) {
        BitmapWorkerTask task = new BitmapWorkerTask(imageView, context);
        task.execute(resId);
    }
}
