package com.dev_marinov.myservicetest.activity;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dev_marinov.myservicetest.R;
import com.dev_marinov.myservicetest.service.MyServiceSync;
//import com.dev_marinov.myservicetest.service.MyService;
//import com.dev_marinov.myservicetest.service.MyServiceSync;


public class FragmentSync extends Fragment {

    View frag;
    boolean bound = false; // подключены мы в данный момент к сервису или нет.
    ServiceConnection serviceConnection;
    BroadcastReceiver broadcastReceiver;
    public final static String SECOND_ACTION = "second_action";
    Intent intent;
    Button btStart, btUp, btDown;
    TextView tvLog;
    ScrollView scrollView;
    MyServiceSync myServiceSync;
    TextView tvInterval;
    long interval;
    boolean flag_onBind;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        frag = inflater.inflate(R.layout.fragment_sync, container, false);

        btStart = frag.findViewById(R.id.btStart);
        btUp = frag.findViewById(R.id.btUp);
        btDown = frag.findViewById(R.id.btDown);
        tvLog = frag.findViewById(R.id.tvLog);
        scrollView = frag.findViewById(R.id.scrollView);

        intent = new Intent(getActivity(), MyServiceSync.class);

        flag_onBind = true;

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String create = intent.getStringExtra("create");
                Log.e("555","-create-" + create);
                if(tvLog.getText().toString().equals(""))
                {
                    tvLog.setText(create);
                }

                String onBind = intent.getStringExtra("onBind");
                Log.e("555","-onBind-" + onBind);
                if(flag_onBind)
                {
                    tvLog.setText(tvLog.getText().toString() + "\n" + onBind);
                    flag_onBind = false;
                    return;
                }

                String run = intent.getStringExtra("run");
                Log.e("555","-run-" + run);
                tvLog.setText(tvLog.getText().toString() + "\n" + run);

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
            IntentFilter intentFilter = new IntentFilter(SECOND_ACTION);
            // регистрируем (включаем) BroadcastReceiver
            getActivity().registerReceiver(broadcastReceiver, intentFilter);

        serviceConnection = new ServiceConnection() {
// B подключаемом Fragment, в методе onServiceConnected мы получим объект, который идет на выход
// метода onBind. Далее преобразуем его к типу MyBinder, вызовем getService и вуаля - у нас в Activity
// будет ссылка на объект-сервис MyService.
            public void onServiceConnected(ComponentName name, IBinder binder) {
                Log.e("444", "MainActivity onServiceConnected");

                tvLog.setText(tvLog.getText().toString() + "\nonServiceConnected");
                myServiceSync = ((MyServiceSync.MyBinder)binder).getService();
                bound = true;
            }

            public void onServiceDisconnected(ComponentName name) {
                Log.e("444", "MainActivity onServiceDisconnected");
                bound = false;
            }
        };

        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startService(intent);
            }
        });
        btUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!bound) return;
                interval = myServiceSync.upInterval(500);
                tvLog.setText(tvLog.getText().toString() + "\ninterval = " + interval);
            }
        });
        btDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!bound) return;
                interval = myServiceSync.downInterval(500);
                tvLog.setText(tvLog.getText().toString() + "\ninterval = " + interval);
            }
        });

        return frag;
    }


    @Override
    public void onStart() {
        super.onStart();
        getActivity().bindService(intent, serviceConnection, 0);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!bound) return;
        getActivity().unbindService(serviceConnection);
        bound = false;
    }

    // при учнитожении приложения закрывается BroadcastReceiver
    @Override
    public void onDestroy() {
        super.onDestroy();
        // дерегистрируем (выключаем) BroadcastReceiver
        getActivity().unregisterReceiver(broadcastReceiver);
    }


}
