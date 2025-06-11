package com.nutrivda.app;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

import com.nutrivda.app.main.activity.BaseActivity;

public class ResetPesoReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // 1. Mostrar notificación
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "nutrividanotif",
                    "NutriVida Notificaciones",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Recordatorios de progreso en NutriVida");
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        Intent intentNotif = new Intent(context, BaseActivity.class);
        PendingIntent pendingIntentNotif = PendingIntent.getActivity(
                context, 0, intentNotif,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "nutrividanotif")
                .setSmallIcon(R.drawable.logo1)
                .setContentTitle("¡Hora de registrar comida!")
                .setContentText("Abre NutriVida y mantén tu progreso actualizado 💪")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntentNotif);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(context).notify(101, builder.build());
        }

        // 2. Reprogramar la próxima alarma (ej: 1 día después)
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent newIntent = new Intent(context, ResetPesoReceiver.class);
        PendingIntent newPendingIntent = PendingIntent.getBroadcast(
                context, 0, newIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        long intervalMillis = 24 * 60 * 60 * 1000L; // 1 día
        long nextTriggerTime = System.currentTimeMillis() + intervalMillis;

        // 3. Verificar permiso antes de setExact()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, nextTriggerTime, newPendingIntent);
            } else {
                Log.w("ResetPesoReceiver", "No tiene permiso para alarmas exactas");
            }
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, nextTriggerTime, newPendingIntent);
        }
    }


}
