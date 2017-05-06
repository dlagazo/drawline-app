package com.linedraw.drawline;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import com.linedraw.drawline.view.GraphView;

public class SplashActivity extends AppCompatActivity {

    private boolean touched;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        GraphView graphView = (GraphView) findViewById(R.id.graph);
        graphView.setCanTouchThis(false);
    }

//    @OnTouch(R.id.splash_parent)
//    public void onSplashClick(View view) {
//        Log.d("Splash", "onTouch");
//        Intent intent = new Intent(this, SelectionActivity.class);
//        startActivity(intent);
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!touched) {
            touched = true;
            Intent intent = new Intent(this, SelectionActivity.class);
            startActivity(intent);
            finish();
        }
        return false;
    }
}
