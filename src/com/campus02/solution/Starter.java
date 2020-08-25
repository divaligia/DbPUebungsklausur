package com.campus02.solution;

import java.util.ArrayList;
import java.util.List;

public class Starter {
    public static void main(String[] args) {

        DbHelper myHelper = new DbHelper();
        myHelper.init();
        myHelper.createKundenTable();
        myHelper.createRechnungTable();

        Kunde k1 = new Kunde("Valentina", "Krammer", "Frau", 2);
        int kundenNumer = myHelper.insertKunde(k1);

        System.out.println(k1);

        k1.setNachname("Bers");
        myHelper.updateKunde(k1);

        Kunde k2 = new Kunde("David", "Hasselhoff", null, 25.9);
        myHelper.insertKunde(k2);

        List<Kunde> kunden = myHelper.getAlleKunden();
        for(Kunde k : kunden){
            System.out.println(k);
        }

        Rechnung r1 = new Rechnung("20.08.2020", 320);
        myHelper.insertRechnung(r1, k1);

        List<Rechnung> rechnungen = myHelper.getRechnungenByKunde(k1.getKDNR());
        for(Rechnung r : rechnungen){
            System.out.println(r);
        }

        ArrayList<Rechnung> rechnungenKD = new ArrayList<>();
        rechnungenKD.add(new Rechnung("22.08.2020", 30.5));
        rechnungenKD.add(new Rechnung("26.08.2020", 300.5));
        rechnungenKD.add(new Rechnung("12.08.2020", 380.5));
        rechnungenKD.add(new Rechnung("22.08.2020", 60.5));

        Kunde kunde = new Kunde("Bärbel", "Wastl", "Frau", 580.7);

        myHelper.insertKundeUndRechnungen(rechnungenKD, kunde);

        System.out.println("kunde = " + kunde.getVorname() + " " + kunde.getNachname());
        List<Rechnung> rechnungen3 = myHelper.getRechnungenByKunde(kunde.getKDNR());
        for(Rechnung r : rechnungen3){
            System.out.println(r);
        }
    }
}
