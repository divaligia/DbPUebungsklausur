package com.campus02.solution;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbHelper {

    private Connection conn = null;

// Aufgabe 3) (5 Punkte)
// Definieren Sie in der Klasse DBHelper eine private Member-Variable vom
// Typ Connection. Implementieren Sie die nachstehende Methode: public void Init()
// Diese Methode soll eine DB-Verbindung aufbauen.
// Instanziieren Sie die con-Variable mit den Pfad auf Ihre SQLite Datenbank

    public void init(){
        String connString = "jdbc:sqlite:klausurdb";

        try {
            conn = DriverManager.getConnection(connString);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

// Aufgabe 3a(5 Punkte)
// Erstellen Sie zwei Methoden zum Erzeugen der Tabellen Kunden und Rechnungen
// mit der folgenden Struktur. Erstellen Sie außerdem eine Methode zum Befüllen
// der Tabellenwerte mit Beispielwerten.

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

// Hilfsmethode um zu prüfen ob Tabelle bereits erstellt wurde
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

// Aufgabe 4a) (10 Punkte)
// Implementieren Sie die nachstehende Methode public Kunde getKunde(int kdnr)
// Laden Sie den Kunden mit der übergebenen kdnr und liefern Sie dieses Objekt
// an den Aufrufer zurück. Falls der Kunde nicht gefunden wurde, soll eine
// entsprechende Meldung ausgegeben werden.

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
            } else try {
                throw new KundeDoesNotExistException();
            } catch (KundeDoesNotExistException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kunde;
    }
// Aufgabe 4b)
// Implementieren Sie die nachstehende Methode: public List<Kunde> getAlleKunden()
// Liefert alle Kunden retour. Falls das Geschlecht NULL ist, soll es im
// Java-Object durch den Wert „nicht definiert“ ersetzt werden.
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

// Aufgabe 5a) (10 Punkte)
// Implementieren Sie die nachstehende Methode: public int insertKunde(Kunde neuerKunde)
// Diese Methode fügt den übergebenen Kunden zu der Kundentabelle hinzu und liefert als
// Ergebnis den neu vergebenen Autowert –SELECT last_insert_rowid()

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

// Aufgabe 5b)
// Implementieren Sie die nachstehende Methode: public void updateKunde(Kunde kunde)
// Diese Methode aktualisiert den übergebenen Kunden. Falls der Kunden nicht gefunden
// wurde, soll eine Meldung / Exception geworfen werden.
// UPDATE Kunde SET Vorname=?, Nachname=?, Geschelcht=?, Bonuspunkte=? WHERE KDNR = ?
// .executeUpdate() - 0 --- Der Kunde existiert nicht
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
            } else System.out.println("Kundendaten wurden verändert!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

// Aufgabe 6a) (10 Punkte)
// Implementieren Sie die nachstehende Methode: public int insertRechnung(Rechnung neueRechnung, Kunde vorhandenerKunde)
// Diese Methode fügt zu einem bereits bestehenden Kunden eine neue Rechnung hinzu.
// Rückgabewert ist die neue Rechnungsnummer.
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

// Aufgabe 6b) (10 Punkte)
// Implementieren Sie die nachstehende Methode:
// public void insertKundeUndRechnungen(ArrayList<Rechnung> neueRechnungen, Kunde neuerKunde)
// Diese Methode fügt zuerst einen neuen Kunden hinzu. Danach werden zu diesem
// neu hinzugefügten Kunden auch die übergebenen Rechnungen in der DB gespeichert.
    public void insertKundeUndRechnungen(ArrayList<Rechnung> neueRechnungen, Kunde neuerKunde){
        insertKunde(neuerKunde);
        for(Rechnung r : neueRechnungen){
            insertRechnung(r, neuerKunde);
        }
    }

// Aufgabe 6c)
// Implementieren Sie die nachstehende Methode:  public void updateRechnung(Rechnung neueRechnung)
// Diese Methode aktualisiert die übergebene Rechnung
    public void updateRechnung(Rechnung neueRechnung){
        String update = "UPDATE Rechnungen SET Datum = ?, Gesamtbetrag = ?, KDNRfk = ? WHERE reNr = ?;";
        try {
            PreparedStatement pstm = conn.prepareStatement(update);
            pstm.setString(1, neueRechnung.getDatum());
            pstm.setDouble(2, neueRechnung.getGesamtbetrag());
            pstm.setInt(3, neueRechnung.getKDNRfk());
            pstm.setInt(4, neueRechnung.getReNr());
            pstm.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

// Aufgabe 6d)
// Implementieren Sie die nachstehende Methode: public List<Rechnung> getRechnungenByKunde(int kdnr)
// Diese Methode liefert alle Rechnungen des Kunden mit der übergebenen Kundennummer
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

// Aufgabe 7) (10 Punkte)
// Implementieren Sie die nachstehende Methode: public ArrayList<Kunde> getWeiblicheKunden()
// Diese Methode liefert eine ArrayListe mit allen weiblichen Kunden zurück
    public ArrayList<Kunde> getWeiblicheKunden(){
        ArrayList<Kunde> frauen = new ArrayList<>();
        String select = "SELECT KDNR, Vorname, Nachname, Geschlecht, Bonuspunkte FROM Kunden WHERE Geschlecht ='Frau';";

        try {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(select);
            Kunde kunde = null;
            while(rs.next()){
                kunde = new Kunde(rs.getInt(1), rs.getString(2), rs.getString(3),rs.getString(4),rs.getDouble(5));
                frauen.add(kunde);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return frauen;
    }

// Aufgabe 8) (10 Punkte)
// Implementieren Sie die nachstehende Methode: public Kunde getKundeMitDenMeistenBonusPunkten()
// Diese Methode liefert als Ergebnis den Kunden mit den meisten Bonuspunkten zurück.
// Sollten mehrere Kunden die gleichen Bonuspunkte haben wird einfach der erste zurückgeliefert
    public Kunde getKundeMitDenMeistenBonusPunkten(){
        Kunde k = null;
        String select = "SELECT  KDNR, Vorname, Nachname, Geschlecht, MAX(Bonuspunkte) FROM Kunden ;";

        try {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(select);
            while(rs.next()) {
                k = new Kunde(rs.getInt(1), rs.getString(2),
                        rs.getString(3), rs.getString(4), rs.getDouble(5));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return k;
    }

//    public List<Kunde> getKundeMitDenMeistenBonusPunkten() {
//        List<Kunde>max = new ArrayList<>();
//        String maxi = "SELECT * FROM Kunden ORDER BY Bonuspunkte DESC;";
//        try {
//            Statement statement = conn.createStatement();
//            ResultSet rs = statement.executeQuery(maxi);
//            rs.next();
//            int points = rs.getInt("Bonuspunkte");
//            System.out.println("Maximum points is " + points);
//            ResultSet rs1 = conn.createStatement().executeQuery("SELECT * FROM Kunden WHERE Bonuspunkte = " + points + ";");
//            Kunde k = null;
//            while (rs1.next()){
//                k = new Kunde(rs1.getInt("KDNR"), rs1.getString("Vorname"), rs1.getString("Nachname"), rs1.getString("Geschlecht"), rs1.getInt("Bonuspunkte"));
//                max.add(k);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return max;
//    }



// Aufgabe 9) (10 Punkte)
// Implementieren Sie die nachstehende Methode: public void loescheAlleRechnungenUndDanachDenKunden(Kunde k)
// Zuerst werden alle Rechnungen zum übergebenen Kunden gelöscht, danach wird der Kunde selbst gelöscht
//* Falls Fehler auftreten muss die Transaktion zurückgerollt werden

    public int deleteAllRechnungenUndDanachDenKunden(Kunde k){
        int records = 0;
        String deleteRechnung = "DELETE FROM Rechnungen WHERE KDNRfk = ?;";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(deleteRechnung);
            preparedStatement.setInt(1, k.getKDNR());
            records = preparedStatement.executeUpdate();
            System.out.println(records + " rechnungen have been deleted");
            deleteKunde(k);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }
    public int deleteKunde(Kunde k){
        int records = 0;
        String deleteKunde = "DELETE FROM Kunden WHERE KDNR = ?;";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = conn.prepareStatement(deleteKunde);
            preparedStatement.setInt(1, k.getKDNR());
            records = preparedStatement.executeUpdate();
            System.out.println(records + " customer has been deleted");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }


// Aufgabe 10) (10 Punkte)
// Implementieren Sie die nachstehende Methode: public void PrintKundenMetadata()
// Diese Methode soll mithilfe von JDBC-Metadaten alle Spalten und den Datentypen
// der Tabelle Kunden ausgeben.
    public void KundenAlsTabelleAusgeben() {
        String selectAll = "SELECT * FROM Kunden;";
        try {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(selectAll);
            int colums = rs.getMetaData().getColumnCount();
            for (int i = 1; i <= colums; i++) {
                System.out.print(String.format("%-17s", rs.getMetaData().getColumnLabel(i)));
            }
            System.out.println();
            System.out.println("---------------------------------------------------------------");
            while (rs.next()) {
                for (int i = 1; i <= colums; i++) {
                    System.out.print(String.format("%-17s", rs.getString(i)));
                }
                System.out.println();
            }
            System.out.println();
            rs.close();
            stm.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void getMetaData() {
        try {
            DatabaseMetaData databaseMetaData = conn.getMetaData();
            String table[] = {"Table"};
            ResultSet rs = databaseMetaData.getTables(null, null, null, table);
            System.out.println("List of Tables");
            while (rs.next()){
                String tablename = rs.getString(3);
                System.out.println("Table Name : " + tablename);
                String select = "SELECT * FROM " + tablename + ";";
                ResultSetMetaData resultSetMetaData = conn.createStatement().executeQuery(select).getMetaData();
                int columns = resultSetMetaData.getColumnCount();
                for (int i = 1; i<=columns; i++)
                    System.out.println(resultSetMetaData.getColumnName(i) + " (" + resultSetMetaData.getColumnTypeName(i) + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void displayMetaData(){
        try
        {
            DatabaseMetaData dbmd=conn.getMetaData();

            System.out.println("Driver Name: "+dbmd.getDriverName());
            System.out.println("Driver Version: "+dbmd.getDriverVersion());
            System.out.println("UserName: "+dbmd.getUserName());
            System.out.println("Database Product Name: "+dbmd.getDatabaseProductName());
            System.out.println("Database Product Version: "+dbmd.getDatabaseProductVersion());
            String table[]={"TABLE"};
            ResultSet rs=dbmd.getTables(null,null,null,table);

            System.out.println("Details zu den Tabellen");
            while(rs.next()){
                String tableName = rs.getString(3);
                System.out.println();
                System.out.println("Tabelle: " + tableName);
                ResultSet rsMeta = conn.createStatement().executeQuery( "SELECT * FROM " + tableName );
                ResultSetMetaData meta = rsMeta.getMetaData();

                int numerics = 0;

                for ( int i = 1; i <= meta.getColumnCount(); i++ )
                {
                    System.out.printf( "%-20s %-20s%n", meta.getColumnLabel( i ),
                            meta.getColumnTypeName( i ) );

                    if ( meta.isSigned( i ) )
                        numerics++;
                }


                System.out.println( "Spalten: " + meta.getColumnCount() +
                        ", Numerisch: " + numerics );
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}

