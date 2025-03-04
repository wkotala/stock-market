package zlecenia;

public class WykonajLubAnuluj extends Natychmiastowy {
    public WykonajLubAnuluj(int aktualnaTura) {
        super(aktualnaTura);
    }

    @Override
    public String toString() {
        return "wykonaj lub anuluj";
    }

    @Override
    public boolean musiByćWykonaneWCałości() {
        return true;
    }
}
