JAVA CONSOLE BANKING MANAGEMENT SYSTEM
======================================

REQUIREMENTS
------------
- JDK 8 or newer
- Command Prompt / Terminal or an IDE such as IntelliJ IDEA, Eclipse, or VS Code

FOLDER STRUCTURE
----------------
BankingManagementSystem/
  src/Main.java
  src/Bank.java
  src/BankAccount.java
  src/Transaction.java
  src/FileStorage.java
  data/accounts.txt          (created automatically)
  data/transactions.txt     (created automatically)

RUN FROM THE PROJECT ROOT
-------------------------
1. Open a terminal in the BankingManagementSystem folder.
2. Compile:
   javac -d out src/*.java
3. Run:
   java -cp out Main

IMPORTANT: Run the program from the project root folder so that the relative "data"
directory is created in the correct place.

FEATURES
--------
- Create account with a generated account number
- Login using account number and 4-digit PIN
- Check balance
- Deposit and withdraw money
- Transfer money between accounts
- View transaction history
- Persist accounts and transactions in text files

JAVA TOPICS USED
----------------
1. Classes and objects: Bank, BankAccount, Transaction, FileStorage.
2. Encapsulation: account fields are private and accessed through methods.
3. Constructors: initialize account and transaction objects.
4. Collections: ArrayList stores accounts and transaction history in memory.
5. Control flow: if/else, switch, while, and for loops.
6. Methods: each banking action is separated into a method.
7. Exception handling: try/catch protects input parsing and file operations.
8. File handling: BufferedReader, BufferedWriter, Files, and Path.
9. String handling: split, trim, replace, and regular expressions.
10. BigDecimal: safer decimal arithmetic for money than float/double.
11. Date and time: LocalDateTime and DateTimeFormatter timestamp transactions.
12. Validation: prevents blank names, malformed PINs, non-positive transactions,
    overdrafts, and transfers to the same account.
13. Separation of concerns: user interface, business logic, models, and persistence
    are placed in separate classes.

DATA FORMAT
-----------
accounts.txt:
accountNumber|holderName|pin|balance

transactions.txt:
transactionId|accountNumber|type|description|amount|timestamp

SECURITY WARNING
----------------
This is an educational console project, not production banking software.
The sample stores PINs in plain text, so do not use real account information or
real PINs. A production application should hash PINs with a suitable password
hashing algorithm, enforce access controls, protect files, use a database,
and implement reliable transactional persistence and audit logging.
