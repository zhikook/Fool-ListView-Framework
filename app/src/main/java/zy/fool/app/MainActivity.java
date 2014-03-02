package zy.fool.app;

import zy.fool.view.FoolAdapter;
import zy.fool.view.MyFoolView;
import zy.fool.widget.FoolAbsView.OnPullOutListener;
import zy.fool.widget.FoolListView;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

public class MainActivity extends Activity implements OnPullOutListener{

    FoolAdapter mFoolAdapter ;
   FoolListView mView ;
    String [] mStrings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        setContentView(R.layout.activity_main);

        View emptyView = findViewById(android.R.id.empty);
        mView = (FoolListView)findViewById(R.id.foolview);
        mFoolAdapter = new FoolAdapter(this);
        mView.setEmptyView(emptyView);
        //mView.setOnPullListener(this);
        //mFoolView.setAdapter(mFoolAdapter);

        mView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, GENRES));
        //mView.setPullation(2);
    } 

    @Override
    public boolean onPullOut() {
        // TODO Auto-generated method stub
        return true;
    }
    
    private static final String[] GENRES = new String[] {
        "-----0-----",
        "-----1-----",
        "-----2-----",
        "-----3-----",
        "-----4-----",
        "-----5-----",
        "-----6-----",
        "-----7-----",
        "-----8-----",
        "-----9-----",
        "-----10-----",
        "-----11-----",
        "-----13-----",
        "-----14-----",
        "-----15-----",
        "-----16-----",


};
}
