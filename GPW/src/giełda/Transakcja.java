package giełda;

import akcje.Akcja;
import inwestorzy.Inwestor;
import symulacja.ObsługaWyjścia;
import zlecenia.ZlecenieKupna;
import zlecenia.ZlecenieSprzedaży;

import java.util.*;

public class Transakcja {
    private final Deque<ZlecenieKupna> kupno;
    private final Deque<ZlecenieSprzedaży> sprzedaż;
    private final Akcja akcja;
    private final int tura;

    public Transakcja(Deque<ZlecenieKupna> kupno, Deque<ZlecenieSprzedaży> sprzedaż, Akcja akcja, int tura) {
        this.akcja = akcja;
        this.kupno = kupno;
        this.sprzedaż = sprzedaż;
        this.tura = tura;
    }

    public static int cenaTransakcji(ZlecenieKupna zlecenieKupna, ZlecenieSprzedaży zlecenieSprzedaży) {
        if (zlecenieKupna.compareTo(zlecenieSprzedaży) < 0) { // jeśli zlecenie kupna było złożone wcześniej
            return zlecenieKupna.limitCeny();
        }
        else {
            return zlecenieSprzedaży.limitCeny();
        }
    }

    public void wykonajZlecenia() {
        wykonajZlecenia(false);
    }

    public boolean wystarczająceAktywaInwestorów() {
        return wykonajZlecenia(true);
    }

    // Wykonuje zlecenia objęte daną transakcją (najczęściej parę zleceń kupno — sprzedaż).
    // Jeśli tylkoSprawdź = false, to rzeczywiście wykonuje zlecenia, aktualizuje portfele inwestorów i akcje.
    // Jeśli tylkoSprawdź = true, to jedynie symuluje wykonanie zleceń i zwraca, czy inwestorom wystarczy aktywów.
    private boolean wykonajZlecenia(boolean tylkoSprawdź) {
        Iterator<ZlecenieKupna> itKupno = dajIteratorKupna(tylkoSprawdź);
        Iterator<ZlecenieSprzedaży> itSprzedaż = dajIteratorSprzedaży(tylkoSprawdź);

        ZlecenieKupna zlecenieKupna = itKupno.next();
        ZlecenieSprzedaży zlecenieSprzedaży = itSprzedaż.next();

        Map<Integer, Integer> gotówka = new HashMap<>(); // gotówka inwestora o danym numerze (do sprawdzania)
        Map<Integer, Integer> akcje = new HashMap<>(); // akcje inwestora o danym numerze (do sprawdzania)

        while (true) {
            if (zlecenieKupna.liczbaAkcji() == 0) {
                if (!itKupno.hasNext())
                    break;
                zlecenieKupna = itKupno.next();
            }
            if (zlecenieSprzedaży.liczbaAkcji() == 0) {
                if (!itSprzedaż.hasNext())
                    break;
                zlecenieSprzedaży = itSprzedaż.next();
            }

            // Dokonujemy transakcji między inwestorami, którzy złożyli dane zlecenia.
            Inwestor kupujący = zlecenieKupna.inwestor();
            Inwestor sprzedający = zlecenieSprzedaży.inwestor();
            int cenaAkcji = cenaTransakcji(zlecenieKupna, zlecenieSprzedaży);
            int liczbaAkcji = Math.min(zlecenieKupna.liczbaAkcji(), zlecenieSprzedaży.liczbaAkcji());

            if (!wymieńAkcjeGotówkę(tylkoSprawdź, gotówka, akcje, kupujący, sprzedający, cenaAkcji, liczbaAkcji,
                    zlecenieKupna, zlecenieSprzedaży)) {
                return false;
            }
            // Aktualizacja zleceń, aby odzwierciedlały tę część, która nie została zrealizowana.
            zlecenieKupna.zrealizowano(liczbaAkcji);
            zlecenieSprzedaży.zrealizowano(liczbaAkcji);
        }

        return true;
    }

    // Zwraca iterator do kolejki ze zleceniami (lub ich kopiami, jeśli tylkoSprawdź = true).
    private Iterator<ZlecenieKupna> dajIteratorKupna(boolean tylkoSprawdź) {
        if (tylkoSprawdź) {
            Deque<ZlecenieKupna> kopiaKupna = new ArrayDeque<>();
            for (ZlecenieKupna zlecenie : kupno) {
                kopiaKupna.addLast(new ZlecenieKupna(zlecenie));
            }
            return kopiaKupna.iterator();
        }
        else {
            return kupno.iterator();
        }
    }

    // Zwraca iterator do kolejki ze zleceniami (lub ich kopiami, jeśli tylkoSprawdź = true).
    private Iterator<ZlecenieSprzedaży> dajIteratorSprzedaży(boolean tylkoSprawdź) {
        if (tylkoSprawdź) {
            Deque<ZlecenieSprzedaży> kopiaSprzedaży = new ArrayDeque<>();
            for (ZlecenieSprzedaży zlecenie : sprzedaż) {
                kopiaSprzedaży.addLast(new ZlecenieSprzedaży(zlecenie));
            }
            return kopiaSprzedaży.iterator();
        }
        else {
            return sprzedaż.iterator();
        }
    }

    // Wymienia akcje na gotówkę między inwestorami (tylko sprawdzając lub rzeczywiście).
    // Zwraca false, jeśli sprawdzał i zabrakło aktywów; w przeciwnym wypadku zwraca true.
    private boolean wymieńAkcjeGotówkę(boolean tylkoSprawdź, Map<Integer, Integer> gotówka, Map<Integer, Integer> akcje,
                                    Inwestor kupujący, Inwestor sprzedający, int cenaAkcji, int liczbaAkcji,
                                    ZlecenieKupna zlecenieKupna, ZlecenieSprzedaży zlecenieSprzedaży) {
        if (tylkoSprawdź) {
            gotówka.put(
                    kupujący.numer(),
                    gotówka.getOrDefault(kupujący.numer(), kupujący.portfel().gotówka()) - liczbaAkcji * cenaAkcji
            );
            akcje.put(
                    sprzedający.numer(),
                    akcje.getOrDefault(sprzedający.numer(), sprzedający.portfel().liczbaAkcji(akcja.id())) - liczbaAkcji
            );
            boolean wynik = gotówka.get(kupujący.numer()) >= 0 && akcje.get(sprzedający.numer()) >= 0;
            gotówka.put(
                    sprzedający.numer(),
                    gotówka.getOrDefault(sprzedający.numer(), sprzedający.portfel().gotówka()) + liczbaAkcji * cenaAkcji
            );
            akcje.put(
                    kupujący.numer(),
                    akcje.getOrDefault(kupujący.numer(), kupujący.portfel().liczbaAkcji(akcja.id())) + liczbaAkcji
            );
            return wynik;
        }
        else {
            ObsługaWyjścia.wypiszKrok(String.format("%s kupił %d %s po cenie %d, które sprzedał mu %s",
                    zlecenieKupna.inwestor(), liczbaAkcji, akcja.id(), cenaAkcji, zlecenieSprzedaży.inwestor()));

            // Przeniesienie aktywów między inwestorami.
            kupujący.portfel().przelejGotówkę(liczbaAkcji * cenaAkcji, sprzedający.portfel());
            sprzedający.portfel().przepiszAkcje(liczbaAkcji, akcja.id(), kupujący.portfel());

            // Aktualizacja danych akcji.
            akcja.aktualizujOstatniąTransakcję(cenaAkcji, tura);
            return true;
        }
    }
}
