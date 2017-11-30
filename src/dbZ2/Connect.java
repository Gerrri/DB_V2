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
			tempRS = Stmt.executeUpdate(sql);
			System.out.println(tempRS);
			return tempRS;
		}

	public ResultSet executeSQLquery(String sql, String tab) throws SQLException
		{
			Statement Stmt;

			ResultSet tempRS;

			// Erzeugen eines Statements aus der DB-Verbindung
			Stmt = con.createStatement();

			// SQL-Anweisung ausführen und Ergebnis in ein ResultSet schreiben
			tempRS = Stmt.executeQuery(sql + tab);
			return tempRS;
		}

	public void rsMetaAusgabe(ResultSet RS) throws SQLException
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
					System.out.println(data);
				}
			RS.close();
		}

	public int sqlHandler(int c, String csv) throws SQLException
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
						} catch (Exception e)
						{

							System.out.println("Keine gültige Artikel Nummer\n" + e.getMessage());
							return -1;

						}

					rsMetaAusgabe(executeSQLquery(
							"SELECT ARTIKEL.ARTNR, ARTIKEL.ARTBEZ, ARTIKEL.MGE, ARTIKEL.PREIS, ARTIKEL.STEU, TO_CHAR(ARTIKEL.EDAT,'DD-MM-YYYY') EDAT , LAGERBESTAND.BSTNR, LAGERBESTAND.LNR, LAGERBESTAND.MENGE, LAGER.LORT, LAGER.LPLZ FROM ARTIKEL,LAGERBESTAND,LAGER ",
							("WHERE ARTIKEL.ARTNR=" + csv + " AND LAGERBESTAND.ARTNR=" + csv
									+ " AND LAGER.LNR= LAGERBESTAND.LNR")));

					rsMetaAusgabe(sumi(csv));
					return 1;

				case 3: //neuer lagerbestand

					String[] csvA = csv.split(";");

					return executeSQL("INSERT INTO LAGERBESTAND VALUES (null, " + csvA[0] + ", " + csvA[1] + ", "
							+ csvA[2] + ")");

				case 4: // update bestand
					String[] csvA2 = csv.split(";");

					return executeSQL("UPDATE LAGERBESTAND SET LAGERBESTAND.MENGE = " + csvA2[2]
							+ " WHERE LAGERBESTAND.ARTNR= " + csvA2[0] + " AND LAGERBESTAND.LNR= " + csvA2[1]);

				}
			return -2;
		}

	private ResultSet sumi(String csv) throws SQLException
		{

			return executeSQLquery("SELECT SUM (LAGERBESTAND.MENGE) Gesamtbestand  FROM LAGERBESTAND",
					" WHERE LAGERBESTAND.ARTNR = " + csv);
		}

	public void jdbcBestellung(String knr, String artnr, String bmenge)
		{
			
		}
}
