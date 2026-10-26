import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bank {
    private final List<BankAccount> accounts;
    private final List<Transaction> transactions;
    private final FileStorage storage;
    private long nextAccountNumber;

    public Bank() {
        storage = new FileStorage();
        accounts = new ArrayList<BankAccount>(storage.loadAccounts());
        transactions = new ArrayList<Transaction>(storage.loadTransactions());
        nextAccountNumber = findNextAccountNumber();
    }

    public BankAccount createAccount(String name, String pin, BigDecimal openingDeposit) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        if (pin == null || !pin.matches("\\d{4}")) {
            throw new IllegalArgumentException("PIN must be exactly 4 digits.");
        }
        if (openingDeposit == null || openingDeposit.compareTo(BigDecimal.ZERO) < 0
                || openingDeposit.scale() > 2) {
            throw new IllegalArgumentException("Opening deposit must be zero or greater with at most 2 decimals.");
        }

        String accountNumber;
        do {
            accountNumber = String.format("100%06d", nextAccountNumber++);
        } while (findAccount(accountNumber) != null);

        BankAccount account = new BankAccount(accountNumber, name.trim(), pin, openingDeposit);
        accounts.add(account);
        try {
            storage.saveAccounts(accounts);
            record(accountNumber, "ACCOUNT_CREATED", "Account opened", openingDeposit.toPlainString());
        } catch (RuntimeException ex) {
            accounts.remove(account);
            throw ex;
        }
        return account;
    }

    public BankAccount authenticate(String accountNumber, String pin) {
        BankAccount account = findAccount(accountNumber);
        return account != null && account.verifyPin(pin) ? account : null;
    }

    public void deposit(String accountNumber, BigDecimal amount) {
        BankAccount account = requireAccount(accountNumber);
        BigDecimal previous = account.getBalance();
        account.deposit(amount);
        try {
            storage.saveAccounts(accounts);
            record(accountNumber, "DEPOSIT", "Cash deposit", amount.toPlainString());
        } catch (RuntimeException ex) {
            account.restoreBalance(previous);
            storage.saveAccounts(accounts);
            throw ex;
        }
    }

    public void withdraw(String accountNumber, BigDecimal amount) {
        BankAccount account = requireAccount(accountNumber);
        BigDecimal previous = account.getBalance();
        account.withdraw(amount);
        try {
            storage.saveAccounts(accounts);
            record(accountNumber, "WITHDRAWAL", "Cash withdrawal", amount.toPlainString());
        } catch (RuntimeException ex) {
            account.restoreBalance(previous);
            storage.saveAccounts(accounts);
            throw ex;
        }
    }

    public void transfer(String fromNumber, String toNumber, BigDecimal amount) {
        if (fromNumber.equals(toNumber)) {
            throw new IllegalArgumentException("Source and recipient accounts must be different.");
        }
        BankAccount from = requireAccount(fromNumber);
        BankAccount to = requireAccount(toNumber);
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0 || amount.scale() > 2) {
            throw new IllegalArgumentException("Transfer amount must be positive and have at most 2 decimals.");
        }
        BigDecimal oldFrom = from.getBalance();
        BigDecimal oldTo = to.getBalance();

        from.withdraw(amount);
        to.deposit(amount);
        try {
            storage.saveAccounts(accounts);
            record(fromNumber, "TRANSFER_SENT", "Transfer to " + toNumber, amount.toPlainString());
            record(toNumber, "TRANSFER_RECEIVED", "Transfer from " + fromNumber, amount.toPlainString());
        } catch (RuntimeException ex) {
            from.restoreBalance(oldFrom);
            to.restoreBalance(oldTo);
            storage.saveAccounts(accounts);
            throw ex;
        }
    }

    public void printTransactionHistory(String accountNumber) {
        requireAccount(accountNumber);
        System.out.println("\n--- Transaction History ---");
        boolean found = false;
        for (Transaction transaction : transactions) {
            if (transaction.getAccountNumber().equals(accountNumber)) {
                System.out.println(transaction);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No transactions found.");
        }
    }

    private void record(String accountNumber, String type, String description, String amount) {
        Transaction transaction = Transaction.create(accountNumber, type, description, amount);
        storage.appendTransaction(transaction);
        transactions.add(transaction);
    }

    private BankAccount requireAccount(String accountNumber) {
        BankAccount account = findAccount(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account not found.");
        }
        return account;
    }

    private BankAccount findAccount(String accountNumber) {
        for (BankAccount account : accounts) {
            if (account.getAccountNumber().equals(accountNumber)) {
                return account;
            }
        }
        return null;
    }

    private long findNextAccountNumber() {
        long max = 0;
        for (BankAccount account : accounts) {
            String number = account.getAccountNumber();
            if (number.startsWith("100")) {
                try {
                    max = Math.max(max, Long.parseLong(number.substring(3)));
                } catch (NumberFormatException ignored) {
                    // Ignore malformed account number when finding the next number.
                }
            }
        }
        return max + 1;
    }
}
