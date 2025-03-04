package giełda;

import akcje.Akcja;
import akcje.Spółka;
import inwestorzy.Inwestor;
import symulacja.Losowanie;
import symulacja.ObsługaWyjścia;
import zlecenia.TerminWażnościZlecenia;
import zlecenia.TypZlecenia;
import zlecenia.Zlecenie;

import java.util.ArrayDeque;
import java.util.Queue;

import static zlecenia.TypZlecenia.KUPNO;
import static zlecenia.TypZlecenia.SPRZEDAŻ;

public class SystemTransakcyjny {
    private int aktualnaTura;
    private int aktualnyNrZlecenia;
    private final Inwestor[] inwestorzy;
    private final Spółka[] spółki;
    private final int liczbaTur;
    private final int maxRóżnicaCen = 10; // maksymalna różnica między ceną ostatniej transakcji a limitem zlecenia
    private Queue<Akcja> sygnałyKupna;
    private Queue<Akcja> sygnałySprzedaży;
    private int sumaGotówki; // łączna ilość gotówki w portfelach inwestorów

    public SystemTransakcyjny(Inwestor[] inwestorzy, Spółka[] spółki, int liczbaTur) {
        this.inwestorzy = inwestorzy;
        this.spółki = spółki;
        this.liczbaTur = liczbaTur;
        sygnałyKupna = new ArrayDeque<>();
        sygnałySprzedaży = new ArrayDeque<>();
    }

    /* Niezmienniki symulacji:
     * łączna ilość gotówki w portfelach inwestorów,
     * dla każdej akcji jej łączna liczba w portfelach inwestorów. */
    private void inicjalizujNiezmiennik() {
        for (Inwestor inwestor : inwestorzy) {
            sumaGotówki += inwestor.portfel().gotówka();
        }

        for (Spółka spółka : spółki) {
            int liczbaAkcji = 0;
            for (Inwestor inwestor : inwestorzy) {
                liczbaAkcji += inwestor.portfel().liczbaAkcji(spółka.akcja().id());
            }
            spółka.akcja().ustawLiczbęWyemitowanych(liczbaAkcji);
        }
    }

    public boolean sprawdźNiezmiennik() { // na potrzeby testowania
        int gotówka = 0;
        for (Inwestor inwestor : inwestorzy) {
            gotówka += inwestor.portfel().gotówka();
        }

        if (gotówka != sumaGotówki)
            return false;

        for (Spółka spółka : spółki) {
            int liczbaAkcji = 0;
            for (Inwestor inwestor : inwestorzy) {
                liczbaAkcji += inwestor.portfel().liczbaAkcji(spółka.akcja().id());
            }

            if (liczbaAkcji != spółka.akcja().liczbaWyemitowanych())
                return false;
        }

        return true;
    }


    public Inwestor[] inwestorzyJUnit() { // getter tylko na potrzeby testów JUnit
        return inwestorzy;
    }

    public int numerAktualnejTury() {
        return aktualnaTura;
    }

    public int liczbaSpółek() {
        return spółki.length;
    }

    public Spółka spółka(int indeks) {
        return spółki[indeks];
    }

    public int liczbaTur() {
        return liczbaTur;
    }

    public int maxRóżnicaCen() {
        return maxRóżnicaCen;
    }

    public int cenaOstatniejTransakcji(Akcja akcja) {
        return akcja.cenaOstatniejTransakcji();
    }

    public int turaOstatniejTransakcji(Akcja akcja) {
        return akcja.turaOstatniejTransakcji();
    }

    // Metoda żądana w treści zadania — odpytanie o cenę ostatniej transakcji akcji danej spółki.
    public int cenaOstatniejTransakcji(Spółka spółka) {
        return cenaOstatniejTransakcji(spółka.akcja());
    }

    // Metoda żądana w treści zadania — odpytanie o turę ostatniej transakcji akcji danej spółki.
    public int turaOstatniejTransakcji(Spółka spółka) {
        return turaOstatniejTransakcji(spółka.akcja());
    }

