package com.codeformas.tuctuc.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codeformas.tuctuc.R;
import com.codeformas.tuctuc.model.Device;

import java.util.List;

import butterknife.BindView;

public class BluetoothDevicesAdapter extends RecyclerView.Adapter<BluetoothDevicesAdapter.CardViewHolder> {
    private Context context = null;
    private List<BluetoothDevice> items = null;

    public BluetoothDevicesAdapter(Context context, List<BluetoothDevice>items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_row_device, parent, false);
        return new CardViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        BluetoothDevice item = items.get(position); //get item from my List<Place>

        holder.itemView.setTag(item); //save items in Tag

        //set text to circle
        //String auxText = item.getNombreAlarma().substring(0, 1);
        holder.textView.setText("DATA DATA");
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    //class static
    class CardViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textView)
        TextView textView;

        public CardViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
        }
    }
}
