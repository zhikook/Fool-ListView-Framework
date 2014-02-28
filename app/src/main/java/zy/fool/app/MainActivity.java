package zy.fool.app;

import android.os.Bundle;

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
        mView = (SubFoolView)findViewById(R.id.foolview);
        mFoolAdapter = new LittleFoolAdapter(this);
        mView.setEmptyView(emptyView);
        //mView.setOnPullListener(this);
        //mFoolView.setAdapter(mFoolAdapter);

        mView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, GENRES));
        //mView.setPullation(2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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



    @Override
    public boolean onPullOut() {
        // TODO Auto-generated method stub
        return true;
    }
}
