package akcje;

public class Spółka {
    private final String id;
    private final Akcja akcja;

    public Spółka(String idSpółki, Akcja akcja) {
        this.id = idSpółki;
        this.akcja = akcja;
    }

    @Override
    public String toString() {
        return "Spółka " + id;
    }

    public Akcja akcja() {
        return akcja;
    }
}
