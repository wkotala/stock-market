package testy;


import giełda.SystemTransakcyjny;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import symulacja.Parser;

import static org.junit.jupiter.api.Assertions.*;

// Uwaga! ścieżki do plików są względne, należy uruchamiać testy z odpowiedniej lokalizacji / zmienić ścieżki.
class SystemTransakcyjnyTest {
    @ParameterizedTest
    @CsvSource({
            "prosty, 1",
            "prosty, 1000",
            "przykład, 1000",
            "przykład, 10000",
            "losowiInwestorzy, 1000",
            "SMAInwestorzy, 1000",
            "WIG20, 10000"
    })
    void testSymulacji(String nazwaTestu, int liczbaTur) {
        String ścieżka = "input/";
        SystemTransakcyjny system = null;

        try (Parser p = new Parser(ścieżka + nazwaTestu + ".in")) {
            system = p.wczytajSystem(liczbaTur);
        } catch (Exception ignored) {}

        assertNotNull(system);
        system.symuluj();
        assertTrue(system.sprawdźNiezmiennik());
    }
}