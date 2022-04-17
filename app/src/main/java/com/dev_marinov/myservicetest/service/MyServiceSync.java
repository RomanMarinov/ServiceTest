package com.dev_marinov.myservicetest.service;

import android.app.Service;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.dev_marinov.myservicetest.activity.FragmentSync;

public class MyServiceSync extends Service {

    // MyBinder расширяет стандартный Binder, мы добавляем в него один метод getService.
    // Этот метод возвращает наш сервис MyServiceSync
    MyBinder binder = new MyBinder();
    Timer timer; // Он позволяет повторять какое-либо действие через заданный промежуток времени
    TimerTask timerTask; // это задача, которую Timer будет периодически выполнять
    long interval = 1000;
    Intent intent;

    // создаем таймер и выполняем метод schedule, в котором стартует задача.
    public void onCreate() {
        super.onCreate();
        intent = new Intent(FragmentSync.SECOND_ACTION);
        Log.e("444", "MyServiceSync onCreate");
        intent.putExtra("create", "MyServiceSync onCreate");

        timer = new Timer();
        schedule();
    }

    // Метод schedule проверяет, что задача уже создана и отменяет ее.
    // Далее планирует новую, с отложенным на 1000 мс запуском и периодом = interval.
    // Т.е. можно сказать, что этот метод перезапускает задачу с использованием текущего
    // интервала повтора (interval), а если задача еще не создана, то создает ее.
    // Сама задача просто выводит в лог текст run. Если interval = 0, то ничего не делаем.
    void schedule() {
        // Чтобы отменить выполнение задачи, необходимо вызвать метод cancel для TimerTask.
        // Отмененную задачу нельзя больше запланировать, и если снова надо ее включить
        // – необходимо создать новый экземпляр TimerTask и скормить его таймеру.
        if (timerTask != null) timerTask.cancel();
        if (interval > 0) {
            timerTask = new TimerTask() {
                public void run() {
                    Log.e("444", "run");

                    intent.putExtra("run", "run");
                    sendBroadcast(intent);
                }
            };
         // для объекта Timer вызываем метод schedule, в который передаем задачу TimerTask,
            // время через которое начнется выполнение, и период повтора.
            timer.schedule(timerTask, 1000, interval);
        }
    }
    // Метод upInterval получает на вход значение, увеличивает interval на это значение и
    // перезапускает задачу. Соответственно задача после этого будет повторяться реже.
    public long upInterval(long gap) {
        interval = interval + gap; // + 500
        schedule();
        return interval;
    }
    // Метод downInterval получает на вход значение, уменьшает interval на это значение (но так, чтоб не меньше 0)
    // и перезапускает задачу. Соответственно задача после этого будет повторяться чаще.
    public long downInterval(long gap) {
        interval = interval - gap; // - 500
        if (interval < 0) interval = 0;
        schedule();
        return interval;
    }

    public IBinder onBind(Intent arg0) {
        Log.e("444", "MyServiceSync onBind");

        intent.putExtra("onBind", "MyServiceSync onBind");
        sendBroadcast(intent);
        return binder;
    }

    public class MyBinder extends Binder {
        public MyServiceSync getService() {
            return MyServiceSync.this;
        }
    }


    @Override
    public void onDestroy() { // уничтожение сервиса
        super.onDestroy();
        intent.putExtra("stop", "service is stopped" +
                "\nservice is destroyed" +
                "\n----------------------------------");
        sendBroadcast(intent);
        timer.cancel();
    }

}
