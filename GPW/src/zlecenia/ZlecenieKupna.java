package zlecenia;

import giełda.ArkuszZleceń;
import inwestorzy.Inwestor;

import java.util.Comparator;

import static zlecenia.TypZlecenia.KUPNO;

public class ZlecenieKupna extends Zlecenie {
    public static final Comparator<Zlecenie> komparator = Comparator
            .comparingInt(Zlecenie::limitCeny).reversed() // malejąco po limit
            .thenComparing(Zlecenie::compareTo); // w kolejności złożenia


    public ZlecenieKupna(int turaZłożenia, int numer, Inwestor inwestor, String idAkcji, int liczbaAkcji,
                         int limitCeny, TerminWażnościZlecenia terminWażności) {
        super(turaZłożenia, numer, inwestor, idAkcji, liczbaAkcji, limitCeny, terminWażności);
    }

    public ZlecenieKupna(ZlecenieKupna inne) {
        super(inne);
    }

    @Override
    public TypZlecenia typ() {
        return KUPNO;
    }

    @Override
    public void dodajDoArkusza(ArkuszZleceń arkusz) {
        arkusz.dodajZlecenieKupna(this);
    }
}
