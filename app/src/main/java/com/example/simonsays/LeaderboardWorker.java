package com.example.simonsays;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LeaderboardWorker extends Worker {

    private static final String CHANNEL_ID = "leaderboard_notifications";
    private static final String PREFS_NAME = "leaderboard";
    private static final String LEADERBOARD_KEY = "leaderboardkey";
    private DatabaseReference mDatabase;

    public LeaderboardWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://ps-q2-10-default-rtdb.europe-west1.firebasedatabase.app/users");
        createNotificationChannel();
    }

    @NonNull
    @Override
    public Result doWork() {
        checkLeaderboardAndNotify();
        return Result.success();
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "Leaderboard Notifications";
            String description = "Notifications for leaderboard changes";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void checkLeaderboardAndNotify() {
        Log.d("LeaderboardWorker", "Checking leaderboard...");
        mDatabase.orderByChild("record").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> previousUserList = getLeaderboardState();
                List<String> currentUserList = new ArrayList<>();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String username = userSnapshot.child("username").getValue(String.class);
                    Long recordValue = userSnapshot.child("record").getValue(Long.class);
                    String record = recordValue != null ? String.valueOf(recordValue) : "0";
                    currentUserList.add(username + "   " + record);
                }
                Collections.reverse(currentUserList);

                String currentUser = getCurrentUser();
                Log.d("LeaderboardWorker", "Current user: " + currentUser);
                Log.d("LeaderboardWorker", "Previous leaderboard: " + previousUserList);
                Log.d("LeaderboardWorker", "Current leaderboard: " + currentUserList);
                if (!previousUserList.isEmpty() && hasUserBeenOvertaken(previousUserList, currentUserList, currentUser)) {
                    Log.d("LeaderboardWorker", "User has been overtaken.");
                    sendNotification();
                } else {
                    Log.d("LeaderboardWorker", "User has not been overtaken.");
                }

                saveLeaderboardState(currentUserList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("LeaderboardWorker", "Database error: " + error.getMessage());
            }
        });
    }

    private void saveLeaderboardState(List<String> leaderboard) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(LEADERBOARD_KEY, String.join(",", leaderboard));
        editor.apply();
    }

    private List<String> getLeaderboardState() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String leaderboardString = prefs.getString(LEADERBOARD_KEY, "");
        if (!leaderboardString.isEmpty()) {
            return Arrays.asList(leaderboardString.split(","));
        }
        return new ArrayList<>();
    }

    private boolean hasUserBeenOvertaken(List<String> previousList, List<String> currentList, String currentUser) {
        int previousPosition = getPosition(previousList, currentUser);
        int currentPosition = getPosition(currentList, currentUser);
        return currentPosition > previousPosition;
    }

    private int getPosition(List<String> list, String username) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).startsWith(username)) {
                return i;
            }
        }
        return -1;
    }

    private void sendNotification() {
        Context context = getApplicationContext();
        int notificationId = 1; // ID de la notificación

        // Intent para abrir la aplicación
        Intent intent = new Intent(context, StartActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Intent para eliminar la notificación
        Intent dismissIntent = new Intent(context, NotificationDismissedReceiver.class);
        dismissIntent.putExtra("notificationId", notificationId);
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(context, 0, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.img_logo) // Asegúrate de tener un icono válido
                .setContentTitle("Leaderboard Update")
                .setContentText("Someone has overtaken you in the leaderboard!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent) // Acción para abrir la aplicación
                .addAction(R.drawable.ic_empty, "Open App", pendingIntent) // Botón para abrir la aplicación
                .addAction(R.drawable.ic_empty, "Dismiss", dismissPendingIntent) // Botón para eliminar la notificación
                .setAutoCancel(true); // Descartar notificación al hacer clic

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, builder.build());
    }


    private String getCurrentUser() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        return prefs.getString("current_user", "Guest");
    }
}
