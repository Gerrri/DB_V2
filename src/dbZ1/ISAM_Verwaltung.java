package dbZ1;

import static java.lang.System.exit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

public class ISAM_Verwaltung
{
	static String pfad = "ARTIKEL.DAT";
	static int offset;
	static String pfad1 = "ARTIKEL.IDX";

	public static void main(String[] args) throws IOException, ClassNotFoundException
		{

			LinkedList<ISAMArtikel> artikelISAMliste = new LinkedList<ISAMArtikel>();
			int choice = 0;
			File file = new File(pfad);
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			RandomAccessFile raf = new RandomAccessFile(pfad, "rw");
			createISAMFile(artikelISAMliste);

			if (artikelISAMliste == null)
				{
					System.out.println("Fehler. Die gesuchte Liste wurde nicht angelegt.");
				}
//MENUE
			do
				{
					System.out.println("Willkommen im Menue:");
					System.out.println("(1)Erfassen eines neuen Datensatzes");
					System.out.println("(2)Aktueller Inhalt der Datei: " + file.getName() + " ausgeben");
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
							addData(artikelISAMliste);
							break;
						case 2:
							showData(artikelISAMliste);
							break;
						case 3:
							searchData(artikelISAMliste);
							break;
						case 4:
							closeProject(artikelISAMliste);
						}
				} while (choice != 0);

		}
// MENUE END
	
	// ISAM FILE aus IDX Datei
	public static void createISAMFile(LinkedList<ISAMArtikel> artikel) throws IOException
		{

			FileReader fr_idx = new FileReader(pfad1);
			BufferedReader br = new BufferedReader(fr_idx);
			String line;

			while ((line = br.readLine()) != null)
				{
					String[] parts = line.split(";");
					artikel.add(new ISAMArtikel(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])));

				}
		}

	
	// DATA METHODS
	
	static void addData(LinkedList artikel) throws IOException
		{

			RandomAccessFile raf = new RandomAccessFile(pfad, "rw");
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			File file = new File(pfad);
			FileWriter write = new FileWriter(file, true);
			FileWriter write_idx = new FileWriter(pfad1, true);
			BufferedWriter bw = new BufferedWriter(write);
			int artnr, steu;
			String artbez, mge;
			double preis;
			String csvTemp;
			int laengeDerDatei;
			ISAMArtikel new_Artikel;

			System.out.println("Bitte geben sie die gewuenschte Artikelnr. ein: ");
			artnr = Integer.parseInt(in.readLine());
			Iterator<ISAMArtikel> it = artikel.iterator();

			while (it.hasNext())
				{
					ISAMArtikel e = it.next();

					if (e.getnr() == artnr)
						{
							System.out.println("Es ist ein Fehler aufgetreten. Artikel ist bereits vorhanden");
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

			laengeDerDatei = (int) raf.length();
			
			//neuen Artikel in die ISAM Liste aufnehmen
			new_Artikel = new ISAMArtikel(artnr, laengeDerDatei);
			artikel.add(new_Artikel);
			
			//neuen Artikel-CSV in ARTIKEL.DAT schreiben
			bw.write(csvTemp);
			bw.newLine();
			bw.close();

		}

	static void showData(LinkedList artikel) throws IOException
		{
			Iterator<ISAMArtikel> it = artikel.iterator();

			while (it.hasNext())
				{
					ISAMArtikel e = it.next();
					System.out.println(getData(e.getOffset()));

				}

		}

	static String getData(int offset) throws IOException
		{
			RandomAccessFile raf = new RandomAccessFile(pfad, "r");
			String line;
			raf.seek(offset);
			line = raf.readLine();
			raf.close();
			return line;
		}

	static void searchData(LinkedList artikel) throws IOException
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			int artnr = 0;
			try
				{
					System.out.println("Bitte geben Sie die gewuenschte Artikelnr. ein:");
					artnr = Integer.parseInt(in.readLine());
				} catch (NumberFormatException e)
				{
					System.out.println("Es ist ein Fehler aufgetreten!" + e.getMessage());
				}

			ListIterator<ISAMArtikel> it = artikel.listIterator();
			while (it.hasNext())
				{
					ISAMArtikel e = it.next();

					if (artnr == e.getnr())
						{
							System.out.println(getData(e.getOffset()));
							return;
						}

				}
			System.out.println("Artikel nicht vorhanden!");
		}

	static void closeProject(LinkedList artikel) throws IOException
		{

			ISAMArtikel liste;
			System.out.println("Save data....");
			File f = new File(pfad1);
			PrintWriter pr = new PrintWriter(pfad1);
			BufferedWriter bw = new BufferedWriter(pr);
			//Vorhandenene Datei loeschen 
			if (f.exists())
				{
					f.delete();
				}
			//Datei neu erstellen
			f.createNewFile();
			//Datei sortieren
			Collections.sort(artikel);
			//aktuellen inhalt der Liste noch mal in Datei schreiben
			for (int i = 0; i < artikel.size(); i++)
				{
					liste = (ISAMArtikel) artikel.get(i);
					pr.println(liste.getnr() + ";" + liste.getOffset());

				}

			pr.flush(); //gepufferte Daten schreiben

			pr.close();

			System.out.println("Datei wurde neu gespeichert");
			exit(0);
		}

}
