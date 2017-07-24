package com.hfad.accel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hfad.accel.budget.BudgetActivity;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView lvModules = (ListView) findViewById(R.id.listView_modules_options);
        lvModules.setOnItemClickListener(new AdapterView.OnItemClickListener(
        ) {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent intent = new Intent(MainActivity.this, BudgetActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }
}
