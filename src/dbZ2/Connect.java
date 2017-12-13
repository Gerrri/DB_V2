package dbZ2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import oracle.jdbc.pool.OracleDataSource;

public class Connect {
	// DATENBANK VERBINDUNG

	static Connection con = null;

	public void connect() throws IOException, SQLException {
		String treiber;
		OracleDataSource ods = new OracleDataSource();

		treiber = "oracle.jdbc.driver.OracleDriver";

		String uName;
		String pW;

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Please enter username: ");
		uName = in.readLine();
		System.out.println("Please enter password: ");
		pW = in.readLine();

		// Treiber laden

		try {

			Class.forName(treiber).newInstance();
		} catch (Exception e) {
			System.out.println("Fehler beim laden des Treibers: " + e.getMessage());
		}

		// Erstellung Datenbank-Verbindungsinstanz
		try {

			ods.setURL("jdbc:oracle:thin:" + uName + "/" + pW + "@schelling.nt.fh-koeln.de:1521:xe");
			con = ods.getConnection();
		} catch (SQLException e) {
			System.out.println("Fehler beim Verbindungsaufbau zur Datenbank!");
			System.out.println(e.getMessage());
		}
		pW = "";
		uName = "";
	}

	// SQL METHODEN
	public int jdbcInsert() throws FileNotFoundException, IOException, SQLException {

		FileReader fr1 = new FileReader("ARTIKEL.CSV");
		BufferedReader br1 = new BufferedReader(fr1);

		int inserts = 0;
		String csvTemp;

		String artnr, artbez, mge, prei, st;

		while ((csvTemp = br1.readLine()) != null) {
			artnr = csvTemp.split(";")[0];
			artbez = csvTemp.split(";")[1];
			mge = csvTemp.split(";")[2];
			prei = csvTemp.split(";")[3];
			st = csvTemp.split(";")[4];
			DateFormat df = new SimpleDateFormat("dd-MM-yyyy");

			String date = df.format(Calendar.getInstance().getTime());

			try {
				inserts += executeSQL("INSERT INTO ARTIKEL VALUES(" + artnr + ",'" + artbez + "'," + mge + ",'" + prei
						+ "','" + st + "',TO_DATE ('" + date + "','DD-MM-YYYY'))");

			} catch (SQLException a) {
				System.out.println("Exception geworfen!\n" + a.getMessage());
				br1.close();

			}

		}
		br1.close();
		return inserts;
	}

	public int executeSQL(String sql) throws SQLException {
		Statement Stmt;

		int tempRS;
		// Erzeugen eines Statements aus der DB-Verbindung
		Stmt = con.createStatement();
		// SQL-Anweisung ausf�hren und Ergebnis in ein ResultSet schreiben
		try {
			tempRS = Stmt.executeUpdate(sql);
			return tempRS;
		} catch (SQLException e) {
			System.out.println(e.getMessage() + "\nFalsche Werte eingegeben!");
			return 0;
		}

	}

	public ResultSet executeSQLquery(String sql, String tab) throws SQLException {
		Statement Stmt;

		ResultSet tempRS = null;

		// Erzeugen eines Statements aus der DB-Verbindung
		Stmt = con.createStatement();
		try {
			// SQL-Anweisung ausf�hren und Ergebnis in ein ResultSet schreiben
			tempRS = Stmt.executeQuery(sql + tab);
			return tempRS;
		} catch (SQLException se) {
			System.out.println(se.getMessage() + "\n�berpr�fen sie ihre Eingaben!");
			return tempRS;
		}

	}

