package bank.entities.client;

public class Adult extends BaseClient{
    //Can only live in CentralBank!
    private static final int INTEREST=4;
    public Adult(String name, String ID,  double income) {
        super(name, ID, INTEREST, income);
    }

    @Override
    public void increase() {
        this.setInterest(this.getInterest()+2);
    }
}
