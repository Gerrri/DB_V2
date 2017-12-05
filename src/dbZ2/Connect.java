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
import java.util.LinkedList;

import oracle.jdbc.pool.OracleDataSource;

public class Connect
{
	//DATENBANK VERBINDUNG

	static Connection con = null;

	public void connect() throws IOException, SQLException
		{
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
					con = ods.getConnection();
				} catch (SQLException e)
				{
					System.out.println("Fehler beim Verbindungsaufbau zur Datenbank!");
					System.out.println(e.getMessage());
				}
			pW = "";
			uName = "";
		}

	//SQL METHODEN
	public int jdbcInsert() throws FileNotFoundException, IOException, SQLException
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

	public int executeSQL(String sql) throws SQLException
		{
			Statement Stmt;

			int tempRS;
			// Erzeugen eines Statements aus der DB-Verbindung
			Stmt = con.createStatement();
			// SQL-Anweisung ausführen und Ergebnis in ein ResultSet schreiben
			try
				{
					tempRS = Stmt.executeUpdate(sql);
					return tempRS;
				} catch (SQLException e)
				{
					System.out.println(e.getMessage() + "\nFalsche Werte eingegeben!");
					return 0;
				}

		}

	public ResultSet executeSQLquery(String sql, String tab) throws SQLException
		{
			Statement Stmt;

			ResultSet tempRS = null;

			// Erzeugen eines Statements aus der DB-Verbindung
			Stmt = con.createStatement();
			try
				{
					// SQL-Anweisung ausführen und Ergebnis in ein ResultSet schreiben
					tempRS = Stmt.executeQuery(sql + tab);
					return tempRS;
				} catch (SQLException se)
				{
					System.out.println(se.getMessage() + "\nÜberprüfen sie ihre Eingaben!");
					return tempRS;
				}

		}

	public void rsMetaAusgabe(ResultSet RS) throws SQLException, NullPointerException
		{
			try
				{
					ResultSetMetaData rsmd = RS.getMetaData();
					System.out.println("\n\n");
					String columnNames = "";
					String data = "";
					int a = 0;
					for (int i = 1; i <= rsmd.getColumnCount(); i++)
						{
							columnNames += rsmd.getColumnName(i) + "\t\t\t";
						}

					while (RS.next())
						{
							data = "";
							for (int i = 1; i <= rsmd.getColumnCount(); i++)
								{
									if (RS.getString(i).length() >= 8)
										{
											if (RS.getString(i).length() >= 16)
												{
													data += RS.getString(i) + "\t";
												} else
												{
													data += RS.getString(i) + "\t\t";
												}
										} else
										{
											data += RS.getString(i) + "\t\t\t";
										}
								}
							if (a == 0)
								{
									System.out.println(columnNames);
									a++;
								}
							System.out.println(data);
						}
					RS.close();
				} catch (NullPointerException e)
				{
					System.out.println("Artikel nicht vorhanden oder nicht im Bestand!");
				}
		}

	public int sqlHandler(int c, String csv) throws SQLException, NumberFormatException
		{
			switch (c)
				{

				case 1:
					String sALL = "SELECT * FROM ";
					if (csv.equals("ARTIKEL"))
						{
							rsMetaAusgabe(executeSQLquery(
									"SELECT ARTIKEL.ARTNR, ARTIKEL.ARTBEZ, ARTIKEL.MGE , ARTIKEL.PREIS, ARTIKEL.STEU, TO_CHAR(ARTIKEL.EDAT,'DD-MM-YYYY') EDAT FROM ",
									"ARTIKEL"));
							return 1;
						} else if (csv.equals("LAGER"))
						{

							rsMetaAusgabe(executeSQLquery(sALL, "LAGER"));
							return 1;
						} else if (csv.equals("KUNDE"))
						{

							rsMetaAusgabe(executeSQLquery(sALL, "KUNDE"));
							return 1;
						}
					break;

				case 2://suche nach artnr
					try
						{

							int t = Integer.parseInt(csv);
							csv = String.valueOf(t);

						} catch (NumberFormatException n)
						{

							System.out.println("Keine gültige Artikel Nummer\n" + n.getMessage());
							return 0;

						}
					try
						{
							rsMetaAusgabe(executeSQLquery(
									"SELECT ARTIKEL.ARTNR, ARTIKEL.ARTBEZ, ARTIKEL.MGE, ARTIKEL.PREIS, ARTIKEL.STEU, TO_CHAR(ARTIKEL.EDAT,'DD-MM-YYYY') EDAT , LAGERBESTAND.BSTNR, LAGERBESTAND.LNR, LAGERBESTAND.MENGE, LAGER.LORT, LAGER.LPLZ FROM ARTIKEL,LAGERBESTAND,LAGER ",
									("WHERE ARTIKEL.ARTNR=" + csv + " AND LAGERBESTAND.ARTNR=" + csv
											+ " AND LAGER.LNR= LAGERBESTAND.LNR")));

							rsMetaAusgabe(sumi(csv));
							return 1;

						} catch (SQLException se)
						{
							System.out.println("SQL FEHLER" + se.getMessage());
							return 0;
						}

				case 3: //neuer lagerbestand

					String[] csvA = csv.split(";");
					try
						{

							int a = Integer.parseInt(csvA[0]), l = Integer.parseInt(csvA[1]),
									m = Integer.parseInt(csvA[2]);
							csvA[0] = String.valueOf(a);
							csvA[1] = String.valueOf(l);
							csvA[2] = String.valueOf(m);

						} catch (NumberFormatException n)
						{

							System.out.println("Keine gültige Eingabe: " + n.getMessage());
							return -2;
						}

					return executeSQL("INSERT INTO LAGERBESTAND VALUES (null, " + csvA[0] + ", " + csvA[1] + ", "
							+ csvA[2] + ")");

				case 4: // update bestand
					String[] csvA2 = csv.split(";");
					try
						{

							int a = Integer.parseInt(csvA2[0]), l = Integer.parseInt(csvA2[1]),
									m = Integer.parseInt(csvA2[2]);

							csvA2[0] = String.valueOf(a);
							csvA2[1] = String.valueOf(l);
							csvA2[2] = String.valueOf(m);

						} catch (NumberFormatException n)
						{

							System.out.println("Keine gültige Eingabe: " + n.getMessage());
							return 0;
						}
					return executeSQL("UPDATE LAGERBESTAND SET LAGERBESTAND.MENGE = " + csvA2[2]
							+ " WHERE LAGERBESTAND.ARTNR= " + csvA2[0] + " AND LAGERBESTAND.LNR= " + csvA2[1]);

				case 5: // neuer KUBEST Eintrag
					break;

				}
			return 0;

		}

	private void lagercheck(String[] csvKB) throws SQLException
		{
			//Gesamtbestand prüfen

			try
				{

					int kunNr = Integer.parseInt(csvKB[0]), artnr = Integer.parseInt(csvKB[1]),
							bestellmenge = Integer.parseInt(csvKB[2]);

					csvKB[0] = String.valueOf(kunNr);
					csvKB[1] = String.valueOf(artnr);
					csvKB[2] = String.valueOf(bestellmenge);
					ResultSet r = sumi(csvKB[1]);
					r.next();
					int gesamtBestand = Integer.parseInt(r.getString(1));

					if (bestellmenge <= gesamtBestand)
						{

							// Bestände erfassen

							ResultSet s = executeSQLquery(
									"SELECT LAGERBESTAND.MENGE, LGAERBESTAND.LNR FROM LAGERBESTAND ",
									("WHERE LAGERBESTAND.ARTNR=" + artnr));

							LinkedList<String[]> bestaende = new LinkedList<String[]>();
							while (s.next())
								{
									String[] bestand = new String[] { s.getString(1), s.getString(2) };
									bestaende.add(bestand);
								}

							// bestände liste nach menge sortieren
							//bestaende.sort(arg0);

							//für alle Lagerbestände prüfen ob die bestellung aus einem Lager beliefert werden kann

							for (int lager = 0; lager <= bestaende.size(); lager++)
								{

									if (Integer.parseInt(bestaende.get(lager)[0]) >= Integer.parseInt(csvKB[2]))
										{
											// SQL Handler Bestand update mit bestaende[0] - csvKB[2] (bestellmenge) auf Lagernnr bestände[1] 
											int neu = (Integer.parseInt(bestaende.get(lager)[0]))
													- (Integer.parseInt(csvKB[2]));
											String neuS = String.valueOf(neu);
											String updateCSV = csvKB[1] + ";" + (bestaende.get(lager)[1]) + ";" + neuS;
											sqlHandler(4, updateCSV);
											// neuer Eintrag in Kubest tabelle

											break;
										} else
										{
											// Bestellung auf versch. Lager aufteilen

										}
								}
						}

				} catch (NumberFormatException n)
				{

					System.out.println("Keine gültige Eingabe: " + n.getMessage());

				}

		}

	private ResultSet sumi(String artNr) throws SQLException
		{
			try
				{
					return executeSQLquery("SELECT SUM (LAGERBESTAND.MENGE) Gesamtbestand  FROM ",
							"LAGERBESTAND WHERE LAGERBESTAND.ARTNR= " + artNr);
				} catch (SQLException e)
				{
					System.out.println(e.getMessage());
					return null;
				}
		}

	public void jdbcBestellung(String csvKb) throws SQLException
		{
			String[] csvKB = csvKb.split(";");

			lagercheck(csvKB);

		}
}
