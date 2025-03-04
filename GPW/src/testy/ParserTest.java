package testy;

import akcje.Akcja;
import giełda.SystemTransakcyjny;
import inwestorzy.Inwestor;
import inwestorzy.PortfelInwestycyjny;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import symulacja.BłędnaStrukturaPlikuWejściowego;
import symulacja.Parser;

import java.util.*;
import java.util.stream.Stream;

import static inwestorzy.StrategiaInwestora.RANDOM;
import static org.junit.jupiter.api.Assertions.*;

// Uwaga! ścieżki do plików są względne, należy uruchamiać testy z odpowiedniej lokalizacji / zmienić ścieżki.
class ParserTest {
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    void wczytajSystemNiepoprawnePliki(int numerPliku) {
        String ścieżka = "input/niepoprawne/";
        assertThrows(BłędnaStrukturaPlikuWejściowego.class, () -> {
            try (Parser p = new Parser(ścieżka + numerPliku + ".in")) {
                p.wczytajSystem(100);
            }
        });
    }

    @ParameterizedTest
    @MethodSource("przypadkiTestowePoprawnePliki")
    void wczytajSystemPoprawnePliki(String nazwaTestu, int oczekiwanaLiczbaInwestorówR, int oczekiwanaLiczbaInwestorówS,
                                    Akcja[] oczekiwaneAkcje, PortfelInwestycyjny oczekiwanyPortfel) {
        String ścieżka = "input/";
        SystemTransakcyjny system = null;

        try (Parser p = new Parser(ścieżka + nazwaTestu + ".in")) {
            system = p.wczytajSystem(100);
        } catch (Exception ignored) {}

        assertNotNull(system);
        assertTrue(poprawniInwestorzy(oczekiwanaLiczbaInwestorówR, oczekiwanaLiczbaInwestorówS, system));
        assertTrue(poprawneAkcje(oczekiwaneAkcje, system));
        assertTrue(poprawnePortfele(oczekiwanyPortfel, system));
    }

    public static Stream<Arguments> przypadkiTestowePoprawnePliki() {
        return Stream.of(
                Arguments.of(
                        "prosty", 1, 1, tablicaAkcji("A 100"),
                        new PortfelInwestycyjny(300, Map.of("A", 2))
                ),
                Arguments.of(
                        "losowiInwestorzy", 3, 0, tablicaAkcji("A 100", "B 200", "C 300"),
                        new PortfelInwestycyjny(1000, Map.of("A", 1, "B", 1, "C", 0))
                ),
                Arguments.of(
                        "SMAInwestorzy", 0, 3, tablicaAkcji("A 100", "B 200", "C 300"),
                        new PortfelInwestycyjny(1000, Map.of("A", 1, "B", 1, "C", 0))
                ),
                Arguments.of(
                        "przykład", 4, 2, tablicaAkcji("APL 145", "MSFT 300", "GOOGL 2700"),
                        new PortfelInwestycyjny(100000, Map.of("APL", 5, "MSFT", 15, "GOOGL", 3))
                ),
                Arguments.of(
                        "WIG20", 10, 10,
                        tablicaAkcji("ALE 37", "ALR 91", "BDX 673", "CDR 134", "CPS 12", "DNP 398", "JSW 29"),
                        new PortfelInwestycyjny(100000, Map.of(
                                "ALE", 405, "ALR", 165, "BDX", 22, "CDR", 112, "CPS", 1250, "DNP", 38, "JSW", 517
                        ))
                )
        );
    }

    // Wywołanie: np. tablicaAkcji("APL 5", "MSFT 15", "GOOGL 3");
    private static Akcja[] tablicaAkcji(String... opisAkcji) {
        Akcja[] akcje = new Akcja[opisAkcji.length];
        for (int i = 0; i < opisAkcji.length; i++) {
            String[] części = opisAkcji[i].split("\\s+");
            akcje[i] = new Akcja(części[0], Integer.parseInt(części[1]));
        }
        return akcje;
    }

    private boolean poprawniInwestorzy(int oczekiwanaLiczbaInwestorówR, int oczekiwanaLiczbaInwestorówS,
                                       SystemTransakcyjny system) {
        Inwestor[] inwestorzyWSystemie = system.inwestorzyJUnit();

        if (inwestorzyWSystemie == null)
            return false;

        int liczbaS = 0;
        int liczbaR = 0;
        for (Inwestor inwestor : inwestorzyWSystemie) {
            if (inwestor == null || inwestor.strategia() == null)
                return false;

            if (inwestor.strategia() == RANDOM)
                liczbaR++;
            else
                liczbaS++;
        }

        return oczekiwanaLiczbaInwestorówR == liczbaR && oczekiwanaLiczbaInwestorówS == liczbaS;
    }

    private boolean poprawneAkcje(Akcja[] oczekiwaneAkcje, SystemTransakcyjny system) {
        if (oczekiwaneAkcje.length != system.liczbaSpółek())
            return false;

        Akcja[] akcjeWSystemie = new Akcja[system.liczbaSpółek()];
        for (int i = 0; i < akcjeWSystemie.length; i++) {
            if (system.spółka(i) == null)
                return false;

            akcjeWSystemie[i] = system.spółka(i).akcja();
        }

        Arrays.sort(akcjeWSystemie, Comparator.comparing(Akcja::id));
        Arrays.sort(oczekiwaneAkcje, Comparator.comparing(Akcja::id));

        for (int i = 0; i < akcjeWSystemie.length; i++) {
            if (!oczekiwaneAkcje[i].id().equals(akcjeWSystemie[i].id()))
                return false;

            if (oczekiwaneAkcje[i].cenaOstatniejTransakcji() != akcjeWSystemie[i].cenaOstatniejTransakcji())
                return false;
        }

        return true;
    }

    private boolean poprawnePortfele(PortfelInwestycyjny oczekiwanyPortfel, SystemTransakcyjny system) {
        Inwestor[] inwestorzyWSystemie = system.inwestorzyJUnit();

        for (Inwestor inwestor : inwestorzyWSystemie) {
            if (inwestor.portfel() == null || inwestor.portfel().gotówka() != oczekiwanyPortfel.gotówka())
                return false;
            if (!inwestor.portfel().equals(oczekiwanyPortfel))
                return false;
        }
        return true;
    }
}