package com.woms.database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple file + ArrayList based backup storage for user accounts.
 *
 * Format per line in accounts.txt:
 *   Role,Username,Password,TimeCreated
 */
public class AccountStorage {

    public static class Account {
        public String role;
        public String username;
        public String password;
        public String timeCreated;
    }

    // Use the accounts.txt file placed in the project root (Final project sa oop\accounts.txt)
    private static final String ACCOUNTS_FILE_NAME = "accounts.txt";
    private static final File ACCOUNTS_FILE = new File(ACCOUNTS_FILE_NAME);
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // In-memory cache
    private static final List<Account> accounts = new ArrayList<>();
    private static boolean loaded = false;

    private static synchronized void ensureLoaded() {
        if (!loaded) {
            loadFromFileInternal();
            loaded = true;
        }
    }

    private static void loadFromFileInternal() {
        accounts.clear();
        if (!ACCOUNTS_FILE.exists()) {
            return; // nothing to load yet
        }
        try (BufferedReader br = new BufferedReader(new FileReader(ACCOUNTS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split(",", 4);
                if (parts.length < 4) continue; // malformed
                Account acc = new Account();
                acc.role = parts[0].trim();
                acc.username = parts[1].trim();
                acc.password = parts[2].trim();
                acc.timeCreated = parts[3].trim();
                accounts.add(acc);
            }
        } catch (IOException e) {
            System.err.println("Failed to read accounts.txt: " + e.getMessage());
        }
    }

    private static synchronized void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ACCOUNTS_FILE))) {
            bw.write("# Role,Username,Password,TimeCreated");
            bw.newLine();
            for (Account acc : accounts) {
                String time = acc.timeCreated != null ? acc.timeCreated : LocalDateTime.now().format(TIME_FMT);
                bw.write(String.join(",",
                        safe(acc.role),
                        safe(acc.username),
                        safe(acc.password),
                        time));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Failed to write accounts.txt: " + e.getMessage());
        }
    }

    private static String safe(String s) {
        return s == null ? "" : s.replace(",", " ").trim();
    }

    public static synchronized void addOrUpdateAccount(String role, String username, String password, String timeCreated) {
        ensureLoaded();
        Account existing = findAccount(username);
        if (existing == null) {
            Account acc = new Account();
            acc.role = role;
            acc.username = username;
            acc.password = password;
            acc.timeCreated = timeCreated != null ? timeCreated : LocalDateTime.now().format(TIME_FMT);
            accounts.add(acc);
        } else {
            existing.role = role != null ? role : existing.role;
            if (password != null && !password.isEmpty()) {
                existing.password = password;
            }
            if (timeCreated != null) {
                existing.timeCreated = timeCreated;
            }
        }
        saveToFile();
    }

    public static synchronized boolean removeAccount(String username) {
        ensureLoaded();
        if (username == null) return false;
        boolean removed = accounts.removeIf(a -> username.equalsIgnoreCase(a.username));
        if (removed) {
            saveToFile();
        }
        return removed;
    }

    public static synchronized boolean authenticate(String expectedRole, String username, String password) {
        ensureLoaded();
        Account acc = findAccount(username);
        if (acc == null) return false;
        if (expectedRole != null && acc.role != null && !expectedRole.equalsIgnoreCase(acc.role)) {
            return false;
        }
        return acc.password != null && acc.password.equals(password);
    }

    private static Account findAccount(String username) {
        if (username == null) return null;
        for (Account a : accounts) {
            if (username.equalsIgnoreCase(a.username)) {
                return a;
            }
        }
        return null;
    }

    /**
     * Export all existing H2 users into accounts.txt, merging with any
     * offline accounts already present in the file.
     */
    public static synchronized void syncFromDatabase() {
        ensureLoaded();
        try {
            List<Account> dbAccounts = Database.getAllUserAccountsForSync();
            for (Account dbAcc : dbAccounts) {
                Account existing = findAccount(dbAcc.username);
                if (existing == null) {
                    // New account from DB
                    accounts.add(dbAcc);
                } else {
                    // Keep existing timeCreated, but refresh role/password from DB
                    existing.role = dbAcc.role;
                    existing.password = dbAcc.password;
                }
            }
            saveToFile();
        } catch (Exception e) {
            System.err.println("Account sync from DB failed: " + e.getMessage());
            // If DB is offline, just keep whatever we have in the file
        }
    }

    /**
     * Attempt to push accounts that only exist in accounts.txt into H2 users table
     * when the database is available.
     */
    public static synchronized void syncToDatabase() {
        ensureLoaded();
        try {
            List<Account> dbAccounts = Database.getAllUserAccountsForSync();
            for (Account fileAcc : accounts) {
                boolean existsInDb = false;
                for (Account dbAcc : dbAccounts) {
                    if (fileAcc.username != null && fileAcc.username.equalsIgnoreCase(dbAcc.username)) {
                        existsInDb = true;
                        break;
                    }
                }
                if (!existsInDb && fileAcc.username != null && !fileAcc.username.isEmpty()) {
                    // Insert minimal account into DB (no person/profile info)
                    Database.createUserAccountFromSync(fileAcc.role, fileAcc.username, fileAcc.password);
                }
            }
        } catch (Exception e) {
            System.err.println("Account sync TO DB failed: " + e.getMessage());
            // Safe to ignore in offline mode
        }
    }
}
