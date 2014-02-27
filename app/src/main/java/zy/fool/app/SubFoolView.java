package zy.fool.app;

import android.content.Context;
import android.util.AttributeSet;

import zy.fool.widget.FoolView;

public class SubFoolView extends FoolView{
	
	public SubFoolView(Context context) {
		super(context);
		initView();
		// TODO Auto-generated constructor stub
	}
	
	public SubFoolView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initView();
		
	}

	public SubFoolView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initView();
		
	}

	private void initView(){
		//setSpotLayout(true);
	}
}
