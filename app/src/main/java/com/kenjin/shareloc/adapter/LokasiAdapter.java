package com.kenjin.shareloc.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;


import com.kenjin.shareloc.R;
import com.kenjin.shareloc.model.mLokasi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
/**
 * Created by Kenjin on 07/23/2017.
 */
public class LokasiAdapter extends RecyclerView.Adapter<LokasiAdapter.ItemViewHolder> {

    private List<mLokasi> lokasi;

    private final List<mLokasi> filteredUserList;
    private UserFilter userFilter;

    public LokasiAdapter(List<mLokasi> loc) {
        lokasi = loc;
        filteredUserList = new ArrayList<>();
            filteredUserList.addAll(loc);
        userFilter = new UserFilter(this, loc);
    }


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_lokasi, parent, false);
        ItemViewHolder viewHolder = new ItemViewHolder(rowView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {

        final mLokasi loc = filteredUserList.get(position);
        holder.lokasname.setText(loc.getLocationName());
        holder.koordinat.setText(String.valueOf(loc.getLatitude()).concat("-").concat(String.valueOf(loc.getLongitude())));
        holder.tgl.setText(loc.getDateTaken());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {}
        });


    }

    @Override
    public int getItemCount() {
        return filteredUserList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {
        public final TextView lokasname, koordinat, tgl;

        public ItemViewHolder(View itemView) {
            super(itemView);
            lokasname = (TextView) itemView.findViewById(R.id.lokasiName);
            koordinat = (TextView) itemView.findViewById(R.id.koordinat);
            tgl = (TextView) itemView.findViewById(R.id.tanggalinput);



        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }


    }


    public Filter setFilter() {
        return userFilter;
    }

    public static class UserFilter extends Filter {

        private final LokasiAdapter adapter;

        private final List<mLokasi> originalList;

        private final List<mLokasi> filteredList;

        private UserFilter(LokasiAdapter adapter, List<mLokasi> originalList) {
            super();
            this.adapter = adapter;
            this.originalList = new LinkedList<>(originalList);
            this.filteredList = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            this.filteredList.clear();
            final FilterResults results = new FilterResults();

            if (constraint.length() == 0) {
                this.filteredList.addAll(originalList);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();

                for (final mLokasi user : originalList) {
                    if (user.getLocationName() != null) {
                        if (user.getLocationName().toLowerCase().contains(filterPattern) ) {
                            this.filteredList.add(user);
                        }
                    }
                }
            }
            results.values = this.filteredList;
            results.count = this.filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            adapter.filteredUserList.clear();
            adapter.filteredUserList.addAll((ArrayList<mLokasi>) results.values);
            adapter.notifyDataSetChanged();
        }
    }


}
