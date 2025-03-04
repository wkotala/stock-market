package testy;

import akcje.Akcja;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

// Testy poprawności wysyłanych sygnałów SMA.
class AkcjaTest {
    private Akcja akcja;

    @BeforeEach
    void setUp() {
        akcja = new Akcja("TEST", 100);
    }

    @ParameterizedTest
    @MethodSource("przypadkiTestoweSMA") // 1 — sygnał kupna, -1 — sygnał sprzedaży, 0 — brak sygnału po rundzie
    void testujSygnałySMA(int[] cenyTransakcjiWKolejnychTurach, int[] oczekiwaneSygnałyKupna) {
        int liczbaTur = cenyTransakcjiWKolejnychTurach.length;
        assertEquals(liczbaTur, oczekiwaneSygnałyKupna.length);
        for (int tura = 0; tura < liczbaTur; tura++) {
            akcja.aktualizujOstatniąTransakcję(cenyTransakcjiWKolejnychTurach[tura], tura);
            akcja.aktualizujPoTurze();
            switch (oczekiwaneSygnałyKupna[tura]) {
                case -1:
                    assertTrue(akcja.sygnałSprzedaży());
                    assertFalse(akcja.sygnałKupna());
                    break;
                case 1:
                    assertFalse(akcja.sygnałSprzedaży());
                    assertTrue(akcja.sygnałKupna());
                    break;
                case 0:
                    assertFalse(akcja.sygnałSprzedaży());
                    assertFalse(akcja.sygnałKupna());
                    break;
            }
        }
    }

    static Stream<Arguments> przypadkiTestoweSMA() {
        return Stream.of(
                Arguments.of(
                        IntStream.rangeClosed(101, 120).toArray(),
                        new int[20]
                ),
                Arguments.of(
                        new int[]{105,110,115,120,125,130,135,140,145,150,140,130,120,130,140,150,140,130,120,110,120,130,140,150,160},
                        new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,-1,0,0,0,1,0,-1,0,0,0,0,1}
                ),
                Arguments.of(
                        new int[]{101,102,103,104,103,102,101,102,103,104,103,102,101,100,99,98,99,100,101,102,103,90,91,92,93,94,95,96},
                        new int[]{0,0,0,0,0,0,0,0,0,-1,0,1,0,-1,0,0,0,0,0,0,1,-1,0,0,0,0,0,0}
                )
        );
    }
}