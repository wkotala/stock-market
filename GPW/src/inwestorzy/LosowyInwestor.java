package inwestorzy;

import akcje.Spółka;
import giełda.SystemTransakcyjny;
import symulacja.Losowanie;

import static inwestorzy.StrategiaInwestora.RANDOM;
import static zlecenia.TypZlecenia.KUPNO;
import static zlecenia.TypZlecenia.SPRZEDAŻ;

/*  Podejmuje losowe decyzje inwestycyjne, przy czym w każdej turze:
    - próbuje złożyć zlecenie kupna z 40% prawdopodobieństwem,
    - próbuje złożyć zlecenie sprzedaży z 40% prawdopodobieństwem,
    - nie próbuje złożyć żadnego zlecenia z 20% prawdopodobieństwem,
    - losuje parametry składanych zleceń.
*/
public class LosowyInwestor extends Inwestor {
    public LosowyInwestor(int id) {
        super(id);
    }

    @Override
    public StrategiaInwestora strategia() {
        return RANDOM;
    }

    @Override
    public void rozpocznijTurę(SystemTransakcyjny system) {
        Spółka spółka = system.spółka(Losowanie.losowaLiczba(0, system.liczbaSpółek() - 1));

        int losowaLiczba = Losowanie.losowaLiczba(1, 5);
        switch (losowaLiczba) {
            case 1, 2 -> złóżZlecenieLosoweParametry(system, KUPNO, spółka.akcja());
            case 3, 4 -> złóżZlecenieLosoweParametry(system, SPRZEDAŻ, spółka.akcja());
            case 5 -> {}
        }
    }
}
