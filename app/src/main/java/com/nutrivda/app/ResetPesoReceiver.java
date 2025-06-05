package com.nutrivda.app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.nutrivda.app.database.DatabaseHelper;

public class ResetPesoReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        //Notificaciones de canal
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

        //Notificaciones
        Intent intentNotif = new Intent(context, BaseActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intentNotif,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "nutrividanotif")
                .setSmallIcon(R.drawable.logo1) // o cualquier ícono que tengas
                .setContentTitle("¡Hora de tu test nutricional!")
                .setContentText("Abre NutriVida y mantén tu progreso actualizado 💪")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {

            NotificationManagerCompat.from(context).notify(101, builder.build());

        } else {
            // Opcional: podrías registrar un log o hacer nada
            Log.w("ResetPesoReceiver", "Permiso POST_NOTIFICATIONS no concedido");
        }

    }
}
