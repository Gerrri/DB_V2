package dbZ1;

import static java.lang.System.exit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

public class ISAMvw
{
	static String dataPath = "ARTIKEL.DAT";
	static long offset;
	static String idxPath = "ARTIKEL.IDX";
	static LinkedList<ISAMArtikel> artikelISAMliste = new LinkedList<ISAMArtikel>();

	public static void main(String[] args) throws IOException
		{

			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			//ISAM Liste aufbauen

			System.out.println("Reading file...\nCreating dataset...\n");
			createISAM();

			//MENUE
			int choice = 0;
			do
				{
					System.out.println("Willkommen im Menue:");
					System.out.println("(1)Erfassen eines neuen Datensatzes");
					System.out.println("(2)Aktueller Inhalt der Datei: ARTIKEL.DAT ausgeben");
					System.out.println("(3)Sortierter Direktzugriff");
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
							addData();
							break;
						case 2:
							showData();
							break;
						case 3:
							searchData();
							break;
						case 4:
							createIDX(artikelISAMliste);
						}
				} while (choice != 0);

		}
	// MENUE END

	static void createISAM() throws IOException
		{

			try
				{
					artikelISAMliste = createISAMfromIDX();
				} catch (FileNotFoundException ex)
				{
					artikelISAMliste = createISAMfromDAT();

				}
		}

	// ISAM FILE aus IDX Datei
	static LinkedList<ISAMArtikel> createISAMfromIDX() throws IOException
		{
			LinkedList<ISAMArtikel> artikel = new LinkedList<ISAMArtikel>();
			FileReader fr_idx = new FileReader(idxPath);
			BufferedReader br = new BufferedReader(fr_idx);
			String line;

			while ((line = br.readLine()) != null)
				{
					String[] parts = line.split(";");
					artikel.add(new ISAMArtikel(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])));

				}
			br.close();
			return artikel;
		}

	// ISAM FILE aus DAT Datei
	static LinkedList<ISAMArtikel> createISAMfromDAT() throws IOException
		{
			LinkedList<ISAMArtikel> artikelListe = new LinkedList<ISAMArtikel>();
			ISAMArtikel newArt;

			RandomAccessFile raf = new RandomAccessFile(dataPath, "r");
			raf.seek(0);

			String csv;
			long pos = 0;
			while ((csv = raf.readLine()) != null)
				{
					//temporäres ArtikelObjekt anlegen
					newArt = new ISAMArtikel((Integer.parseInt(csv.split(";")[0])), pos);

					ListIterator<ISAMArtikel> iti = artikelListe.listIterator(0);
					if (artikelListe.size() >= 1)
						{
							while (iti.hasNext() && (iti.next().getnr() < newArt.getnr()))
								{

								}
							//falls neuer nicht der größte,Iterator zurücksetzen für Eintrag
							//if (!(iti.hasNext()))
							iti.previous();
						}
					//offset des nächsten Eintrags vorspeichern
					pos = raf.getFilePointer();
					// neuen Eintrag in ISAM Liste aufnehmen
					iti.add(newArt);
				}

			raf.close();
			return artikelListe;
		}

	// DATA METHODS

	static void addData() throws IOException
		{
			System.out.println("\n\n(1)Erfassen eines neuen Datensatzes:\n");
			RandomAccessFile raf = new RandomAccessFile(dataPath, "rw");
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			File file = new File(dataPath);
			FileWriter write = new FileWriter(file, true);

			BufferedWriter bw = new BufferedWriter(write);
			int artnr, steu;
			String artbez, mge;
			double preis;
			String csvTemp;
			long offset;
			//Werte einlesen
			System.out.println("Bitte geben sie die gewuenschte Artikelnr. ein: ");
			artnr = Integer.parseInt(in.readLine());
			Iterator<ISAMArtikel> it = artikelISAMliste.iterator();

			while (it.hasNext())
				{
					ISAMArtikel e = it.next();

					if (e.getnr() == artnr)
						{
							System.out.println("Es ist ein Fehler aufgetreten. Artikel ist bereits vorhanden");
							bw.close();
							raf.close();
							return;
						}
				}

			System.out.println("Bitte geben Sie die gewuenschte Artikelbez. ein: ");
			artbez = in.readLine();
			System.out.println("Bitte geben Sie die gewuenschte Mengen-Einheit ein: ");
			mge = in.readLine();
			System.out.println("Bitte geben Sie den gewuenschten Preis ein: ");
			preis = Double.parseDouble(in.readLine());
			System.out.println("Wie hoch ist der Steuersatzt? ");
			steu = Integer.parseInt(in.readLine());

			csvTemp = (artnr + ";" + artbez + ";" + mge + ";" + preis + ";" + steu + " ");

			//neuen Artikel-CSV in ARTIKEL.DAT schreiben
			offset = raf.length();
			bw.write(csvTemp);
			bw.newLine();
			bw.close();

			raf.close();
			ISAMArtikel newArt = new ISAMArtikel((Integer.parseInt(csvTemp.split(";")[0])), offset);

			//neuen Artikel in die ISAM Liste aufnehmen
			//temporäres ArtikelObjekt anlegen

			ListIterator<ISAMArtikel> iti = artikelISAMliste.listIterator(0);
			if (artikelISAMliste.size() >= 1)
				{
					while (iti.next().getnr() < newArt.getnr())
						{

						}
					//falls neuer nicht der größte,Iterator zurücksetzen für Eintrag
					//if (!(iti.hasNext()))
					iti.previous();
				}

			// neuen Eintrag in ISAM Liste aufnehmen
			iti.add(newArt);

			System.out.println("\n\n");
		}

	static void showData() throws IOException
		{
			Iterator<ISAMArtikel> it = artikelISAMliste.iterator();
			System.out.println("\n\n(2)Aktueller Inhalt der Datei ARTIKEL.DAT:\n");
			while (it.hasNext())
				{
					ISAMArtikel e = it.next();
					System.out.println(getData(e.getOffset()));

				}
			System.out.println("\n\n");
		}

	static String getData(long offset) throws IOException
		{
			RandomAccessFile raf = new RandomAccessFile(dataPath, "r");
			String line;
			raf.seek(offset);
			line = raf.readLine();
			raf.close();
			return line;
		}

	static void searchData() throws IOException
		{
			System.out.println("\n\n(3)Sortierter Direktzugriff\n");
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			int artnr = 0;
			try
				{
					System.out.println("Bitte geben Sie die gewuenschte Artikelnr. ein:");
					artnr = Integer.parseInt(in.readLine());
				} catch (NumberFormatException e)
				{
					System.out.println("Es ist ein Fehler aufgetreten!" + e.getMessage());

					System.out.println("\n\n");
				}

			ListIterator<ISAMArtikel> it = artikelISAMliste.listIterator();
			while (it.hasNext())
				{
					ISAMArtikel e = it.next();

					if (artnr == e.getnr())
						{
							System.out.println("Gesuchter Artikel: " + getData(e.getOffset()));

							System.out.println("\n\n");
							return;
						}

				}
			System.out.println("Artikel nicht vorhanden!");

			System.out.println("\n\n");
		}

	// IDX Datei erstellen
	static void createIDX(LinkedList<ISAMArtikel> artikel) throws FileNotFoundException, IOException
		{
			ISAMArtikel tempArt;
			System.out.println("Save data....");
			File f = new File(idxPath);
			PrintWriter pr = new PrintWriter(idxPath);
			//	BufferedWriter bw = new BufferedWriter(pr);
			//Vorhandenene Datei loeschen 
			if (f.exists())
				{
					f.delete();
				}
			//Datei neu erstellen
			f.createNewFile();

			//aktuellen inhalt der Liste noch mal in Datei schreiben
			for (int i = 0; i < artikel.size(); i++)
				{
					tempArt = (ISAMArtikel) artikel.get(i);
					pr.println(tempArt.getnr() + ";" + tempArt.getOffset());

				}

			pr.flush(); //gepufferte Daten schreiben

			pr.close();

			System.out.println("Datei wurde neu gespeichert");
			exit(0);
		}

}
