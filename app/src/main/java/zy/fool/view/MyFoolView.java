package zy.fool.view;

import android.content.Context;
import android.util.AttributeSet;

import zy.fool.widget.FoolListView;

public class MyFoolView extends FoolListView{
	
	public MyFoolView(Context context) {
		super(context);
		initView();
		// TODO Auto-generated constructor stub
	}
	
	public MyFoolView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initView();
		
	}

	private void initView(){
		//setSpotLayout(true);
	}
}
