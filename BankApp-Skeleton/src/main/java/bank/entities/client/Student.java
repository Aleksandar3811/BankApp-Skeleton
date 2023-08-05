package bank.entities.client;

public class Student extends BaseClient{
    //Can only live in BranchBank!
    private static final int INTEREST=2;
    public Student(String name, String ID,  double income) {
        super(name, ID, INTEREST, income);
    }

    @Override
    public void increase() {
        this.setInterest(this.getInterest()+1);
    }
}
