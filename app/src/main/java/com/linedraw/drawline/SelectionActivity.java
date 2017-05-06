package com.linedraw.drawline;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class SelectionActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        Button button1 = (Button) findViewById(R.id.button_twopoints);
        Button button2 = (Button) findViewById(R.id.button_yintercept);
        Button button3 = (Button) findViewById(R.id.button_pointslope);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.button_twopoints:
                intent = new Intent(this, TwoPointActivity.class);
                break;
            case R.id.button_yintercept:
                intent = new Intent(this, YInterceptSlopeActivity.class);
                break;
            case R.id.button_pointslope:
                intent = new Intent(this, PointSlopeActivity.class);
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }
}
