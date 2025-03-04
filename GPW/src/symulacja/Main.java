package symulacja;

import giełda.SystemTransakcyjny;

import java.io.FileNotFoundException;

public class Main {
    private Main() {}

    public static void main(String[] args) {
        //ObsługaWyjścia.włączKrokiSymulacji(); // można odkomentować w celu wypisywania kroków symulacji
        ObsługaWyjścia.włączDodatkoweStatystyki(); // można zakomentować w celu ukrycia dodatkowych statystyk

        if (args.length != 2) {
            System.out.println("Podaj poprawne argumenty, tj. ścieżkę do pliku wejściowego i liczbę tur symulacji.");
            throw new IllegalArgumentException("Niepoprawne argumenty programu.");
        }

        SystemTransakcyjny system;
        int liczbaTur;

        try {
            liczbaTur = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Podaj poprawną liczbę tur, podano: " + args[1]);
            throw new IllegalArgumentException("Niepoprawne argumenty programu.");
        }

        if (liczbaTur <= 0) {
            System.out.println("Podaj dodatnią liczbę tur, podano: " + args[0]);
            throw new IllegalArgumentException("Niepoprawne argumenty programu.");
        }

        try (Parser p = new Parser(args[0])) {
            system = p.wczytajSystem(liczbaTur);
        } catch (FileNotFoundException e) {
            System.out.println("Podaj poprawną ścieżkę do pliku, podano: " + args[0]);
            throw new RuntimeException(e);
        } catch (BłędnaStrukturaPlikuWejściowego e) {
            System.out.println("Niepoprawna struktura pliku wejściowego.");
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ObsługaWyjścia.wypiszKrok("Wejście poprawne, wykonuję symulację z ziarnem losowania = " + Losowanie.seed());

        system.symuluj();
    }
}