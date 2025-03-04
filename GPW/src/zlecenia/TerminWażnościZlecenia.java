package zlecenia;

public abstract class TerminWażnościZlecenia {
    // konstruktor domyślny

    @Override
    public abstract String toString();

    public abstract boolean upłynął(int nrTury);

    public boolean musiByćWykonaneWCałości() {
        return false;
    }
}
