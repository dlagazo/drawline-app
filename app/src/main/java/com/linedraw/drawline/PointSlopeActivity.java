package com.linedraw.drawline;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.linedraw.drawline.utils.Fraction;

import butterknife.ButterKnife;

public class PointSlopeActivity extends GameActivity {

    private static final String TAG = PointSlopeActivity.class.getSimpleName();
    private AlertDialog pointSlopeDialog;

    @Override
    protected GameManager.GameMode getGameMode() {
        return GameManager.GameMode.POINTSLOPE;
    }

    @Override
    protected void createMyOwn() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_create_pointslope, null);
        final EditText edittextPointaX = ButterKnife.findById(mView, R.id.edittext_pointa_x);
        edittextPointaX.setText("1");
        final EditText edittextPointaY = ButterKnife.findById(mView, R.id.edittext_pointa_y);
        edittextPointaY.setText("1");
        final EditText edittextSlopeNum = ButterKnife.findById(mView, R.id.edittext_slope_num);
        edittextSlopeNum.setText("1");
        final EditText edittextSlopeDen = ButterKnife.findById(mView, R.id.edittext_slope_den);
        edittextSlopeDen.setText("1");
        Button buttonOk = ButterKnife.findById(mView, R.id.button_ok);
        Button buttonCancel = ButterKnife.findById(mView, R.id.button_cancel);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pointax = 1;
                int pointay = 1;
                int slopenum = 1;
                int slopeden = 1;
                String sPointaX = edittextPointaX.getText().toString();
                if (!TextUtils.isEmpty(sPointaX)) {
                    pointax = Integer.valueOf(sPointaX);
                }
                if (pointax > 10 || pointax < -10) {
                    Toast.makeText(PointSlopeActivity.this, "Coordinate must be -10 >= n <= 10", Toast.LENGTH_SHORT).show();
                    return;
                }

                String sPointaY = edittextPointaY.getText().toString();
                if (!TextUtils.isEmpty(sPointaY)) {
                    pointay = Integer.valueOf(sPointaY);
                }
                if (pointay > 10 || pointay < -10) {
                    Toast.makeText(PointSlopeActivity.this, "Coordinate must be -10 >= n <= 10", Toast.LENGTH_SHORT).show();
                    return;
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

                gameManager.setPointAndSlope(new Point(pointax, pointay), slope);
                populate();
                pointSlopeDialog.dismiss();
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pointSlopeDialog.cancel();
            }
        });
        mBuilder.setView(mView);
        mBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                finish();
            }
        });
        pointSlopeDialog = mBuilder.create();
        pointSlopeDialog.show();
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
        textviewGametitle.setText(R.string.point_slope);
    }
}
