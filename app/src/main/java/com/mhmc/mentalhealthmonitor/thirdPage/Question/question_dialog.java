package com.mhmc.mentalhealthmonitor.thirdPage.Question;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.data.BarEntry;
import com.mhmc.mentalhealthmonitor.MYSQL.SQL;
import com.mhmc.mentalhealthmonitor.MYSQL.buffer;
import com.mhmc.mentalhealthmonitor.MainActivity;
import com.mhmc.mentalhealthmonitor.R;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.mhmc.mentalhealthmonitor.Voice.WavRecorder;

public class question_dialog extends AppCompatActivity {
    ImageButton Micbutton;
    AlertDialog.Builder Micbuilder;
    LayoutInflater Miclayoutinflater;
    Button MicSubmit, MicCancel;
    AlertDialog Micalertdialog;
    private ImageButton imageWriteButton, emotionbutton;
    private Button writebutton, writeCancel, writeSubmit, emotionCancel, emotionSubmit, videoCancel, recordCancel;
    private TextView writetextview, emotionedittext, tv;
    private AlertDialog.Builder writebuilder, emotionbuilder;
    private LayoutInflater writelayoutinflater, emotionlayoutinflater;
    private EditText writeedittext;
    private AlertDialog writealertdialog, emotionalertdialog;
    private SeekBar AngrySeekbar, BoredomSeekbar, DisgustSeekbar, AnxietySeekbar, HappinessSeekbar, SadnessSeekbar, SurprisedSeekbar;
    private String tvContent, word, icontype;
    private String[] mood = new String[7];

    private Button NoQuestion;
    private TextView ask_question;
    private String account, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        //設定隱藏標題
        getSupportActionBar().hide();

        //set view initial
        setDialogInitial();

        //SetMic
        MicEmtionDialog();
        micDialog();

        //SetVideo
        videoDialog();
        VideoEmotionButton();

        ask_question = findViewById(R.id.ask_question);
        ask_question.setText("現在心情如何呢?");
        readAccount();

        System.out.println(buffer.getAlert_question_voice());
        if (buffer.getAlert_question_voice().equals("ON")) {
            String path = "http://140.116.82.102:8080/app/question/question1.wav";
            MediaPlayer mp = new MediaPlayer();
            try {
                mp.setDataSource(path);
                mp.prepare();
                mp.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /***********************************************************************************************/
    //按下返回鍵回到homepage畫面
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { // 按下的如果是BACK，同时没有重复
            // Finish the registration screen and return to the Login activity
            Intent intent = new Intent(getApplicationContext(), question_dialog.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }

        return super.onKeyDown(keyCode, event);
    }

    /********************************************Dialog**********************************************/
    //設置Dialog 文字
    private void setDialogInitial() {

        //initial writeTextview
        writetextview = (TextView) findViewById(R.id.textView1);
        writebutton = (Button) findViewById(R.id.buttonMain);
        writelayoutinflater = getLayoutInflater();
        View Dview = writelayoutinflater.inflate(R.layout.dialog_write, null);
        writebuilder = new AlertDialog.Builder(question_dialog.this);
        writebuilder.setCancelable(false);
        writebuilder.setView(Dview);
        writeedittext = (EditText) Dview.findViewById(R.id.writeeditText);
        writeSubmit = (Button) Dview.findViewById(R.id.writebutton);
        writeCancel = (Button) Dview.findViewById(R.id.writeCancel);
        writealertdialog = writebuilder.create();

        //initial writeIcon
        imageWriteButton = (ImageButton) findViewById(R.id.imageWrite);
        imageWriteButton.setOnClickListener(writelis);

        //initial Icon
        emotionbutton = (ImageButton) findViewById(R.id.imageButton2);
        emotionbutton.setOnClickListener(emotionlis);
        emotionbuilder = new AlertDialog.Builder(question_dialog.this);
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

        //set Button
        NoQuestion = findViewById(R.id.NoQuestion);
        NoQuestion.setOnClickListener(NoQuestionListener);
    }

    private View.OnClickListener NoQuestionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(question_dialog.this, MainActivity.class);
            startActivity(intent);
        }
    };

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
            writebuilder = new AlertDialog.Builder(question_dialog.this);
            writelayoutinflater = getLayoutInflater();

            writeSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String writeEditTextValue = writeedittext.getText().toString();
                    writealertdialog.cancel();
                    if (writeEditTextValue.replace("[\r\n\\s     　]", "").length() > 1) {
                        tvContent = writeEditTextValue;
                        icontype = "0";
                        word = writeEditTextValue;
                        writeedittext.setText("");
                        //emotionbutton.callOnClick();
                        whichmicbutton = true;
                        writealertdialog.cancel();
                    }
                }
            });
            writeCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    whichmicbutton = false;
                    writealertdialog.cancel();
                    writeedittext.setText("");
                }
            });
            writealertdialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    if (whichmicbutton) {
                        mood[0] = "0";
                        mood[1] = "0";
                        mood[2] = "0";
                        mood[3] = "0";
                        mood[4] = "0";
                        mood[5] = "0";
                        mood[6] = "0";
                        prepareNewData(false);
                    }
                }
            });
            writealertdialog.show();
        }
    };

    private TextView pAngry, pBoredom, pDisgust, pAnxiety, pHappiness, pSadness, pSurprised;
    //設置情緒Dialog監聽
    private View.OnClickListener emotionlis = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //每次近來先初始化
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

            if (tvContent != null) {
                if (tvContent.length() > 1)
                    tv.setText(tvContent);
                else
                    tv.setText("Emotion");
            }

            emotionSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (word == null)
                        word = " ";
                    if (word.length() < 2)
                        icontype = "2";
                    else
                        icontype = "0";

                    emotionalertdialog.cancel();
                    mood[0] = pAngry.getText().toString().replaceAll("[ \r\n\\s]", "");
                    mood[1] = pBoredom.getText().toString().replaceAll("[ \r\n\\s]", "");
                    mood[2] = pDisgust.getText().toString().replaceAll("[ \r\n\\s]", "");
                    mood[3] = pAnxiety.getText().toString().replaceAll("[ \r\n\\s]", "");
                    mood[4] = pHappiness.getText().toString().replaceAll("[ \r\n\\s]", "");
                    mood[5] = pSadness.getText().toString().replaceAll("[ \r\n\\s]", "");
                    mood[6] = pSurprised.getText().toString().replaceAll("[ \r\n\\s]", "");
                    prepareNewData(true);//call function to set new Data
                    tvContent = "";
                }
            });

            emotionCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (word == null)
                        word = " ";
                    if (word.equals("") || word.equals("null") || word.length() < 2) {
                        icontype = "2";
                    } else {
                        icontype = "0";
                        prepareNewData(false);//call function to set new Data
                    }
                    emotionalertdialog.cancel();
                    tvContent = "";
                }
            });
            emotionalertdialog.show();
        }
    };

    /**************************************錄影 Initial data****************************************/
    private ImageButton imageVideoButton;
    private AlertDialog.Builder videobuilder;
    private LayoutInflater videolayoutinflater;
    private Dialog videoalertdialog;
    private ImageButton video_Recorder;
    private SeekBar videoAngrySeekbar, videoBoredomSeekbar, videoDisgustSeekbar, videoAnxietySeekbar, videoHappinessSeekbar, videoSadnessSeekbar, videoSurprisedSeekbar;
    private TextView videopAngry, videopBoredom, videopDisgust, videopAnxiety, videopHappiness, videopSadness, videopSurprised;
    private Calendar vcal = Calendar.getInstance();
    private SimpleDateFormat vsdf = new SimpleDateFormat("yyyyMMdd-HH-mm-ss");
    private String dir_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RDataR/MP4Recorder/Data/";
    private String str_mp4 = vsdf.format(vcal.getInstance().getTime()).toString() + ".mp4";
    private static final int VIDEO_CAPTURE = 101;
    Uri videoUri;

    private void videoDialog() {
        imageVideoButton = findViewById(R.id.imageVideo);
        imageVideoButton.setOnClickListener(videolist);
    }

    private View.OnClickListener videolist = new View.OnClickListener() {
        public void onClick(View v) {
            videobuilder = new AlertDialog.Builder(question_dialog.this);
            videolayoutinflater = getLayoutInflater();
            View Dview = videolayoutinflater.inflate(R.layout.dialog_video, null);
            videobuilder.setCancelable(false);
            videobuilder.setView(Dview);
            videoalertdialog = videobuilder.create();


            videoCancel = Dview.findViewById(R.id.video_Cancel);
            videoCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    whichmicbutton = false;
                    videoalertdialog.cancel();
                }
            });

            video_Recorder = (ImageButton) Dview.findViewById(R.id.video_Recorder);
            video_Recorder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {

                        dir_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RDataR/MP4Recorder/Data/";
                        str_mp4 = vsdf.format(vcal.getInstance().getTime()).toString() + ".mp4";
                        isExist(dir_path);
                        File mediaFile = new File(dir_path + str_mp4);
                        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        videoUri = FileProvider.getUriForFile(
                                v.getContext(),
                                getPackageName() + ".provider",
                                mediaFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
                        startActivityForResult(intent, VIDEO_CAPTURE);
                    } else {
                        Toast.makeText(question_dialog.this, "找不到裝置", Toast.LENGTH_LONG).show();
                    }
                }
            });

            videoalertdialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    if (whichmicbutton) {
                        mood[0] = "0";
                        mood[1] = "0";
                        mood[2] = "0";
                        mood[3] = "0";
                        mood[4] = "0";
                        mood[5] = "0";
                        mood[6] = "0";
                        prepareNewData(false);
                    }
                }
            });

            videoalertdialog.show();
        }
    };

    private ImageButton VideoEmotionButton;
    private AlertDialog.Builder videoEmotionbuilder;
    private LayoutInflater videoEmotionlayoutinflater;
    private Dialog videoEmotionalertdialog;
    private Button videoEmotionSubmit, videoEmotionCancel;

    private void VideoEmotionButton() {
        VideoEmotionButton = (ImageButton) findViewById(R.id.imageButtonVideo);
        VideoEmotionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoEmotionbuilder = new AlertDialog.Builder(question_dialog.this);
                videoEmotionlayoutinflater = getLayoutInflater();
                View Dview = videoEmotionlayoutinflater.inflate(R.layout.dialog_videoemotion, null);
                videoEmotionbuilder.setCancelable(false);
                videoEmotionbuilder.setView(Dview);
                videoEmotionSubmit = (Button) Dview.findViewById(R.id.videobutton);
                videoEmotionCancel = (Button) Dview.findViewById(R.id.videoCancel);

                //seekbar
                videoAngrySeekbar = (SeekBar) Dview.findViewById(R.id.Angry);
                videopAngry = (TextView) Dview.findViewById(R.id.angryValue);
                videoBoredomSeekbar = (SeekBar) Dview.findViewById(R.id.Boredom);
                videopBoredom = (TextView) Dview.findViewById(R.id.boredomValue);
                videoDisgustSeekbar = (SeekBar) Dview.findViewById(R.id.Disgust);
                videopDisgust = (TextView) Dview.findViewById(R.id.disgustValue);
                videoAnxietySeekbar = (SeekBar) Dview.findViewById(R.id.Anxiety);
                videopAnxiety = (TextView) Dview.findViewById(R.id.anxietyValue);
                videoHappinessSeekbar = (SeekBar) Dview.findViewById(R.id.Happiness);
                videopHappiness = (TextView) Dview.findViewById(R.id.happinessValue);
                videoSadnessSeekbar = (SeekBar) Dview.findViewById(R.id.Sadness);
                videopSadness = (TextView) Dview.findViewById(R.id.sadnessValue);
                videoSurprisedSeekbar = (SeekBar) Dview.findViewById(R.id.Surprised);
                videopSurprised = (TextView) Dview.findViewById(R.id.surprisedValue);
                videogetemotion();

                //每次近來先初始化
                videopAngry.setText("0");
                videopBoredom.setText("0");
                videopDisgust.setText("0");
                videopAnxiety.setText("0");
                videopHappiness.setText("0");
                videopSadness.setText("0");
                videopSurprised.setText("0");
                videoAngrySeekbar.setProgress(0);
                videoBoredomSeekbar.setProgress(0);
                videoDisgustSeekbar.setProgress(0);
                videoAnxietySeekbar.setProgress(0);
                videoHappinessSeekbar.setProgress(0);
                videoSadnessSeekbar.setProgress(0);
                videoSurprisedSeekbar.setProgress(0);

                videoEmotionalertdialog = videoEmotionbuilder.create();
                videoEmotionSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        icontype = "3";
                        videoEmotionalertdialog.cancel();
                        mood[0] = videopAngry.getText().toString().replaceAll("[ \r\n\\s]", "");
                        mood[1] = videopBoredom.getText().toString().replaceAll("[ \r\n\\s]", "");
                        mood[2] = videopDisgust.getText().toString().replaceAll("[ \r\n\\s]", "");
                        mood[3] = videopAnxiety.getText().toString().replaceAll("[ \r\n\\s]", "");
                        mood[4] = videopHappiness.getText().toString().replaceAll("[ \r\n\\s]", "");
                        mood[5] = videopSadness.getText().toString().replaceAll("[ \r\n\\s]", "");
                        mood[6] = videopSurprised.getText().toString().replaceAll("[ \r\n\\s]", "");
                        word = str_mp4;
                        prepareNewData(true);


                        //每次結束先初始化
                        videopAngry.setText("0");
                        videopBoredom.setText("0");
                        videopDisgust.setText("0");
                        videopAnxiety.setText("0");
                        videopHappiness.setText("0");
                        videopSadness.setText("0");
                        videopSurprised.setText("0");
                        videoAngrySeekbar.setProgress(0);
                        videoBoredomSeekbar.setProgress(0);
                        videoDisgustSeekbar.setProgress(0);
                        videoAnxietySeekbar.setProgress(0);
                        videoHappinessSeekbar.setProgress(0);
                        videoSadnessSeekbar.setProgress(0);
                        videoSurprisedSeekbar.setProgress(0);
                    }
                });

                videoEmotionCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        icontype = "3";
                        videoEmotionalertdialog.cancel();
                        word = str_mp4;
                        prepareNewData(false);
                    }
                });
                videoEmotionalertdialog.show();
            }
        });
    }

    private void videogetemotion() {
        videoAngrySeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                videopAngry.setText(" " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        videoBoredomSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                videopBoredom.setText(" " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        videoDisgustSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                videopDisgust.setText(" " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        videoAnxietySeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                videopAnxiety.setText(" " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        videoHappinessSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                videopHappiness.setText(" " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        videoSadnessSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                videopSadness.setText(" " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        videoSurprisedSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                videopSurprised.setText(" " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VIDEO_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Thread thread = new Thread(outputvideo);
                thread.start();
                //VideoEmotionButton.callOnClick();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Video recording cancelled.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Failed to record video", Toast.LENGTH_LONG).show();
            }
        }
    }
    private Runnable outputvideo = new Runnable(){
        public void run(){
            //這裡要儲存
            upload(dir_path + str_mp4, true);
            icontype = "3";
            word = str_mp4;
            whichmicbutton = true;
            videoalertdialog.cancel();
        }
    };
    /**************************************錄音 Initial data****************************************/
    private ImageButton imageMicButton;
    private AlertDialog.Builder micbuilder;
    private LayoutInflater miclayoutinflater;
    private Dialog micalertdialog;
    private SeekBar micAngrySeekbar, micBoredomSeekbar, micDisgustSeekbar, micAnxietySeekbar, micHappinessSeekbar, micSadnessSeekbar, micSurprisedSeekbar;
    private String dir_Root = Environment.getExternalStorageDirectory().getPath() + "/RDataR/WavRecorder/";
    private String dir_Data = "Data/";
    private Calendar cal = Calendar.getInstance();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH-mm-ss");
    private SimpleDateFormat smonth = new SimpleDateFormat("MM");
    private String str_wav = sdf.format(cal.getTime()).toString() + ".wav";
    private TextView atextMic;
    public WavRecorder wavRecorder;
    public boolean isRecorder = false;
    public ImageButton ib_Recorder;
    private TextView micpAngry, micpBoredom, micpDisgust, micpAnxiety, micpHappiness, micpSadness, micpSurprised;

    private boolean whichmicbutton = false;//這是為了判斷micDialog是因為錄音結束關閉，還是使用者按下關閉按鈕;

    //設置Dialog 音檔
    private void micDialog() {
        imageMicButton = (ImageButton) findViewById(R.id.imageMic);
        imageMicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                micbuilder = new AlertDialog.Builder(question_dialog.this);
                miclayoutinflater = getLayoutInflater();
                View Dview = miclayoutinflater.inflate(R.layout.dialog_record, null);
                micbuilder.setCancelable(false);
                micbuilder.setView(Dview);
                micalertdialog = micbuilder.create();

                recordCancel = Dview.findViewById(R.id.record_Cancel);
                recordCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        whichmicbutton = false;
                        micalertdialog.cancel();
                    }
                });

                atextMic = Dview.findViewById(R.id.record_title);
                ib_Recorder = (ImageButton) Dview.findViewById(R.id.ib_Recorder);
                ib_Recorder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isRecorder) {
                            recordCancel.setVisibility(View.GONE);
                            atextMic.setText("再次點擊-結束錄音");

                            isRecorder = true;
                            ib_Recorder.setBackgroundResource(R.drawable.recorder);

                            Log.i("Msg", "Initial");
                            str_wav = sdf.format(cal.getInstance().getTime()).toString() + ".wav";
                            String month = smonth.format(cal.getInstance().getTime()).toString();//這是月份
                            wavRecorder = wavRecorder.getInstanse(false);
                            wavRecorder.setOutputFile(dir_Root + dir_Data + str_wav);
                            Log.i("Msg", "Prepare");
                            wavRecorder.prepare();
                            Log.i("Msg", "Start");
                            wavRecorder.start();
                        } else {
                            atextMic.setText("點擊麥克風-敘述近期狀況");

                            isRecorder = false;
                            ib_Recorder.setBackgroundResource(R.drawable.microphone);
                            Log.i("Msg", "Stop");
                            wavRecorder.stop();
                            Log.i("Msg", "Release");
                            wavRecorder.release();

                            whichmicbutton = true;
                            Thread thread = new Thread() {
                                @Override
                                public void run() {
                                    //這裡要儲存
                                    isExist(dir_Root + dir_Data);
                                    upload(dir_Root + dir_Data + str_wav, false);
                                    micalertdialog.cancel();
                                }
                            };
                            thread.start();
                            loading();
                            //Micbutton.callOnClick();
                        }
                    }
                });

                micalertdialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (whichmicbutton) {
                            prepareNewData_NoneTagEmotion();
                        }
                    }
                });
                micalertdialog.show();
            }
        });
    }

    //設置Dialog 情緒
    private void MicEmtionDialog() {
        Micbutton = (ImageButton) findViewById(R.id.imageButtonMic);
        Micbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Micbuilder = new AlertDialog.Builder(question_dialog.this);
                Miclayoutinflater = getLayoutInflater();
                View Dview = Miclayoutinflater.inflate(R.layout.dialog_micemotion, null);
                Micbuilder.setCancelable(false);
                Micbuilder.setView(Dview);
                MicSubmit = (Button) Dview.findViewById(R.id.micbutton);
                MicCancel = (Button) Dview.findViewById(R.id.micCancel);

                //seekbar
                micAngrySeekbar = (SeekBar) Dview.findViewById(R.id.Angry);
                micpAngry = (TextView) Dview.findViewById(R.id.angryValue);
                micBoredomSeekbar = (SeekBar) Dview.findViewById(R.id.Boredom);
                micpBoredom = (TextView) Dview.findViewById(R.id.boredomValue);
                micDisgustSeekbar = (SeekBar) Dview.findViewById(R.id.Disgust);
                micpDisgust = (TextView) Dview.findViewById(R.id.disgustValue);
                micAnxietySeekbar = (SeekBar) Dview.findViewById(R.id.Anxiety);
                micpAnxiety = (TextView) Dview.findViewById(R.id.anxietyValue);
                micHappinessSeekbar = (SeekBar) Dview.findViewById(R.id.Happiness);
                micpHappiness = (TextView) Dview.findViewById(R.id.happinessValue);
                micSadnessSeekbar = (SeekBar) Dview.findViewById(R.id.Sadness);
                micpSadness = (TextView) Dview.findViewById(R.id.sadnessValue);
                micSurprisedSeekbar = (SeekBar) Dview.findViewById(R.id.Surprised);
                micpSurprised = (TextView) Dview.findViewById(R.id.surprisedValue);
                getmicemotion();

                //每次近來先初始化
                micpAngry.setText("0");
                micpBoredom.setText("0");
                micpDisgust.setText("0");
                micpAnxiety.setText("0");
                micpHappiness.setText("0");
                micpSadness.setText("0");
                micpSurprised.setText("0");
                micAngrySeekbar.setProgress(0);
                micBoredomSeekbar.setProgress(0);
                micDisgustSeekbar.setProgress(0);
                micAnxietySeekbar.setProgress(0);
                micHappinessSeekbar.setProgress(0);
                micSadnessSeekbar.setProgress(0);
                micSurprisedSeekbar.setProgress(0);

                Micalertdialog = Micbuilder.create();
                MicSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        icontype = "1";
                        Micalertdialog.cancel();
                        mood[0] = micpAngry.getText().toString().replaceAll("[ \r\n\\s]", "");
                        mood[1] = micpBoredom.getText().toString().replaceAll("[ \r\n\\s]", "");
                        mood[2] = micpDisgust.getText().toString().replaceAll("[ \r\n\\s]", "");
                        mood[3] = micpAnxiety.getText().toString().replaceAll("[ \r\n\\s]", "");
                        mood[4] = micpHappiness.getText().toString().replaceAll("[ \r\n\\s]", "");
                        mood[5] = micpSadness.getText().toString().replaceAll("[ \r\n\\s]", "");
                        mood[6] = micpSurprised.getText().toString().replaceAll("[ \r\n\\s]", "");
                        word = str_wav;
                        prepareNewData(true);

                        //每次結束先初始化
                        micpAngry.setText("0");
                        micpBoredom.setText("0");
                        micpDisgust.setText("0");
                        micpAnxiety.setText("0");
                        micpHappiness.setText("0");
                        micpSadness.setText("0");
                        micpSurprised.setText("0");
                        micAngrySeekbar.setProgress(0);
                        micBoredomSeekbar.setProgress(0);
                        micDisgustSeekbar.setProgress(0);
                        micAnxietySeekbar.setProgress(0);
                        micHappinessSeekbar.setProgress(0);
                        micSadnessSeekbar.setProgress(0);
                        micSurprisedSeekbar.setProgress(0);
                    }
                });

                MicCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        icontype = "1";
                        Micalertdialog.cancel();
                        word = str_wav;
                        prepareNewData(false);
                    }
                });
                Micalertdialog.show();
            }
        });
    }

    private void getmicemotion() {
        micAngrySeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                micpAngry.setText(" " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        micBoredomSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                micpBoredom.setText(" " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        micDisgustSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                micpDisgust.setText(" " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        micAnxietySeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                micpAnxiety.setText(" " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        micHappinessSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                micpHappiness.setText(" " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        micSadnessSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                micpSadness.setText(" " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        micSurprisedSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                micpSurprised.setText(" " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void upload(String file, boolean tf) {
        String existingFileName = file;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        String responseFromServer = "";
        String str_URL = "http://140.116.82.102:8080/app/";
        String urlString = str_URL + "upload.php";

        //判斷要上傳音樂檔還是影像檔
        if (tf) {
            urlString = str_URL + "upload_video.php";
        } else {
            Calendar mCal = Calendar.getInstance();
            CharSequence s = DateFormat.format("yyyy-MM-dd kk:mm:ss", mCal.getTime());
            String time = s.toString();
            urlString = str_URL + "SER/upload_mic.php?Account=" + com.mhmc.mentalhealthmonitor.MYSQL.buffer.getAccount() + "&time=" + time;
        }

        //上傳音樂檔或影像檔
        HttpURLConnection conn = null;
        DataOutputStream dos;
        try {
            // CLIENT REQUEST
            FileInputStream fileInputStream = new FileInputStream(new File(existingFileName));

            // open a URL connection to the Servlet
            URL url = new URL(urlString);

            // Open a HTTP connection to the URL
            conn = (HttpURLConnection) url.openConnection();

            // Allow Inputs
            conn.setDoInput(true);

            // Allow Outputs
            conn.setDoOutput(true);

            // Don't use a cached copy.
            conn.setUseCaches(false);

            // Use a post method.
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + file + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            // create a buffer of maximum size
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // close streams
            Log.e("Msg", "File is written");
            fileInputStream.close();
            dos.flush();
            dos.close();

        } catch (MalformedURLException ex) {
            Log.e("Msg", "error: " + ex.getMessage(), ex);
        } catch (IOException ioe) {
            Log.e("Msg", "error: " + ioe.getMessage(), ioe);
        }

        //取得Response
        String response;
        try {
            InputStream is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF8"));
            StringBuffer sb = new StringBuffer();
            String str = "";
            response = "";
            while ((str = br.readLine()) != null) {
                sb.append(str);
                Log.e("Msg", "Server Response " + str);
                response += str + "\r\n";
            }
            is.close();

        } catch (IOException ioex) {
            Log.e("Msg", "error: " + ioex.getMessage(), ioex);
        }
    }

    /***********************************新增圖資料*************************************/
    private void prepareNewData(boolean HasWord) {
        //產生loading畫面
        loading();

        //chart
        ArrayList<BarEntry> yVals;

        //Data
        Calendar mCal = Calendar.getInstance();
        CharSequence s = DateFormat.format("yyyy-MM-dd kk:mm:ss", mCal.getTime());
        String time = s.toString();
        String content = word;
        String emotion = "0,0,0,0,0,0,0";
        mood[0] = "0";
        mood[1] = "0";
        mood[2] = "0";
        mood[3] = "0";
        mood[4] = "0";
        mood[5] = "0";
        mood[6] = "0";

        //set chart Data
        int j = 0;
        float Max = 0;
        yVals = new ArrayList<>();
        for (String st : mood) {
            Max += Float.valueOf(st);
        }
        for (String st : mood) {
            yVals.add(new BarEntry(j, Float.valueOf(st) / Max * 100));
            j++;
        }

        threadtime = time;
        threadcontent = content;
        threademotion = emotion;
        Thread thread = new Thread(sql);
        thread.start();

        word = "";

        next_question();
    }

    //執行thread，將data存至SQL
    String threadtime,threadcontent,threademotion;
    private Runnable sql = new Runnable(){
        public void run(){
            //set Data to SQL
            SQL sql1 = new SQL();
            sql1.InsertNewData_new(account, threadtime, threadcontent, threademotion, icontype);
        }
    };

    private void prepareNewData_NoneTagEmotion() {
        //產生loading畫面

        icontype = "1";
        word = str_wav;

        //chart
        ArrayList<BarEntry> yVals;

        //Data
        Calendar mCal = Calendar.getInstance();
        CharSequence s = DateFormat.format("yyyy-MM-dd kk:mm:ss", mCal.getTime());
        String time = s.toString();
        String content = word;
        String emotion = "0,0,0,0,0,0,0";
        mood[0] = "0";
        mood[1] = "0";
        mood[2] = "0";
        mood[3] = "0";
        mood[4] = "0";
        mood[5] = "0";
        mood[6] = "0";

        //set chart Data
        int j = 0;
        float Max = 0;
        yVals = new ArrayList<>();
        for (String st : mood) {
            Max += Float.valueOf(st);
        }
        for (String st : mood) {
            yVals.add(new BarEntry(j, Float.valueOf(st) / Max * 100));
            j++;
        }

        next_question();
    }

    /***********************************建立資料夾*************************************/
    public void isExist(String path) {
        File file = new File(path);
        //判斷文件夾是否存在,如果不存在則建立文件夾
        if (!file.exists()) {
            file.mkdir();
        }
    }

    /***********************************Loading用**************************************/
    private Dialog dialog;

    private void loading() {
        dialog = ProgressDialog.show(this,
                "儲存中", "請等待2秒...", true);
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed'
                        dialog.dismiss();
                        // onLoginFailed();
                        //progressDialog.dismiss();
                    }
                }, 2000);
    }

    //loading dialog出錯，立即將其銷毀
    @Override
    protected void onDestroy() {
        if(dialog != null) {
            dialog.dismiss();
        }
        super.onDestroy();
    }

    /***********************************Next Question**************************************/
    private void next_question() {
        Intent intent = new Intent(question_dialog.this, question_dialog2.class);
        startActivity(intent);
    }

    /***********************************Read Account**************************************/
    //密碼、鬧鐘問題是否需要聲音一併讀取
    private void readAccount() {
        String path = "/storage/emulated/0/RDataR/";
        String myData = "";
        try {
            FileInputStream fis = new FileInputStream(path + "user.txt");
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                if (strLine.contains("帳號:") && strLine.length() > 6) {
                    myData = strLine;
                    account = myData.replace("帳號:", "");
                    buffer.setaccount(account);
                } else if (strLine.contains("密碼:") && strLine.length() > 6) {
                    myData = strLine;
                    password = myData.replace("密碼:", "");
                    buffer.setpassword(password);
                } else if (strLine.contains("鬧鐘聲音:") && strLine.length() > 6) {
                    myData = strLine;
                    String offon = myData.replace("鬧鐘聲音:", "");
                    buffer.setAlert_question_voice(offon);
                }
            }

            SearchAccount.CheckAccount(account, password);
            in.close();
        } catch (Exception e) {
        }
    }
}