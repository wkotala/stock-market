package symulacja;

import akcje.Akcja;
import akcje.Spółka;
import giełda.SystemTransakcyjny;
import inwestorzy.Inwestor;
import inwestorzy.LosowyInwestor;
import inwestorzy.PortfelInwestycyjny;
import inwestorzy.SMAInwestor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Parser implements AutoCloseable {
    private final Scanner sc;

    public Parser (String ścieżka) throws FileNotFoundException {
        sc = new Scanner(new File(ścieżka));
    }

    @Override
    public void close() throws Exception {
        sc.close();
    }

    public SystemTransakcyjny wczytajSystem(int liczbaTur) throws BłędnaStrukturaPlikuWejściowego {
        Inwestor[] inwestorzy = wczytajInwestorów();
        Spółka[] spółki = wczytajSpółki();
        PortfelInwestycyjny portfel = wczytajPortfel();
        przypiszPortfele(inwestorzy, spółki, portfel);

        return new SystemTransakcyjny(inwestorzy, spółki, liczbaTur);
    }

    private Inwestor[] wczytajInwestorów() throws BłędnaStrukturaPlikuWejściowego {
        ArrayList<Inwestor> inwestorzy = new ArrayList<>();
        int idInwestora = 0;
        while (sc.hasNextLine()) {
            String wiersz = sc.nextLine();
            if (wiersz.isEmpty() || wiersz.charAt(0) == '#') // pusta linia / komentarz w pliku wejściowym — pomijamy
                continue;

            Scanner skanerWiersza = new Scanner(wiersz);
            while (skanerWiersza.hasNext()) {
                String inwestorStr = skanerWiersza.next();
                switch (inwestorStr.toLowerCase()) {
                    case "r", "random", "losowy" -> inwestorzy.add(new LosowyInwestor(idInwestora++));
                    case "s", "sma" -> inwestorzy.add(new SMAInwestor(idInwestora++));
                    default -> throw new BłędnaStrukturaPlikuWejściowego("Niepoprawna nazwa inwestora.");
                }
            }

            break; // wczytaliśmy inwestorów, wychodzimy z pętli
        }

        if (inwestorzy.isEmpty())
            throw new BłędnaStrukturaPlikuWejściowego("Nie wykryto opisu inwestorów.");

        return inwestorzy.toArray(new Inwestor[0]);
    }

    private Spółka[] wczytajSpółki() throws BłędnaStrukturaPlikuWejściowego {
        ArrayList<Spółka> spółki = new ArrayList<>();
        Set<String> nazwy = new HashSet<>();
        while (sc.hasNextLine()) {
            String wiersz = sc.nextLine();
            if (wiersz.isEmpty() || wiersz.charAt(0) == '#') // pusta linia / komentarz w pliku wejściowym — pomijamy
                continue;

            // dane — tablica ciągów znaków oddzielonych dwukropkami / białymi znakami
            String[] dane = wiersz.split("[\\s:]+");

            if ((dane.length % 2) == 1) {
                throw new BłędnaStrukturaPlikuWejściowego("Niepoprawny opis akcji z ostatnimi cenami.");
            }

            for (int i = 0; i < dane.length; i += 2) {
                if (!dane[i].matches("[A-Z]{1,5}")) // powinien być niepusty ciąg znaków ASCII A-Z nie dłuższy niż 5
                    throw new BłędnaStrukturaPlikuWejściowego("Niepoprawne id akcji: " + dane[i]);

                int ostatniaCena;
                try {
                    ostatniaCena = Integer.parseInt(dane[i + 1]);
                } catch (NumberFormatException e) {
                    throw new BłędnaStrukturaPlikuWejściowego(
                            "Niepoprawna ostatnia cena transakcji akcji " + dane[i] + ": " + dane[i + 1]);
                }
                if (ostatniaCena <= 0) {
                    throw new BłędnaStrukturaPlikuWejściowego(
                            "Ujemna ostatnia cena transakcji akcji " + dane[i] + ": " + dane[i + 1]);
                }

                if (nazwy.contains(dane[i])) {
                    throw new BłędnaStrukturaPlikuWejściowego(
                            "Wielokrotnie podano ostatnią cenę transakcji akcji " + dane[i]);
                }
                nazwy.add(dane[i]);
                spółki.add(new Spółka(dane[i], new Akcja(dane[i], ostatniaCena)));
            }

            break; // wczytaliśmy spółki, wychodzimy z pętli
        }

        if (spółki.isEmpty())
            throw new BłędnaStrukturaPlikuWejściowego("Nie wykryto opisu akcji z ostatnimi cenami.");

        return spółki.toArray(new Spółka[0]);
    }

    private PortfelInwestycyjny wczytajPortfel() throws BłędnaStrukturaPlikuWejściowego {
        int gotówka = -1;
        Map<String, Integer> akcje = new HashMap<>();
        while (sc.hasNextLine()) {
            String wiersz = sc.nextLine();
            if (wiersz.isEmpty() || wiersz.charAt(0) == '#') // pusta linia / komentarz w pliku wejściowym — pomijamy
                continue;

            // dane — tablica ciągów znaków oddzielonych dwukropkami / białymi znakami
            String[] dane = wiersz.split("[\\s:]+");

            try {
                gotówka = Integer.parseInt(dane[0]);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new BłędnaStrukturaPlikuWejściowego("Nie wykryto opisu stanu portfeli.");
            } catch (NumberFormatException e) {
                throw new BłędnaStrukturaPlikuWejściowego("Niepoprawna ilość gotówki w portfelu: " + dane[0]);
            }
            if (gotówka < 0) {
                throw new BłędnaStrukturaPlikuWejściowego("Ujemna ilość gotówki: " + gotówka);
            }

            if ((dane.length % 2) == 0) {
                throw new BłędnaStrukturaPlikuWejściowego("Niepoprawny opis stanu portfeli.");
            }

            for (int i = 1; i < dane.length; i += 2) {
                if (!dane[i].matches("[A-Z]{1,5}")) // powinien być niepusty ciąg znaków ASCII A-Z nie dłuższy niż 5
                    throw new BłędnaStrukturaPlikuWejściowego("Niepoprawne id akcji: " + dane[i]);

                int liczbaAkcji;
                try {
                    liczbaAkcji = Integer.parseInt(dane[i + 1]);
                } catch (NumberFormatException e) {
                    throw new BłędnaStrukturaPlikuWejściowego("Niepoprawna liczba akcji " + dane[i] + ": " + dane[i + 1]);
                }
                if (liczbaAkcji < 0) {
                    throw new BłędnaStrukturaPlikuWejściowego("Ujemna liczba akcji: " + liczbaAkcji);
                }

                if (akcje.containsKey(dane[i]))
                    throw new BłędnaStrukturaPlikuWejściowego("Wielokrotnie podano liczbę akcji " + dane[i]);
                else
                    akcje.put(dane[i], liczbaAkcji);
            }

            break; // wczytaliśmy spółki, wychodzimy z pętli
        }

        if (gotówka == -1)
            throw new BłędnaStrukturaPlikuWejściowego("Nie wykryto opisu stanu portfeli.");

        return new PortfelInwestycyjny(gotówka, akcje);
    }

    // Przypisuje kopie podanego portfela każdemu inwestorowi.
    // Jeśli na wejściu nie pojawiła się informacja o liczbie akcji pewnej spółki, to zakładam, że wynosi 0.
    // Jeśli na wejściu pojawiła się informacja o liczbie akcji niebędącej w obrocie, to rzucam błąd.
    private void przypiszPortfele(Inwestor[] inwestorzy, Spółka[] spółki, PortfelInwestycyjny portfel)
            throws BłędnaStrukturaPlikuWejściowego {

        portfel.uzupełnijOAkcje(spółki);

        if (portfel.liczbaRóżnychAkcji() > spółki.length) {
            throw new BłędnaStrukturaPlikuWejściowego("Niepoprawny opis portfela. Podano akcje, które nie są w obrocie.");
        }

        for (Inwestor inwestor : inwestorzy) {
            inwestor.ustawPortfel(new PortfelInwestycyjny(portfel));
        }
    }
}
