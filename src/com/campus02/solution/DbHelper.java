package com.campus02.solution;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbHelper {

    private Connection conn = null;

    public void init(){
        String connString = "jdbc:sqlite:klausurdb";

        try {
            conn = DriverManager.getConnection(connString);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createKundenTable(){
        String createKundenTable = "CREATE TABLE Kunden(KDNR INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " Vorname VARCHAR(20), Nachname VARCHAR(50), Geschlecht VARCHAR(10), Bonuspunkte decimal(5,2));";
        try {
            Statement stm = conn.createStatement();
            if(!tableExists("Kunden")){
                stm.executeUpdate(createKundenTable);
                System.out.println("Table Kunden created");
            } else
                System.out.println("Table Kunden already exists!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createRechnungTable(){
        String createRechnungenTable = "CREATE TABLE Rechnungen(reNr INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " Datum VARCHAR(20), Gesamtbetrag decimal (8,2), KDNRfk INTEGER," +
                " FOREIGN KEY (KDNRfk) REFERENCES Kunden (KDNR));";
        try {
            Statement stm = conn.createStatement();
            if(!tableExists("Rechnungen")){
                stm.executeUpdate(createRechnungenTable);
                System.out.println("Table Rechnungen created");
            } else
                System.out.println("Table Rechnungen already exists!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean tableExists(String tablename){
        String check = "SELECT * FROM sqlite_master WHERE type = 'table' and name= ?;";
        boolean exists = false;
        try {
            PreparedStatement pstm = conn.prepareStatement(check);
            pstm.setString(1,tablename);
            // !!!! BEI SELECT IMMER QUERY ausführen
            ResultSet rs = pstm.executeQuery();

            if(rs.next()){
                exists = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exists;
    }

    public int insertKunde(Kunde neuerKunde){
        int KDNR = 0;
        String insert = "INSERT INTO Kunden (Vorname, Nachname, Geschlecht, Bonuspunkte) VALUES (?,?,?,?);";

        try {
            PreparedStatement pstm = conn.prepareStatement(insert);
            pstm.setString(1, neuerKunde.getVorname());
            pstm.setString(2, neuerKunde.getNachname());
            pstm.setString(3, neuerKunde.getGeschlecht());
            pstm.setDouble(4, neuerKunde.getBonuspunkte());
            pstm.executeUpdate();
            String lastrow = "SELECT last_insert_rowid();";
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(lastrow);
            rs.next();
            // Damit der erstellte Kunde auch gleich die KDNR zugewiesen bekommt
            // diese am besten gleich hier einfügen
            KDNR = rs.getInt(1);
            neuerKunde.setKDNR(KDNR);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return KDNR;
    }

    public void updateKunde(Kunde kunde){
        String update = "UPDATE Kunden SET Vorname = ?, Nachname = ?, Geschlecht = ?, Bonuspunkte = ? WHERE KDNR = ?;";
        try {
            PreparedStatement pstm = conn.prepareStatement(update);
            pstm.setString(1, kunde.getVorname());
            pstm.setString(2, kunde.getNachname());
            pstm.setString(3, kunde.getGeschlecht());
            pstm.setDouble(4, kunde.getBonuspunkte());
            pstm.setInt(5, kunde.getKDNR());
            // Hilfsinteger um herauszufinden, ob etwas verändert wurde:
            // wenn kein datensatz verändert wurde, ist int 0 --> Exception geworfen
            int count = pstm.executeUpdate();
            if(count == 0){
                try {
                    throw new KundeDoesNotExistException();
                } catch (KundeDoesNotExistException e) {
                    e.printStackTrace();
                }
            } else System.out.println("Kundendaten wurden verändert");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Kunde getKunde(int KDNR){
        Kunde kunde = null;
        String select = "SELECT * FROM Kunden WHERE KDNR = ?;";

        try {
            PreparedStatement pstm = conn.prepareStatement(select);
            pstm.setInt(1,KDNR);
            ResultSet rs = pstm.executeQuery();
            if(rs.next()){
                kunde = new Kunde(rs.getInt(1), rs.getString(2),
                        rs.getString(3),rs.getString(4),rs.getDouble(5));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kunde;
    }

    public List<Kunde> getAlleKunden(){
        List<Kunde> kunden = new ArrayList<>();
        String select = "SELECT *, ifnull(Geschlecht, 'Nicht Definiert') AS nullgeschlecht FROM Kunden;";

        try {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(select);
            Kunde kunde = null;
            while(rs.next()){
                if(rs.getString("nullgeschlecht").equals("Nicht Definiert")) {
                    kunde = new Kunde(rs.getInt(1), rs.getString(2), rs.getString(3),rs.getString("nullgeschlecht"),rs.getDouble(5));
                } else
                kunde = new Kunde(rs.getInt(1), rs.getString(2), rs.getString(3),rs.getString(4),rs.getDouble(5));
                kunden.add(kunde);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kunden;
    }

    public int insertRechnung(Rechnung neueRechnung, Kunde vorhandenerKunde){
        int reNr = 0;
        String insert = "INSERT INTO Rechnungen(Datum, Gesamtbetrag, KDNRfk) VALUES (?,?,?);";

        try {
            // neue rechnung in db stecken
            PreparedStatement pstm = conn.prepareStatement(insert);
            pstm.setString(1,neueRechnung.getDatum());
            pstm.setDouble(2,neueRechnung.getGesamtbetrag());
            pstm.setInt(3,vorhandenerKunde.getKDNR());
            pstm.executeUpdate();
            // rechnung dem vorhandenen kunden zuweisen
            String lastrow = "SELECT last_insert_rowid();";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(lastrow);
            rs.next();
            neueRechnung.setReNr(reNr);
            reNr = rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reNr;
    }

    public List<Rechnung> getRechnungenByKunde(int kdnr){
        List<Rechnung> rechnungen = new ArrayList<>();

        String select = "SELECT * FROM Rechnungen WHERE KDNRfk = ?;";

        try {
            PreparedStatement pstm = conn.prepareStatement(select);
            pstm.setInt(1, kdnr);
            ResultSet rs = pstm.executeQuery();
            Rechnung re = null;
            while (rs.next()){
                re = new Rechnung(rs.getInt(1), rs.getString(2), rs.getDouble(3), rs.getInt(4));
                rechnungen.add(re);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rechnungen;
    }

    public void insertKundeUndRechnungen(ArrayList<Rechnung> neueRechnungen, Kunde neuerKunde){
        insertKunde(neuerKunde);
        for(Rechnung r : neueRechnungen){
            insertRechnung(r, neuerKunde);
        }

    }

    public void updateRechnung(Rechnung neueRechnung){
        String update = "UPDATE Rechnungen SET reNr = ?, Datum = ?, Gesamtbetrag = ?, Bonuspunkte = ? WHERE KDNR = ?;";

    }

}
