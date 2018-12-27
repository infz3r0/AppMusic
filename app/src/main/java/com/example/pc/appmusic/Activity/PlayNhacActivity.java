package com.example.pc.appmusic.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.pc.appmusic.Adapter.ViewPagerPlayListnhac;
import com.example.pc.appmusic.Fragment.Fragment_Dia_Nhac;
import com.example.pc.appmusic.Fragment.Fragment_Play_Danh_Sach_Cac_Bai_Hat;
import com.example.pc.appmusic.Model.Baihat;
import com.example.pc.appmusic.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

public class PlayNhacActivity extends AppCompatActivity {

    Toolbar toolbarplaynhac;
    TextView txtTimesong, txtTotaltimesong;
    SeekBar sktime;
    ImageButton imgplay, imgrepeat, imgnext, imgpre, imgrandom;
    ViewPager viewPagerplaynhac;

    public static ArrayList<Baihat> mangbaihat = new ArrayList<>();
    public static ViewPagerPlayListnhac adapternhac;

    Fragment_Dia_Nhac fragment_dia_nhac;
    Fragment_Play_Danh_Sach_Cac_Bai_Hat fragment_play_danh_sach_cac_bai_hat;

    static MediaPlayer mediaPlayer;
    static Handler handler1, handler2;
    static Runnable r1, r2;
    boolean next;
    int position = 0;
    boolean repeat = false;
    boolean random = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_nhac);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //Lấy dữ liệu các bài hát
        GetDataFromIntent();
        //Khởi tạo các biến và play bài hát đầu tiên
        init();
        //Cài đặt các event
        eventClick();

    }

    private void eventClick() {
        //Lấy hình ảnh của bài hát
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(adapternhac.getItem(1) != null)
                {
                    if(mangbaihat.size()>0)
                    {
                        fragment_dia_nhac.PlayNhac(mangbaihat.get(0).getHinhbaihat());
                        handler.removeCallbacks(this);
                    }else {
                        handler.postDelayed(this,300);
                    }
                }
            }
        },500);

        //Cài đặt sự kiện click cho nút play/pause
        imgplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    imgplay.setImageResource(R.drawable.iconplay);
                }else{
                    mediaPlayer.start();
                    imgplay.setImageResource(R.drawable.iconpause);
                }
            }
        });

        //Cài đặt sự kiện click cho nút repeat
        imgrepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (repeat == false)
                {
                    if (random == true)
                    {
                        random = false;
                        imgrandom.setImageResource(R.drawable.iconsuffle);
                    }

                    imgrepeat.setImageResource(R.drawable.iconsyned);
                    repeat = true;
                }
                else
                {
                    imgrepeat.setImageResource(R.drawable.iconrepeat);
                    repeat = false;
                }
            }
        });

        //Cài đặt sự kiện click cho nút shuffle
        imgrandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (random == false)
                {
                    if (repeat == true)
                    {
                        repeat = false;
                        imgrepeat.setImageResource(R.drawable.iconrepeat);
                    }
                    imgrandom.setImageResource(R.drawable.iconshuffled);
                    random = true;
                }
                else
                {
                    imgrandom.setImageResource(R.drawable.iconsuffle);
                    random = false;
                }
            }
        });

        //Cài đặt sự kiện click cho thanh seekbar
        sktime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            //Play bài hát tại vị trí người dùng kéo thanh seekbar tới
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        //Cài đặt sự kiện click cho nút next
        imgnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mangbaihat.size() > 0)
                {
                    position++;
                    if (repeat == true)
                    {
                        position--;
                    }
                    if (random == true)
                    {
                        Random rand = new Random();
                        int r;
                        do {
                            r = rand.nextInt(mangbaihat.size());
                        } while (r == position);
                        position = r;

                    }
                    if (position >= mangbaihat.size())
                    {
                        position = 0;
                    }
                    //
                    if (mediaPlayer == null)
                    {
                        mediaPlayer = new MediaPlayer();
                    }
                    else
                    {
                        if (mediaPlayer.isPlaying())
                        {
                            handler1.removeCallbacks(r1);
                            handler2.removeCallbacks(r2);
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            mediaPlayer = new MediaPlayer();
                        }
                    }

                    new PlayMp3().execute(mangbaihat.get(position).getLinkbaihat());
                    imgplay.setImageResource(R.drawable.iconpause);
                    getSupportActionBar().setTitle(mangbaihat.get(position).getTenbaihat());
                    fragment_dia_nhac.PlayNhac(mangbaihat.get(position).getHinhbaihat());
                }

                //Disable các nút để hạn chế người dùng bấm liên tục
                imgnext.setClickable(false);
                imgpre.setClickable(false);
                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imgnext.setClickable(true);
                        imgpre.setClickable(true);
                    }
                }, 2000);
            }
        });

        //Cài đặt sự kiện click cho nút prev
        imgpre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mangbaihat.size() > 0)
                {
                    position--;
                    if (repeat == true)
                    {
                        position++;
                    }
                    if (random == true)
                    {
                        Random rand = new Random();
                        int r;
                        do {
                            r = rand.nextInt(mangbaihat.size());
                        } while (r == position);
                        position = r;

                    }
                    if (position < 0)
                    {
                        position = mangbaihat.size() - 1;
                    }
                    //
                    if (mediaPlayer == null)
                    {
                        mediaPlayer = new MediaPlayer();
                    }
                    else
                    {
                        if (mediaPlayer.isPlaying())
                        {
                            handler1.removeCallbacks(r1);
                            handler2.removeCallbacks(r2);
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            mediaPlayer = new MediaPlayer();
                        }
                    }

                    new PlayMp3().execute(mangbaihat.get(position).getLinkbaihat());
                    imgplay.setImageResource(R.drawable.iconpause);
                    getSupportActionBar().setTitle(mangbaihat.get(position).getTenbaihat());
                    fragment_dia_nhac.PlayNhac(mangbaihat.get(position).getHinhbaihat());
                }

                //Disable các nút để hạn chế người dùng bấm liên tục
                imgnext.setClickable(false);
                imgpre.setClickable(false);
                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imgnext.setClickable(true);
                        imgpre.setClickable(true);
                    }
                }, 2000);
            }
        });
    }

    //Lấy dữ liệu trong intent được truyền từ DanhsachbaihatAdapter, DanhsachbaihatActivity, BaihathotAdapter
    private void GetDataFromIntent() {
        Intent intent = getIntent();
        mangbaihat.clear();
        if(intent != null) {
            if (intent.hasExtra("cakhuc")) {
                Baihat baihat = intent.getParcelableExtra("cakhuc");
                mangbaihat.add(baihat);
            }
            if (intent.hasExtra("cacbaihat")) {
                ArrayList<Baihat> baihatArrayList = intent.getParcelableArrayListExtra("cacbaihat");
                mangbaihat = baihatArrayList;
            }
        }
    }

    private void init() {
        toolbarplaynhac = findViewById(R.id.toolbarplaynhac);
        txtTimesong = findViewById(R.id.textviewtimesong);
        txtTotaltimesong = findViewById(R.id.textviewtotaltimesong);
        sktime = findViewById(R.id.seekbarsong);
        imgplay = findViewById(R.id.imagebuttonplay);
        imgrepeat = findViewById(R.id.imagebuttonrepeat);
        imgnext = findViewById(R.id.imagebuttonnext);
        imgpre = findViewById(R.id.imagebuttonpre);
        imgrandom = findViewById(R.id.imagebuttonsuffle);
        viewPagerplaynhac = findViewById(R.id.viewpagerplaynhac);
        setSupportActionBar(toolbarplaynhac);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarplaynhac.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbarplaynhac.setTitleTextColor(Color.WHITE);
        fragment_dia_nhac = new Fragment_Dia_Nhac();
        fragment_play_danh_sach_cac_bai_hat = new Fragment_Play_Danh_Sach_Cac_Bai_Hat();
        adapternhac = new ViewPagerPlayListnhac(getSupportFragmentManager());
        adapternhac.AddFragment(fragment_play_danh_sach_cac_bai_hat);
        adapternhac.AddFragment(fragment_dia_nhac);
        viewPagerplaynhac.setAdapter(adapternhac);
        fragment_dia_nhac = (Fragment_Dia_Nhac) adapternhac.getItem(1);
        
        if(mangbaihat.size()>0){
            getSupportActionBar().setTitle(mangbaihat.get(0).getTenbaihat());

            if (mediaPlayer == null)
            {
                mediaPlayer = new MediaPlayer();
            }
            else
            {
                if (mediaPlayer.isPlaying())
                {
                    handler1.removeCallbacks(r1);
                    handler2.removeCallbacks(r2);
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = new MediaPlayer();
                }
            }

            new PlayMp3().execute(mangbaihat.get(0).getLinkbaihat());
            imgplay.setImageResource(R.drawable.iconpause);
        }
    }

    class PlayMp3 extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            return strings[0];
        }
        @Override
        protected void onPostExecute(String baihat) {
            super.onPostExecute(baihat);
            try {

                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                });
                mediaPlayer.setDataSource(baihat);
                mediaPlayer.prepare();
            }catch (IOException e)
            {
                e.printStackTrace();
            }
            mediaPlayer.start();
            TimeSong();
            UpdateTime();
        }
    }

    //Cập nhật thời lượng bài hát
    private void TimeSong() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        txtTotaltimesong.setText(simpleDateFormat.format(mediaPlayer.getDuration()));
        sktime.setMax(mediaPlayer.getDuration());
    }

    //Cập nhật thời điểm hiện tại của bài hát
    private void UpdateTime() {
        handler1 = new Handler();
        r1 = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    sktime.setProgress(mediaPlayer.getCurrentPosition());
                    SimpleDateFormat date = new SimpleDateFormat("mm:ss");
                    txtTimesong.setText(date.format(mediaPlayer.getCurrentPosition()));
                    handler1.postDelayed(this, 300);
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            next = true;
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {

                            }
                        }
                    });
                }
            }
        };
        handler1.postDelayed(r1, 300);

        //Thực hiện các lênh tương tự nút next khi hết bài
        handler2 = new Handler();
        r2 = new Runnable() {
            @Override
            public void run() {
                if (next == true){
                    next = false;
                    if (mangbaihat.size() > 0)
                    {
                        position++;
                        if (repeat == true)
                        {
                            position--;
                        }
                        if (random == true)
                        {
                            Random rand = new Random();
                            int r;
                            do {
                                r = rand.nextInt(mangbaihat.size());
                            } while (r == position);
                            position = r;

                        }
                        if (position >= mangbaihat.size())
                        {
                            position = 0;
                        }
                        //
                            handler1.removeCallbacks(r1);
                            handler2.removeCallbacks(r2);
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            mediaPlayer = new MediaPlayer();

                        new PlayMp3().execute(mangbaihat.get(position).getLinkbaihat());
                        imgplay.setImageResource(R.drawable.iconpause);
                        getSupportActionBar().setTitle(mangbaihat.get(position).getTenbaihat());
                        fragment_dia_nhac.PlayNhac(mangbaihat.get(position).getHinhbaihat());
                    }

                    imgnext.setClickable(false);
                    imgpre.setClickable(false);
                    Handler h = new Handler();
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            imgnext.setClickable(true);
                            imgpre.setClickable(true);
                        }
                    }, 2000);
                }else {
                    handler2.postDelayed(this, 1000);
                }
            }
        };
        handler2.postDelayed(r2, 1000);



    }






}
