package zlecenia;

public class Terminowy extends TerminWażnościZlecenia {
    private final int ostatniaTura;

    public Terminowy(int ostatniaTura) {
        this.ostatniaTura = ostatniaTura;
    }

    @Override
    public String toString() {
        return "terminowe (do tury " + ostatniaTura + ")";
    }

    @Override
    public boolean upłynął(int nrTury) {
        return nrTury > ostatniaTura;
    }
}
