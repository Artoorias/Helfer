package pl.ilo.erasmus.helfer;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.StrictMode;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

//sensor listener to catch shaking

public class sl implements SensorListener {
    Context cnt;
    long lastUpdate = 0;
    float last_x = 0;
    ActivityManager am;
    float last_y = 0;
    float last_z = 0;
    ComponentName cn;
    boolean is_s = false;

    sl(Context get) {
        cnt = get;
        am = (ActivityManager) cnt.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
    }

    @Override
    public void onSensorChanged(int sensor, float[] values) {
        cn = am.getRunningTasks(1).get(0).topActivity;
        //work only for my window
        if ("pl.ilo.erasmus.helfer.MainActivity".equals(cn.getClassName())) {
            if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
                long curTime = System.currentTimeMillis();
                if ((curTime - lastUpdate) > 100) {
                    long diffTime = (curTime - lastUpdate);
                    lastUpdate = curTime;

                    float x = values[SensorManager.DATA_X];
                    float y = values[SensorManager.DATA_Y];
                    float z = values[SensorManager.DATA_Z];

                    float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                    if (((speed > config.SHAKE_THRESHOLD&&config.debug==false)||(speed > config.SHAKE_THRESHOLD_DEBUG&&config.debug==true)) && !is_s) {
                        utils.show_debug_message("sensor", "shake detected w/ speed: " + speed);
                        View view = ((Activity) cnt).getWindow().getDecorView();
                        view.setDrawingCacheEnabled(true);
                        view.buildDrawingCache();
                        Bitmap b1 = view.getDrawingCache();
                        Rect frame = new Rect();
                        ((Activity) cnt).getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
                        int statusBarHeight = frame.top;
                        int width = ((Activity) cnt).getWindowManager().getDefaultDisplay().getWidth();
                        int height = ((Activity) cnt).getWindowManager().getDefaultDisplay().getHeight();
                        final Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight);
                        view.destroyDrawingCache();
                        android.support.v7.app.AlertDialog.Builder alertdialog = new android.support.v7.app.AlertDialog.Builder(cnt);
                        alertdialog.setTitle("Użyłeś opcji potrząsania do wyslania opinii. Czy wysłać?");
                        alertdialog.setCancelable(false);
                        alertdialog.setPositiveButton("Anuluj", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                is_s = false;
                            }
                        });
                        alertdialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                is_s = false;
                            }
                        });
                        alertdialog.setNeutralButton("Wyślij", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    is_s = false;
                                    //prepare screenshot
                                    //must be in external storage, because externall app (like e-mail) can't have access to this picture
                                    File file = new File(cnt.getExternalCacheDir(), "logicchip.png");
                                    FileOutputStream fOut = null;
                                    fOut = new FileOutputStream(file);
                                    b.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                                    fOut.flush();
                                    fOut.close();
                                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                                    StrictMode.setVmPolicy(builder.build());
                                    file.setReadable(true, false);
                                    Intent intent = new Intent(Intent.ACTION_SEND);
                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{config.email});
                                    intent.putExtra(Intent.EXTRA_SUBJECT, "I have a idea!");
                                    intent.putExtra(Intent.EXTRA_TEXT, "Your message here");
                                    intent.setType("text/html");
                                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                                    intent.setType("image/jpeg");
                                    cnt.startActivity(Intent.createChooser(intent, "Wyślij zrzut"));

                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                    Toast.makeText(cnt.getApplicationContext(), "Nie mogę zapisać zżutu", Toast.LENGTH_LONG).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                        alertdialog.show();
                        is_s = true;
                    }
                    last_x = x;
                    last_y = y;
                    last_z = z;
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(int sensor, int accuracy) {

    }
}
