package zlecenia;

public class Bezterminowy extends TerminWażnościZlecenia {
    // konstruktor domyślny

    @Override
    public String toString() {
        return "bezterminowe";
    }

    @Override
    public boolean upłynął(int nrTury) {
        return false;
    }
}
