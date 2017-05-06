package com.linedraw.drawline;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.linedraw.drawline.utils.Fraction;
import com.linedraw.drawline.utils.YIntercept;
import com.linedraw.drawline.view.GraphView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;

public abstract class GameActivity extends AppCompatActivity
        implements GraphView.Listener, GameManager.Listener {

    private static final String TAG = GameActivity.class.getSimpleName();

    @BindView(R.id.textview_gametitle)
    TextView textviewGametitle;
    @BindView(R.id.textview_point1)
    TextView textviewPointA;
    @BindView(R.id.textview_point2)
    TextView textviewPointB;
    @BindView(R.id.textview_linedraw)
    TextView textviewLinedraw;
    @BindView(R.id.textview_slope)
    TextView textviewSlope;
    @BindView(R.id.textview_yintercept)
    TextView textviewYintercept;
    @BindView(R.id.graphview)
    GraphView graphView;

    protected GameManager gameManager;

    private AlertDialog alertDialog;
    private AlertDialog congratsDialog;
    private AlertDialog startDialog;

    private SoundPool mSoundPool;
    private int correctSound, wrongSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);

        mSoundPool =
                new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        correctSound = mSoundPool.load(this, R.raw.correct, 1);
        wrongSound = mSoundPool.load(this, R.raw.wrong, 1);

        gameManager = new GameManager(getGameMode(), this);
        graphView.setListener(this);
        graphView.setGameManager(gameManager);
        initialize();
        showStartDialog();
    }

    protected abstract void initialize();

    protected abstract GameManager.GameMode getGameMode();

    @Override
    public void onPointClicked(int index) {
        mSoundPool.play(correctSound, 1, 1, 0, 0, 1);
        Point point;
        if (index == 0) {
            point = gameManager.getPoints()[0];
            textviewPointA.setText("Point A: (" + point.x + ", " + point.y + ")");
        } else if (index == 1) {
            point = gameManager.getPoints()[1];
            textviewPointB.setText("Point B: (" + point.x + ", " + point.y + ")");
        }
    }

    @Override
    public void onLineDrawn() {
        mSoundPool.play(correctSound, 1, 1, 0, 0, 1);
        gameManager.setLineDrawn(true);
        textviewLinedraw.setBackgroundColor(Color.GREEN);
    }

    @OnTouch(R.id.textview_slope)
    public boolean slope(TextView view) {
        showSlopeDialog(view);
        return false;
    }

    @OnTouch(R.id.textview_yintercept)
    public boolean yintercept(TextView view) {
        showYInterceptDialog(view);
        return false;
    }

    private void showSlopeDialog(final TextView textView) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_fraction_input, null);
        final EditText mWhole = (EditText) mView.findViewById(R.id.whole);
        final EditText mNum = (EditText) mView.findViewById(R.id.numerator);
        final EditText mDen = (EditText) mView.findViewById(R.id.denominator);
        mWhole.setVisibility(View.GONE);
        Button buttonOk = (Button) mView.findViewById(R.id.buttonOK);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fraction fraction = new Fraction();
                int num = 1, den = 1;
                String sNum = mNum.getText().toString();
                if (!TextUtils.isEmpty(sNum)) {
                    num = Integer.valueOf(sNum);
                }
                String sDen = mDen.getText().toString();
                if (!TextUtils.isEmpty(sDen)) {
                    den = Integer.valueOf(sDen);
                }

                fraction.setValue(num, den);

                textView.setText(fraction.toString());
                if (gameManager.getSlope().equals(fraction)) {
                    mSoundPool.play(correctSound, 1, 1, 0, 0, 1);
                    gameManager.setSlopeInputCorrect(true);
                    textView.setBackgroundColor(Color.GREEN);
                } else {
                    mSoundPool.play(wrongSound, 1, 1, 0, 0, 1);
                }

                alertDialog.dismiss();
            }
        });
        mBuilder.setView(mView);
        alertDialog = mBuilder.create();
        alertDialog.show();
    }

    private void showYInterceptDialog(final TextView textView) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_fraction_input, null);
        final EditText mNumerator = ButterKnife.findById(mView, R.id.numerator);
        final EditText mDenominator = ButterKnife.findById(mView, R.id.denominator);
        Button buttonOk = (Button) mView.findViewById(R.id.buttonOK);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int numerator = 0;
                String sNumerator = mNumerator.getText().toString();
                if (!TextUtils.isEmpty(sNumerator)) {
                    numerator = Integer.valueOf(sNumerator);
                }

                int denominator = 0;
                String sDenominator = mDenominator.getText().toString();
                if (!TextUtils.isEmpty(sDenominator)) {
                    denominator = Integer.valueOf(sDenominator);
                }

                YIntercept yIntercept = new YIntercept(numerator, denominator);

                textView.setText(yIntercept.toString());
                if (yIntercept.equals(gameManager.getyIntercept())) {
                    mSoundPool.play(correctSound, 1, 1, 0, 0, 1);
                    gameManager.setyInterceptInputCorrect(true);
                    textView.setBackgroundColor(Color.GREEN);
                } else {
                    mSoundPool.play(wrongSound, 1, 1, 0, 0, 1);
                }

                alertDialog.dismiss();
            }
        });
        mBuilder.setView(mView);
        alertDialog = mBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onGameCompleted() {
        showCongratulations();
    }

    private void showCongratulations() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_congratulations, null);
        Button buttonOk = (Button) mView.findViewById(R.id.button_ok);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                congratsDialog.dismiss();
            }
        });
        mBuilder.setView(mView);
        congratsDialog = mBuilder.create();
        congratsDialog.show();
    }

    private void showStartDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_game_start, null);
        Button buttonGenerate = (Button) mView.findViewById(R.id.button_generate);
        buttonGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameManager.generateGame();
                populate();
                startDialog.dismiss();
            }
        });
        Button buttonCreateMyOwn = (Button) mView.findViewById(R.id.button_createmyown);
        buttonCreateMyOwn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createMyOwn();
                startDialog.dismiss();
            }
        });
        mBuilder.setView(mView);
        mBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                finish();
            }
        });
        startDialog = mBuilder.create();
        startDialog.show();
    }

    protected abstract void createMyOwn();

    protected abstract void populate();
}
