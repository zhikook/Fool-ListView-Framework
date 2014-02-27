package zy.fool.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import zy.fool.app.R;

public class LittleFoolAdapter extends BaseAdapter{

	private LayoutInflater mInflater;
	
	public LittleFoolAdapter(Context context){
		mInflater =LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 40;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		
		
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LinearLayout mView;
        
		if (convertView == null) {
            mView = (LinearLayout)mInflater.inflate(R.layout.m, parent, false);
        } else {
        	mView = (LinearLayout)convertView;
        }

        return mView;
	}

}
