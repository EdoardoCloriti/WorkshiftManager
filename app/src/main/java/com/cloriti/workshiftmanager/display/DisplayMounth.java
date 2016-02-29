package com.cloriti.workshiftmanager.display;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cloriti.workshiftmanager.R;
import com.cloriti.workshiftmanager.util.Week;
import com.cloriti.workshiftmanager.util.db.AccessToDB;
import com.google.common.base.Predicates;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.List;

public class DisplayMounth extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_mounth);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle input = this.getIntent().getExtras();
        int month = input.getInt("MONTH");
        int year = input.getInt("YEAR");
        Button back = (Button) findViewById(R.id.back);
        TextView meseAttuale = (TextView) findViewById(R.id.mese_attuale);
        TextView ore = (TextView) findViewById(R.id.ore);
        TextView straordinari = (TextView) findViewById(R.id.straordinari);
        meseAttuale.setText(new DateFormatSymbols().getMonths()[month - 1] + " " + year);
        ore.setText(calculateHour(month, year));
        straordinari.setText(calculateOvertime(month, year));
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private String calculateHour(int mounth, int year) {
        AccessToDB db = new AccessToDB();
        List<Week> weeks = db.getMounth(mounth, year, getApplicationContext());
        double hours = 0;
        for (Week week : weeks) {
            hours = hours + week.getHour();
        }
        return Double.toString(hours);

    }

    private String calculateOvertime(int mounth, int year) {
        AccessToDB db = new AccessToDB();
        List<Week> weeks = db.getMounth(mounth, year, getApplicationContext());
        double hours = 0;
        for (Week week : weeks) {
            hours = hours + week.getExtraHour();
        }
        return Double.toString(hours);

    }

}
