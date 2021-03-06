package com.cloriti.workshiftmanager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cloriti.workshiftmanager.selection.MultiSelectionMenu;
import com.cloriti.workshiftmanager.util.Property;
import com.cloriti.workshiftmanager.util.db.AccessToDB;
import com.cloriti.workshiftmanager.util.tutorial.WorkshiftManagerTutorial;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Activity Main dell'applicazione serve come Home page di ingresso e controllo del primo accesso,
 * delle notifiche giornaliere e della pulizia dei dati annule
 *
 * @Author edoardo.cloriti@studio.unibo.it
 */
public class WorkshiftManager extends AppCompatActivity {

    private final static String PATTERN = "dd/MM/yyyy";
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    private String CLEANING_DATE = "16/02/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workshift_manager);
        //Setting delle impostazioni della Action Bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_insert_invitation_black_48dp);
        toolbar.setTitle(R.string.title_app_upper);
        setSupportActionBar(toolbar);
        //Portait (Verticale)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // creazione del button di uscita dall'applicaizone
        Button exitButton = (Button) findViewById(R.id.exitbutton);
        // creazione del button di ingresso dell'appicazione
        Button startButton = (Button) findViewById(R.id.startbutton);

        //Controllo se oggi è il gionro della pulizia dello storico dati
        Calendar sysCalendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(PATTERN);
        try {
            CLEANING_DATE = CLEANING_DATE + sysCalendar.get(Calendar.YEAR);
            Date cleaning = sdf.parse(CLEANING_DATE);
            if (sysCalendar.getTime().equals(cleaning)) {
                AccessToDB db = new AccessToDB();
                int year = sysCalendar.get(Calendar.YEAR);
                int yearAnnual = sysCalendar.get(Calendar.YEAR) - 1;
                int yearBiannual = sysCalendar.get(Calendar.YEAR) - 1;
                //in caso il controllo restituisce esito positivo chiamo le funzionalita di pulizia dei dati
                db.clearTurnByYear(yearAnnual, getApplicationContext());
                db.cleanWeekByYearToYear(yearBiannual, year, getApplicationContext());
                Toast.makeText(getApplicationContext(), "Effettuata pulizia annuale dei dati", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Impossibile Effettuare la  pulizia annuale di turni", Toast.LENGTH_LONG).show();
        }
        AccessToDB db = new AccessToDB();
        if (db.existPropery(Property.NOTIFICA, getApplicationContext()) != 0) {

            alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getApplicationContext(), WorkShiftManagerReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

            // Set the alarm to start at some time.
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int curHr = calendar.get(Calendar.HOUR_OF_DAY);
            // Checking whether current hour is over 15
            if (curHr >= 15) {
                // Since current hour is over 15, setting the date to the next day
                calendar.add(Calendar.DATE, 1);
            }
            calendar.set(Calendar.HOUR_OF_DAY, 15);
            calendar.set(Calendar.MINUTE, 00);
            // every day
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }

        startButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent multiSelectionMenu = new Intent(getApplicationContext(), MultiSelectionMenu.class);
                Intent applicationSetting = new Intent(getApplicationContext(), WorkShiftManagerSetting.class);

                //controllo se è la prima volta che si esegue l'accesso, in tal caso si viene reindirizzati alla selezione delle configurazioni
                AccessToDB db = new AccessToDB();
                if (db.getProperty(Property.READYTOGO, getApplicationContext()) == null) {
                    startActivity(applicationSetting);
                } else {
                    startActivity(multiSelectionMenu);
                }
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Si esegue la chiusura dell'applicazione
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_workshift_manager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(getApplicationContext(), WorkShiftManagerSetting.class);
            startActivity(i);
        }
        if (id == R.id.action_help) {
            WorkshiftManagerTutorial.showWorkShiftManagerTurorial(WorkshiftManager.this, "WorkShiftManager");
        }


        return super.onOptionsItemSelected(item);
    }
}
