package com.mhmc.mentalhealthmonitor.thirdPage.Question;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import com.mhmc.mentalhealthmonitor.R;

public class question extends AppCompatActivity {
    private ImageButton imageWriteButton, emotionbutton;
    private Button writebutton, writeCancel, writeSubmit, emotionCancel, emotionSubmit;
    private TextView writetextview, emotionedittext, tv;
    private AlertDialog.Builder writebuilder, emotionbuilder;
    private LayoutInflater writelayoutinflater, emotionlayoutinflater;
    private EditText writeedittext;
    private AlertDialog writealertdialog, emotionalertdialog;
    private SeekBar AngrySeekbar, BoredomSeekbar, DisgustSeekbar, AnxietySeekbar, HappinessSeekbar, SadnessSeekbar, SurprisedSeekbar;
    private String tvContent, word;
    private String[] mood = new String[7];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //設定隱藏標題
        getSupportActionBar().hide();
        setContentView(R.layout.dialog_write);
        setDialogInitial();
    }
    /********************************************Dialog**********************************************/
    //設置Dialog 文字
    private void setDialogInitial() {

        //initial writeTextview
        writetextview = (TextView) findViewById(R.id.textView1);
        writebutton = (Button) findViewById(R.id.buttonMain);
        writelayoutinflater = getLayoutInflater();
        View Dview = writelayoutinflater.inflate(R.layout.dialog_write, null);
        writebuilder = new AlertDialog.Builder(question.this);
        writebuilder.setCancelable(false);
        writebuilder.setView(Dview);
        writeedittext = (EditText) Dview.findViewById(R.id.writeeditText);
        writeSubmit = (Button) Dview.findViewById(R.id.writebutton);
        writeCancel = (Button) Dview.findViewById(R.id.writeCancel);
        writealertdialog = writebuilder.create();
        writebutton.setOnClickListener(writelis);

        //initial writeIcon
        imageWriteButton = (ImageButton) findViewById(R.id.imageWrite);
        imageWriteButton.setOnClickListener(writelis);

        //initial Icon
        emotionbutton = (ImageButton) findViewById(R.id.imageButton2);
        emotionbutton.setOnClickListener(emotionlis);
        emotionbuilder = new AlertDialog.Builder(question.this);
        emotionlayoutinflater = getLayoutInflater();
        Dview = emotionlayoutinflater.inflate(R.layout.dialog_emotion, null);
        emotionbuilder.setCancelable(false);
        emotionbuilder.setView(Dview);
        emotionedittext = (EditText) Dview.findViewById(R.id.emtioneditText);
        emotionSubmit = (Button) Dview.findViewById(R.id.emtionbutton);
        emotionCancel = (Button) Dview.findViewById(R.id.emtionCancel);
        tv = (TextView) Dview.findViewById(R.id.emotiontextview);
        emotionalertdialog = emotionbuilder.create();

        //seekbar
        AngrySeekbar = (SeekBar) Dview.findViewById(R.id.Angry);
        pAngry = (TextView) Dview.findViewById(R.id.angryValue);
        BoredomSeekbar = (SeekBar) Dview.findViewById(R.id.Boredom);
        pBoredom = (TextView) Dview.findViewById(R.id.boredomValue);
        DisgustSeekbar = (SeekBar) Dview.findViewById(R.id.Disgust);
        pDisgust = (TextView) Dview.findViewById(R.id.disgustValue);
        AnxietySeekbar = (SeekBar) Dview.findViewById(R.id.Anxiety);
        pAnxiety = (TextView) Dview.findViewById(R.id.anxietyValue);
        HappinessSeekbar = (SeekBar) Dview.findViewById(R.id.Happiness);
        pHappiness = (TextView) Dview.findViewById(R.id.happinessValue);
        SadnessSeekbar = (SeekBar) Dview.findViewById(R.id.Sadness);
        pSadness = (TextView) Dview.findViewById(R.id.sadnessValue);
        SurprisedSeekbar = (SeekBar) Dview.findViewById(R.id.Surprised);
        pSurprised = (TextView) Dview.findViewById(R.id.surprisedValue);
        setSeekBar();
    }

    private void setSeekBar() {
        AngrySeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                pAngry.setText(" " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        BoredomSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                pBoredom.setText(" " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        DisgustSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                pDisgust.setText(" " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        AnxietySeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                pAnxiety.setText(" " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        HappinessSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                pHappiness.setText(" " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        SadnessSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                pSadness.setText(" " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        SurprisedSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                pSurprised.setText(" " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    //設置文字Dialog監聽
    private View.OnClickListener writelis = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            writebuilder = new AlertDialog.Builder(question.this);
            writelayoutinflater = getLayoutInflater();

            writeSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String writeEditTextValue = writeedittext.getText().toString();
                    writealertdialog.cancel();
                    if (writeEditTextValue.replace("[\r\n\\s     　]", "").length() > 1) {
                        tvContent = writeEditTextValue;
                        word = writeEditTextValue;
                        writeedittext.setText("");
                        emotionbutton.callOnClick();
                    }
                }
            });
            writeCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    writealertdialog.cancel();
                    writeedittext.setText("");
                }
            });
            writealertdialog.show();
        }
    };

    private TextView pAngry, pBoredom, pDisgust, pAnxiety, pHappiness, pSadness, pSurprised;

    private  void seekbarInitial(){
        //初始化
        pAngry.setText("0");
        pBoredom.setText("0");
        pDisgust.setText("0");
        pAnxiety.setText("0");
        pHappiness.setText("0");
        pSadness.setText("0");
        pSurprised.setText("0");
        AngrySeekbar.setProgress(0);
        BoredomSeekbar.setProgress(0);
        DisgustSeekbar.setProgress(0);
        AnxietySeekbar.setProgress(0);
        HappinessSeekbar.setProgress(0);
        SadnessSeekbar.setProgress(0);
        SurprisedSeekbar.setProgress(0);
    }
    //設置情緒Dialog監聽
    private View.OnClickListener emotionlis = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            seekbarInitial();

            emotionSubmit.setOnClickListener(emotionSubmitListener);

            emotionCancel.setOnClickListener(emotionCancelListener);
            emotionalertdialog.show();
        }
    };

    private View.OnClickListener emotionSubmitListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            emotionalertdialog.cancel();
            mood[0] = pAngry.getText().toString().replaceAll("[ \r\n\\s]", "");
            mood[1] = pBoredom.getText().toString().replaceAll("[ \r\n\\s]", "");
            mood[2] = pDisgust.getText().toString().replaceAll("[ \r\n\\s]", "");
            mood[3] = pAnxiety.getText().toString().replaceAll("[ \r\n\\s]", "");
            mood[4] = pHappiness.getText().toString().replaceAll("[ \r\n\\s]", "");
            mood[5] = pSadness.getText().toString().replaceAll("[ \r\n\\s]", "");
            mood[6] = pSurprised.getText().toString().replaceAll("[ \r\n\\s]", "");
            //prepareNewData(true);//call function to set new Data
            tvContent = "";
        }
    };

    private View.OnClickListener emotionCancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //prepareNewData(false);//call function to set new Data

            emotionalertdialog.cancel();
            tvContent = "";
        }
    };
}
