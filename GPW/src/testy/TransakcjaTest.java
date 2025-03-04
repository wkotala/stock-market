package testy;

import akcje.Akcja;
import giełda.Transakcja;
import inwestorzy.Inwestor;
import inwestorzy.LosowyInwestor;
import inwestorzy.PortfelInwestycyjny;
import inwestorzy.SMAInwestor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import zlecenia.*;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TransakcjaTest {
    private static int tura;
    private static String idAkcji;
    private static Akcja akcja;
    private static Inwestor inwestorR;
    private static Inwestor inwestorS;

    @BeforeAll
    static void beforeAll() {
        tura = 0;
        idAkcji = "TEST";
        akcja = new Akcja(idAkcji, 10);
        Map<String, Integer> akcje = new HashMap<>();
        akcje.put("TEST", 5);
        PortfelInwestycyjny portfelR = new PortfelInwestycyjny(50, akcje);
        PortfelInwestycyjny portfelS = new PortfelInwestycyjny(portfelR);

        inwestorR = new LosowyInwestor(0);
        inwestorS = new SMAInwestor(1);
        inwestorR.ustawPortfel(portfelR);
        inwestorS.ustawPortfel(portfelS);
    }

    @ParameterizedTest
    @MethodSource("przypadkiWystarczająceAktywa")
    void wystarczająceAktywaInwestorów(Deque<ZlecenieKupna> kupno, Deque<ZlecenieSprzedaży> sprzedaż) {
        Transakcja t = new Transakcja(kupno, sprzedaż, akcja, tura);
        assertTrue(t.wystarczająceAktywaInwestorów());
    }

    @ParameterizedTest
    @MethodSource("przypadkiNiewystarczająceAktywa")
    void niewystarczająceAktywaInwestorów(Deque<ZlecenieKupna> kupno, Deque<ZlecenieSprzedaży> sprzedaż) {
        Transakcja t = new Transakcja(kupno, sprzedaż, akcja, tura);
        assertFalse(t.wystarczająceAktywaInwestorów());
    }


    static Stream<Arguments> przypadkiWystarczająceAktywa() {
        return Stream.of(
                Arguments.of(
                        new ArrayDeque<>(Collections.singleton(
                                new ZlecenieKupna(tura, 1, inwestorR, idAkcji, 5, 20, new Natychmiastowy(tura)))),
                        new ArrayDeque<>(Collections.singleton(
                                new ZlecenieSprzedaży(tura, 0, inwestorS, idAkcji, 5, 10, new Terminowy(tura + 1))))
                ),
                Arguments.of(
                        new ArrayDeque<>(Arrays.asList(
                                new ZlecenieKupna(tura, 0, inwestorR, idAkcji, 2, 25, new WykonajLubAnuluj(tura)),
                                new ZlecenieKupna(tura, 1, inwestorS, idAkcji, 1, 25, new Natychmiastowy(tura))
                        )),
                        new ArrayDeque<>(Arrays.asList(
                                new ZlecenieSprzedaży(tura, 2, inwestorS, idAkcji, 1, 25, new Natychmiastowy(tura)),
                                new ZlecenieSprzedaży(tura, 3, inwestorS, idAkcji, 2, 25, new WykonajLubAnuluj(tura))
                        ))
                )
        );
    }

    static Stream<Arguments> przypadkiNiewystarczająceAktywa() {
        return Stream.of(
                Arguments.of(
                        new ArrayDeque<>(Collections.singleton(
                                new ZlecenieKupna(tura, 0, inwestorR, idAkcji, 5, 20, new Natychmiastowy(tura)))),
                        new ArrayDeque<>(Collections.singleton(
                                new ZlecenieSprzedaży(tura, 1, inwestorS, idAkcji, 5, 10, new Terminowy(tura + 1))))
                ),
                Arguments.of(
                        new ArrayDeque<>(Collections.singleton(
                                new ZlecenieKupna(tura, 1, inwestorR, idAkcji, 6, 20, new Natychmiastowy(tura)))),
                        new ArrayDeque<>(Collections.singleton(
                                new ZlecenieSprzedaży(tura, 0, inwestorS, idAkcji, 7, 10, new Terminowy(tura + 1))))
                ),
                Arguments.of(
                        new ArrayDeque<>(Arrays.asList(
                                new ZlecenieKupna(tura, 0, inwestorR, idAkcji, 2, 25, new WykonajLubAnuluj(tura)),
                                new ZlecenieKupna(tura, 1, inwestorR, idAkcji, 1, 25, new Natychmiastowy(tura))
                        )),
                        new ArrayDeque<>(Arrays.asList(
                                new ZlecenieSprzedaży(tura, 2, inwestorS, idAkcji, 1, 25, new Natychmiastowy(tura)),
                                new ZlecenieSprzedaży(tura, 3, inwestorS, idAkcji, 2, 25, new WykonajLubAnuluj(tura))
                        ))
                ),
                Arguments.of(
                        new ArrayDeque<>(Arrays.asList(
                                new ZlecenieKupna(tura, 0, inwestorR, idAkcji, 2, 26, new WykonajLubAnuluj(tura)),
                                new ZlecenieKupna(tura, 1, inwestorS, idAkcji, 1, 25, new Natychmiastowy(tura))
                        )),
                        new ArrayDeque<>(Arrays.asList(
                                new ZlecenieSprzedaży(tura, 2, inwestorS, idAkcji, 1, 25, new Natychmiastowy(tura)),
                                new ZlecenieSprzedaży(tura, 3, inwestorS, idAkcji, 2, 25, new WykonajLubAnuluj(tura))
                        ))
                )
        );
    }
}