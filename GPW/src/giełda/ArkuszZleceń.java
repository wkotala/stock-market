package giełda;

import akcje.Akcja;
import zlecenia.ZlecenieKupna;
import zlecenia.ZlecenieSprzedaży;

import java.util.*;

public class ArkuszZleceń {
    private TreeSet<ZlecenieKupna> zleceniaKupna;
    private TreeSet<ZlecenieSprzedaży> zleceniaSprzedaży;
    private final Akcja akcja;

    public ArkuszZleceń(Akcja akcja) {
        this.akcja = akcja;
        this.zleceniaKupna = new TreeSet<>(ZlecenieKupna.komparator);
        this.zleceniaSprzedaży = new TreeSet<>(ZlecenieSprzedaży.komparator);
    }

    public void dodajZlecenieKupna(ZlecenieKupna zlecenie) {
        if (zlecenie == null)
            return;

        zleceniaKupna.add(zlecenie);
    }

    public void dodajZlecenieSprzedaży(ZlecenieSprzedaży zlecenie) {
        if (zlecenie == null)
            return;

        zleceniaSprzedaży.add(zlecenie);
    }

    public void usuńPrzeterminowane(int następnaTura) {
        zleceniaKupna.removeIf(zlecenie -> zlecenie.terminWażności().upłynął(następnaTura));
        zleceniaSprzedaży.removeIf(zlecenie -> zlecenie.terminWażności().upłynął(następnaTura));
    }

    /* Realizuje wszystkie możliwe zlecenia danej akcji, powtarzając poniższy algorytm:
       - wybierz zlecenie sprzedaży o najmniejszym limicie oraz zlecenie kupna o największym limicie,
       - spróbuj je zrealizować, ewentualnie dobierając kolejne zlecenia (przypadek z Wykonaj lub anuluj),
       - jeśli nie jest to możliwe, usuń z systemu zlecenie, które zablokowało transakcję,
       - w przeciwnym razie zrealizuj zlecenia. */
    public void realizujZlecenia(int tura) {
        while (!zleceniaKupna.isEmpty() && !zleceniaSprzedaży.isEmpty()) {
            Iterator<ZlecenieKupna> itKupno = zleceniaKupna.iterator();
            Iterator<ZlecenieSprzedaży> itSprzedaż = zleceniaSprzedaży.iterator();
            Deque<ZlecenieKupna> kupno = new ArrayDeque<>();
            Deque<ZlecenieSprzedaży> sprzedaż = new ArrayDeque<>();
            int łącznieAkcjiKupno = 0;
            int łącznieAkcjiSprzedaż = 0;
            boolean możliwaTransakcja = false;

            while (itKupno.hasNext() && itSprzedaż.hasNext()) {
                kupno.addLast(itKupno.next());
                sprzedaż.addLast(itSprzedaż.next());
                łącznieAkcjiKupno += kupno.getLast().liczbaAkcji();
                łącznieAkcjiSprzedaż += sprzedaż.getLast().liczbaAkcji();

                możliwaTransakcja = możliwaTransakcja(kupno, sprzedaż, łącznieAkcjiKupno, łącznieAkcjiSprzedaż);
                if (możliwaTransakcja) {
                    break;
                }
            }

            Transakcja transakcja = new Transakcja(kupno, sprzedaż, akcja, tura);
            if (możliwaTransakcja && transakcja.wystarczająceAktywaInwestorów()) {
                transakcja.wykonajZlecenia();
                wyczyśćZleceniaPoTransakcji();
            }
            else {
                usuńZlecenieNieDoSpełnienia();
            }
        }
    }

    // Zwraca, czy transakcja jest możliwa (nie biorąc pod uwagę ewentualnych braków środków inwestorów).
    private boolean możliwaTransakcja(Deque<ZlecenieKupna> kupno, Deque<ZlecenieSprzedaży> sprzedaż,
                                      int łącznieAkcjiKupno, int łącznieAkcjiSprzedaż) {

        // Jeśli oba zlecenia są Wykonaj lub anuluj.
        if (kupno.getLast().musiByćWykonaneWCałości() && sprzedaż.getLast().musiByćWykonaneWCałości()) {
            return łącznieAkcjiKupno == łącznieAkcjiSprzedaż;
        }
        else if (kupno.getLast().musiByćWykonaneWCałości()) { // tylko kupno jest Wykonaj lub anuluj
            return łącznieAkcjiKupno <= łącznieAkcjiSprzedaż;
        }
        else if (sprzedaż.getLast().musiByćWykonaneWCałości()) { // tylko sprzedaż jest Wykonaj jub anuluj
            return łącznieAkcjiSprzedaż <= łącznieAkcjiKupno;
        }
        else { // żadne nie jest Wykonaj lub anuluj
            return true;
        }
    }

    // Usuwa z systemu zlecenie, które zablokowało transakcję.
    private void usuńZlecenieNieDoSpełnienia() {
        ZlecenieKupna pierwszeKupno = zleceniaKupna.first();
        ZlecenieSprzedaży pierwszaSprzedaż = zleceniaSprzedaży.first();

        int pierwszaLiczbaAkcji = Math.min(pierwszeKupno.liczbaAkcji(), pierwszaSprzedaż.liczbaAkcji());
        int pierwszaŁącznaCena = pierwszaLiczbaAkcji * Transakcja.cenaTransakcji(pierwszeKupno, pierwszaSprzedaż);

        if (!pierwszeKupno.inwestor().portfel().czyWystarczyGotówki(pierwszaŁącznaCena)) {
            zleceniaKupna.pollFirst();
        }
        else if (!pierwszaSprzedaż.inwestor().portfel().czyWystarczyAkcji(akcja.id(), pierwszaLiczbaAkcji)) {
            zleceniaSprzedaży.pollFirst();
        }
        else {
            // Obaj inwestorzy mają wystarczające aktywa, ale transakcja jest niemożliwa, więc blokuje Wykonaj lub anuluj.
            if (pierwszeKupno.musiByćWykonaneWCałości() && pierwszaSprzedaż.musiByćWykonaneWCałości()) {
                if (pierwszeKupno.compareTo(pierwszaSprzedaż) < 0)
                    zleceniaKupna.pollFirst();
                else
                    zleceniaSprzedaży.pollFirst();
            }
            else if (pierwszeKupno.musiByćWykonaneWCałości()) {
                zleceniaKupna.pollFirst();
            }
            else if (pierwszaSprzedaż.musiByćWykonaneWCałości()) {
                zleceniaSprzedaży.pollFirst();
            }
            else {
                throw new IllegalStateException(
                        "Niespodziewany stan programu. Transakcja nie udała się pomimo wystarczających środków.");
            }
        }
    }

    // Czyści zbiory zleceń po wykonanej transakcji poprzez usunięcie w całości wykonanych.
    private void wyczyśćZleceniaPoTransakcji() {
        while (!zleceniaKupna.isEmpty() && zleceniaKupna.first().liczbaAkcji() == 0) {
            zleceniaKupna.pollFirst();
        }
        while (!zleceniaSprzedaży.isEmpty() && zleceniaSprzedaży.first().liczbaAkcji() == 0) {
            zleceniaSprzedaży.pollFirst();
        }
    }
}
