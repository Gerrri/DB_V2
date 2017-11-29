package dbZ2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;

import oracle.jdbc.pool.OracleDataSource;

public class Connect
{
	Connection dbConnection = null;
	OracleDataSource ods = new OracleDataSource();

	public Connect() throws SQLException, IOException
	{
		String treiber;

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
				dbConnection = ods.getConnection();
			} catch (SQLException e)
			{
				System.out.println("Fehler beim Verbindungsaufbau zur Datenbank!");
				System.out.println(e.getMessage());
			}
		pW = "";
		uName = "";

	}

	public Connection getDbConnection()
		{
			return dbConnection;
		}

	public void setDbConnection(Connection dbConnection)
		{
			this.dbConnection = dbConnection;
		}

	public OracleDataSource getOds()
		{
			return ods;
		}

	public void setOds(OracleDataSource ods)
		{
			this.ods = ods;
		}

}