	public void rsMetaAusgabe(ResultSet RS) throws SQLException, NullPointerException {
		try {
			ResultSetMetaData rsmd = RS.getMetaData();
			System.out.println("\n\n");
			String columnNames = "";
			String data = "";
			int a = 0;
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				columnNames += rsmd.getColumnName(i) + "\t\t\t";
			}

			while (RS.next()) {
				data = "";
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					if (RS.getString(i).length() >= 8) {
						if (RS.getString(i).length() >= 16) {
							data += RS.getString(i) + "\t";
						} else {
							data += RS.getString(i) + "\t\t";
						}
					} else {
						data += RS.getString(i) + "\t\t\t";
					}
				}
				if (a == 0) {
					System.out.println(columnNames);
					a++;
				}
				System.out.println(data);
			}
			RS.close();
		} catch (NullPointerException e) {
			System.out.println("Artikel nicht vorhanden oder nicht im Bestand!");
		}
	}

	public int sqlHandler(int c, String csv) throws SQLException, NumberFormatException, IOException {
		switch (c) {

		case 1:
			String sALL = "SELECT * FROM ";
			if (csv.equals("ARTIKEL")) {
				rsMetaAusgabe(executeSQLquery(
						"SELECT ARTIKEL.ARTNR, ARTIKEL.ARTBEZ, ARTIKEL.MGE , ARTIKEL.PREIS, ARTIKEL.STEU, TO_CHAR(ARTIKEL.EDAT,'DD-MM-YYYY') EDAT FROM ",
						"ARTIKEL"));
				return 1;
			} else if (csv.equals("LAGER")) {

				rsMetaAusgabe(executeSQLquery(sALL, "LAGER"));
				return 1;
			} else if (csv.equals("KUNDE")) {

				rsMetaAusgabe(executeSQLquery(sALL, "KUNDE"));
				return 1;
			}
			break;

		case 2:// suche nach artnr
			try {

				int t = Integer.parseInt(csv);
				csv = String.valueOf(t);

			} catch (NumberFormatException n) {

				System.out.println("Keine g�ltige Artikel Nummer\n" + n.getMessage());
				return 0;

			}
			try {
				rsMetaAusgabe(executeSQLquery(
						"SELECT ARTIKEL.ARTNR, ARTIKEL.ARTBEZ, ARTIKEL.MGE, ARTIKEL.PREIS, ARTIKEL.STEU, TO_CHAR(ARTIKEL.EDAT,'DD-MM-YYYY') EDAT , LAGERBESTAND.BSTNR, LAGERBESTAND.LNR, LAGERBESTAND.MENGE, LAGER.LORT, LAGER.LPLZ FROM ARTIKEL,LAGERBESTAND,LAGER ",
						("WHERE ARTIKEL.ARTNR=" + csv + " AND LAGERBESTAND.ARTNR=" + csv
								+ " AND LAGER.LNR= LAGERBESTAND.LNR")));
				if (sumi(csv)==-1) {
					System.out.println("Artikel nicht vorhanden");
					return 0;
				}else {
				System.out.println("Gesamtbestand: " + (sumi(csv)));
				return 1;
				}
			} catch (SQLException se) {
				System.out.println("SQL FEHLER" + se.getMessage());
				return 0;
			}

		case 3: // neuer lagerbestand

			String[] csvA = csv.split(";");
			try {

				int a = Integer.parseInt(csvA[0]), l = Integer.parseInt(csvA[1]), m = Integer.parseInt(csvA[2]);
				csvA[0] = String.valueOf(a);
				csvA[1] = String.valueOf(l);
				csvA[2] = String.valueOf(m);

			} catch (NumberFormatException n) {

				System.out.println("Keine g�ltige Eingabe: " + n.getMessage());
				return -2;
			}

			return executeSQL(
					"INSERT INTO LAGERBESTAND VALUES (null, " + csvA[0] + ", " + csvA[1] + ", " + csvA[2] + ")");

		case 4: // update bestand
			String[] csvA2 = csv.split(";");
			try {

				int a = Integer.parseInt(csvA2[0]), l = Integer.parseInt(csvA2[1]), m = Integer.parseInt(csvA2[2]);

				csvA2[0] = String.valueOf(a);
				csvA2[1] = String.valueOf(l);
				csvA2[2] = String.valueOf(m);

			} catch (NumberFormatException n) {

				System.out.println("Keine g�ltige Eingabe: " + n.getMessage());
				return 0;
			}
			return executeSQL("UPDATE LAGERBESTAND SET LAGERBESTAND.MENGE = " + csvA2[2] + " WHERE LAGERBESTAND.ARTNR= "
					+ csvA2[0] + " AND LAGERBESTAND.LNR= " + csvA2[1]);

		case 5: // neuer KUBEST Eintrag

			String[] csvKB = csv.split(";");// 0 KunNr 1 ArtNr 2 BMenge

			// create Calendar instance with actual date
			Date now = new Date();
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(now);

			// add 14 days to calendar instance
			calendar.add(Calendar.DAY_OF_MONTH, 14);

			// get the new date instance
			Date future = calendar.getTime();

			// print out the dates...
			DateFormat df = new SimpleDateFormat("dd-MM-yyyy");

			String bestDate = df.format(now);

			String liefDate = df.format(future);

			// RECHNUNGSBETRAG ERMITTELN

			ResultSet temp = executeSQLquery("SELECT ARTIKEL.PREIS FROM ARTIKEL WHERE ARTIKEL.ARTNR=", csvKB[1]);
			temp.next();
			double preis = Float.valueOf(temp.getString(1));
			preis *= Double.valueOf(csvKB[2]);
			BigDecimal myDec = new BigDecimal(preis);
			myDec = myDec.setScale(2, BigDecimal.ROUND_HALF_UP);
			preis = myDec.doubleValue();

			// Eintrag machen
			executeSQL("INSERT INTO KUBEST VALUES (null," + csvKB[0] + ", " + csvKB[1] + ", " + csvKB[2] + ",TO_DATE ('"
					+ bestDate + "','DD-MM-YYYY'),TO_DATE ('" + liefDate + "','DD-MM-YYYY'),1," + preis + ")");

			// Rechnung erstellen

			return rechnung(csv + ";" + liefDate + ";" + preis);
		}
		return 0;

	}

	private int sumi(String artNr) throws SQLException {
		try {
			ResultSet r = executeSQLquery("SELECT SUM (LAGERBESTAND.MENGE) Gesamtbestand  FROM ",
					"LAGERBESTAND WHERE LAGERBESTAND.ARTNR= " + artNr);

			r.next();
			return Integer.parseInt(r.getString(1));

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return -1;
		} catch (Exception n) {
			System.out.println("Es ist ein Fehler aufgetreten");
			System.out.println(n.getMessage());
			return -1;
		}
	}

	public boolean jdbcBestellung(String csvKb) throws SQLException, IOException {
		String[] csvKB = csvKb.split(";");

		// Gesamtbestand pr�fen

		try {

			int kunNr = Integer.parseInt(csvKB[0]), artnr = Integer.parseInt(csvKB[1]),
					bestellmenge = Integer.parseInt(csvKB[2]);

			csvKB[0] = String.valueOf(kunNr);
			csvKB[1] = String.valueOf(artnr);
			csvKB[2] = String.valueOf(bestellmenge);

			int gesamtBestand = sumi(csvKB[1]);
			
			
			
			if (bestellmenge <= gesamtBestand) {

				// Best�nde erfassen

				ResultSet s = executeSQLquery("SELECT LAGERBESTAND.MENGE, LAGERBESTAND.LNR FROM LAGERBESTAND ",
						("WHERE LAGERBESTAND.ARTNR=" + artnr));

				LinkedList<int[]> bestaende = new LinkedList<int[]>();
				while (s.next()) {
					int[] bestand = new int[] { Integer.valueOf(s.getString(1)), Integer.valueOf(s.getString(2)) };
					bestaende.add(bestand);
				}

				// best�nde liste nach menge sortieren
				// Collections.sort(bestaende);
				Collections.sort(bestaende, new Comparator<int[]>() {
					public int compare(int[] a, int[] b) {
						if (b[0] - a[0] == 0) // if equals
						{
							return a[1] - b[1];// recompare
						} else
							return b[0] - a[0];
					}
				});

				// f�r alle Lagerbest�nde pr�fen ob die bestellung aus einem Lager beliefert
				// werden kann

				if ((bestaende.get(0)[0]) >= Integer.parseInt(csvKB[2])) {
					// SQL Handler Bestand update mit bestaende[0] - csvKB[2] (bestellmenge) auf
					// Lagernnr best�nde[1]
					int neu = (bestaende.get(0)[0]) - (Integer.parseInt(csvKB[2]));
					String neuS = String.valueOf(neu);
					String updateCSV = csvKB[1] + ";" + (bestaende.get(0)[1]) + ";" + neuS;
					sqlHandler(4, updateCSV);
					// neuer Eintrag in Kubest tabelle

					sqlHandler(5, csvKb);
					return true;
				} else {
					int bmeng_temp = bestellmenge; // tempor�r bestellmenge
					for (int best = 0; best < bestaende.size(); best++) {

						// Bestellung auf versch. Lager aufteilen

						int lager_nachher_bes;
						int aktLager_bes = Integer.valueOf(bestaende.get(best)[0]);

						if (bmeng_temp > aktLager_bes) {
							lager_nachher_bes = 0;
							bmeng_temp -= aktLager_bes;
							// Da komplettes Lager Leer - dann zum n�chsten
						} else {
							lager_nachher_bes = aktLager_bes - bmeng_temp; // Aktueller bestand - (rest bestell menge)
																			// =neuer bestand
							bmeng_temp = 0;
						}

						String updateCSV = csvKB[1] + ";" + (bestaende.get(best)[1]) + ";" + lager_nachher_bes; // Bestand
						sqlHandler(4, updateCSV);

					}
					sqlHandler(5, csvKb);

					return true;

				}
			} // endif bestellung m�glich?
			else {
				System.out.println("\nNicht ge�gend Lagerbestand des Artikels vorhanden.Es fehlen "
						+ (bestellmenge - gesamtBestand) + " zur gew�nschten Menge.\n\n");
				return false;
			}
		} catch (NumberFormatException n) {

			System.out.println("Keine g�ltige Eingabe: " + n.getMessage());
			return false;
		}

	}

	int rechnung(String insert) throws SQLException, IOException {

		String[] csvIn = insert.split(";"); // 0=KNR;1=ARTNR;2=BMENGE;3=LDAT;4=RBET
		ResultSet ben = executeSQLquery("SELECT KUBEST.BENR FROM KUBEST",
				" WHERE KUBEST.KNR= " + csvIn[0] + " AND KUBEST.ARTNR= " + csvIn[1] + " AND KUBEST.REBT= " + csvIn[4]);

		ResultSet rech = executeSQLquery(
				"SELECT KUBEST.KNR KNr, KUNDE.KNAME Name, KUNDE.STRASSE Stra�e, KUNDE.PLZ, KUNDE.ORT Ort, TO_CHAR(KUBEST.LDAT,'DD-MM-YYYY') LDatum , KUBEST.BEMENGE Menge, KUBEST.REBT Betrag FROM KUNDE,KUBEST",
				" WHERE KUBEST.KNR= " + csvIn[0] + " AND KUBEST.ARTNR= " + csvIn[1] + " AND KUBEST.REBT= " + csvIn[4]
						+ " AND KUBEST.KNR = KUNDE.KNR");

		ben.next();
		String benr = String.valueOf(ben.getInt("BENR"));

		if (rsMetaDateiAusgabe(rech, "AB" + csvIn[0] + "B" + benr + ".txt")) {
			System.out.println("\nRechnung angelegt!\n");
			return 1;
		} else {
			return -1;
		}

	}

	boolean rsMetaDateiAusgabe(ResultSet in, String dateipfad) throws SQLException, IOException {
		{
			try {
				ResultSetMetaData rsmd = in.getMetaData();
				System.out.println("\n\n");
				String columnNames = "";
				String data = "";
				int a = 0;
				File rechnung = new File(dateipfad);
				PrintWriter pr = new PrintWriter(rechnung);
				// Vorhandenene Datei loeschen
				if (rechnung.exists()) {
					rechnung.delete();
				}
				// Datei neu erstellen
				rechnung.createNewFile();

				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					columnNames += rsmd.getColumnName(i) + "\t\t\t";
				}
				// in.first();
				while (in.next()) {
					data = "";
					for (int i = 1; i <= rsmd.getColumnCount(); i++) {
						if (in.getString(i).length() >= 8) {
							if (in.getString(i).length() >= 16) {
								data += in.getString(i) + "\t";
							} else {
								data += in.getString(i) + "\t\t";
							}
						} else {
							data += in.getString(i) + "\t\t\t";
						}
					}
					if (a == 0) {
						pr.println(columnNames);
						a++;
					}
					pr.println(data);
				}
				in.close();
				pr.close();
				return true;
			} catch (NullPointerException e) {
				System.out.println("Bestellung nicht in Datenbank!");
				return false;
			}
		}

	}
}
