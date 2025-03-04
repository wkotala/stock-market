package symulacja;

import java.util.Random;

public class Losowanie {
    // Dzięki zapisaniu ziarna możemy łatwo odtworzyć nieudane wywołanie programu.
    private static final int seed = new Random().nextInt();
    private static final Random random = new Random(seed);

    private Losowanie() {}

    public static int seed() {
        return seed;
    }

    // Zwraca liczbę pseudolosową z zakresu [dolna, górna].
    public static int losowaLiczba(int dolna, int górna) {
        if (dolna > górna) {
            throw new NiepoprawnyZakresLosowania("Dolna granica losowania nie może być większa od górnej.");
        }

        return random.nextInt(dolna, górna + 1);
    }

    // Przestawia pseudolosowo elementy podanej tablicy.
    public static <T> void tasujTablicę(T[] tablica) {
        for (int i = tablica.length - 1; i > 0; i--) {
            int j = losowaLiczba(0, i);
            T chw = tablica[i];
            tablica[i] = tablica[j];
            tablica[j] = chw;
        }
    }
}
