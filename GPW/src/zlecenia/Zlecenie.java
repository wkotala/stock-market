package zlecenia;

import giełda.ArkuszZleceń;
import inwestorzy.Inwestor;

import java.util.Objects;

import static zlecenia.TypZlecenia.KUPNO;
import static zlecenia.TypZlecenia.SPRZEDAŻ;

public abstract class Zlecenie implements Comparable<Zlecenie> {
    private final int turaZłożenia;
    private final int numer;
    private final Inwestor inwestor;
    private final String idAkcji;
    private final int limitCeny;
    private final TerminWażnościZlecenia terminWażności;
    private int liczbaAkcji;

    public Zlecenie(int turaZłożenia, int numer, Inwestor inwestor, String idAkcji, int liczbaAkcji,
                    int limitCeny, TerminWażnościZlecenia terminWażności) {
        this.turaZłożenia = turaZłożenia;
        this.numer = numer;
        this.inwestor = inwestor;
        this.idAkcji = idAkcji;
        this.liczbaAkcji = liczbaAkcji;
        this.limitCeny = limitCeny;
        this.terminWażności = terminWażności;
    }

    public Zlecenie(Zlecenie inne) {
        this(inne.turaZłożenia, inne.numer, inne.inwestor, inne.idAkcji, inne.liczbaAkcji, inne.limitCeny, inne.terminWażności);
    }

    public static Zlecenie stwórz(TypZlecenia typZlecenia, int turaZłożenia, int numer, Inwestor inwestor,
                                  String idAkcji, int liczbaAkcji, int limitCeny, TerminWażnościZlecenia terminWażności) {

        if (typZlecenia == KUPNO)
            return new ZlecenieKupna(turaZłożenia, numer, inwestor, idAkcji, liczbaAkcji, limitCeny, terminWażności);
        else if (typZlecenia == SPRZEDAŻ)
            return new ZlecenieSprzedaży(turaZłożenia, numer, inwestor, idAkcji, liczbaAkcji, limitCeny, terminWażności);
        else
            throw new IllegalArgumentException("Niespodziewany typ zlecenia: " + typZlecenia);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Zlecenie zlecenie = (Zlecenie) o;
        return turaZłożenia == zlecenie.turaZłożenia && numer == zlecenie.numer;
    }

    @Override
    public int hashCode() {
        return Objects.hash(turaZłożenia, numer);
    }

    @Override
    public int compareTo(Zlecenie drugi) {
        int porównanieTur = Integer.compare(this.turaZłożenia, drugi.turaZłożenia); // rosnąco po turze złożenia
        if (porównanieTur != 0)
            return porównanieTur;
        return Integer.compare(this.numer, drugi.numer); // rosnąco po numerze zlecenia w turze
    }

    @Override
    public String toString() {
        return String.format("Zlecenie %s (%s %s x %d po cenie %d)", terminWażności, typ(), idAkcji, liczbaAkcji, limitCeny);
    }

    public int turaZłożenia() {
        return turaZłożenia;
    }

    public int numer() {
        return numer;
    }

    public Inwestor inwestor() {
        return inwestor;
    }

    public String idAkcji() {
        return idAkcji;
    }

    public int limitCeny() {
        return limitCeny;
    }

    public TerminWażnościZlecenia terminWażności() {
        return terminWażności;
    }

    public int liczbaAkcji() {
        return liczbaAkcji;
    }

    public int łącznyLimit() {
        return limitCeny * liczbaAkcji;
    }

    public boolean musiByćWykonaneWCałości() {
        return terminWażności.musiByćWykonaneWCałości();
    }

    // Aktualizuje pozostałą liczbę akcji w zleceniu.
    public void zrealizowano(int liczbaAkcji) {
        this.liczbaAkcji -= liczbaAkcji;
    }

    public abstract TypZlecenia typ();

    public abstract void dodajDoArkusza(ArkuszZleceń arkusz);
}
