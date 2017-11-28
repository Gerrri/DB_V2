package dbZ2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import oracle.jdbc.pool.OracleDataSource;

public class JDBC_Verwaltung
{

	static Connection con = null;

	public static ResultSet executeSQL(String sql, String tab) throws SQLException
		{
			Statement Stmt;

			ResultSet tempRS;
			// Erzeugen eines Statements aus der DB-Verbindung
			Stmt = con.createStatement();

			String SQL = sql + tab;

			// SQL-Anweisung ausführen und Ergebnis in ein ResultSet schreiben
			tempRS = Stmt.executeQuery(SQL);
			return tempRS;
		}

	public static void metaHandling(ResultSet RS) throws SQLException
		{
			ResultSetMetaData rsmd = RS.getMetaData();

			String names = "  ";
			String data = "   ";
			for (int i = 1; i <= rsmd.getColumnCount(); i++)
				{
					names += rsmd.getColumnName(i) + "          ";
				}
			System.out.println(names);
			while (RS.next())
				{
					data = "   ";
					for (int i = 1; i <= rsmd.getColumnCount(); i++)
						{
							data += RS.getString(i) + "          ";
						}

					System.out.println(data);
				}
			RS.close();
		}

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

	public static void main(String[] args) throws IOException
		{

			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

			// Datenbankverbindung erstellen mit Hilfe der "connect()"-Methode
			try
				{
					con = connect();

					// JDBC Objekte
					Statement Stmt = con.createStatement();
					ResultSet RS;
					String SQL;
				

					//MENUE
					int choice = 0;
					do
						{
							System.out.println("Willkommen im Menue:");
							System.out.println("(1)Neue Datensätze importieren mit JDBC INSERT");
							System.out.println("(2)Datensätze abrufen");
							System.out.println("(3)Artikel Auskunft, nach ArtikelNr");
							System.out.println("(4)Programm beenden");

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
									SQL = ("INSERT INTO ARTIKEL VALUES (null, 'Datteln',1,'5,99','0,19', TO_DATE ('01-01-2017','DD-MM-YYYY'))");

									int a = Stmt.executeUpdate(SQL);
									if (a == 1)
										{
											System.out.println("Update erfolgreich!");
										} else
										{
											System.out.println("Update fehlgeschlagen!");
										}
									break;

								case 2:
									//SUB MENÜ

									do
										{
											System.out.println("Welche Tabelle soll geladen werden?:");
											System.out.println("(1)ARTIKEL");
											System.out.println("(2)LAGER");
											System.out.println("(3)KUNDEN");

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
													RS = executeSQL(sALL, "ARTIKEL");
													metaHandling(RS);
													break;

												case 2:
													RS = executeSQL(sALL, "LAGER");
													metaHandling(RS);
													break;

												case 3:
													RS = executeSQL(sALL, "KUNDE");
													metaHandling(RS);
													break;
												}//SWITCH END

										} while (choice != 0);
									// SUB MENÜ END

									break;

									
									//ARTIKEL SUCHE NACH ARTNR
								case 3:
									
									System.out.println("Bitte Artikel Nr eingeben: ");
									String artNr = in.readLine();
									RS= executeSQL("","");
									metaHandling(RS);
									break;

								case 4:

								}
						} while (choice != 0);

					// MENUE END

					// SQL Exception abfangen
				} catch (SQLException e)
				{
					System.out.println(e.getMessage());
					System.out.println("SQL Exception wurde geworfen!");
				}

		}

}