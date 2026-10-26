import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String transactionId;
    private final String accountNumber;
    private final String type;
    private final String description;
    private final String amount;
    private final String timestamp;

    public Transaction(String transactionId, String accountNumber, String type,
                       String description, String amount, String timestamp) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.type = type;
        this.description = description;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public static Transaction create(String accountNumber, String type,
                                     String description, String amount) {
        return new Transaction(
                "TXN" + System.currentTimeMillis() + (int)(Math.random() * 1000),
                accountNumber, type, description, amount,
                LocalDateTime.now().format(FORMATTER));
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String toFileLine() {
        return clean(transactionId) + "|" + clean(accountNumber) + "|" + clean(type)
                + "|" + clean(description) + "|" + clean(amount) + "|" + clean(timestamp);
    }

    public static Transaction fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length != 6) {
            throw new IllegalArgumentException("Invalid transaction record.");
        }
        return new Transaction(p[0], p[1], p[2], p[3], p[4], p[5]);
    }

    private static String clean(String value) {
        return value == null ? "" : value.replace("|", "/").replace("\n", " ");
    }

    @Override
    public String toString() {
        return timestamp + " | " + type + " | " + description + " | Amount: " + amount
                + " | Ref: " + transactionId;
    }
}
