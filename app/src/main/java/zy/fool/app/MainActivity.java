package zy.fool.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import zy.fool.view.LittleFoolAdapter;
import zy.fool.view.SubFoolView;

public class MainActivity extends Activity {

    LittleFoolAdapter mFoolAdapter ;
    SubFoolView mView ;
    String [] mStrings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        setContentView(R.layout.activity_main);

        View emptyView = findViewById(android.R.id.empty);
        mView = (SubFoolView)findViewById(android.R.id.foolview);
        mFoolAdapter = new LittleFoolAdapter(this);
        mView.setEmptyView(emptyView);
        //mView.setOnPullListener(this);
        //mFoolView.setAdapter(mFoolAdapter);

        mView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, GENRES));
        //mView.setPullation(2);
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



    @Override
    public boolean onPullOut() {
        // TODO Auto-generated method stub
        return true;
    }
}
