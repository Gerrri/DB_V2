package dbZ2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import oracle.jdbc.pool.OracleDataSource;

public class JDBC_Verwaltung
{

	//DATENBANK VERBINDUNG

	static Connection con = null;

	public static Connection connect() throws IOException, SQLException
		{
			String treiber;
			OracleDataSource ods = new OracleDataSource();

			treiber = "oracle.jdbc.driver.OracleDriver";
			Connection dbConnection = null;
			String uName;
			String pW;
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Please enter username: ");
			uName = in.readLine();
			System.out.println("Please enter password: ");
			pW = in.readLine();

			// Treiber laden

			try
				{

					Class.forName(treiber).newInstance();
				} catch (Exception e)
				{
					System.out.println("Fehler beim laden des Treibers: " + e.getMessage());
				}

			// Erstellung Datenbank-Verbindungsinstanz
			try
				{

					ods.setURL("jdbc:oracle:thin:" + uName + "/" + pW + "@schelling.nt.fh-koeln.de:1521:xe");
					dbConnection = ods.getConnection();
				} catch (SQLException e)
				{
					System.out.println("Fehler beim Verbindungsaufbau zur Datenbank!");
					System.out.println(e.getMessage());
				}
			pW = "";
			uName = "";
			return dbConnection;
		}

	//SQL METHODEN
	public static int jdbcInsert() throws FileNotFoundException, IOException, SQLException
		{

			FileReader fr1 = new FileReader("ARTIKEL.CSV");
			BufferedReader br1 = new BufferedReader(fr1);

			int inserts = 0;
			String csvTemp;

			String artnr, artbez, mge, prei, st;

			while ((csvTemp = br1.readLine()) != null)
				{
					artnr = csvTemp.split(";")[0];
					artbez = csvTemp.split(";")[1];
					mge = csvTemp.split(";")[2];
					prei = csvTemp.split(";")[3];
					st = csvTemp.split(";")[4];
					DateFormat df = new SimpleDateFormat("dd-MM-yyyy");

					String date = df.format(Calendar.getInstance().getTime());

					try
						{
							inserts += executeSQL("INSERT INTO ARTIKEL VALUES(" + artnr + ",'" + artbez + "'," + mge
									+ ",'" + prei + "','" + st + "',TO_DATE ('" + date + "','DD-MM-YYYY'))");

						} catch (SQLException a)
						{
							System.out.println("Exception geworfen!\n" + a.getMessage());
							br1.close();
							return -1;
						}

				}
			br1.close();
			return inserts;
		}

	public static int executeSQL(String sql) throws SQLException
		{
			Statement Stmt;

			int tempRS;
			// Erzeugen eines Statements aus der DB-Verbindung
			Stmt = con.createStatement();
			// SQL-Anweisung ausführen und Ergebnis in ein ResultSet schreiben
			tempRS = Stmt.executeUpdate(sql);
			return tempRS;
		}

	public static ResultSet executeSQLquery(String sql, String tab) throws SQLException
		{
			Statement Stmt;

			ResultSet tempRS;

			// Erzeugen eines Statements aus der DB-Verbindung
			Stmt = con.createStatement();

			// SQL-Anweisung ausführen und Ergebnis in ein ResultSet schreiben
			tempRS = Stmt.executeQuery(sql + tab);
			return tempRS;
		}

	public static void rsMetaHandling(ResultSet RS) throws SQLException
		{
			ResultSetMetaData rsmd = RS.getMetaData();
			System.out.println("\n\n");
			String columnNames = "";
			String data = "";

			for (int i = 1; i <= rsmd.getColumnCount(); i++)
				{
					columnNames += rsmd.getColumnName(i) + "\t\t\t";
				}
			System.out.println(columnNames);
			while (RS.next())
				{
					data = "";
					for (int i = 1; i <= rsmd.getColumnCount(); i++)
						{
							if (RS.getString(i).length() >= 8)
								{
									data += RS.getString(i) + "\t\t";

								} else
								{
									data += RS.getString(i) + "\t\t\t";
								}
						}
					System.out.println(data);
				}
			RS.close();
		}

