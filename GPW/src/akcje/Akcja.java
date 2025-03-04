package akcje;

import giełda.ArkuszZleceń;

public class Akcja {
    private final String id;
    private final ArkuszZleceń arkuszZleceń;
    private final SMA sma5;
    private final SMA sma10;
    private int cenaOstatniejTransakcji;
    private int turaOstatniejTransakcji;
    private int liczbaWyemitowanych; // łączna liczba danych akcji w symulacji

    public Akcja(String id, int cenaOstatniejTransakcji) {
        this.id = id;
        this.cenaOstatniejTransakcji = cenaOstatniejTransakcji;
        this.turaOstatniejTransakcji = -1;
        this.arkuszZleceń = new ArkuszZleceń(this);
        this.sma5 = new SMA(5, cenaOstatniejTransakcji);
        this.sma10 = new SMA(10, cenaOstatniejTransakcji);
    }

    @Override
    public String toString() {
        return id + " (" + cenaOstatniejTransakcji + ", " + turaOstatniejTransakcji + ")";
    }

    public void ustawLiczbęWyemitowanych(int łącznaLiczbaAkcji) {
        liczbaWyemitowanych = łącznaLiczbaAkcji;
    }

    public String id() {
        return id;
    }

    public int cenaOstatniejTransakcji() {
        return cenaOstatniejTransakcji;
    }

    public int turaOstatniejTransakcji() {
        return turaOstatniejTransakcji;
    }

    public ArkuszZleceń arkuszZleceń() {
        return arkuszZleceń;
    }

    public int liczbaWyemitowanych() {
        return liczbaWyemitowanych;
    }

    public boolean sygnałKupna() {
        return sma10.sygnałKupna(sma5);
    }

    public boolean sygnałSprzedaży() {
        return sma10.sygnałSprzedaży(sma5);
    }

    public void aktualizujOstatniąTransakcję(int cena, int tura) {
        this.cenaOstatniejTransakcji = cena;
        this.turaOstatniejTransakcji = tura;
    }

    public void aktualizujPoTurze() {
        sma5.dodajDaną(cenaOstatniejTransakcji);
        sma10.dodajDaną(cenaOstatniejTransakcji);
    }
}
