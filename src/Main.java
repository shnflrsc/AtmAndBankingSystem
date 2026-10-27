import java.util.Scanner;

public class Main {

    static final String BANK = "Flower-Esque Bank";
    static final double FEE = 10;
    static final double MAX_AMOUNT = 50000;

    static LinkedList accounts = new LinkedList();
    static LinkedList transactions = new LinkedList();
    static Scanner input = new Scanner(System.in);
    static int nextAccount = 1001;
    static int nextTransaction = 1;

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n===== " + BANK + " =====");
            System.out.println("1. Create Account");
            System.out.println("2. Login");
            System.out.println("3. Bank Report");
            System.out.println("4. Exit");
            String choice = readText("Choose: ");

            if (choice.equals("1")) {
                createAccount();
            } else if (choice.equals("2")) {
                LinkedList.Node account = login();
                if (account != null) {
                    accountMenu(account);
                }
            } else if (choice.equals("3")) {
                report();
            } else if (choice.equals("4")) {
                System.out.println("Thank you for banking with " + BANK + "!");
                return;
            } else {
                System.out.println("Invalid choice. Please enter 1 to 4.");
            }
        }
    }

    static String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String text = input.nextLine().trim();
            if (!text.isEmpty()) {
                return text;
            }
            System.out.println("Input cannot be empty.");
        }
    }

    static String readPin(String prompt) {
        while (true) {
            String pin = readText(prompt);
            if (pin.matches("\\d{4}")) {
                return pin;
            }
            System.out.println("PIN must be exactly 4 digits.");
        }
    }

    static double readAmount(String prompt) {
        while (true) {
            String text = readText(prompt);
            try {
                double amount = Double.parseDouble(text);
                amount = Math.round(amount * 100) / 100.0;
                if (amount > 0 && amount <= MAX_AMOUNT) {
                    return amount;
                }
                System.out.printf("Amount must be more than 0 and not above %.2f.%n", MAX_AMOUNT);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    static void createAccount() {
        System.out.println("\n--- Create Account ---");
        String name = readText("Full name: ");
        while (!name.matches("[A-Za-z ]+")) {
            System.out.println("Name must contain letters and spaces only.");
            name = readText("Full name: ");
        }
        String pin = readPin("Set a 4-digit PIN: ");
        double opening = readAmount("Opening deposit: ");

        String number = String.valueOf(nextAccount);
        nextAccount++;
        accounts.add(new LinkedList.Node(number, name, pin, opening));
        System.out.println("Account created! Your account number is " + number);
        printReceipt(record(number, "Deposit", "Opening deposit", opening, opening));
    }

    static LinkedList.Node login() {
        System.out.println("\n--- Login ---");
        LinkedList.Node account = accounts.find(readText("Account number: "));
        if (account == null) {
            System.out.println("Account not found.");
            return null;
        }
        for (int tries = 3; tries > 0; tries--) {
            if (readText("PIN: ").equals(account.pin)) {
                System.out.println("Welcome, " + account.name + "!");
                return account;
            }
            System.out.println("Wrong PIN. Attempts left: " + (tries - 1));
        }
        System.out.println("Too many wrong attempts. Returning to main menu.");
        return null;
    }

    static void accountMenu(LinkedList.Node account) {
        while (true) {
            System.out.println("\n===== " + BANK + " ATM =====");
            System.out.println("Account: " + account.id + " - " + account.name);
            System.out.println("1. Balance Inquiry");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Fund Transfer");
            System.out.println("5. Transaction History");
            System.out.println("6. View Receipt");
            System.out.println("7. Change PIN");
            System.out.println("8. Close Account");
            System.out.println("9. Logout");
            String choice = readText("Choose: ");

            if (choice.equals("1")) {
                System.out.printf("Current balance: %.2f%n", account.amount);
            } else if (choice.equals("2")) {
                deposit(account);
            } else if (choice.equals("3")) {
                withdraw(account);
            } else if (choice.equals("4")) {
                transfer(account);
            } else if (choice.equals("5")) {
                history(account);
            } else if (choice.equals("6")) {
                viewReceipt(account);
            } else if (choice.equals("7")) {
                changePin(account);
            } else if (choice.equals("8")) {
                if (closeAccount(account)) {
                    return;
                }
            } else if (choice.equals("9")) {
                System.out.println("You have been logged out.");
                return;
            } else {
                System.out.println("Invalid choice. Please enter 1 to 9.");
            }
        }
    }

    static void deposit(LinkedList.Node account) {
        double amount = readAmount("Deposit amount: ");
        double newBalance = account.amount + amount;
        accounts.update(account.id, newBalance);
        printReceipt(record(account.id, "Deposit", "Cash deposit", amount, newBalance));
    }

    static void withdraw(LinkedList.Node account) {
        double amount = readAmount("Withdraw amount: ");
        if (amount > account.amount) {
            System.out.printf("Insufficient funds. Your balance is %.2f%n", account.amount);
            return;
        }
        double newBalance = account.amount - amount;
        accounts.update(account.id, newBalance);
        printReceipt(record(account.id, "Withdrawal", "Cash withdrawal", amount, newBalance));
    }

    static void transfer(LinkedList.Node account) {
        LinkedList.Node receiver = accounts.find(readText("Receiver account number: "));
        if (receiver == null) {
            System.out.println("Receiver account not found.");
            return;
        }
        if (receiver == account) {
            System.out.println("You cannot transfer to your own account.");
            return;
        }
        double amount = readAmount("Transfer amount: ");
        if (amount + FEE > account.amount) {
            System.out.printf("Insufficient funds. Amount plus %.2f fee is %.2f, your balance is %.2f%n",
                    FEE, amount + FEE, account.amount);
            return;
        }
        double senderBalance = account.amount - amount - FEE;
        double receiverBalance = receiver.amount + amount;
        accounts.update(account.id, senderBalance);
        accounts.update(receiver.id, receiverBalance);
        LinkedList.Node receipt = record(account.id, "Transfer Out", "To " + receiver.id + " (" + receiver.name + ")",
                amount, senderBalance);
        record(receiver.id, "Transfer In", "From " + account.id + " (" + account.name + ")",
                amount, receiverBalance);
        printReceipt(receipt);
    }

    static void history(LinkedList.Node account) {
        System.out.println("\n--- Transaction History for " + account.id + " ---");
        int count = 0;
        for (int i = 0; i < transactions.size(); i++) {
            LinkedList.Node t = transactions.getAt(i);
            if (t.pin.equals(account.id)) {
                System.out.printf("Receipt #%s | %s | %s | %.2f | Balance: %.2f%n",
                        t.id, t.date, t.name, t.amount, t.balanceAfter);
                count++;
            }
        }
        if (count == 0) {
            System.out.println("No transactions yet.");
        }
    }

    static void viewReceipt(LinkedList.Node account) {
        LinkedList.Node t = transactions.find(readText("Receipt number: "));
        if (t == null || !t.pin.equals(account.id)) {
            System.out.println("Receipt not found for this account.");
        } else {
            printReceipt(t);
        }
    }

    static void changePin(LinkedList.Node account) {
        if (!readText("Current PIN: ").equals(account.pin)) {
            System.out.println("Wrong PIN. PIN not changed.");
            return;
        }
        account.pin = readPin("New 4-digit PIN: ");
        System.out.println("PIN changed successfully.");
    }

    static boolean closeAccount(LinkedList.Node account) {
        if (account.amount > 0) {
            System.out.printf("Withdraw your remaining balance of %.2f before closing the account.%n", account.amount);
            return false;
        }
        if (readText("Type YES to close this account: ").equalsIgnoreCase("YES")) {
            accounts.remove(account.id);
            System.out.println("Account closed. Goodbye, " + account.name + "!");
            return true;
        }
        System.out.println("Account closing cancelled.");
        return false;
    }

    static LinkedList.Node record(String accountNumber, String type, String details, double amount, double balanceAfter) {
        LinkedList.Node t = new LinkedList.Node(String.valueOf(nextTransaction), type, accountNumber, amount);
        nextTransaction++;
        t.details = details;
        t.balanceAfter = balanceAfter;
        t.date = new java.util.Date().toString();
        transactions.add(t);
        return t;
    }

    static void printReceipt(LinkedList.Node t) {
        double fee = 0;
        if (t.name.equals("Transfer Out")) {
            fee = FEE;
        }
        System.out.println("\n==================================");
        System.out.println("        " + BANK);
        System.out.println("            RECEIPT");
        System.out.println("==================================");
        System.out.println("Receipt No : " + t.id);
        System.out.println("Date/Time  : " + t.date);
        System.out.println("Account    : " + t.pin);
        System.out.println("Type       : " + t.name);
        System.out.println("Details    : " + t.details);
        System.out.println("----------------------------------");
        System.out.printf("Amount     : %.2f%n", t.amount);
        System.out.printf("Fee        : %.2f%n", fee);
        System.out.printf("Total      : %.2f%n", t.amount + fee);
        System.out.println("----------------------------------");
        System.out.printf("Balance    : %.2f%n", t.balanceAfter);
        System.out.println("==================================");
    }

    static void report() {
        System.out.println("\n===== " + BANK + " Report =====");
        if (accounts.isEmpty()) {
            System.out.println("No accounts yet.");
            return;
        }
        double total = 0;
        for (int i = 0; i < accounts.size(); i++) {
            LinkedList.Node a = accounts.getAt(i);
            System.out.printf("%s | %-20s | %.2f%n", a.id, a.name, a.amount);
            total += a.amount;
        }
        System.out.println("Total accounts     : " + accounts.size());
        System.out.printf("Total money held   : %.2f%n", total);
        System.out.println("Total transactions : " + transactions.size());
    }
}
