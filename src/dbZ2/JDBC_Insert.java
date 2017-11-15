package dbZ2;

import java.io.*;
import java.sql.*;
import oracle.jdbc.pool.*;

public class JDBC_Insert
{

	public static Connection connect() throws SQLException, IOException
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
					
					ods.setURL("jdbc:oracle:thin:"+uName+"/"+pW+"@schelling.nt.fh-koeln.de:1521:xe");
					dbConnection = ods.getConnection();
				} catch (SQLException e)
				{
					System.out.println("Fehler beim Verbindungsaufbau zur Datenbank!");
					System.out.println(e.getMessage());
				}
			pW="";
			uName="";
			return dbConnection;
		}

	public static void main(String[] args) throws IOException
		{

			// Datenbankverbindung erstellen mit Hilfe der "connect()"-Methode
			try
				{
					Connection con = connect();

					// JDBC Objekte
					Statement Stmt;
					ResultSet RS;

					String SQL;
					int artnr;
					String artbez;
					int mge;
					double preis;
					double steu;

					// Erzeugen eines Statements aus der DB-Verbindung
					Stmt = con.createStatement();

					/*********************************************************************/
					/*                                                                   */
					/* Eine SQL-SELECT Anfrage */
					/*                                                                   */
					/*********************************************************************/
					// Eine SQL Select Anweisung
					SQL = "SELECT * FROM ARTIKEL";

					// SQL-Anweisung ausführen und Ergebnis in ein ResultSet schreiben
					RS = Stmt.executeQuery(SQL);

					// Das ResultSet Datensatzweise durchlaufen
					while (RS.next())
						{
							artnr = RS.getInt("ARTNR");
							artbez = RS.getString("ARTBEZ");
							mge = RS.getInt("MGE");
							preis = RS.getDouble("PREIS");
							steu = RS.getDouble("STEU");

							System.out.println("ARTNr. : " + artnr);
							System.out.println("ARTBEZ: " + artbez);
							System.out.println("MGE: " + mge);
							System.out.println("Preis: " + preis);
							System.out.println("Steuer: " + steu);
							System.out.println();
						}

					
					/*********************************************************************/
				      /*                                                                   */
				      /*                  Eine SQL-UPDATE Anweisung                        */
				      /*                  (DELETE & INSERT analog)                         */
				      /*                                                                   */
				      /*********************************************************************/
				      /*SQL   = "INSERT INTO ARTIKEL SET ewz = 83.3 WHERE lname = 'Deutschland'";
				      int a = Stmt.executeUpdate(SQL);
				      if (a==1){
				        System.out.println("Update erfolgreich!");
				      } else {
				        System.out.println("Update fehlgeschlagen!");
				      }*/
					
					
					
					// SQL Exception abfangen
				} catch (SQLException e)
				{
					System.out.println(e.getMessage());
					System.out.println("SQL Exception wurde geworfen!");
				}
		}

}