	//MAIN VERWALTUNG UND MENÜ
	public static void main(String[] args) throws IOException
		{

			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

			// Datenbankverbindung erstellen mit Hilfe der "connect()"-Methode
			try
				{
					con = connect();

					// JDBC Objekte
					//	Statement Stmt = con.createStatement();
					ResultSet RS;

					//MENUE
					int choice = 0;
					do
						{
							System.out.println("Willkommen im Menue:");
							System.out.println("(1)Neue Datensätze importieren mit JDBC INSERT");
							System.out.println("(2)Datensätze abrufen");
							System.out.println("(3)Artikel Auskunft, nach ArtikelNr");
							System.out.println("(4)Neuer Lagerbestand für Artikel");
							System.out.println("(5)Menge eines Lagerbestandes aktualisieren");
							System.out.println("(0)Programm beenden");

							try
								{
									choice = Integer.parseInt(in.readLine());

								} catch (Exception e)
								{
									System.out.println(e.getMessage());
								}

							switch (choice)
								{
								case 1:
									System.out.println("Datensätze werden aus ARTIKEL.CSV importiert...\n\n");
									int a = jdbcInsert();
									if (a >= 1)
										{
											System.out.println("Update erfolgreich!\nEingepflegte Datensätze: " + a);
										} else
										{
											System.out.println("Update fehlgeschlagen!");
										}

									System.out.println("\n\n");
									break;

								case 2:
									//SUB MENÜ

									do
										{
											System.out.println("Welche Tabelle soll geladen werden?:");
											System.out.println("(1)ARTIKEL");
											System.out.println("(2)LAGER");
											System.out.println("(3)KUNDEN");
											System.out.println("(0)QUIT");

											try
												{
													choice = Integer.parseInt(in.readLine());

												} catch (Exception e)
												{
													System.out.println(e.getMessage());
												}

											String sALL = "SELECT * FROM ";
											switch (choice)

												{
												case 1:
													System.out.println("\n\n");
													RS = executeSQLquery(
															"SELECT ARTIKEL.ARTNR, ARTIKEL.ARTBEZ, ARTIKEL.MGE , ARTIKEL.PREIS, ARTIKEL.STEU, TO_CHAR(ARTIKEL.EDAT,'DD-MM-YYYY') EDAT FROM ",
															"ARTIKEL");
													rsMetaHandling(RS);
													System.out.println("\n\n");
													break;

												case 2:
													System.out.println("\n\n");
													RS = executeSQLquery(sALL, "LAGER");
													rsMetaHandling(RS);
													System.out.println("\n\n");
													break;

												case 3:
													System.out.println("\n\n");
													RS = executeSQLquery(sALL, "KUNDE");
													rsMetaHandling(RS);
													System.out.println("\n\n");
													break;
												}//SWITCH END

										} while (choice != 0);
									// SUB MENÜ END
									choice = 1;
									break;

								//ARTIKEL SUCHE NACH ARTNR
								case 3:
									System.out.println("\n\n");
									System.out.println("Bitte Artikel Nr eingeben: ");
									String artNr = in.readLine();
									RS = executeSQLquery(
											"SELECT ARTIKEL.ARTNR, ARTIKEL.ARTBEZ, ARTIKEL.MGE, ARTIKEL.PREIS, ARTIKEL.STEU, TO_CHAR(ARTIKEL.EDAT,'DD-MM-YYYY') EDAT , LAGERBESTAND.BSTNR, LAGERBESTAND.LNR, LAGERBESTAND.MENGE, LAGER.LORT, LAGER.LPLZ FROM ARTIKEL,LAGERBESTAND,LAGER ",
											("WHERE ARTIKEL.ARTNR=" + artNr + " AND LAGERBESTAND.ARTNR=" + artNr
													+ " AND LAGER.LNR= LAGERBESTAND.LNR"));
									rsMetaHandling(RS);

									RS = executeSQLquery(
											"SELECT SUM (LAGERBESTAND.MENGE) Gesamtbestand  FROM LAGERBESTAND",
											" WHERE LAGERBESTAND.ARTNR = " + artNr);
									rsMetaHandling(RS);
									System.out.println("\n\n");
									break;

								case 4: //neuer lagerbestand
									System.out.println("\n\n");
									// User Abfragen
									System.out.println(
											"Für welchen Artikel möchten sie einen Bestand hinzufügen?\nBitte Artikel Nr. eingeben: ");
									String artNrTemp = in.readLine();
									System.out.println(
											"In welchem Lager möchten sie den Bestand hinzufügen?\nBitte Lager Nr. eingeben: ");
									String laNr = in.readLine();
									System.out.println(
											"Wie lautet der neue Bestand?\nBitte Bestand in ganzen zahlen eingeben: ");
									String menge = in.readLine();

									//SQL GESCHWURBEL

									System.out.println("Angefügte Bestände: "
											+ executeSQL("INSERT INTO LAGERBESTAND VALUES (null, " + artNrTemp + ", "
													+ laNr + ", " + menge + ")"));
									System.out.println("\n\n");
									break;

								case 5: //update
									int updates = 0;

									// User Abfragen
									System.out.println(
											"Für welchen Artikel möchten sie den Bestand aktualisieren?\nBitte Artikel Nr. eingeben: ");
									String artNrTemp2 = in.readLine();
									System.out.println(
											"In welchem Lager möchten sie den Bestand aktualisieren?\nBitte Lager Nr. eingeben: ");
									String laNr2 = in.readLine();
									System.out.println(
											"Wie lautet der neue Bestand?\nBitte Bestand in ganzen zahlen eingeben: ");
									String menge2 = in.readLine();

									//SQL geschwurbel
									System.out.println("\n\n");
									updates = executeSQL("UPDATE LAGERBESTAND SET LAGERBESTAND.MENGE = " + menge2
											+ " WHERE LAGERBESTAND.ARTNR= " + artNrTemp2 + " AND LAGERBESTAND.LNR= "
											+ laNr2);
									System.out.println("Geänderte Bestände: " + updates);

									System.out.println("\n\n");
									break;

								}
						} while (choice != 0);
					System.out.println("...shutting down..\n\nBYE BYE!!!");
					// MENUE END

					// SQL Exception abfangen für Connection Methode
				} catch (SQLException e)
				{
					System.out.println(e.getMessage());
					System.out.println("SQL Exception wurde geworfen!");
				}

		}

}