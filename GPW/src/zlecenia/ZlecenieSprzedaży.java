package zlecenia;

import giełda.ArkuszZleceń;
import inwestorzy.Inwestor;

import java.util.Comparator;

import static zlecenia.TypZlecenia.SPRZEDAŻ;

public class ZlecenieSprzedaży extends Zlecenie {
    public static final Comparator<Zlecenie> komparator = Comparator
            .comparingInt(Zlecenie::limitCeny) // rosnąco po limit
            .thenComparing(Zlecenie::compareTo); // w kolejności złożenia


    public ZlecenieSprzedaży(int turaZłożenia, int numer, Inwestor inwestor, String idAkcji, int liczbaAkcji,
                             int limitCeny, TerminWażnościZlecenia terminWażności) {
        super(turaZłożenia, numer, inwestor, idAkcji, liczbaAkcji, limitCeny, terminWażności);
    }

    public ZlecenieSprzedaży(ZlecenieSprzedaży inne) {
        super(inne);
    }

    @Override
    public TypZlecenia typ() {
        return SPRZEDAŻ;
    }

    @Override
    public void dodajDoArkusza(ArkuszZleceń arkusz) {
        arkusz.dodajZlecenieSprzedaży(this);
    }
}
