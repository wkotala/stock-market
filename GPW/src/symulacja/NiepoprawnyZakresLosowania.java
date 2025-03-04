package symulacja;

public class NiepoprawnyZakresLosowania extends RuntimeException {
    public NiepoprawnyZakresLosowania(String opis) {
        super(opis);
    }
}
