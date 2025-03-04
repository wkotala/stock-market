package zlecenia;

public class Natychmiastowy extends Terminowy {
    public Natychmiastowy(int aktualnaTura) {
        super(aktualnaTura);
    }

    @Override
    public String toString() {
        return "natychmiastowe";
    }
}
