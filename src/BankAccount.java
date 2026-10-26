import java.math.BigDecimal;

public class BankAccount {
    private final String accountNumber;
    private final String holderName;
    private final String pin;
    private BigDecimal balance;

    public BankAccount(String accountNumber, String holderName, String pin, BigDecimal balance) {
        if (accountNumber == null || holderName == null || pin == null || balance == null) {
            throw new IllegalArgumentException("Account fields cannot be null.");
        }
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.pin = pin;
        this.balance = balance.setScale(2);
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public boolean verifyPin(String enteredPin) {
        return pin.equals(enteredPin);
    }

    public void deposit(BigDecimal amount) {
        validatePositiveAmount(amount);
        balance = balance.add(amount).setScale(2);
    }

    public void withdraw(BigDecimal amount) {
        validatePositiveAmount(amount);
        if (balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance.");
        }
        balance = balance.subtract(amount).setScale(2);
    }

    private void validatePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0
                || amount.scale() > 2) {
            throw new IllegalArgumentException("Amount must be positive and have at most 2 decimal places.");
        }
    }

    // Used by the file-storage layer to restore a saved balance.
    void restoreBalance(BigDecimal savedBalance) {
        if (savedBalance == null || savedBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Invalid saved balance.");
        }
        balance = savedBalance.setScale(2);
    }

    // Package-private for saving data; do not display the PIN to users.
    String getPinForStorage() {
        return pin;
    }
}
