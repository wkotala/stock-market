package symulacja;

import akcje.Spółka;
import inwestorzy.Inwestor;

import java.util.Arrays;
import java.util.Comparator;

import static inwestorzy.StrategiaInwestora.RANDOM;
import static inwestorzy.StrategiaInwestora.SMA;

public class ObsługaWyjścia {
    private static boolean krokiSymulacji = false;
    private static boolean dodatkoweStatystyki = false;

    private ObsługaWyjścia() {}

    public static void włączKrokiSymulacji() {
        krokiSymulacji = true;
    }

    public static void włączDodatkoweStatystyki() {
        dodatkoweStatystyki = true;
    }

    // Wypisuje końcowe stany portfeli inwestorów w kolejności, w jakiej zostali podani na wejściu.
    public static void wypiszKońcowyStanPortfeli(Inwestor[] inwestorzy) {
        Arrays.sort(inwestorzy, Comparator.comparingInt(Inwestor::numer));

        for (Inwestor inwestor : inwestorzy) {
            System.out.println(inwestor.portfel());
        }
    }

    public static void wypiszKrok(String krok) {
        if (krokiSymulacji) {
            System.out.println(krok + ".");
        }
    }

    public static void wypiszDodatkoweStatystyki(Inwestor[] inwestorzy, Spółka[] spółki) {
        if (!dodatkoweStatystyki) {
            return;
        }

        int sumaLosowych = 0;
        int sumaSMA = 0;
        int liczbaLosowych = 0;
        int liczbaSMA = 0;
        for (Inwestor inwestor : inwestorzy) {
            if (inwestor.strategia() == RANDOM) {
                liczbaLosowych++;
                sumaLosowych += inwestor.portfel().wartość(spółki);
            }
            else if (inwestor.strategia() == SMA) {
                liczbaSMA++;
                sumaSMA += inwestor.portfel().wartość(spółki);
            }
        }

        if (liczbaLosowych > 0) {
            System.out.printf("Średni wynik (gotówka + akcje liczone po cenie ostatniej transakcji) inwestorów RANDOM: %d\n",
                    sumaLosowych / liczbaLosowych);
        }
        if (liczbaSMA > 0) {
            System.out.printf("Średni wynik (gotówka + akcje liczone po cenie ostatniej transakcji) inwestorów SMA: %d\n",
                    sumaSMA / liczbaSMA);
        }
    }
}
