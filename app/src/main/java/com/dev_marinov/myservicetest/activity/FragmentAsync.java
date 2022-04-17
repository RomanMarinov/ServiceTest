package com.dev_marinov.myservicetest.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dev_marinov.myservicetest.R;
import com.dev_marinov.myservicetest.service.MyServiceAsync;

import java.util.ArrayList;


public class FragmentAsync extends Fragment {

    View frag;
    public TextView tvLog;
    ScrollView scrollView;
    Button btStart, btStop;
    BroadcastReceiver broadcastReceiver;
    public final static String FIRST_ACTION = "first_action";
    boolean flag_create, flag_start, flag_stop;
    String create, start, stop;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        frag = inflater.inflate(R.layout.fragment_async, container, false);

        tvLog = frag.findViewById(R.id.tvLog);
        btStart = frag.findViewById(R.id.btStart);
        btStop = frag.findViewById(R.id.btStop);

        scrollView = frag.findViewById(R.id.scrollView);
        flag_create = true;
        flag_start = true;
        flag_stop = false;
        btStop.setEnabled(false);

        // создаем BroadcastReceiver
        // Broadcast Receiver - это механизм для отсылки и получения сообщений в Android. ... Например,
        // отправить сообщение из одной части приложения в другую, при этом пересылка сообщений потоко-безопасна,
        // т. е. мы без проблем можем отослать сообщение в одном потоке, а словить его в другом.
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // действия при получении сообщений
                stop = intent.getStringExtra("stop");
                if(flag_stop)
                {
                    tvLog.setText(tvLog.getText() + "\n" + stop);
                    flag_stop = false;
                    return;
                }

                create = intent.getStringExtra("create");
                if(flag_create)
                {
                    tvLog.setText(tvLog.getText() + "\n" + create);
                    flag_create = false;
                    return;
                }

                start = intent.getStringExtra("start");
                if(flag_start) // зайдет один раз при старте (это все просто для отображения)
                {
                    tvLog.setText(tvLog.getText() + "\n" + start);
                    flag_start = false;
                }

                int i = intent.getIntExtra("count", 0);
                tvLog.setText(tvLog.getText().toString() + "\ni = " + i);//     НАДО ЛИ ПОМЕТИСТЬ В UI???

                // исполнение в ui потоке
                scrollView.post(new Runnable() {
                    public void run() {
                        scrollView.fullScroll(scrollView.FOCUS_DOWN);
                    }
                });
            }
        };

        // создаем фильтр для BroadcastReceiver
        // Он будет запускаться в основном потоке активности (также как поток пользовательского интерфейса).
        IntentFilter intentFilter = new IntentFilter(FIRST_ACTION);
        // регистрируем (включаем) BroadcastReceiver
        getActivity().registerReceiver(broadcastReceiver, intentFilter);

  // кнопка запуска сервиса
        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btStart.setEnabled(false);

                    Log.e("333","start button click");

                    if(tvLog.getText().equals(""))
                    {
                        tvLog.setText("-start button click-");
                    }
                    else
                    {
                        tvLog.setText(tvLog.getText() + "\nstart button click");
                    }


                    Intent intent = new Intent(getActivity(), MyServiceAsync.class);
                    getActivity().startService(intent);

                    btStop.setEnabled(true);
               }
        });
        // кнопка остановки сервиса
        btStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btStop.setEnabled(false);

                tvLog.setText(tvLog.getText() + "\n-stop button click-");
                Log.e("333","btStop click");
                Intent intent = new Intent(getActivity(), MyServiceAsync.class);
                getActivity().stopService(intent);
                btStart.setEnabled(true);
                flag_start = true;

                flag_stop = true;
                flag_create = true;
            }
        });

        return frag;
    }

    // при учнитожении приложения закрывается BroadcastReceiver
    @Override
    public void onDestroy() {
        super.onDestroy();
        // дерегистрируем (выключаем) BroadcastReceiver
        getActivity().unregisterReceiver(broadcastReceiver);
    }

}