package com.dev_marinov.myservicetest.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import com.dev_marinov.myservicetest.activity.FragmentAsync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MyServiceAsync extends Service { // сервис это задачаб которая в фоне не используя ui.


    // declaring object of MediaPlayer
    private MediaPlayer player;

    // interface ExecutorService исполняет асинхронный код в одном или нескольких потоках.
    ExecutorService executorService;
    //Object someRes; // Этот объект будет использоваться сервисом в обработках вызовов.

    boolean flag = true;
    int i = -1;
    Intent intent;

    @Override
    public void onCreate() { // сервис начинает свое создание
        super.onCreate();
        executorService = Executors.newFixedThreadPool(1); // создал пул с одним потоком
        //someRes = new Object();
        intent = new Intent(FragmentAsync.FIRST_ACTION);
        intent.putExtra("create", "service is created");
        sendBroadcast(intent);
        Log.e("333", "onCreate: сервис создался");
    }

    // Метод onStartCommand – срабатывает, когда сервис запущен методом startService.
    // Intent параметр тот самый, который отправляется в путь, когда мы стартуем сервис с помощью метода startService.
    // Соответственно вы можете использовать его для передачи данных в ваш сервис.
    // startId. Простыми словами – это счетчик вызовов startService пока сервис запущен.
    // Т.е. вы запустили сервис методом startService, сработал метод onStartCommand и получил на вход startId = 1.
    // Вызываем еще раз метод startService, сработал метод onStartCommand и получил на вход startId = 2.
    // И так далее. Счетчик сбрасывается, когда сервис будет остановлен методами stopService, stopSelf и пр.
    // После этого вызовы снова идут с единицы.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { // сервис запущен
        Log.e("333", "onStartCommand: -------");

        MyRun myRun = new MyRun();
        executorService.execute(myRun);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() { // уничтожение сервиса
        super.onDestroy();
        intent.putExtra("stop", "service *melody & counter* is stopped" +
                "\nservice is destroyed" +
                "\n----------------------------------");
        sendBroadcast(intent);
        // stopping the process
        player.stop();
        flag = false; // после уничтожения сервиса надо уничтожить бесконечный цикл
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("333", "onUnbind: ----------------------");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.e("333", "onRebind: ----------------------");
        super.onRebind(intent);
    }

    //@Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("333", "onBind: ----------------------");
        return new Binder();
    }

    public class MyRun implements Runnable {
        @Override
        public void run() {
            // звук мелодии звонка по умолчанию на устройстве Android
            player = MediaPlayer.create(getApplicationContext(), Settings.System.DEFAULT_RINGTONE_URI );
            player.setLooping(true);  // true для воспроизведения звука в цикле
            player.start();// старт

            intent.putExtra("start", "service *melody & counter* is started");

            while (flag)
            {
                i++;
                Log.e("333","-i- " + i);
                try {
                    intent.putExtra("count",i);
                    sendBroadcast(intent);

                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    Log.e("333","-try catch- " + e);
                }
            }


        }

    }
}
