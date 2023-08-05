package bank.core;

import bank.entities.bank.Bank;
import bank.entities.bank.BranchBank;
import bank.entities.bank.CentralBank;
import bank.entities.client.Adult;
import bank.entities.client.Client;
import bank.entities.client.Student;
import bank.entities.loan.Loan;
import bank.entities.loan.MortgageLoan;
import bank.entities.loan.StudentLoan;
import bank.repositories.LoanRepository;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static bank.common.ConstantMessages.*;
import static bank.common.ExceptionMessages.*;

public class ControllerImpl implements Controller {
    private LoanRepository loan;
    private Map<String, Bank> banks;

    public ControllerImpl() {
        this.loan = new LoanRepository();
        this.banks = new LinkedHashMap<>();
    }


    @Override
    public String addBank(String type, String name) {
        Bank bank;
        switch (type) {
            case "CentralBank":
                bank = new CentralBank(name);
                break;
            case "BranchBank":
                bank = new BranchBank(name);
                break;
            default:
                throw new IllegalArgumentException(INVALID_BANK_TYPE);
        }
        this.banks.put(name, bank);
        return String.format(SUCCESSFULLY_ADDED_BANK_OR_LOAN_TYPE, type);
    }

    @Override
    public String addLoan(String type) {
        Loan loan;
        switch (type) {
            case "StudentLoan":
                loan = new StudentLoan();
                break;
            case "MortgageLoan":
                loan = new MortgageLoan();
                break;
            default:
                throw new IllegalArgumentException(INVALID_LOAN_TYPE);
        }
        this.loan.addLoan(loan);
        return String.format(SUCCESSFULLY_ADDED_BANK_OR_LOAN_TYPE, type);
    }

    @Override
    public String returnedLoan(String bankName, String loanType) {
        Loan returnedLoan = this.loan.findFirst(loanType);
        if (returnedLoan == null) {
            throw new IllegalArgumentException(String.format(NO_LOAN_FOUND, loanType));

        }
        Bank bank = this.banks.get(bankName);
        bank.addLoan(returnedLoan);
        this.loan.removeLoan(returnedLoan);

        return String.format(SUCCESSFULLY_ADDED_CLIENT_OR_LOAN_TO_BANK, loanType, bankName);
    }

    @Override
    public String addClient(String bankName, String clientType, String clientName, String clientID, double income) {
        Client client;
        switch (clientType) {
            case "Student":
                client = new Student(clientName, clientID, income);
                break;
            case "Adult":
                client = new Adult(clientName, clientID, income);
                break;
            default:
                throw new IllegalArgumentException(INVALID_CLIENT_TYPE);
        }
        Bank bank = this.banks.get(bankName);
        String output;
        if (!isSuitableService(clientType, bank)) {
            output = UNSUITABLE_BANK;

        } else {
            bank.addClient(client);
            output = String.format(SUCCESSFULLY_ADDED_CLIENT_OR_LOAN_TO_BANK, clientType, bankName);
        }

        return output;
    }

    private boolean isSuitableService(String clientType, Bank bank) {
        String bankType = bank.getClass().getSimpleName();

        if (clientType.equals("Adult") && bankType.equals("CentralBank")) {
            return true;
        } else return clientType.equals("Student") && bankType.equals("BranchBank");
    }

    @Override
    public String finalCalculation(String bankName) {
        Bank bank = this.banks.get(bankName);
        double clientsIncome = bank.getClients().stream()
                .mapToDouble(Client::getIncome)
                .sum();
        double loansAmount = bank.getLoans().stream()
                .mapToDouble(Loan::getAmount)
                .sum();

        return String.format(FUNDS_BANK, bankName, clientsIncome + loansAmount);
    }

    @Override
    public String getStatistics() {
        return banks.values()
                .stream()
                .map(Bank::getStatistics)
                .collect(Collectors.joining(System.lineSeparator().trim()));

    }
}