    public Akcja sygnałKupna() {
        return sygnałyKupna.poll();
    }

    public Akcja sygnałSprzedaży() {
        return sygnałySprzedaży.poll();
    }

    public void symuluj() {
        inicjalizujNiezmiennik();
        ObsługaWyjścia.wypiszKrok("Początek symulacji");
        for (int tura = 0; tura < liczbaTur; tura++) {
            otwórzGiełdę(tura);
            zamknijGiełdę();
        }
        ObsługaWyjścia.wypiszKrok("Koniec symulacji");

        ObsługaWyjścia.wypiszKońcowyStanPortfeli(inwestorzy);
        ObsługaWyjścia.wypiszDodatkoweStatystyki(inwestorzy, spółki);
    }

    private void otwórzGiełdę(int tura) {
        ObsługaWyjścia.wypiszKrok("Początek tury " + tura);
        aktualnaTura = tura;
        aktualnyNrZlecenia = 0;

        Losowanie.tasujTablicę(inwestorzy);
        for (Inwestor inwestor : inwestorzy) {
            inwestor.rozpocznijTurę(this);
        }

        for (Spółka spółka : spółki) {
            spółka.akcja().arkuszZleceń().realizujZlecenia(aktualnaTura);
        }
    }

    private void zamknijGiełdę() {
        sygnałyKupna = new ArrayDeque<>();
        sygnałySprzedaży = new ArrayDeque<>();

        for (Spółka spółka : spółki) {
            Akcja akcja = spółka.akcja();

            akcja.aktualizujPoTurze();
            akcja.arkuszZleceń().usuńPrzeterminowane(aktualnaTura + 1);
            if (akcja.sygnałKupna()) {
                sygnałyKupna.add(akcja);
            }
            if (akcja.sygnałSprzedaży()) {
                sygnałySprzedaży.add(akcja);
            }
        }
    }

    // Przyjmuje zlecenie, sprawdza jego poprawność oraz to, czy inwestor ma wystarczające środki do jego realizacji.
    // W przypadku pozytywnym wysyła rozkaz zapisania go w arkuszu zleceń odpowiedniej akcji.
    public void zgłośZlecenie(TypZlecenia typZlecenia, Inwestor inwestor, Akcja akcja,
                              int liczbaAkcji, int limit, TerminWażnościZlecenia termin) {
        Zlecenie zlecenie = Zlecenie.stwórz(
                typZlecenia, aktualnaTura, aktualnyNrZlecenia++, inwestor,
                akcja.id(), liczbaAkcji, limit, termin
        );

        if (poprawneZlecenie(zlecenie, akcja) && inwestorMaAktywa(inwestor, zlecenie)) {
            zlecenie.dodajDoArkusza(akcja.arkuszZleceń());
            ObsługaWyjścia.wypiszKrok(inwestor + " złożył " + zlecenie);
        }
    }

    private boolean poprawneZlecenie(Zlecenie zlecenie, Akcja akcja) {
        return zlecenie.liczbaAkcji() > 0 && zlecenie.limitCeny() > 0 &&
                Math.abs(zlecenie.limitCeny() - akcja.cenaOstatniejTransakcji()) < maxRóżnicaCen;
    }

    // Zwraca, czy inwestor ma w portfelu aktywa niezbędne do złożenia zlecenia.
    private boolean inwestorMaAktywa(Inwestor inwestor, Zlecenie zlecenie) {
        if (zlecenie.typ() == KUPNO) {
            return inwestor.portfel().czyWystarczyGotówki(zlecenie.łącznyLimit());
        }
        else if (zlecenie.typ() == SPRZEDAŻ) {
            return inwestor.portfel().czyWystarczyAkcji(zlecenie.idAkcji(), zlecenie.liczbaAkcji());
        }
        else {
            throw new IllegalArgumentException("Niespodziewany typ zlecenia: " + zlecenie.typ());
        }
    }
}
