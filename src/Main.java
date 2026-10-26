import java.math.BigDecimal;
import java.util.Scanner;

public class Main {
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        Bank bank = new Bank();
        System.out.println("==========================================");
        System.out.println("     ATCHAYA BANKING MANAGEMENT SYSTEM");
        System.out.println("==========================================");

        while (true) {
            System.out.println("\n1. Create account");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            int choice = readInt("Choose an option: ");

            switch (choice) {
                case 1:
                    createAccount(bank);
                    break;
                case 2:
                    login(bank);
                    break;
                case 3:
                    System.out.println("Thank you for using the banking system.");
                    SCANNER.close();
                    return;
                default:
                    System.out.println("Invalid option. Choose 1, 2, or 3.");
            }
        }
    }

    private static void createAccount(Bank bank) {
        System.out.println("\n--- Create Account ---");
        System.out.print("Enter your full name: ");
        String name = SCANNER.nextLine().trim();
        if (name.isEmpty() || name.contains("|")) {
            System.out.println("Name cannot be empty or contain the | character.");
            return;
        }

        String pin = readPin("Create a 4-digit PIN: ");
        if (pin == null) {
            return;
        }

        BigDecimal openingDeposit = readMoney("Opening deposit (minimum 0.00): ");
        if (openingDeposit == null || openingDeposit.compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("Invalid opening deposit.");
            return;
        }

        try {
            BankAccount account = bank.createAccount(name, pin, openingDeposit);
            System.out.println("\nAccount created successfully!");
            System.out.println("Your account number is: " + account.getAccountNumber());
            System.out.println("Please keep your account number safe.");
        } catch (RuntimeException ex) {
            System.out.println("Could not create account: " + ex.getMessage());
        }
    }

    private static void login(Bank bank) {
        System.out.println("\n--- Account Login ---");
        String accountNumber = readNonEmpty("Account number: ");
        String pin = readPin("4-digit PIN: ");
        if (pin == null) {
            return;
        }

        BankAccount account = bank.authenticate(accountNumber, pin);
        if (account == null) {
            System.out.println("Invalid account number or PIN.");
            return;
        }

        System.out.println("Welcome, " + account.getHolderName() + "!");
        accountMenu(bank, account);
    }

    private static void accountMenu(Bank bank, BankAccount account) {
        while (true) {
            System.out.println("\n--- Account Menu (" + account.getAccountNumber() + ") ---");
            System.out.println("1. Check balance");
            System.out.println("2. Deposit money");
            System.out.println("3. Withdraw money");
            System.out.println("4. Transfer money");
            System.out.println("5. View transaction history");
            System.out.println("6. Account details");
            System.out.println("7. Logout");

            int choice = readInt("Choose an option: ");
            switch (choice) {
                case 1:
                    System.out.println("Available balance: " + account.getBalance().toPlainString());
                    break;
                case 2: {
                    BigDecimal amount = readPositiveMoney("Deposit amount: ");
                    if (amount != null) {
                        perform(() -> bank.deposit(account.getAccountNumber(), amount),
                                "Deposit completed successfully.");
                    }
                    break;
                }
                case 3: {
                    BigDecimal amount = readPositiveMoney("Withdrawal amount: ");
                    if (amount != null) {
                        perform(() -> bank.withdraw(account.getAccountNumber(), amount),
                                "Withdrawal completed successfully.");
                    }
                    break;
                }
                case 4: {
                    String recipient = readNonEmpty("Recipient account number: ");
                    BigDecimal amount = readPositiveMoney("Transfer amount: ");
                    if (amount != null) {
                        perform(() -> bank.transfer(account.getAccountNumber(), recipient, amount),
                                "Transfer completed successfully.");
                    }
                    break;
                }
                case 5:
                    bank.printTransactionHistory(account.getAccountNumber());
                    break;
                case 6:
                    System.out.println("Account number: " + account.getAccountNumber());
                    System.out.println("Account holder: " + account.getHolderName());
                    System.out.println("Balance: " + account.getBalance().toPlainString());
                    break;
                case 7:
                    System.out.println("Logged out successfully.");
                    return;
                default:
                    System.out.println("Invalid option. Choose 1 to 7.");
            }
        }
    }

    private static void perform(BankOperation operation, String successMessage) {
        try {
            operation.run();
            System.out.println(successMessage);
        } catch (RuntimeException ex) {
            System.out.println("Operation failed: " + ex.getMessage());
        }
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = SCANNER.nextLine().trim();
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException ex) {
                System.out.println("Enter a valid whole number.");
            }
        }
    }

    private static String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = SCANNER.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("This field cannot be empty.");
        }
    }

    private static String readPin(String prompt) {
        System.out.print(prompt);
        String pin = SCANNER.nextLine().trim();
        if (!pin.matches("\\d{4}")) {
            System.out.println("PIN must contain exactly 4 digits.");
            return null;
        }
        return pin;
    }

    private static BigDecimal readMoney(String prompt) {
        System.out.print(prompt);
        String value = SCANNER.nextLine().trim();
        try {
            BigDecimal amount = new BigDecimal(value);
            if (amount.scale() > 2) {
                System.out.println("Enter at most 2 decimal places.");
                return null;
            }
            return amount.setScale(2);
        } catch (NumberFormatException ex) {
            System.out.println("Enter a valid amount, for example 500.00.");
            return null;
        }
    }

    private static BigDecimal readPositiveMoney(String prompt) {
        BigDecimal amount = readMoney(prompt);
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Amount must be greater than zero.");
            return null;
        }
        return amount;
    }

    private interface BankOperation {
        void run();
    }
}
