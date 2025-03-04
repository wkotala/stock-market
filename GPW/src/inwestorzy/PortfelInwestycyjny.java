package inwestorzy;

import akcje.Spółka;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class PortfelInwestycyjny {
    private int gotówka;
    private final Map<String, Integer> akcje; // id akcji -> liczba akcji w portfelu

    public PortfelInwestycyjny(int gotówka, Map<String, Integer> akcje) {
        this.gotówka = gotówka;
        this.akcje = akcje;
    }

    public PortfelInwestycyjny(PortfelInwestycyjny portfel) {
        this.gotówka = portfel.gotówka;
        this.akcje = new HashMap<>(portfel.akcje);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(gotówka);

        Map<String, Integer> posortowaneAkcje = new TreeMap<>(akcje);
        for (Map.Entry<String, Integer> entry : posortowaneAkcje.entrySet()) {
            sb.append(" ").append(entry.getKey()).append(":").append(entry.getValue());
        }

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortfelInwestycyjny portfel = (PortfelInwestycyjny) o;
        return gotówka == portfel.gotówka && akcje.equals(portfel.akcje);
    }

    public int gotówka() {
        return gotówka;
    }

    public int liczbaRóżnychAkcji() {
        return akcje.size();
    }

    public int liczbaAkcji(String idAkcji) {
        return akcje.get(idAkcji);
    }

    public boolean czyWystarczyGotówki(int cena) {
        return gotówka >= cena;
    }

    public boolean czyWystarczyAkcji(String idAkcji, int liczbaAkcji) {
        return akcje.get(idAkcji) >= liczbaAkcji;
    }

    // Zwraca szacunkową wartość portfela, liczoną jako suma gotówki i posiadanych akcji spółek danych jako argument,
    // liczonych po cenie ostatniej transakcji.
    public int wartość(Spółka[] spółki) {
        int wynik = gotówka;
        for (Spółka spółka : spółki) {
            wynik += akcje.getOrDefault(spółka.akcja().id(), 0) * spółka.akcja().cenaOstatniejTransakcji();
        }
        return wynik;
    }

    public void przelejGotówkę(int ile, PortfelInwestycyjny komu) {
        if (gotówka < ile) {
            throw new IllegalArgumentException("Niewystarczające środki: " + gotówka + " < " + ile + ".");
        }

        this.gotówka -= ile;
        komu.gotówka += ile;
    }

    public void przepiszAkcje(int ile, String idAkcji, PortfelInwestycyjny komu) {
        if (!akcje.containsKey(idAkcji) || !komu.akcje.containsKey(idAkcji)) {
            throw new IllegalArgumentException("Niepoprawny identyfikator akcji: " + idAkcji + ".");
        }
        if (akcje.get(idAkcji) < ile) {
            throw new IllegalArgumentException(String.format("Niewystarczająca liczba akcji %s w portfelu: %d < %d",
                    idAkcji, akcje.get(idAkcji), ile));
        }

        this.akcje.put(idAkcji, this.akcje.get(idAkcji) - ile);
        komu.akcje.put(idAkcji, komu.akcje.get(idAkcji) + ile);
    }

    // Uzupełnia słownik (mapę) akcji o akcje podanych spółek (w liczbie 0).
    public void uzupełnijOAkcje(Spółka[] spółki) {
        for (Spółka spółka : spółki) {
            String idAkcji = spółka.akcja().id();
            if (!akcje.containsKey(idAkcji)) {
                akcje.put(idAkcji, 0);
            }
        }
    }
}
