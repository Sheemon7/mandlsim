package cz.cvut.fel.memorice.view.fragments.detail;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cz.cvut.fel.memorice.R;
import cz.cvut.fel.memorice.model.entities.entries.Entry;

/**
 * Created by sheemon on 30.4.16.
 */
public class SequenceDetailListAdapter extends EntityDetailListAdapter<SequenceDetailListAdapter.ViewHolder, Entry> {

    public SequenceDetailListAdapter(RecyclerView view) {
        super(view);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_sequence_line, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtValue.setText(items.get(position).getValue());
        holder.txtNumber.setText(String.valueOf(position + 1) + ".");
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtValue;
        private TextView txtNumber;

        public ViewHolder(View v) {
            super(v);
            txtValue = (TextView) v.findViewById(R.id.entry_value);
            txtNumber = (TextView) v.findViewById(R.id.entry_num);
        }
    }
}