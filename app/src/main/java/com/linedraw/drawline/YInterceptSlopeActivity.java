package com.linedraw.drawline;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.linedraw.drawline.utils.Fraction;

import butterknife.ButterKnife;

public class YInterceptSlopeActivity extends GameActivity {

    private static final String TAG = YInterceptSlopeActivity.class.getSimpleName();

    private AlertDialog yInterceptDialog;

    @Override
    protected GameManager.GameMode getGameMode() {
        return GameManager.GameMode.YSLOPE;
    }

    @Override
    protected void createMyOwn() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_create_yslope, null);
        final EditText edittextYIntercept = ButterKnife.findById(mView, R.id.edittext_y_intercept);
        edittextYIntercept.setText("1");
        final EditText edittextSlopeDen = ButterKnife.findById(mView, R.id.edittext_slope_den);
        edittextSlopeDen.setText("1");
        final EditText edittextSlopeNum = ButterKnife.findById(mView, R.id.edittext_slope_num);
        edittextSlopeNum.setText("1");
        Button buttonOk = ButterKnife.findById(mView, R.id.button_ok);
        Button buttonCancel = ButterKnife.findById(mView, R.id.button_cancel);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int yintercept = 1;
                int slopenum = 1;
                int slopeden = 1;
                String sYIntercept = edittextYIntercept.getText().toString();
                if (!TextUtils.isEmpty(sYIntercept)) {
                    yintercept = Integer.valueOf(sYIntercept);
                }

                String sSlopeNum = edittextSlopeNum.getText().toString();
                if (!TextUtils.isEmpty(sSlopeNum)) {
                    slopenum = Integer.valueOf(sSlopeNum);
                    slopenum = slopenum == 0 ? 1 : slopenum;
                }

                String sSlopeDen = edittextSlopeDen.getText().toString();
                if (!TextUtils.isEmpty(sSlopeDen)) {
                    slopeden = Integer.valueOf(sSlopeDen);
                    slopeden = slopeden == 0 ? 1 : slopeden;
                }

                Fraction slope = new Fraction(slopenum, slopeden);

                gameManager.setYInterceptAndSlope(yintercept, slope);
                populate();
                yInterceptDialog.dismiss();
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yInterceptDialog.cancel();
            }
        });
        mBuilder.setView(mView);
        mBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                finish();
            }
        });
        yInterceptDialog = mBuilder.create();
        yInterceptDialog.show();
    }

    @Override
    protected void populate() {
        Point point = gameManager.getPoints()[0];
        textviewPointA.setText("Point A: (" + point.x + ", " + point.y + ")");
        textviewSlope.setText(gameManager.getSlope().toString());
        textviewSlope.setBackgroundColor(Color.GREEN);
        gameManager.setSlopeInputCorrect(true);
        graphView.invalidate();
    }

    @Override
    protected void initialize() {
        textviewGametitle.setText(R.string.yintercept_slope);
    }
}
