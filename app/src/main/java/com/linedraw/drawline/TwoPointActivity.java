package com.linedraw.drawline;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.ButterKnife;

public class TwoPointActivity extends GameActivity {

    private static final String TAG = TwoPointActivity.class.getSimpleName();

    private Dialog twoPointsDialog;

    @Override
    protected GameManager.GameMode getGameMode() {
        return GameManager.GameMode.TWOPOINTS;
    }

    @Override
    protected void createMyOwn() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_create_twopoints, null);
        final EditText edittextPointaX = ButterKnife.findById(mView, R.id.edittext_pointa_x);
        edittextPointaX.setText("1");
        final EditText edittextPointaY = ButterKnife.findById(mView, R.id.edittext_pointa_y);
        edittextPointaY.setText("1");
        final EditText edittextPointbX = ButterKnife.findById(mView, R.id.edittext_pointb_x);
        edittextPointbX.setText("2");
        final EditText edittextPointbY = ButterKnife.findById(mView, R.id.edittext_pointb_y);
        edittextPointbY.setText("2");
        Button buttonOk = ButterKnife.findById(mView, R.id.button_ok);
        Button buttonCancel = ButterKnife.findById(mView, R.id.button_cancel);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pointax = 1;
                int pointay = 1;
                int pointbx = -1;
                int pointby = -1;
                String sPointaX = edittextPointaX.getText().toString();
                if (!TextUtils.isEmpty(sPointaX)) {
                    pointax = Integer.valueOf(sPointaX);
                }
                if (pointax > 10 || pointax < -10) {
                    Toast.makeText(TwoPointActivity.this, "Coordinate must be -10 >= n <= 10", Toast.LENGTH_SHORT).show();
                    return;
                }

                String sPointaY = edittextPointaY.getText().toString();
                if (!TextUtils.isEmpty(sPointaY)) {
                    pointay = Integer.valueOf(sPointaY);
                }
                if (pointay > 10 || pointay < -10) {
                    Toast.makeText(TwoPointActivity.this, "Coordinate must be -10 >= n <= 10", Toast.LENGTH_SHORT).show();
                    return;
                }

                String sPointbX = edittextPointbX.getText().toString();
                if (!TextUtils.isEmpty(sPointbX)) {
                    pointbx = Integer.valueOf(sPointbX);
                }
                if (pointbx > 10 || pointbx < -10) {
                    Toast.makeText(TwoPointActivity.this, "Coordinate must be -10 >= n <= 10", Toast.LENGTH_SHORT).show();
                    return;
                }

                String sPointbY = edittextPointbY.getText().toString();
                if (!TextUtils.isEmpty(sPointbY)) {
                    pointby = Integer.valueOf(sPointbY);
                }
                if (pointby > 10 || pointby < -10) {
                    Toast.makeText(TwoPointActivity.this, "Coordinate must be -10 >= n <= 10", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (pointax == pointbx) {
                    Toast.makeText(TwoPointActivity.this,
                            "Points must not have equal x and y coordinates", Toast.LENGTH_SHORT).show();
                    return;
                }

                gameManager.setPoints(new Point(pointax, pointay), new Point(pointbx, pointby));
                populate();
                twoPointsDialog.dismiss();
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                twoPointsDialog.cancel();
            }
        });
        mBuilder.setView(mView);
        mBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                finish();
            }
        });
        twoPointsDialog = mBuilder.create();
        twoPointsDialog.show();
    }

    @Override
    protected void populate() {
        Point point = gameManager.getPoints()[0];
        textviewPointA.setText("Point A: (" + point.x + ", " + point.y + ")");
        point = gameManager.getPoints()[1];
        textviewPointB.setText("Point B: (" + point.x + ", " + point.y + ")");
        graphView.invalidate();
    }

    @Override
    protected void initialize() {
        textviewGametitle.setText(R.string.gametitle_twopoints);
    }
}
