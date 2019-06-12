package com.mhmc.mentalhealthmonitor.ControlData;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.mhmc.mentalhealthmonitor.MPChart.BarChartCustomRenderer;
import com.mhmc.mentalhealthmonitor.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.widget.VideoView;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.MyViewHolder> {
    private ArrayList<Bitmap> chartList, chartListSubject;
    private List<Data> DataList;
    private ArrayList<BarEntry> yVals, yValsSystem;
    public View v;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView time, content, musicduration;
        public ImageView icon, pic;//pic:大頭貼
        private BarChart mChart, mChart_System;
        private Button play, pause, videostart, videopause;
        private SeekBar sb;
        private VideoView mVideoView;
        private RelativeLayout micAndvideo, micRelativeLayout;

        public MyViewHolder(View view) {
            super(view);
            time = view.findViewById(R.id.datetime);
            mChart = view.findViewById(R.id.chart1);
            mChart_System = view.findViewById(R.id.chart1_System_Object);
            content = view.findViewById(R.id.content);
            icon = view.findViewById(R.id.title_icon);
            pic = view.findViewById(R.id.pic);
            pause = view.findViewById(R.id.pause);
            play = view.findViewById(R.id.play);
            sb = view.findViewById(R.id.sb);
            mVideoView = view.findViewById(R.id.video_view);
            videostart = (Button) view.findViewById(R.id.videostart);
            videopause = (Button) view.findViewById(R.id.videopause);
            v = view;

            micAndvideo = view.findViewById(R.id.micAndvideo);
            musicduration = view.findViewById(R.id.musicduration);
            micRelativeLayout = view.findViewById(R.id.micRelativeLayout);
        }
    }


    public DataAdapter(List<Data> DataList) {
        this.DataList = DataList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.customlayouy, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Data data = DataList.get(position);
        holder.time.setText(data.getTime());
        media ma;
        //set Icon type
        String type = data.getIcon_type();
        switch (type) {
            case "0":
                holder.icon.setBackgroundResource(R.drawable.chat);
                ma = new media(holder);
                holder.content.setText(data.getContent());
                holder.mVideoView.setVisibility(View.GONE);
                holder.videostart.setVisibility(View.GONE);
                holder.videopause.setVisibility(View.GONE);
                holder.mChart_System.setVisibility(View.GONE);
                holder.musicduration.setVisibility(View.GONE);
                holder.micRelativeLayout.setVisibility(View.GONE);
                holder.micAndvideo.setBackgroundColor(Color.parseColor("#00000000"));
                holder.mChart.setVisibility(View.GONE);
                break;
            case "1":
                holder.icon.setBackgroundResource(R.drawable.mic);
                ma = new media(holder, v, data.getContent());
                holder.content.setText("");
                holder.mVideoView.setVisibility(View.GONE);
                holder.videostart.setVisibility(View.GONE);
                holder.videopause.setVisibility(View.GONE);

                if (data.getyValsSystem() != null && !data.getyValsSystem().equals("null,null,null") && data.getyValsSystem().size() > 0) {
                    chartListSubject = data.getchartListSystem();
                    yValsSystem = data.getyValsSystem();
                    setBarChartSubject(holder);
                    holder.mChart_System.setVisibility(View.VISIBLE);
                }
                holder.micAndvideo.setBackgroundColor(Color.parseColor("#00000000"));
                holder.micRelativeLayout.setVisibility(View.VISIBLE);
                holder.mChart.setVisibility(View.GONE);
                break;
            case "2":
                holder.icon.setBackgroundResource(R.drawable.review);
                ma = new media(holder);
                holder.content.setText("");
                holder.mVideoView.setVisibility(View.GONE);
                holder.videostart.setVisibility(View.GONE);
                holder.videopause.setVisibility(View.GONE);
                holder.mChart_System.setVisibility(View.GONE);
                holder.musicduration.setVisibility(View.GONE);
                holder.micRelativeLayout.setVisibility(View.GONE);
                holder.micAndvideo.setBackgroundColor(Color.parseColor("#00000000"));
                holder.mChart.setVisibility(View.VISIBLE);
                break;
            case "3":
                holder.icon.setBackgroundResource(R.drawable.video);
                ma = new media(holder);
                onPlayLocalVideo(holder, data.getContent());
                holder.content.setText("");
                holder.mVideoView.setVisibility(View.VISIBLE);
                holder.mChart_System.setVisibility(View.GONE);
                holder.musicduration.setVisibility(View.GONE);
                holder.micRelativeLayout.setVisibility(View.GONE);
                holder.micAndvideo.setBackgroundColor(Color.parseColor("#000000"));
                holder.mChart.setVisibility(View.GONE);
                break;
        }

        //set chart
        chartList = data.getchartList();
        yVals = data.getyVals();
        setBarChart(holder);
    }

    @Override
    public int getItemCount() {
        return DataList.size();
    }

    /****************************************錄影**************************************/
    public void onPlayLocalVideo(MyViewHolder holder, String path) {
        //String dir_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MP4Recorder/Data/";
        //File f = new File(dir_path + path);
        //holder.mVideoView.setVideoURI(Uri.parse(f.toString()));
        holder.mVideoView.setVideoURI(Uri.parse("http://140.116.82.102:8080/app/upload_video/" + path));
        holder.mVideoView.seekTo(100);
        MediaController mc = new MediaController(v.getContext());
        mc.setVisibility(View.VISIBLE);
        holder.mVideoView.setMediaController(mc);
        holder.mVideoView.requestFocus();
        /*
        holder.mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                holder.mVideoView.start();
            }
        });*/
        //holder.mVideoView.start();

        holder.videostart.setVisibility(View.GONE);
        holder.videostart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mVideoView.start();
                holder.videostart.setVisibility(View.GONE);
                holder.videopause.setVisibility(View.GONE);
            }
        });

        holder.videopause.setVisibility(View.GONE);
        holder.videopause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mVideoView.pause();
                holder.videostart.setVisibility(View.GONE);
                holder.videopause.setVisibility(View.GONE);
            }
        });
    }

    /****************************************chart**************************************/
    private void setBarChart(MyViewHolder holder) {
        holder.mChart.getDescription().setEnabled(false);
        holder.mChart.setDrawValueAboveBar(false);
        holder.mChart.setPinchZoom(false);
        holder.mChart.setDrawGridBackground(false);
        holder.mChart.setFitBars(true);

        XAxis xAxis = holder.mChart.getXAxis();
        xAxis.setDrawGridLines(true);
        xAxis.setLabelCount(3);//原本7個
        xAxis.setDrawLabels(false);
        YAxis yleftAxis = holder.mChart.getAxisLeft();
        YAxis yrightAxis = holder.mChart.getAxisRight();
        yrightAxis.setDrawLabels(false);
        yleftAxis.setDrawGridLines(true);
        yleftAxis.setAxisMaxValue(100);
        yleftAxis.setAxisMinValue(0);
        yleftAxis.setLabelCount(10);
        yrightAxis.setDrawGridLines(false);

        setData(holder);

        Legend l = holder.mChart.getLegend();
        l.setEnabled(false);
    }

    private void setData(MyViewHolder holder) {
        BarDataSet set = new BarDataSet(yVals, "Data Set");
        set.setColors(ColorTemplate.COLORFUL_COLORS);
        set.setDrawValues(true);

        BarData data = new BarData(set);
        data.setDrawValues(true);
        holder.mChart.setData(data);
        holder.mChart.setScaleEnabled(false);
        holder.mChart.setRenderer(new BarChartCustomRenderer(holder.mChart, holder.mChart.getAnimator(), holder.mChart.getViewPortHandler(), chartList, v.getContext()));
        holder.mChart.setExtraOffsets(0, 0, 0, 23);
        //加入下面兩行就解決不同item chart value的問題了
        holder.mChart.notifyDataSetChanged();
        holder.mChart.invalidate();
    }

    private void setBarChartSubject(MyViewHolder holder) {
        holder.mChart_System.getDescription().setEnabled(false);
        holder.mChart_System.setDrawValueAboveBar(false);
        holder.mChart_System.setPinchZoom(false);
        holder.mChart_System.setDrawGridBackground(false);
        holder.mChart_System.setFitBars(true);

        XAxis xAxis = holder.mChart_System.getXAxis();
        xAxis.setDrawGridLines(true);
        xAxis.setLabelCount(3);//原本7個
        xAxis.setDrawLabels(false);
        YAxis yleftAxis = holder.mChart_System.getAxisLeft();
        YAxis yrightAxis = holder.mChart_System.getAxisRight();
        yrightAxis.setDrawLabels(false);
        yleftAxis.setDrawGridLines(true);
        yleftAxis.setAxisMaxValue(100);
        yleftAxis.setAxisMinValue(0);
        yleftAxis.setLabelCount(10);
        yrightAxis.setDrawGridLines(false);

        setDataSubject(holder);

        Legend l = holder.mChart_System.getLegend();
        l.setEnabled(false);
    }

    private void setDataSubject(MyViewHolder holder) {
        BarDataSet set = new BarDataSet(yValsSystem, "Data Set");
        set.setColors(ColorTemplate.COLORFUL_COLORS);
        set.setDrawValues(true);

        BarData data = new BarData(set);
        data.setDrawValues(true);
        holder.mChart_System.setData(data);
        holder.mChart_System.setScaleEnabled(false);
        holder.mChart_System.setRenderer(new BarChartCustomRenderer(holder.mChart_System, holder.mChart_System.getAnimator(), holder.mChart_System.getViewPortHandler(), chartListSubject, v.getContext()));
        holder.mChart_System.setExtraOffsets(0, 0, 0, 23);
        //加入下面兩行就解決不同item chart value的問題了
        holder.mChart_System.notifyDataSetChanged();
        holder.mChart_System.invalidate();
    }

    /********************************************音樂放器************************************************************/
    class media {
        Button play, pause;
        SeekBar sb;
        MediaPlayer mp;
        Handler handler;
        int Duration;
        private String dir_Root = Environment.getExternalStorageDirectory().getPath() + "/RDataR/WavRecorder/";
        private String dir_Data = "Data/";

        media(MyViewHolder holder) {
            holder.play.setVisibility(View.GONE);
            holder.pause.setVisibility(View.GONE);
            holder.sb.setVisibility(View.GONE);
        }

        //設置音樂初始化
        media(MyViewHolder holder, View v, String path) {
            play = holder.play;
            pause = holder.pause;
            sb = holder.sb;

            handler = new Handler();

            holder.play.setVisibility(View.VISIBLE);
            holder.pause.setVisibility(View.GONE);
            holder.sb.setVisibility(View.VISIBLE);
            holder.musicduration.setVisibility(View.VISIBLE);

            File f = new File(dir_Root + dir_Data + path);
            mp = new MediaPlayer();
            try {
                mp.setDataSource("http://140.116.82.102:8080/app/SER/wav/" + path);
                mp.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //找到相应View
            //mp = MediaPlayer.create(v.getContext(), Uri.parse(f.toString()));
            //后面的参数必须是URI形式的，所以要把相应路径转换成URI

            holder.play.setOnClickListener(playlis);
            holder.pause.setOnClickListener(pauselis);
            holder.sb.setOnSeekBarChangeListener(sbLis);

            //监听器
            Duration = mp.getDuration();
            int mstos = Duration/1000;
            if(mstos<10)
                holder.musicduration.setText("0:0"+String.valueOf(mstos));
            else if(mstos<60)
                holder.musicduration.setText("0:"+String.valueOf(mstos));
            else{
                int mt = mstos/60;
                int s = mstos%60;
                if(s>=10)
                    holder.musicduration.setText(String.valueOf(mt)+":"+String.valueOf(s));
                else
                    holder.musicduration.setText(String.valueOf(mt)+":0"+String.valueOf(s));
            }
            //音乐文件持续时间
            holder.sb.setMax(Duration);
            //设置SeekBar最大值为音乐文件持续时间
        }

        //播音樂
        private View.OnClickListener playlis = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                handler.post(start);
                play.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
                //调用handler播放
            }
        };
        Runnable start = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                mp.start();
                handler.post(updatesb);
                //用一个handler更新SeekBar
            }

        };
        Runnable updatesb = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                sb.setProgress(mp.getCurrentPosition());
                handler.postDelayed(updatesb, 1000);
                //每秒钟更新一次
            }

        };
        private View.OnClickListener pauselis = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mp.pause();
                play.setVisibility(View.VISIBLE);
                pause.setVisibility(View.GONE);
                //暂停
            }
        };
        private SeekBar.OnSeekBarChangeListener sbLis = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                mp.seekTo(sb.getProgress());
                //SeekBar确定位置后，跳到指定位置
            }
        };
    }
}