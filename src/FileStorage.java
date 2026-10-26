import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class FileStorage {
    private final Path dataDirectory = Paths.get("data");
    private final Path accountsFile = dataDirectory.resolve("accounts.txt");
    private final Path transactionsFile = dataDirectory.resolve("transactions.txt");

    public FileStorage() {
        try {
            Files.createDirectories(dataDirectory);
            if (Files.notExists(accountsFile)) {
                Files.createFile(accountsFile);
            }
            if (Files.notExists(transactionsFile)) {
                Files.createFile(transactionsFile);
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to initialize data files: " + ex.getMessage(), ex);
        }
    }

    public List<BankAccount> loadAccounts() {
        List<BankAccount> accounts = new ArrayList<BankAccount>();
        try (BufferedReader reader = Files.newBufferedReader(accountsFile, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                try {
                    String[] p = line.split("\\|", -1);
                    if (p.length != 4) {
                        continue;
                    }
                    accounts.add(new BankAccount(p[0], p[1], p[2], new BigDecimal(p[3])));
                } catch (RuntimeException ex) {
                    System.err.println("Skipping invalid account record.");
                }
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to read accounts: " + ex.getMessage(), ex);
        }
        return accounts;
    }

    public void saveAccounts(List<BankAccount> accounts) {
        try (BufferedWriter writer = Files.newBufferedWriter(accountsFile, StandardCharsets.UTF_8,
                StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
            for (BankAccount account : accounts) {
                writer.write(clean(account.getAccountNumber()) + "|"
                        + clean(account.getHolderName()) + "|"
                        + clean(account.getPinForStorage()) + "|"
                        + account.getBalance().toPlainString());
                writer.newLine();
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to save accounts: " + ex.getMessage(), ex);
        }
    }

    public List<Transaction> loadTransactions() {
        List<Transaction> transactions = new ArrayList<Transaction>();
        try (BufferedReader reader = Files.newBufferedReader(transactionsFile, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                try {
                    transactions.add(Transaction.fromFileLine(line));
                } catch (RuntimeException ex) {
                    System.err.println("Skipping invalid transaction record.");
                }
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to read transactions: " + ex.getMessage(), ex);
        }
        return transactions;
    }

    public void appendTransaction(Transaction transaction) {
        try (BufferedWriter writer = Files.newBufferedWriter(transactionsFile, StandardCharsets.UTF_8,
                StandardOpenOption.APPEND, StandardOpenOption.WRITE)) {
            writer.write(transaction.toFileLine());
            writer.newLine();
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to save transaction: " + ex.getMessage(), ex);
        }
    }

    private String clean(String value) {
        return value.replace("|", "/").replace("\n", " ").replace("\r", " ");
    }
}
