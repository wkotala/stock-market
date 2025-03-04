package inwestorzy;

import akcje.Akcja;
import giełda.SystemTransakcyjny;
import symulacja.Losowanie;
import symulacja.NiepoprawnyZakresLosowania;
import zlecenia.*;

import static zlecenia.TypZlecenia.KUPNO;
import static zlecenia.TypZlecenia.SPRZEDAŻ;

public abstract class Inwestor {
    private final int numer;
    private PortfelInwestycyjny portfel;

    public Inwestor(int numer) {
        this.numer = numer;
    }

    @Override
    public String toString() {
        return "Inwestor " + strategia() + " nr " + numer;
    }

    public int numer() {
        return numer;
    }

    public PortfelInwestycyjny portfel() {
        return portfel;
    }

    public void ustawPortfel(PortfelInwestycyjny portfel) {
        this.portfel = portfel;
    }

    public abstract StrategiaInwestora strategia();

    public abstract void rozpocznijTurę(SystemTransakcyjny system);

    protected void złóżZlecenieLosoweParametry(SystemTransakcyjny system, TypZlecenia typZlecenia, Akcja akcja) {
        int ostatniaCena = system.cenaOstatniejTransakcji(akcja);
        int limit = Losowanie.losowaLiczba(
                Math.max(1, ostatniaCena - system.maxRóżnicaCen()),
                ostatniaCena + system.maxRóżnicaCen()
        );

        int liczbaAkcji;
        try {
            if (typZlecenia == KUPNO)
                liczbaAkcji =  Losowanie.losowaLiczba(1, Math.min(akcja.liczbaWyemitowanych(), portfel().gotówka() / limit));
            else if (typZlecenia == SPRZEDAŻ)
                liczbaAkcji = Losowanie.losowaLiczba(1, portfel().liczbaAkcji(akcja.id()));
            else
                throw new IllegalArgumentException("Niespodziewany typ zlecenia: " + typZlecenia);
        }
        catch (NiepoprawnyZakresLosowania e) {
            // W przypadku gdy górna granica losowania jest < 1, nie składamy żadnego zlecenia.
            return;
        }

        int losowaLiczba = Losowanie.losowaLiczba(1,4);
        TerminWażnościZlecenia termin = switch (losowaLiczba) {
            case 1 -> new Bezterminowy();
            case 2 -> new Terminowy(system.numerAktualnejTury() + Losowanie.losowaLiczba(0, system.liczbaTur() / 10));
            case 3 -> new Natychmiastowy(system.numerAktualnejTury());
            case 4 -> new WykonajLubAnuluj(system.numerAktualnejTury());
            default -> throw new IllegalStateException("Niespodziewana wylosowana wartość: " + losowaLiczba);
        };

        system.zgłośZlecenie(typZlecenia, this, akcja, liczbaAkcji, limit, termin);
    }
}
