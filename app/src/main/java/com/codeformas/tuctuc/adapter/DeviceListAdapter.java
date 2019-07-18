package com.codeformas.tuctuc.adapter;

import java.util.List;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.codeformas.tuctuc.R;

public class DeviceListAdapter extends BaseAdapter{
	private LayoutInflater mInflater;	
	private List<BluetoothDevice> mData;

	private OnPairButtonClickListener mListener;
	private OnPlayButtonClickListener pListener;

	public DeviceListAdapter(Context context) { 
        mInflater = LayoutInflater.from(context);        
    }
	
	public void setData(List<BluetoothDevice> data) {
		mData = data;
	}
	
	public void setListener(OnPairButtonClickListener listener) {
		mListener = listener;
	}
	
	public int getCount() {
		return (mData == null) ? 0 : mData.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return position;
	}

	public OnPlayButtonClickListener getpListener() {
		return pListener;
	}

	public void setpListener(OnPlayButtonClickListener pListener) {
		this.pListener = pListener;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null) {			
			convertView	= mInflater.inflate(R.layout.list_item_device, null);
			
			holder = new ViewHolder();
			
			holder.nameTv = convertView.findViewById(R.id.textName);
			holder.addressTv = convertView.findViewById(R.id.textMac);
			holder.pairBtn = (Button) convertView.findViewById(R.id.btnPair);
			holder.playBtn = (Button) convertView.findViewById(R.id.btnPlay);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		BluetoothDevice device	= mData.get(position);
		
		holder.nameTv.setText(device.getName());
		holder.addressTv.setText(device.getAddress());
		holder.pairBtn.setText((device.getBondState() == BluetoothDevice.BOND_BONDED) ? "Unpair" : "Pair");
		holder.pairBtn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.onPairButtonClick(position);
				}
			}
		});
		holder.playBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (pListener != null) {
					pListener.onPlayButtonClick(position);
				}
			}
		});
		
        return convertView;
	}

	static class ViewHolder {
		TextView nameTv;
		TextView addressTv;
		Button pairBtn;
		Button playBtn;
	}
	
	public interface OnPairButtonClickListener {
		public abstract void onPairButtonClick(int position);
	}

	public interface OnPlayButtonClickListener {
		public abstract void onPlayButtonClick(int position);
	}
}