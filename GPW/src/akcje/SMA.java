package akcje;

import java.util.ArrayDeque;
import java.util.Queue;

public class SMA {
    private final Queue<Integer> ceny;
    private final int liczbaTur; // liczba tur, z których bierzemy średnią
    private int suma;
    private Double poprzednieSMA;

    public SMA(int liczbaTur, int pierwszaCena) {
        this.ceny = new ArrayDeque<>();
        this.ceny.add(pierwszaCena);
        this.liczbaTur = liczbaTur;
        this.suma = pierwszaCena;
        this.poprzednieSMA = null;
    }

    public void dodajDaną(int cena) {
        try {
            poprzednieSMA = aktualneSMA();
        } catch (ZaMałoDanychSMA e) {
            poprzednieSMA = null;
        }

        if (ceny.size() >= liczbaTur) {
            suma -= ceny.remove();
        }
        ceny.add(cena);
        suma += cena;
    }

    public boolean sygnałKupna(SMA drugieSMA) {
        SMA krótsze = this.liczbaTur < drugieSMA.liczbaTur ? this : drugieSMA;
        SMA dłuższe = this.liczbaTur < drugieSMA.liczbaTur ? drugieSMA : this;

        try {
            return dłuższe.poprzednieSMA() >= krótsze.poprzednieSMA() && dłuższe.aktualneSMA() < krótsze.aktualneSMA();
        } catch (ZaMałoDanychSMA e) {
            return false;
        }
    }

    public boolean sygnałSprzedaży(SMA drugieSMA) {
        SMA krótsze = this.liczbaTur < drugieSMA.liczbaTur ? this : drugieSMA;
        SMA dłuższe = this.liczbaTur < drugieSMA.liczbaTur ? drugieSMA : this;

        try {
            return krótsze.poprzednieSMA() >= dłuższe.poprzednieSMA() && krótsze.aktualneSMA() < dłuższe.aktualneSMA();
        } catch (ZaMałoDanychSMA e) {
            return false;
        }
    }

    private double aktualneSMA() throws ZaMałoDanychSMA {
        if (ceny.size() < liczbaTur) {
            throw new ZaMałoDanychSMA();
        }

        return (double) suma / liczbaTur;
    }

    private double poprzednieSMA() throws ZaMałoDanychSMA {
        if (poprzednieSMA == null) {
            throw new ZaMałoDanychSMA();
        }

        return poprzednieSMA;
    }

}
