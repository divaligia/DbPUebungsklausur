package com.campus02.solution;

public class Rechnung {
    private int reNr;
    private String datum;
    private double gesamtbetrag;
    private int KDNRfk;

    public Rechnung(int reNr, String datum, double gesamtbetrag, int KDNRfk) {
        this.reNr = reNr;
        this.datum = datum;
        this.gesamtbetrag = gesamtbetrag;
        this.KDNRfk = KDNRfk;
    }

    public Rechnung(String datum, double gesamtbetrag) {
        this.datum = datum;
        this.gesamtbetrag = gesamtbetrag;
        this.KDNRfk = KDNRfk;
    }

    public int getReNr() {
        return reNr;
    }

    public void setReNr(int reNr) {
        this.reNr = reNr;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public double getGesamtbetrag() {
        return gesamtbetrag;
    }

    public void setGesamtbetrag(double gesamtbetrag) {
        this.gesamtbetrag = gesamtbetrag;
    }

    public int getKDNRfk() {
        return KDNRfk;
    }

    public void setKDNRfk(int KDNRfk) {
        this.KDNRfk = KDNRfk;
    }

    @Override
    public String toString() {
        return "Rechnung{" +
                "reNr=" + reNr +
                ", datum='" + datum + '\'' +
                ", gesamtbetrag=" + gesamtbetrag +
                ", KDNRfk=" + KDNRfk +
                '}';
    }
}
