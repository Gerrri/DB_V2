package dbZ2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

public class JDBC_Verwaltung
{

	//MAIN VERWALTUNG UND MENÜ
	public static void main(String[] args) throws IOException
		{

			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

			// Datenbankverbindung erstellen mit Hilfe der "connect()"-Methode
			try
				{
					// Verbindungsobjekt wird erstellt
					Connect con = new Connect();
					// Verbindung wird aufgebaut
					con.connect();

				

					//MENUE
					int choice = 0;
					do
						{
							System.out.println("Willkommen im Menue:");
							
							System.out.println("(1)Datensätze abrufen");
							System.out.println("(2)Artikel Auskunft, nach ArtikelNr");
							System.out.println("(3)Neuer Lagerbestand für Artikel");
							System.out.println("(4)Menge eines Lagerbestandes aktualisieren");
							System.out.println("(5)Neue Datensätze importieren mit JDBC INSERT");
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

											switch (choice)

												{
												case 1:
													System.out.println("\n\n");

													con.sqlHandler(1, "ARTIKEL");

													System.out.println("\n\n");
													break;

												case 2:
													System.out.println("\n\n");

													con.sqlHandler(1, "LAGER");

													System.out.println("\n\n");
													break;

												case 3:
													System.out.println("\n\n");

													con.sqlHandler(1, "KUNDE");

													System.out.println("\n\n");
													break;
												}//SWITCH END

										} while (choice != 0);
									// SUB MENÜ END
									choice = 1;
									break;

								//ARTIKEL SUCHE NACH ARTNR
								case 2:
									System.out.println("\n\n");
									System.out.println("Bitte Artikel Nr eingeben: ");
									String artNr = in.readLine();

									con.sqlHandler(2, artNr);

									System.out.println("\n\n");
									break;

								case 3: //neuer lagerbestand
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

									String csv = (artNrTemp + ";" + laNr + ";" + menge);
									//SQL GESCHWURBEL
									if (con.sqlHandler(3, csv)==1)
										{
											System.out.println("Erfolgreich angelegt!");
										}

									System.out.println("\n\n");
									break;

								case 4: //UPDATE
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
									String csv2 = (artNrTemp2 + ";" + laNr2 + ";" + menge2);
									//SQL geschwurbel
									System.out.println("\n\n");
																		
									updates = con.sqlHandler(4, csv2);
									System.out.println("Geänderte Bestände: " + updates);

									System.out.println("\n\n");
									break;
									
									
								case 5: //JDBC INSERT
								
									System.out.println("Datensätze werden aus ARTIKEL.CSV importiert...\n\n");
									int a = con.jdbcInsert();
									if (a >= 1)
										{
											System.out.println("Update erfolgreich!\nEingepflegte Datensätze: " + a);
										} else
										{
											System.out.println("Update fehlgeschlagen!");
										}

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