package inwestorzy;

import akcje.Akcja;
import giełda.SystemTransakcyjny;
import symulacja.Losowanie;

import static inwestorzy.StrategiaInwestora.SMA;
import static zlecenia.TypZlecenia.KUPNO;
import static zlecenia.TypZlecenia.SPRZEDAŻ;

/*  Podejmuje decyzje inwestycyjne na podstawie wskaźnika SMA:
    - próbuje złożyć zlecenie kupna, jeśli otrzyma sygnał kupna (przecięcie SMA 10 od dołu przez SMA 5),
    - próbuje złożyć zlecenie sprzedaży, jeśli otrzyma sygnał sprzedaży (przecięcie SMA 10 od góry przez SMA 5),
    - losuje parametry składanych zleceń.
*/
public class SMAInwestor extends Inwestor {
    public SMAInwestor(int id) {
        super(id);
    }

    @Override
    public StrategiaInwestora strategia() {
        return SMA;
    }

    @Override
    public void rozpocznijTurę(SystemTransakcyjny system) {
        int preferowanaOpcja = Losowanie.losowaLiczba(1, 2);
        Akcja akcja;
        if (preferowanaOpcja == 1) { // wolimy kupić
            if ((akcja = system.sygnałKupna()) != null) {
                złóżZlecenieLosoweParametry(system, KUPNO, akcja);
            }
            else if ((akcja = system.sygnałSprzedaży()) != null) {
                złóżZlecenieLosoweParametry(system, SPRZEDAŻ, akcja);
            }
        }
        else if (preferowanaOpcja == 2) { // wolimy sprzedać
            if ((akcja = system.sygnałSprzedaży()) != null) {
                złóżZlecenieLosoweParametry(system, SPRZEDAŻ, akcja);
            }
            else if ((akcja = system.sygnałKupna()) != null) {
                złóżZlecenieLosoweParametry(system, KUPNO, akcja);
            }
        }
    }
}
