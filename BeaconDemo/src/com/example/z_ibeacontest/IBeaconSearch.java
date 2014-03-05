package com.example.z_ibeacontest;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;

import com.radiusnetworks.ibeacon.IBeacon;
import com.radiusnetworks.ibeacon.IBeaconConsumer;
import com.radiusnetworks.ibeacon.IBeaconManager;
import com.radiusnetworks.ibeacon.RangeNotifier;
import com.radiusnetworks.ibeacon.Region;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class IBeaconSearch extends Activity implements IBeaconConsumer{

	private ListView lv;
	private ArrayList<IBeacon> arrayL = new ArrayList<IBeacon>();
	private IBeaconManager iBeaconManager = IBeaconManager.getInstanceForApplication(this);
	private BeaconAdapter adapter;
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			adapter.notifyDataSetChanged();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		iBeaconManager.bind(this);
		init();
	}

	private void init() {
		lv = (ListView) findViewById(R.id.lv);
		adapter = new BeaconAdapter();
		lv.setAdapter(adapter);
	}

	@Override
	public void onIBeaconServiceConnect() {
		 iBeaconManager.setRangeNotifier(new RangeNotifier() {
	            @Override
	            public void didRangeBeaconsInRegion(Collection<IBeacon> iBeacons, Region region) {
	                if (iBeacons.size() > 0) {
	                	arrayL.clear();
	    				arrayL.addAll((ArrayList<IBeacon>) iBeacons);
	    				handler.sendEmptyMessage(0);
	                }
	            }
	        });
	        
	        try {
	            iBeaconManager.startRangingBeaconsInRegion(new Region("com.aprilbrother.myRegion", null,
	                    null, null));
	        } catch (RemoteException e) {

	        }
	}
	
	private class BeaconAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (arrayL != null && arrayL.size() > 0)
				return arrayL.size();
			else
				return 0;
		}

		@Override
		public IBeacon getItem(int arg0) {
			return arrayL.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			view = View.inflate(getApplicationContext(), R.layout.tupple_monitoring, null);
			TextView tv_uuid = (TextView) view.findViewById(R.id.BEACON_uuid);
			TextView tv_major = (TextView) view.findViewById(R.id.BEACON_major);
			TextView tv_minor = (TextView) view.findViewById(R.id.BEACON_minor);
			TextView tv_proximity = (TextView) view.findViewById(R.id.BEACON_proximity);
			TextView tv_rssi = (TextView) view.findViewById(R.id.BEACON_rssi);
			TextView tv_txpower = (TextView) view.findViewById(R.id.BEACON_txpower);
			TextView tv_range = (TextView) view.findViewById(R.id.BEACON_range);
			
			double distance = arrayL.get(position).getAccuracy();
			DecimalFormat decimalFormat = new DecimalFormat("##.##");
            double distanceFormatted = Double.valueOf(decimalFormat.format(distance));
            
			tv_uuid.setText(arrayL.get(position).getProximityUuid());
			tv_major.setText("major:"+arrayL.get(position).getMajor());
			tv_minor.setText("minor:"+arrayL.get(position).getMinor());
			tv_proximity.setText("proximity:"+arrayL.get(position).getProximity());
			tv_rssi.setText("rssi"+arrayL.get(position).getRssi());
			tv_txpower.setText("txpower:"+arrayL.get(position).getTxPower());
			tv_range.setText("distance:"+distanceFormatted+"m");
			return view;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		iBeaconManager.unBind(this);
	}
}
