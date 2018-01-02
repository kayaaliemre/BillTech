package com.example.kaya.billtech;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class LocationService extends Service {
    ConnectionClass connectionClass;
    Map<Integer, BoardClass> boardMap = new HashMap<Integer, BoardClass>();
    int previousBoard =0;
    int lastBoard;
    float minimumDistance;
    String boardName;

    public LocationService() {
    }

    public static final int TWO_MINUTES = 60000; // 120 seconds
    public static Boolean isRunning = false;

    public LocationManager mLocationManager;
    public LocationUpdaterListener mLocationListener;
    public Location previousBestLocation = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        System.out.println("Servis");
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationUpdaterListener();
        super.onCreate();
    }

    Handler mHandler = new Handler();
    Runnable mHandlerTask = new Runnable() {
        @Override
        public void run() {
            if (!isRunning) {
                startListening();
            }
            mHandler.postDelayed(mHandlerTask, TWO_MINUTES);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mHandlerTask.run();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopListening();
        mHandler.removeCallbacks(mHandlerTask);
        super.onDestroy();
    }

    private void startListening() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (mLocationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) mLocationListener);

            if (mLocationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
        }
        //getting billboards from database
        connectionClass = new ConnectionClass();
        Boolean isSuccess = false;
        String message;
        try {
            Connection con = connectionClass.connectionDb();
            if (con == null) {
                message = "Error in connection with SQL server";
            } else {
                String query;

                query = "SELECT billboard_name, billboard_locationx, billboard_locationy FROM BillboardInfo";
                PreparedStatement preparedStatement = con.prepareStatement(query);
                ResultSet rs = preparedStatement.executeQuery();
                int i = 0;
                while (rs.next()) {
                    String billBoardName = rs.getString(1);
                    String billBoardLocationx = rs.getString(2);
                    String billBoardLocationy = rs.getString(3);
                    boardMap.put(i, new BoardClass(billBoardName, billBoardLocationx, billBoardLocationy));
                    i++;
                }
                con.close();
                message = "Added Successfully";
                isSuccess = true;

            }
        } catch (Exception ex) {
            isSuccess = false;
            ex.printStackTrace();
            message = "Exceptions";
        }
        isRunning = true;
    }

    private void stopListening() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationManager.removeUpdates(mLocationListener);
        }
        isRunning = false;
    }

    public class LocationUpdaterListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if (isBetterLocation(location, previousBestLocation)) {
                previousBestLocation = location;
                float[] dist = new float[1];
                for (int i = 0; i < boardMap.size(); i++) {
                    double xlocation = Double.parseDouble(boardMap.get(i).getBoardLocationx());
                    double ylocation = Double.parseDouble(boardMap.get(i).getBoardLocationy());
                    Location.distanceBetween(previousBestLocation.getLatitude(), previousBestLocation.getLongitude(), xlocation, ylocation, dist);
                   /* if ((dist[0] / 1000 < 1)) {
                        Context context = getApplicationContext();
                        Intent notificationIntent = new Intent(context,
                                BoardMap.class);
                        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), notificationIntent, 0);
                        Notification noti = new Notification.Builder(context)
                                .setContentTitle("You are close to " + boardMap.get(i).getBoardName() + ".")
                                .setContentText("If you want to display adverts, just click.").setSmallIcon(R.drawable.marker)
                                .setContentIntent(pIntent)
                                .addAction(R.drawable.marker, "Go", pIntent).build();
                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        // hide the notification after its selected
                        noti.flags |= Notification.FLAG_AUTO_CANCEL;
                        notificationManager.notify(0, noti);
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        // Vibrate for 500 milliseconds
                        v.vibrate(500);
                    }*/

                   if(i==0){
                       minimumDistance=dist[0];
                       lastBoard=i+1;
                       boardName=boardMap.get(i).getBoardName();
                   }
                   else if(dist[0]<minimumDistance){
                       minimumDistance=dist[0];
                       lastBoard=i+1;
                       boardName=boardMap.get(i).getBoardName();
                   }

                }
                if(minimumDistance/ 1000 < 1&&lastBoard!=previousBoard){

                    Context context = getApplicationContext();
                    Intent notificationIntent = new Intent(context,
                            BoardMap.class);
                    PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), notificationIntent, 0);
                    Notification noti = new Notification.Builder(context)
                            .setContentTitle("You are close to " + boardName + ".")
                            .setContentText("If you want to display adverts, just click.").setSmallIcon(R.drawable.marker)
                            .setContentIntent(pIntent)
                            .addAction(R.drawable.marker, "Go", pIntent).build();
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    // hide the notification after its selected
                    noti.flags |= Notification.FLAG_AUTO_CANCEL;
                    notificationManager.notify(0, noti);
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    // Vibrate for 500 milliseconds
                    v.vibrate(500);

                    previousBoard=lastBoard;
                }
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderDisabled(String provider) {
            stopListening();
        }

        @Override
        public void onProviderEnabled(String provider) {
        }
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}