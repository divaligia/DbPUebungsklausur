package com.campus02.solution;
// Aufgabe 1) (10 Punkte)
// Erstellen Sie ein neues Projekt mit dem Namen „PrjKlausurvorbereitung“.
// Erstellen Sie eine Klasse „Kunde“ und eine Klasse „Rechnungen“ mit den
// erforderlichen privaten Membervariablen und den dazu gehörigen Gettern und Settern.
// (Das Datum kann als normaler String behandelt werden)

public class Kunde {
    private int KDNR;
    private String vorname;
    private String nachname;
    private String geschlecht;
    private double bonuspunkte;

    public Kunde(int KDNR, String vorname, String nachname, String geschlecht, double bonuspunkte) {
        this.KDNR = KDNR;
        this.vorname = vorname;
        this.nachname = nachname;
        this.geschlecht = geschlecht;
        this.bonuspunkte = bonuspunkte;
    }


    public Kunde(String vorname, String nachname, String geschlecht, double bonuspunkte) {
        this.vorname = vorname;
        this.nachname = nachname;
        this.geschlecht = geschlecht;
        this.bonuspunkte = bonuspunkte;
        this.KDNR = 0;
    }

    public int getKDNR() {
        return KDNR;
    }

    public void setKDNR(int KDNR) {
        this.KDNR = KDNR;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public String getGeschlecht() {
        return geschlecht;
    }

    public void setGeschlecht(String geschlecht) {
        this.geschlecht = geschlecht;
    }

    public double getBonuspunkte() {
        return bonuspunkte;
    }

    public void setBonuspunkte(double bonuspunkte) {
        this.bonuspunkte = bonuspunkte;
    }

    @Override
    public String toString() {
        return "Kunde{" +
                "KDNR=" + KDNR +
                ", vorname='" + vorname + '\'' +
                ", nachname='" + nachname + '\'' +
                ", geschlecht='" + geschlecht + '\'' +
                ", bonuspunkte=" + bonuspunkte +
                '}';
    }
}
