package cz.cvut.fel.memorice.view.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cz.cvut.fel.memorice.R;
import cz.cvut.fel.memorice.model.database.dataaccess.ASyncListReadDatabase;
import cz.cvut.fel.memorice.model.database.dataaccess.ASyncSimpleAccessDatabase;
import cz.cvut.fel.memorice.model.entities.Entity;
import cz.cvut.fel.memorice.view.activities.detail.DictionaryDetailActivity;
import cz.cvut.fel.memorice.view.activities.detail.SequenceDetailActivity;
import cz.cvut.fel.memorice.view.activities.detail.SetDetailActivity;

/**
 * Created by sheemon on 21.4.16.
 */
public class EntityListAdapter extends RecyclerView.Adapter<EntityListAdapter.ViewHolder> {

    private List<Entity> mDataset;
    private RecyclerView view;
    private String filter = "";

    public EntityListAdapter(RecyclerView view) {
        this.view = view;
        mDataset = new ArrayList<>();
    }

    public void showAll(Context context) {
        ASyncListReadDatabase access = new ASyncListReadDatabase(context);
        access.setAdapter(this);
        access.execute(ASyncListReadDatabase.ALL_ENTITIES);
        view.setAdapter(this);
    }

    public void setData(List<Entity> data) {
        mDataset = data;
        notifyDataSetChanged();
    }

    public void showFavorites(Context context) {
        ASyncListReadDatabase access = new ASyncListReadDatabase(context);
        access.setAdapter(this);
        if (filter.equals("")) {
            access.execute(ASyncListReadDatabase.FAVOURITE_ENTITIES);
        } else {
            access.setFilter(filter);
            access.execute(ASyncListReadDatabase.FAVOURITE_FILTER_ENTITIES);
        }
    }

    public void remove(Entity item, Context context) {
        int position = mDataset.indexOf(item);
        mDataset.remove(position);
        ASyncSimpleAccessDatabase access = new ASyncSimpleAccessDatabase(context);
        access.setEntity(item);
        access.execute(ASyncSimpleAccessDatabase.DELETE_ENTITY);
        notifyItemRemoved(position);
    }

    public void toggleFavorite(Entity item, Context context) {
        ASyncSimpleAccessDatabase access = new ASyncSimpleAccessDatabase(context);
        access.setEntity(item);
        access.execute(ASyncSimpleAccessDatabase.TOGGLE_FAVOURITE);
        notifyItemChanged(mDataset.indexOf(item));
    }

    public void filter(String filter, Context context) {
        this.filter = filter;
        ASyncListReadDatabase access = new ASyncListReadDatabase(context);
        access.setAdapter(this);
        access.setFilter(filter);
        access.execute(ASyncListReadDatabase.FILTERED_ENTITIES);
    }

    @Override
    public EntityListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.entity_line, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Entity e = mDataset.get(position);
        holder.txtHeader.setText(e.getLabel(), TextView.BufferType.SPANNABLE);
        holder.txtType.setText(e.getType().getName(), TextView.BufferType.SPANNABLE);

        if (filter != null) {
            int i = e.getLabel().toLowerCase().indexOf(filter.toLowerCase());
            if (i != -1) {
                Spannable str = (Spannable) holder.txtHeader.getText();
                int color;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    color = holder.txtHeader.getHighlightColor();
                } else {
                    color = Color.BLACK;
                }
                str.setSpan(new ForegroundColorSpan(color), i, i + filter.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        View.OnClickListener listener = null;
        switch (e.getType()) {
            case SET:
                holder.imageType.setImageResource(R.drawable.ic_set_inverted_24dp);
                listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent myIntent = new Intent(view.getContext(), SetDetailActivity.class);
                        myIntent.putExtra("entity_label", e.getLabel());
                        view.getContext().startActivity(myIntent);
                    }
                };
                break;
            case SEQUENCE:
                holder.imageType.setImageResource(R.drawable.ic_list_inverted_24dp);
                listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent myIntent = new Intent(view.getContext(), SequenceDetailActivity.class);
                        myIntent.putExtra("entity_label", e.getLabel());
                        view.getContext().startActivity(myIntent);
                    }
                };
                break;
            case DICTIONARY:
                holder.imageType.setImageResource(R.drawable.ic_dictionary_inverted_24dp);
                listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent myIntent = new Intent(view.getContext(), DictionaryDetailActivity.class);
                        myIntent.putExtra("entity_label", e.getLabel());
                        view.getContext().startActivity(myIntent);
                    }
                };
                break;
        }
        holder.txtHeader.setOnClickListener(listener);
        holder.txtType.setOnClickListener(listener);
        holder.imageType.setOnClickListener(listener);

        if (e.isFavourite()) {
            holder.imageFav.setImageResource(R.drawable.ic_favorite_true_24dp);
        } else {
            holder.imageFav.setImageResource(R.drawable.ic_favorite_false_24dp);
        }
        holder.imageFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavorite(e, view.getContext());
            }
        });

        holder.imageDel.setImageResource(R.drawable.ic_delete_inverted_24dp);
        holder.imageDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext(), R.style.AlertDialogTheme);
                alertDialogBuilder.setTitle("Delete " + e.getLabel());
                alertDialogBuilder.setMessage("Are you sure?");
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        remove(e, view.getContext());
                        dialog.cancel();
                    }
                });
                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = alertDialogBuilder.create();
                alert.show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtHeader;
        private TextView txtType;
        private ImageView imageType;
        private ImageView imageFav;
        private ImageView imageDel;

        public ViewHolder(View v) {
            super(v);
            txtHeader = (TextView) v.findViewById(R.id.label_line);
            txtType = (TextView) v.findViewById(R.id.typeLine);
            imageType = (ImageView) v.findViewById(R.id.icon_type);
            imageFav = (ImageView) v.findViewById(R.id.icon_favorite);
            imageDel = (ImageView) v.findViewById(R.id.icon_delete);
        }
    }
}
