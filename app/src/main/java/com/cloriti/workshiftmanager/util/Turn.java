package com.cloriti.workshiftmanager.util;


import android.content.Intent;
import android.os.Bundle;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class Turn {
    public static final String PATTERN = "dd/MM/yyyy";

    private int id = 0;

    private int weekId;
    private int year;
    private int mounth;
    private String datariferimento = null;

    private Integer inizioMattinaH = null;
    private Integer fineMattinaH = null;
    private Integer inizioMattinaM = null;
    private Integer fineMattinaM = null;

    private Integer inizioPomeriggioH = null;
    private Integer finePomeriggioH = null;
    private Integer inizioPomeriggioM = null;
    private Integer finePomeriggioM = null;

    private double hour = 0;
    private double overtime = 0;

    private boolean isImportante = false;

    private String googleCalendarIDMattina = null;
    private String googleCalendarIDPomeriggio = null;

    private boolean isOnlyDelete = false;
    private String googleCalendarIDMattina2Rem = null;
    private String googleCalendarIDPomeriggio2Rem = null;

    /**
     * Dato un Bundle contenente i dati per un turno restituisce un oggetto di tipo Turn con i dati settati dal Bundle
     *
     * @param bundle
     * @return
     */
    public static final Turn turnByBundle(Bundle bundle) {
        Turn turn = new Turn();
        turn.setId(bundle.getInt(IDs.ID));
        turn.setDatariferimento(bundle.getString(IDs.DATA));
        turn.setMounth(bundle.getInt(IDs.MONTH));
        if (bundle.containsKey(IDs.INIZIO_MATTINA))
            turn.setIniziotMattina(intervalIsNotNull(bundle.getString(IDs.INIZIO_MATTINA)) ? bundle.getString(IDs.INIZIO_MATTINA) : null);
        if (bundle.containsKey(IDs.FINE_MATTINA))
            turn.setFineMattina(intervalIsNotNull(bundle.getString(IDs.FINE_MATTINA)) ? bundle.getString(IDs.FINE_MATTINA) : null);
        if (bundle.containsKey(IDs.INIZIO_POMERIGGIO))
            turn.setIniziotPomeriggio(intervalIsNotNull(bundle.getString(IDs.INIZIO_POMERIGGIO)) ? bundle.getString(IDs.INIZIO_POMERIGGIO) : null);
        if (bundle.containsKey(IDs.FINE_POMERIGGIO))
            turn.setFinePomeriggio(intervalIsNotNull(bundle.getString(IDs.FINE_POMERIGGIO)) ? bundle.getString(IDs.FINE_POMERIGGIO) : null);
        if (bundle.containsKey(IDs.ORE))
            turn.setHour(new Double(bundle.getDouble(IDs.ORE, 0)));
        if (bundle.containsKey(IDs.OVERTIME))
            turn.setOvertime(new Double(bundle.getDouble(IDs.OVERTIME, 0)));
        turn.setIsImportante(bundle.getInt(IDs.PRIORITY));
        if (bundle.containsKey(IDs.DEL_REQ))
            turn.setIsOnlyDelete(bundle.getInt(IDs.DEL_REQ));
        if (bundle.containsKey(IDs.GOOGLE_ID_MATTINA))
            turn.setGoogleCalendarIDMattina(bundle.getString(IDs.GOOGLE_ID_MATTINA));
        if (bundle.containsKey(IDs.GOOGLE_ID_POMERIGGIO))
            turn.setGoogleCalendarIDPomeriggio(bundle.getString(IDs.GOOGLE_ID_POMERIGGIO));
        return turn;
    }

    /**
     * Dato un oggetto di tipo Turn e un intent filla tutti i valori del Turn all'interno dell'intent
     *
     * @param i
     * @param turn
     * @return
     */
    public static final Intent intentByTurn(Intent i, Turn turn) {
        i.putExtra(IDs.ID, turn.getId());
        i.putExtra(IDs.DATA, turn.getDataRierimentoDateStr());
        i.putExtra(IDs.INIZIO_MATTINA, turn.getInizioMattina());
        i.putExtra(IDs.FINE_MATTINA, turn.getFineMattina());
        i.putExtra(IDs.INIZIO_POMERIGGIO, turn.getInizioPomeriggio());
        i.putExtra(IDs.FINE_POMERIGGIO, turn.getFinePomeriggio());
        i.putExtra(IDs.WEEK_ID, turn.getWeekId());
        i.putExtra(IDs.YEAR, turn.getYear());
        i.putExtra(IDs.MONTH, turn.getMounth());
        i.putExtra(IDs.ORE, turn.getHour());
        i.putExtra(IDs.OVERTIME, turn.getOvertime());
        i.putExtra(IDs.PRIORITY, turn.getIsImportante() ? 1 : 0);
        i.putExtra(IDs.DEL_REQ, turn.isOnlyDelete() ? 1 : 0);
        i.putExtra(IDs.GOOGLE_ID_MATTINA, turn.getGoogleCalendarIDMattina());
        i.putExtra(IDs.GOOGLE_ID_POMERIGGIO, turn.getGoogleCalendarIDPomeriggio());

        return i;
    }

    /**
     * metodo per controllare che un intervallo di tempo non sia null ("null:null" || null)
     *
     * @param interval
     * @return
     */
    public static boolean intervalIsNotNull(String interval) {
        return interval == null || "null:null".equals(interval) ? false : true;
    }


    /**
     * Controlla se il Tunr è null tramite la data di riferimento
     *
     * @return
     */
    public boolean isNull() {
        if (this.datariferimento == null)
            return true;
        else
            return false;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setHour(double hour) {
        this.hour = hour;
    }

    /**
     * Metodo per settare le ore effettuate calcolandole direttamente dagli orari inseriti
     */
    public void setHour() {
        double tmpHour = 0;
        if (inizioMattinaH != null && inizioMattinaM != null && fineMattinaH != null && fineMattinaM != null) {
            double tmpInizioMattina = inizioMattinaH + convertMinute(inizioMattinaM);
            double tmpFineMattina = fineMattinaH + convertMinute(fineMattinaM);
            tmpHour = tmpHour + (tmpFineMattina - tmpInizioMattina);
        }
        if (inizioPomeriggioH != null && inizioPomeriggioM != null && finePomeriggioH != null && finePomeriggioM != null) {
            double tmpInizioPomeriggio = inizioPomeriggioH + convertMinute(inizioPomeriggioM);
            double tmpFinePomeriggio = finePomeriggioH + convertMinute(finePomeriggioM);
            tmpHour = tmpHour + (tmpFinePomeriggio - tmpInizioPomeriggio);
        }
        this.hour = tmpHour;
    }

    public void setOvertime(double overtime) {
        this.overtime = overtime;
    }

    public void setIsImportante(boolean isImportante) {
        this.isImportante = isImportante;
    }

    public void setIsImportante(String isImportante) {
        this.isImportante = !"0".equals(isImportante);
    }

    // getter
    public int getId() {
        return id;
    }

    public void setId(String id) {
        this.id = Integer.parseInt(id);
    }

    public int getWeekId() {
        return weekId;
    }

    public void setWeekId(int weekId) {
        this.weekId = weekId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getDatariferimento() {
        return datariferimento;
    }

    /**
     * Metodo per settare la Data di Riferimento
     *
     * @param datariferimento
     */
    public void setDatariferimento(String datariferimento) {
        if (year == 0)
            year = new Integer(datariferimento.substring(6, 10)).intValue();
        if (mounth == 0)
            mounth = new Integer(datariferimento.substring(3, 5)).intValue();
        if (weekId == 0) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(PATTERN);
                Calendar selected = Calendar.getInstance();
                selected.setTime(sdf.parse(datariferimento));
                weekId = selected.get(Calendar.WEEK_OF_YEAR);
            } catch (Exception e) {
                weekId = 0;
            }
        }
        this.datariferimento = datariferimento;
    }

    public String getDataRierimentoDateStr() {
        return datariferimento;
    }

    public Calendar getDataRierimentoDate() {
        Calendar cal = Calendar.getInstance();
        try {
            Date data = new SimpleDateFormat(PATTERN).parse(datariferimento);
            cal.setTime(data);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return cal;
    }


    public Integer getInizioMattinaH() {
        return inizioMattinaH;
    }

    public void setInizioMattinaH(Integer inizioMattinaH) {
        this.inizioMattinaH = inizioMattinaH;
    }

    public Integer getFineMattinaH() {
        return fineMattinaH;
    }

    public void setFineMattinaH(Integer fineMattinaH) {
        this.fineMattinaH = fineMattinaH;
    }

    public Integer getFineMattinaM() {
        return fineMattinaM;
    }

    public void setFineMattinaM(Integer fineMattinaM) {
        this.fineMattinaM = fineMattinaM;
    }

    public Integer getInizioMattinaM() {
        return inizioMattinaM;
    }

    public void setInizioMattinaM(Integer inizioMattinaM) {
        this.inizioMattinaM = inizioMattinaM;
    }

    public Integer getInizioPomeriggioH() {
        return inizioPomeriggioH;
    }

    public void setInizioPomeriggioH(Integer inizioPomeriggioH) {
        this.inizioPomeriggioH = inizioPomeriggioH;
    }

    public Integer getFinePomeriggioH() {
        return finePomeriggioH;
    }

    public void setFinePomeriggioH(Integer finePomeriggioH) {
        this.finePomeriggioH = finePomeriggioH;
    }

    public Integer getInizioPomeriggioM() {
        return inizioPomeriggioM;
    }

    public void setInizioPomeriggioM(Integer inizioPomeriggioM) {
        this.inizioPomeriggioM = inizioPomeriggioM;
    }

    public Integer getFinePomeriggioM() {
        return finePomeriggioM;
    }

    public void setFinePomeriggioM(Integer finePomeriggioM) {
        this.finePomeriggioM = finePomeriggioM;
    }

    public double getHour() {
        return hour;
    }

    public void setHour(String hour) {
        try {
            this.hour = new Double(hour).doubleValue();
        } catch (Throwable t) {
            this.hour = 0;
        }
    }

    public double getOvertime() {
        return overtime;
    }

    public void setOvertime(String overtime) {
        this.overtime = Double.parseDouble(overtime);
    }

    public int getMounth() {
        return mounth;
    }

    public void setMounth(int mounth) {
        this.mounth = mounth;
    }

    public Boolean getIsImportante() {
        return isImportante;
    }

    public void setIsImportante(int isImportante) {
        this.isImportante = 0 != isImportante;
    }

    // setter personalizzati
    public void setIniziotMattina(String mattina) {
        if (mattina != null) {
            String[] singleTime = new String[2];
            singleTime = mattina.split(":");
            setInizioMattinaH(Integer.parseInt(singleTime[0]));
            setInizioMattinaM(Integer.parseInt(singleTime[1]));
        }
    }

    public void setIniziotPomeriggio(String pomeriggio) {
        if (pomeriggio != null) {
            String[] singleTime = new String[2];
            singleTime = pomeriggio.split(":");
            setInizioPomeriggioH(Integer.parseInt(singleTime[0]));
            setInizioPomeriggioM(Integer.parseInt(singleTime[1]));
        }
    }

    public String getInizioMattina() {
        if (inizioMattinaH != null && inizioPomeriggioM != null) {
            String h = inizioMattinaH.toString();
            String m = inizioMattinaM.toString();
            StringUtils.leftPad(h, 2, '0');
            StringUtils.rightPad(m, 2, '0');
            return h + ":" + m;
        } else
            return inizioMattinaH + ":" + inizioMattinaM;
    }

    public String getFineMattina() {
        if (fineMattinaH != null && fineMattinaM != null) {
            String h = fineMattinaH.toString();
            String m = fineMattinaM.toString();
            StringUtils.leftPad(h, 2, '0');
            StringUtils.rightPad(m, 2, '0');
            return h + ":" + m;
        } else
            return fineMattinaH + ":" + fineMattinaM;
    }

    public void setFineMattina(String mattina) {
        if (mattina != null) {
            String[] singleTime = new String[2];
            singleTime = mattina.split(":");
            setFineMattinaH(Integer.parseInt(singleTime[0]));
            setFineMattinaM(Integer.parseInt(singleTime[1]));
        }
    }

    public String getInizioPomeriggio() {
        if (inizioPomeriggioH != null && inizioPomeriggioM != null) {
            String h = inizioPomeriggioH.toString();
            String m = inizioPomeriggioM.toString();
            StringUtils.leftPad(h, 2, '0');
            StringUtils.rightPad(m, 2, '0');
            return h + ":" + m;
        } else
            return inizioPomeriggioH + ":" + inizioPomeriggioM;
    }

    public String getFinePomeriggio() {
        if (finePomeriggioM != null && finePomeriggioM != null) {
            String h = finePomeriggioH.toString();
            String m = finePomeriggioM.toString();
            StringUtils.leftPad(h, 2, '0');
            StringUtils.rightPad(m, 2, '0');
            return h + ":" + m;
        } else
            return finePomeriggioH + ":" + finePomeriggioM;
    }

    public void setFinePomeriggio(String pomeriggio) {
        if (pomeriggio != null) {
            String[] singleTime = new String[2];
            singleTime = pomeriggio.split(":");
            setFinePomeriggioH(Integer.parseInt(singleTime[0]));
            setFinePomeriggioM(Integer.parseInt(singleTime[1]));
        }
    }

    private double convertMinute(int minute) {
        switch (minute) {
            case 15:
                return 0.25;
            case 30:
                return 0.50;
            case 45:
                return 0.75;
            default:
                return 0;
        }
    }

    public String getGoogleCalendarIDMattina() {
        return googleCalendarIDMattina;
    }

    public void setGoogleCalendarIDMattina(String googleCalendarID) {
        this.googleCalendarIDMattina = googleCalendarID;
    }

    public String getGoogleCalendarIDPomeriggio() {
        return googleCalendarIDPomeriggio;
    }

    public void setGoogleCalendarIDPomeriggio(String googleCalendarID) {
        this.googleCalendarIDPomeriggio = googleCalendarID;
    }

    public String getGoogleCalendarIDMattina2Rem() {
        return googleCalendarIDMattina2Rem;
    }

    public void setGoogleCalendarIDMattina2Rem(String googleCalendarIDMattina2Rem) {
        this.googleCalendarIDMattina2Rem = googleCalendarIDMattina2Rem;
    }

    public String getGoogleCalendarIDPomeriggio2Rem() {
        return googleCalendarIDPomeriggio2Rem;
    }

    public void setGoogleCalendarIDPomeriggio2Rem(String googleCalendarIDPomeriggio2Rem) {
        this.googleCalendarIDPomeriggio2Rem = googleCalendarIDPomeriggio2Rem;
    }

    public boolean isOnlyDelete() {
        return isOnlyDelete;
    }

    public void setIsOnlyDelete(boolean isOnlyDelete) {
        this.isOnlyDelete = isOnlyDelete;
    }

    public void setIsOnlyDelete(int isOnlyDelete) {
        this.isOnlyDelete = isOnlyDelete != 0;
    }
}
