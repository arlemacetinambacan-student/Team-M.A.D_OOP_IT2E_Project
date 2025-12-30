package com.woms.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String DB_URL = "jdbc:h2:./data/srms";

    static {
        try {
            Class.forName("org.h2.Driver");
            System.out.println("H2 JDBC driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("==========================================");
            System.err.println("WARNING: H2 JDBC driver not found!");
            System.err.println("==========================================");
            System.err.println("The driver will be attempted to load at runtime.");
            System.err.println("If you get 'No suitable driver' error, make sure");
            System.err.println("h2-1.4.200.jar (Java 8 compatible) is in your classpath.");
            System.err.println("==========================================");
        }
    }

    // Start H2 web console server
    public static void startWebConsole() {
        try {
            // Start H2 web server on port 8082 (H2 1.4.200 syntax)
            // Using reflection to avoid compile-time dependency issues
            Class<?> serverClass = Class.forName("org.h2.tools.Server");
            Object webServer = serverClass.getMethod("createWebServer", String[].class)
                .invoke(null, (Object) new String[]{"-web", "-webPort", "8082"});
            serverClass.getMethod("start").invoke(webServer);
            System.out.println("========================================");
            System.out.println("H2 Database Web Console Started!");
            System.out.println("========================================");
            System.out.println("Open your browser and go to:");
            System.out.println("http://localhost:8082");
            System.out.println("========================================");
            System.out.println("Connection Settings:");
            System.out.println("  JDBC URL: jdbc:h2:./data/srms");
            System.out.println("  User: (leave blank)");
            System.out.println("  Password: (leave blank)");
            System.out.println("========================================");
        } catch (Exception e) {
            System.err.println("Could not start H2 web console: " + e.getMessage());
            System.err.println("You can still use the application normally.");
            // Suppress stack trace for cleaner output
        }
    }

    // initialize DB and tables
    public static void init() {
        try (Connection conn = getConnection(); Statement s = conn.createStatement()) {
            // Migrate from email to username if old schema exists (do this FIRST before creating tables)
            try (ResultSet tables = conn.getMetaData().getTables(null, null, "USERS", null)) {
                if (tables.next()) {
                    try (ResultSet columns = conn.getMetaData().getColumns(null, null, "USERS", "EMAIL")) {
                        if (columns.next()) {
                            System.out.println("Detected old schema with email column. Migrating to username...");
                            
                            // Disable foreign key checks
                            s.execute("SET REFERENTIAL_INTEGRITY FALSE");
                            
                            // Backup old data
                            s.execute("CREATE TABLE users_backup AS SELECT * FROM users");
                            
                            // Drop dependent tables first (they will be recreated later)
                            try {
                                s.execute("DROP TABLE IF EXISTS household_members");
                                s.execute("DROP TABLE IF EXISTS persons");
                                s.execute("DROP TABLE IF EXISTS applications");
                                s.execute("DROP TABLE IF EXISTS applicant_needs");
                                s.execute("DROP TABLE IF EXISTS applicant_skills");
                                s.execute("DROP TABLE IF EXISTS training_enrollments");
                                s.execute("DROP TABLE IF EXISTS employment_status");
                            } catch (SQLException ex) {
                                // Some tables might not exist, that's okay
                            }
                            
                            // Drop and recreate users table
                            s.execute("DROP TABLE users");
                            s.execute("CREATE TABLE users(" +
                                    "user_id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                                    "username VARCHAR(255) UNIQUE, password VARCHAR(255), role VARCHAR(50))");
                            
                            // Migrate data (use email as username)
                            try {
                                s.execute("INSERT INTO users(user_id, username, password, role) " +
                                        "SELECT user_id, email, password, role FROM users_backup");
                                System.out.println("Data migrated successfully!");
                            } catch (SQLException ex) {
                                System.out.println("Migration note: " + ex.getMessage());
                            }
                            
                            // Drop backup
                            s.execute("DROP TABLE users_backup");
                            
                            // Re-enable foreign key checks
                            s.execute("SET REFERENTIAL_INTEGRITY TRUE");
                            System.out.println("Migration completed!");
                        }
                    }
                }
            } catch (SQLException e) {
                // No migration needed or table doesn't exist yet
                System.out.println("Schema check: " + e.getMessage());
            }
            
            // Enable foreign key constraints for H2
            s.execute("SET REFERENTIAL_INTEGRITY TRUE");
            
            // users
            s.execute("CREATE TABLE IF NOT EXISTS users(" +
                    "user_id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                    "username VARCHAR(255) UNIQUE, password VARCHAR(255), role VARCHAR(50))");
            // persons
            s.execute("CREATE TABLE IF NOT EXISTS persons(" +
                    "person_id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                    "user_id INTEGER, name VARCHAR(255), age INTEGER, phone VARCHAR(50), " +
                    "FOREIGN KEY(user_id) REFERENCES users(user_id))");
            // jobs
            s.execute("CREATE TABLE IF NOT EXISTS jobs(" +
                    "job_id INTEGER PRIMARY KEY AUTO_INCREMENT, title VARCHAR(255), description VARCHAR(1000), salary DOUBLE)");
            // applications
            s.execute("CREATE TABLE IF NOT EXISTS applications(" +
                    "application_id INTEGER PRIMARY KEY AUTO_INCREMENT, job_id INTEGER, applicant_id INTEGER, status VARCHAR(50), " +
                    "first_name VARCHAR(255), middle_name VARCHAR(255), last_name VARCHAR(255), gender VARCHAR(50), " +
                    "age INTEGER, address VARCHAR(500), experience VARCHAR(2000), submission_date VARCHAR(255), " +
                    "FOREIGN KEY(job_id) REFERENCES jobs(job_id), FOREIGN KEY(applicant_id) REFERENCES users(user_id))");
            // Add submission_date column to existing tables if it doesn't exist
            try {
                s.execute("ALTER TABLE applications ADD COLUMN submission_date VARCHAR(255)");
            } catch (SQLException e) {
                // Column already exists, ignore
            }
            // households
            s.execute("CREATE TABLE IF NOT EXISTS households(" +
                    "household_id INTEGER PRIMARY KEY AUTO_INCREMENT, name VARCHAR(255), address VARCHAR(500))");
            // household_members
            s.execute("CREATE TABLE IF NOT EXISTS household_members(" +
                    "household_id INTEGER, applicant_id INTEGER, " +
                    "PRIMARY KEY(household_id, applicant_id), " +
                    "FOREIGN KEY(household_id) REFERENCES households(household_id), FOREIGN KEY(applicant_id) REFERENCES users(user_id))");
            // resources
            s.execute("CREATE TABLE IF NOT EXISTS resources(" +
                    "resource_id INTEGER PRIMARY KEY AUTO_INCREMENT, name VARCHAR(255), quantity INTEGER)");
            // simulations
            s.execute("CREATE TABLE IF NOT EXISTS simulations(" +
                    "simulation_id INTEGER PRIMARY KEY AUTO_INCREMENT, description VARCHAR(1000), run_at VARCHAR(255), results VARCHAR(5000))");
            // applicant_needs
            s.execute("CREATE TABLE IF NOT EXISTS applicant_needs(" +
                    "need_id INTEGER PRIMARY KEY AUTO_INCREMENT, applicant_id INTEGER, need_description VARCHAR(1000), " +
                    "category VARCHAR(100), status VARCHAR(50), submitted_date VARCHAR(255), " +
                    "FOREIGN KEY(applicant_id) REFERENCES users(user_id))");
            // applicant_skills
            s.execute("CREATE TABLE IF NOT EXISTS applicant_skills(" +
                    "skill_id INTEGER PRIMARY KEY AUTO_INCREMENT, applicant_id INTEGER, skill_name VARCHAR(255), " +
                    "skill_level VARCHAR(50), FOREIGN KEY(applicant_id) REFERENCES users(user_id))");
            // training_programs
            s.execute("CREATE TABLE IF NOT EXISTS training_programs(" +
                    "program_id INTEGER PRIMARY KEY AUTO_INCREMENT, title VARCHAR(255), description VARCHAR(1000), " +
                    "duration_days INTEGER, skills_taught VARCHAR(500), status VARCHAR(50))");
            // training_enrollments
            s.execute("CREATE TABLE IF NOT EXISTS training_enrollments(" +
                    "enrollment_id INTEGER PRIMARY KEY AUTO_INCREMENT, applicant_id INTEGER, program_id INTEGER, " +
                    "enrollment_date VARCHAR(255), completion_date VARCHAR(255), status VARCHAR(50), " +
                    "FOREIGN KEY(applicant_id) REFERENCES users(user_id), FOREIGN KEY(program_id) REFERENCES training_programs(program_id))");
            // employment_status
            s.execute("CREATE TABLE IF NOT EXISTS employment_status(" +
                    "employment_id INTEGER PRIMARY KEY AUTO_INCREMENT, applicant_id INTEGER, job_id INTEGER, " +
                    "start_date VARCHAR(255), end_date VARCHAR(255), monthly_income DOUBLE, status VARCHAR(50), " +
                    "FOREIGN KEY(applicant_id) REFERENCES users(user_id), FOREIGN KEY(job_id) REFERENCES jobs(job_id))");
            // job_required_skills
            s.execute("CREATE TABLE IF NOT EXISTS job_required_skills(" +
                    "job_id INTEGER, skill_name VARCHAR(255), " +
                    "PRIMARY KEY(job_id, skill_name), FOREIGN KEY(job_id) REFERENCES jobs(job_id))");
            // attendance
            s.execute("CREATE TABLE IF NOT EXISTS attendance(" +
                    "attendance_id INTEGER PRIMARY KEY AUTO_INCREMENT, applicant_id INTEGER, " +
                    "date VARCHAR(255), time_in VARCHAR(255), time_out VARCHAR(255), " +
                    "status VARCHAR(50), hours_worked DOUBLE, " +
                    "FOREIGN KEY(applicant_id) REFERENCES users(user_id))");
            // interviews
            s.execute("CREATE TABLE IF NOT EXISTS interviews(" +
                    "interview_id INTEGER PRIMARY KEY AUTO_INCREMENT, application_id INTEGER, applicant_id INTEGER, job_id INTEGER, " +
                    "interview_date VARCHAR(255), interview_time VARCHAR(255), status VARCHAR(50), notes VARCHAR(1000), " +
                    "created_date VARCHAR(255), " +
                    "FOREIGN KEY(application_id) REFERENCES applications(application_id), " +
                    "FOREIGN KEY(applicant_id) REFERENCES users(user_id), " +
                    "FOREIGN KEY(job_id) REFERENCES jobs(job_id))");

            // Always remove IT jobs that don't align with poverty reduction project
            // This runs every time the application starts to ensure no IT jobs remain
            try {
                // Delete IT jobs by title
                try (PreparedStatement delJobs = conn.prepareStatement("DELETE FROM jobs WHERE title IN (?, ?, ?, ?, ?, ?)")) {
                    delJobs.setString(1, "Software Developer");
                    delJobs.setString(2, "Data Analyst");
                    delJobs.setString(3, "Project Manager");
                    delJobs.setString(4, "UI/UX Designer");
                    delJobs.setString(5, "Database Administrator");
                    delJobs.setString(6, "Network Engineer");
                    int deleted = delJobs.executeUpdate();
                    if (deleted > 0) {
                        System.out.println("Removed " + deleted + " IT job(s) that don't align with poverty reduction project.");
                    }
                }
                
                // Also delete any jobs with high salaries (likely IT jobs) - salaries above 50000 are likely IT
                try (PreparedStatement delHighSalary = conn.prepareStatement("DELETE FROM jobs WHERE salary > 50000")) {
                    int deletedHigh = delHighSalary.executeUpdate();
                    if (deletedHigh > 0) {
                        System.out.println("Removed " + deletedHigh + " high-salary job(s) (likely IT jobs).");
                    }
                }
            } catch (SQLException e) {
                // Ignore if jobs table doesn't exist yet
                System.out.println("Note: Could not delete IT jobs - " + e.getMessage());
            }
            
            // ensure default admin exists
            try (PreparedStatement ps = conn.prepareStatement("SELECT user_id FROM users WHERE username = ?")) {
                ps.setString(1, "teammad");
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        try (PreparedStatement ins = conn.prepareStatement("INSERT INTO users(username,password,role) VALUES(?,?,?)")) {
                            ins.setString(1, "teammad");
                            ins.setString(2, "admin123");
                            ins.setString(3, "ADMIN");
                            ins.executeUpdate();
                        }
                    }
                }
            }

            // After ensuring users table and default admin, export all accounts to accounts.txt
            try {
                AccountStorage.syncFromDatabase();
            } catch (Exception ex) {
                System.err.println("Warning: could not sync accounts to accounts.txt: " + ex.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
            // Allow application to continue in offline mode using AccountStorage backup
            System.err.println("Continuing in offline mode. H2 features will be unavailable until the database is reachable.");
        }
    }

    // Ensure poverty reduction jobs always exist (called on every startup)
    // This method DELETES ALL existing jobs and recreates ONLY poverty reduction jobs
    public static void ensurePovertyReductionJobs() {
        try (Connection conn = getConnection()) {
            // Delete ALL existing jobs first (clean slate approach)
            try (Statement delAll = conn.createStatement()) {
                // First delete related data
                delAll.executeUpdate("DELETE FROM job_required_skills");
                delAll.executeUpdate("DELETE FROM applications");
                delAll.executeUpdate("DELETE FROM employment_status");
                // Then delete all jobs
                int deleted = delAll.executeUpdate("DELETE FROM jobs");
                if (deleted > 0) {
                    System.out.println("Removed " + deleted + " existing job(s) to ensure clean slate.");
                }
            } catch (SQLException e) {
                System.out.println("Note: " + e.getMessage());
            }
            
            // Now create ONLY the 10 poverty reduction jobs
            System.out.println("Creating poverty reduction jobs...");
            createJob("Construction Worker", "General construction work including building, renovation, and maintenance tasks. No experience required, training provided. Helps provide stable income for families.", 25000.0);
            createJob("Farm Laborer", "Agricultural work including planting, harvesting, and farm maintenance. Suitable for rural communities. Provides livelihood opportunities in agriculture sector.", 20000.0);
            createJob("Food Service Worker", "Work in restaurants, cafes, or food preparation areas. Entry-level position with on-the-job training. Helps individuals gain work experience and earn income.", 22000.0);
            createJob("Housekeeping Staff", "Cleaning and maintenance work in hotels, offices, or residential areas. Flexible schedule available. Provides employment opportunities for those seeking stable work.", 20000.0);
            createJob("Security Guard", "Provide security services and monitor premises. Training provided. Offers stable employment with regular income to support families.", 23000.0);
            createJob("Delivery Driver", "Deliver goods and packages to customers. Own vehicle preferred but not required. Flexible work schedule. Helps individuals earn income through delivery services.", 22000.0);
            createJob("Street Vendor Assistant", "Assist in street vending operations, selling goods in markets or streets. Learn business skills while earning income. Suitable for those starting their livelihood journey.", 18000.0);
            createJob("Warehouse Worker", "Handle inventory, packing, and shipping in warehouse facilities. Physical work, no prior experience needed. Provides employment for able-bodied individuals seeking work.", 21000.0);
            createJob("Caregiver", "Provide care and assistance to elderly or persons with disabilities. Compassionate work that helps others while earning income. Training and support provided.", 24000.0);
            createJob("Laundry Worker", "Operate laundry machines, fold clothes, and manage laundry services. Simple tasks, suitable for various skill levels. Provides steady employment opportunity.", 19000.0);
            System.out.println("All 10 poverty reduction jobs created successfully!");
        } catch (SQLException e) {
            System.err.println("Error ensuring poverty reduction jobs: " + e.getMessage());
        }
    }
    
    // Seed sample data for all tables
    public static void seedSampleData() {
        try (Connection conn = getConnection()) {
            // Always clean up old IT jobs first (remove IT-related jobs that don't align with poverty reduction)
            try (PreparedStatement delJobs = conn.prepareStatement("DELETE FROM jobs WHERE title IN (?, ?, ?, ?, ?, ?)")) {
                delJobs.setString(1, "Software Developer");
                delJobs.setString(2, "Data Analyst");
                delJobs.setString(3, "Project Manager");
                delJobs.setString(4, "UI/UX Designer");
                delJobs.setString(5, "Database Administrator");
                delJobs.setString(6, "Network Engineer");
                int deleted = delJobs.executeUpdate();
                if (deleted > 0) {
                    System.out.println("Removed " + deleted + " old IT job(s) from database.");
                }
            } catch (SQLException e) {
                // Ignore if error occurs
            }
            
            // Check if data already exists
            try (PreparedStatement check = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE role = 'APPLICANT'");
                 ResultSet rs = check.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Sample data already exists. Skipping seed.");
                    return;
                }
            }

            System.out.println("Seeding sample data...");

            // Create applicants (users + persons) - General applicants for poverty reduction program
            int app1 = createApplicant("maria_santos", "password123", "Maria Santos", 35, "555-0101");
            int app2 = createApplicant("juan_delacruz", "password123", "Juan Dela Cruz", 42, "555-0102");
            int app3 = createApplicant("rosa_garcia", "password123", "Rosa Garcia", 28, "555-0103");
            int app4 = createApplicant("carlos_reyes", "password123", "Carlos Reyes", 38, "555-0104");
            int app5 = createApplicant("ana_torres", "password123", "Ana Torres", 31, "555-0105");

            // Delete old IT jobs if they exist (clean up old data)
            try (PreparedStatement delJobs = conn.prepareStatement("DELETE FROM jobs WHERE title IN (?, ?, ?, ?, ?, ?)")) {
                delJobs.setString(1, "Software Developer");
                delJobs.setString(2, "Data Analyst");
                delJobs.setString(3, "Project Manager");
                delJobs.setString(4, "UI/UX Designer");
                delJobs.setString(5, "Database Administrator");
                delJobs.setString(6, "Network Engineer");
                delJobs.executeUpdate();
            } catch (SQLException e) {
                // Ignore if table doesn't exist or no IT jobs found
            }
            
            // Delete all existing jobs to ensure clean slate (optional - comment out if you want to keep manually added jobs)
            // Uncomment the next line if you want to remove ALL existing jobs and start fresh
            // try (Statement delAll = conn.createStatement()) { delAll.executeUpdate("DELETE FROM jobs"); }
            
            // Create poverty reduction jobs - ONLY these jobs should exist
            // Entry-level jobs that provide income opportunities for vulnerable populations
            System.out.println("Creating: Construction Worker");
            int job1 = createJob("Construction Worker", "General construction work including building, renovation, and maintenance tasks. No experience required, training provided. Helps provide stable income for families.", 25000.0);
            
            System.out.println("Creating: Farm Laborer");
            int job2 = createJob("Farm Laborer", "Agricultural work including planting, harvesting, and farm maintenance. Suitable for rural communities. Provides livelihood opportunities in agriculture sector.", 20000.0);
            
            System.out.println("Creating: Food Service Worker");
            int job3 = createJob("Food Service Worker", "Work in restaurants, cafes, or food preparation areas. Entry-level position with on-the-job training. Helps individuals gain work experience and earn income.", 22000.0);
            
            System.out.println("Creating: Housekeeping Staff");
            int job4 = createJob("Housekeeping Staff", "Cleaning and maintenance work in hotels, offices, or residential areas. Flexible schedule available. Provides employment opportunities for those seeking stable work.", 20000.0);
            
            System.out.println("Creating: Security Guard");
            int job5 = createJob("Security Guard", "Provide security services and monitor premises. Training provided. Offers stable employment with regular income to support families.", 23000.0);
            
            System.out.println("Creating: Delivery Driver");
            int job6 = createJob("Delivery Driver", "Deliver goods and packages to customers. Own vehicle preferred but not required. Flexible work schedule. Helps individuals earn income through delivery services.", 22000.0);
            
            System.out.println("Creating: Street Vendor Assistant");
            int job7 = createJob("Street Vendor Assistant", "Assist in street vending operations, selling goods in markets or streets. Learn business skills while earning income. Suitable for those starting their livelihood journey.", 18000.0);
            
            System.out.println("Creating: Warehouse Worker");
            int job8 = createJob("Warehouse Worker", "Handle inventory, packing, and shipping in warehouse facilities. Physical work, no prior experience needed. Provides employment for able-bodied individuals seeking work.", 21000.0);
            
            System.out.println("Creating: Caregiver");
            int job9 = createJob("Caregiver", "Provide care and assistance to elderly or persons with disabilities. Compassionate work that helps others while earning income. Training and support provided.", 24000.0);
            
            System.out.println("Creating: Laundry Worker");
            int job10 = createJob("Laundry Worker", "Operate laundry machines, fold clothes, and manage laundry services. Simple tasks, suitable for various skill levels. Provides steady employment opportunity.", 19000.0);
            
            System.out.println("All 10 poverty reduction jobs created successfully!");

            // Create applications with personal details
            createApplication(app1, job1, "Maria", "Santos", "Santos", "Female", 35, "123 Barangay Street, City", "3 years construction experience");
            createApplication(app1, job3, "Maria", "Santos", "Santos", "Female", 35, "123 Barangay Street, City", "2 years food service");
            createApplication(app2, job2, "Juan", "Dela", "Cruz", "Male", 42, "456 Rural Road, Province", "10 years farming experience");
            createApplication(app2, job6, "Juan", "Dela", "Cruz", "Male", 42, "456 Rural Road, Province", "5 years driving experience");
            createApplication(app3, job4, "Rosa", "", "Garcia", "Female", 28, "789 Village Lane, City", "2 years housekeeping");
            createApplication(app4, job5, "Carlos", "Reyes", "Reyes", "Male", 38, "321 Community Ave, City", "4 years security work");
            createApplication(app5, job3, "Ana", "Torres", "Torres", "Female", 31, "654 Neighborhood St, City", "3 years food service");
            createApplication(app3, job1, "Rosa", "", "Garcia", "Female", 28, "789 Village Lane, City", "1 year construction");
            
            // Note: job7, job8, job9, job10 are available for future use

            // Update some application statuses
            updateApplicationStatus(1, "APPROVED"); // Maria's Construction Worker app approved
            updateApplicationStatus(3, "APPROVED"); // Juan's Farm Laborer app approved
            updateApplicationStatus(5, "PENDING"); // Rosa's Housekeeping app pending

            // Create households
            int hh1 = createHousehold("Santos Family", "123 Barangay Street, City");
            int hh2 = createHousehold("Dela Cruz Residence", "456 Rural Road, Province");
            int hh3 = createHousehold("Garcia Household", "789 Village Lane, City");

            // Add members to households
            addHouseholdMember(hh1, app1); // Maria Santos in Santos Family
            addHouseholdMember(hh2, app2); // Juan Dela Cruz in Dela Cruz Residence
            addHouseholdMember(hh3, app3); // Rosa Garcia in Garcia Household
            addHouseholdMember(hh1, app4); // Carlos Reyes also in Santos Family

            // Create resources - Basic necessities and aid resources
            createResource("Rice (50kg bags)", 100);
            createResource("Canned Goods", 500);
            createResource("Clothing (sets)", 200);
            createResource("Blankets", 150);
            createResource("Hygiene Kits", 300);
            createResource("School Supplies", 250);
            createResource("Medical Supplies", 100);
            createResource("Water Containers", 80);

            // Create simulations
            String now = java.time.LocalDateTime.now().toString();
            createSimulation("Initial System Analysis", now, "Total applicants: 5, Total jobs: 6, Pending applications: 4");
            createSimulation("Resource Allocation Check", now, "Resources available: 8 types, Total items: 1680");

            System.out.println("Sample data seeded successfully!");
        } catch (SQLException e) {
            System.err.println("Error seeding sample data: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            // Try multiple methods to ensure driver is loaded
            try {
                Class.forName("org.h2.Driver");
            } catch (ClassNotFoundException e) {
                // Try to check if driver is already registered
                java.util.Enumeration<Driver> drivers = DriverManager.getDrivers();
                boolean found = false;
                while (drivers.hasMoreElements()) {
                    Driver driver = drivers.nextElement();
                    if (driver.getClass().getName().equals("org.h2.Driver")) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    throw new SQLException("H2 JDBC driver not found in classpath. " +
                        "Please ensure h2-1.4.200.jar (Java 8 compatible) is included when running the application.");
                }
            }
            
            Connection conn = DriverManager.getConnection(DB_URL);
            return conn;
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("No suitable driver")) {
                System.err.println("==========================================");
                System.err.println("ERROR: H2 JDBC driver not found!");
                System.err.println("==========================================");
                System.err.println("SOLUTION:");
                System.err.println("1. If using command line:");
                System.err.println("   java -cp \".;h2-1.4.200.jar\" Main");
                System.err.println("2. If using batch file:");
                System.err.println("   Use the provided run.bat file");
                System.err.println("3. If using IDE:");
                System.err.println("   Add h2-1.4.200.jar to your project libraries");
                System.err.println("   Download from: https://mvnrepository.com/artifact/com.h2database/h2");
                System.err.println("==========================================");
            } else {
                System.err.println("Failed to connect to database: " + DB_URL);
                System.err.println("Error: " + e.getMessage());
            }
            throw e;
        }
    }

    // --- USER (admin/applicant) minimal methods ---
    public static Integer createApplicant(String username, String password, String name, int age, String phone) throws SQLException {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement p1 = conn.prepareStatement("INSERT INTO users(username,password,role) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
                p1.setString(1, username);
                p1.setString(2, password);
                p1.setString(3, "APPLICANT");
                p1.executeUpdate();
                try (ResultSet keys = p1.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("No user id");
                    int uid = keys.getInt(1);
                    try (PreparedStatement p2 = conn.prepareStatement("INSERT INTO persons(user_id,name,age,phone) VALUES(?,?,?,?)")) {
                        p2.setInt(1, uid);
                        p2.setString(2, name);
                        p2.setInt(3, age);
                        p2.setString(4, phone);
                        p2.executeUpdate();
                    }
                    conn.commit();

                    // Keep backup storage in sync
                    AccountStorage.addOrUpdateAccount("APPLICANT", username, password, null);
                    return uid;
                }
            }
        } catch (SQLException e) {
            // If H2 is offline, fall back to file-based storage only
            System.err.println("createApplicant DB error, using offline AccountStorage: " + e.getMessage());
            AccountStorage.addOrUpdateAccount("APPLICANT", username, password, null);
            // Return a dummy ID for offline mode; callers usually don't depend on this value
            return -1;
        }
    }

    public static List<ApplicantRecord> getAllApplicants() throws SQLException {
        List<ApplicantRecord> list = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery("SELECT u.user_id,u.username,p.person_id,p.name,p.age,p.phone FROM users u LEFT JOIN persons p ON u.user_id=p.user_id WHERE u.role='APPLICANT'")) {
            while (rs.next()) {
                ApplicantRecord a = new ApplicantRecord();
                a.userId = rs.getInt(1);
                a.username = rs.getString(2);
                a.personId = rs.getInt(3);
                a.name = rs.getString(4);
                a.age = rs.getInt(5);
                a.phone = rs.getString(6);
                list.add(a);
            }
        }
        return list;
    }

    public static boolean deleteApplicant(int userId) throws SQLException {
        try (Connection conn = getConnection()) {
            PreparedStatement p1 = conn.prepareStatement("DELETE FROM persons WHERE user_id=?");
            p1.setInt(1, userId); p1.executeUpdate();
            PreparedStatement p2 = conn.prepareStatement("DELETE FROM household_members WHERE applicant_id=?");
            p2.setInt(1, userId); p2.executeUpdate();
            PreparedStatement p3 = conn.prepareStatement("DELETE FROM users WHERE user_id=?");
            // Get username before deletion so we can also update backup storage
            String username = null;
            try (PreparedStatement lookup = conn.prepareStatement("SELECT username FROM users WHERE user_id=?")) {
                lookup.setInt(1, userId);
                try (ResultSet rs = lookup.executeQuery()) {
                    if (rs.next()) {
                        username = rs.getString(1);
                    }
                }
            }
            p3.setInt(1, userId); int c = p3.executeUpdate();
            if (c > 0 && username != null) {
                AccountStorage.removeAccount(username);
            }
            return c>0;
        } catch (SQLException e) {
            System.err.println("deleteApplicant DB error: " + e.getMessage());
            // If DB is offline, we cannot delete from H2, but we can still remove from backup
            // Callers will typically show an error dialog; we keep backup consistent at least.
            return false;
        }
    }

    // --- JOBS ---
    public static int createJob(String title, String desc, double salary) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement("INSERT INTO jobs(title,description,salary) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, title); p.setString(2, desc); p.setDouble(3, salary);
            p.executeUpdate(); ResultSet keys = p.getGeneratedKeys(); keys.next();
            return keys.getInt(1);
        }
    }
    public static List<JobRecord> getAllJobs() throws SQLException {
        List<JobRecord> list = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery("SELECT job_id,title,description,salary FROM jobs")) {
            while (rs.next()) {
                JobRecord j = new JobRecord();
                j.id = rs.getInt(1); j.title = rs.getString(2); j.description = rs.getString(3); j.salary = rs.getDouble(4);
                list.add(j);
            }
        }
        return list;
    }
    public static boolean deleteJob(int id) throws SQLException {
        try (Connection conn = getConnection()) {
            PreparedStatement p = conn.prepareStatement("DELETE FROM jobs WHERE job_id=?");
            p.setInt(1, id); return p.executeUpdate()>0;
        }
    }

    // --- APPLICATIONS ---
    public static int createApplication(int applicantId, int jobId, String firstName, String middleName, String lastName, 
            String gender, int age, String address, String experience) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement("INSERT INTO applications(job_id,applicant_id,status,first_name,middle_name,last_name,gender,age,address,experience,submission_date) VALUES(?,?,?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            p.setInt(1, jobId);
            p.setInt(2, applicantId);
            p.setString(3, "PENDING");
            p.setString(4, firstName);
            p.setString(5, middleName);
            p.setString(6, lastName);
            p.setString(7, gender);
            p.setInt(8, age);
            p.setString(9, address);
            p.setString(10, experience);
            // Add current date and time
            p.setString(11, java.time.LocalDateTime.now().toString());
            p.executeUpdate();
            ResultSet keys = p.getGeneratedKeys();
            keys.next();
            return keys.getInt(1);
        }
    }
    
    // Overloaded method for backward compatibility (will use default values)
    public static int createApplication(int applicantId, int jobId) throws SQLException {
        // Get applicant info from persons table
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT name, age FROM persons WHERE user_id=?")) {
            ps.setInt(1, applicantId);
            ResultSet rs = ps.executeQuery();
            String name = "";
            int age = 0;
            if (rs.next()) {
                name = rs.getString(1);
                age = rs.getInt(2);
            }
            String[] nameParts = name.split(" ", 3);
            String firstName = nameParts.length > 0 ? nameParts[0] : "";
            String middleName = nameParts.length > 2 ? nameParts[1] : "";
            String lastName = nameParts.length > 1 ? nameParts[nameParts.length - 1] : "";
            return createApplication(applicantId, jobId, firstName, middleName, lastName, "Not Specified", age, "Not Specified", "No experience provided");
        }
    }
    public static List<ApplicationRecord> getAllApplications() throws SQLException {
        List<ApplicationRecord> list = new ArrayList<>();
        // Check if submission_date column exists, if not use a simpler query
        String q;
        try (Connection conn = getConnection(); Statement s = conn.createStatement()) {
            // Try to check if column exists
            try {
                s.executeQuery("SELECT submission_date FROM applications LIMIT 1");
                // Column exists, use full query
                q = "SELECT a.application_id,a.job_id,a.applicant_id,a.status,j.title,u.username,p.name," +
                    "a.first_name,a.middle_name,a.last_name,a.gender,a.age,a.address,a.experience,a.submission_date " +
                    "FROM applications a " +
                    "LEFT JOIN jobs j ON a.job_id=j.job_id LEFT JOIN users u ON a.applicant_id=u.user_id LEFT JOIN persons p ON u.user_id=p.user_id " +
                    "ORDER BY a.application_id DESC";
            } catch (SQLException e) {
                // Column doesn't exist yet, use query without submission_date
                q = "SELECT a.application_id,a.job_id,a.applicant_id,a.status,j.title,u.username,p.name," +
                    "a.first_name,a.middle_name,a.last_name,a.gender,a.age,a.address,a.experience,NULL " +
                    "FROM applications a " +
                    "LEFT JOIN jobs j ON a.job_id=j.job_id LEFT JOIN users u ON a.applicant_id=u.user_id LEFT JOIN persons p ON u.user_id=p.user_id " +
                    "ORDER BY a.application_id DESC";
            }
        }
        
        try (Connection conn = getConnection(); Statement s = conn.createStatement(); ResultSet rs = s.executeQuery(q)) {
            while (rs.next()) {
                ApplicationRecord r = new ApplicationRecord();
                r.id = rs.getInt(1);
                r.jobId = rs.getInt(2);
                r.applicantId = rs.getInt(3);
                r.status = rs.getString(4);
                r.jobTitle = rs.getString(5);
                r.applicantUsername = rs.getString(6);
                r.applicantName = rs.getString(7);
                r.firstName = rs.getString(8);
                r.middleName = rs.getString(9);
                r.lastName = rs.getString(10);
                r.gender = rs.getString(11);
                r.age = rs.getInt(12);
                r.address = rs.getString(13);
                r.experience = rs.getString(14);
                try {
                    r.submissionDate = rs.getString(15);
                } catch (Exception e) {
                    r.submissionDate = null;
                }
                list.add(r);
            }
        }
        return list;
    }
    public static boolean updateApplicationStatus(int applicationId, String status) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement("UPDATE applications SET status=? WHERE application_id=?")) {
            p.setString(1,status); p.setInt(2,applicationId); return p.executeUpdate()>0;
        }
    }
    
    // --- INTERVIEWS ---
    public static int createInterview(int applicationId, int applicantId, int jobId, String interviewDate, String interviewTime, String notes) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement("INSERT INTO interviews(application_id,applicant_id,job_id,interview_date,interview_time,status,notes,created_date) VALUES(?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            p.setInt(1, applicationId);
            p.setInt(2, applicantId);
            p.setInt(3, jobId);
            p.setString(4, interviewDate);
            p.setString(5, interviewTime);
            p.setString(6, "SCHEDULED");
            p.setString(7, notes != null ? notes : "");
            p.setString(8, java.time.LocalDateTime.now().toString());
            p.executeUpdate();
            ResultSet keys = p.getGeneratedKeys();
            keys.next();
            return keys.getInt(1);
        }
    }
    
    public static List<InterviewRecord> getAllInterviews() throws SQLException {
        List<InterviewRecord> list = new ArrayList<>();
        String q = "SELECT i.interview_id,i.application_id,i.applicant_id,i.job_id,i.interview_date,i.interview_time,i.status,i.notes,i.created_date," +
                "j.title,u.username,p.name " +
                "FROM interviews i " +
                "LEFT JOIN jobs j ON i.job_id=j.job_id " +
                "LEFT JOIN users u ON i.applicant_id=u.user_id " +
                "LEFT JOIN persons p ON u.user_id=p.user_id " +
                "ORDER BY i.interview_id DESC";
        try (Connection conn = getConnection(); Statement s = conn.createStatement(); ResultSet rs = s.executeQuery(q)) {
            while (rs.next()) {
                InterviewRecord r = new InterviewRecord();
                r.id = rs.getInt(1);
                r.applicationId = rs.getInt(2);
                r.applicantId = rs.getInt(3);
                r.jobId = rs.getInt(4);
                r.interviewDate = rs.getString(5);
                r.interviewTime = rs.getString(6);
                r.status = rs.getString(7);
                r.notes = rs.getString(8);
                r.createdDate = rs.getString(9);
                r.jobTitle = rs.getString(10);
                r.applicantUsername = rs.getString(11);
                r.applicantName = rs.getString(12);
                list.add(r);
            }
        }
        return list;
    }
    
    public static boolean updateInterviewStatus(int interviewId, String status) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement("UPDATE interviews SET status=? WHERE interview_id=?")) {
            p.setString(1, status);
            p.setInt(2, interviewId);
            return p.executeUpdate() > 0;
        }
    }
    
    public static InterviewRecord getInterviewByApplicationId(int applicationId) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement("SELECT interview_id,application_id,applicant_id,job_id,interview_date,interview_time,status,notes,created_date FROM interviews WHERE application_id=?")) {
            p.setInt(1, applicationId);
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                InterviewRecord r = new InterviewRecord();
                r.id = rs.getInt(1);
                r.applicationId = rs.getInt(2);
                r.applicantId = rs.getInt(3);
                r.jobId = rs.getInt(4);
                r.interviewDate = rs.getString(5);
                r.interviewTime = rs.getString(6);
                r.status = rs.getString(7);
                r.notes = rs.getString(8);
                r.createdDate = rs.getString(9);
                return r;
            }
        }
        return null;
    }

    // --- HOUSEHOLDS ---
    public static int createHousehold(String name, String address) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement("INSERT INTO households(name,address) VALUES(?,?)", Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1,name); p.setString(2,address); p.executeUpdate(); ResultSet keys = p.getGeneratedKeys(); keys.next();
            return keys.getInt(1);
        }
    }
    public static List<HouseholdRecord> getAllHouseholds() throws SQLException {
        List<HouseholdRecord> list = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery("SELECT household_id,name,address FROM households")) {
            while (rs.next()) {
                HouseholdRecord h = new HouseholdRecord();
                h.id = rs.getInt(1); h.name = rs.getString(2); h.address = rs.getString(3);
                // members
                try (PreparedStatement pm = conn.prepareStatement("SELECT applicant_id FROM household_members WHERE household_id=?")) {
                    pm.setInt(1, h.id);
                    try (ResultSet rm = pm.executeQuery()) {
                        while (rm.next()) h.memberIds.add(rm.getInt(1));
                    }
                }
                list.add(h);
            }
        }
        return list;
    }
    public static boolean addHouseholdMember(int householdId, int applicantId) throws SQLException {
        try (Connection conn = getConnection()) {
            // Check if already exists
            try (PreparedStatement check = conn.prepareStatement(
                "SELECT 1 FROM household_members WHERE household_id=? AND applicant_id=?")) {
                check.setInt(1, householdId);
                check.setInt(2, applicantId);
                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next()) {
                        return false; // Already exists
                    }
                }
            }
            
            // Insert if not exists
            try (PreparedStatement p = conn.prepareStatement(
                "INSERT INTO household_members(household_id,applicant_id) VALUES(?,?)")) {
                p.setInt(1, householdId); 
                p.setInt(2, applicantId); 
                return p.executeUpdate() > 0;
            }
        }
    }

    // --- RESOURCES ---
    public static int createResource(String name,int qty) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement("INSERT INTO resources(name,quantity) VALUES(?,?)", Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1,name); p.setInt(2,qty); p.executeUpdate(); ResultSet keys = p.getGeneratedKeys(); keys.next(); return keys.getInt(1);
        }
    }
    public static List<ResourceRecord> getAllResources() throws SQLException {
        List<ResourceRecord> list = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery("SELECT resource_id,name,quantity FROM resources")) {
            while (rs.next()) {
                ResourceRecord r = new ResourceRecord();
                r.id = rs.getInt(1); r.name = rs.getString(2); r.quantity = rs.getInt(3);
                list.add(r);
            }
        }
        return list;
    }

    // --- SIMULATIONS ---
    public static int createSimulation(String description, String runAt, String results) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement("INSERT INTO simulations(description,run_at,results) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1,description); p.setString(2,runAt); p.setString(3,results); p.executeUpdate(); ResultSet keys = p.getGeneratedKeys(); keys.next(); return keys.getInt(1);
        }
    }
    public static List<SimulationRecord> getAllSimulations() throws SQLException {
        List<SimulationRecord> list = new ArrayList<>();
        try (Connection conn = getConnection(); Statement s = conn.createStatement(); ResultSet rs = s.executeQuery("SELECT simulation_id,description,run_at,results FROM simulations")) {
            while (rs.next()) {
                SimulationRecord r = new SimulationRecord();
                r.id = rs.getInt(1); r.description = rs.getString(2); r.runAt = rs.getString(3); r.results = rs.getString(4);
                list.add(r);
            }
        }
        return list;
    }

    // --- AUTH (admin login) ---
    public static boolean adminAuthenticate(String username, String password) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement("SELECT role FROM users WHERE username=? AND password=?")) {
            p.setString(1, username);
            p.setString(2, password);
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                String role = rs.getString(1);
                boolean ok = role != null && "ADMIN".equalsIgnoreCase(role);
                if (ok) {
                    // Keep backup store in sync
                    AccountStorage.addOrUpdateAccount("ADMIN", username, password, null);
                }
                return ok;
            }
        } catch (SQLException e) {
            System.err.println("Authentication error (DB), falling back to AccountStorage: " + e.getMessage());
            // Fall back to file/ArrayList-based auth when DB is offline
            return AccountStorage.authenticate("ADMIN", username, password);
        }
        // If not found in DB, also try backup store
        return AccountStorage.authenticate("ADMIN", username, password);
    }
    
    // --- AUTH (applicant login) ---
    public static boolean applicantAuthenticate(String username, String password) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement("SELECT role FROM users WHERE username=? AND password=?")) {
            p.setString(1, username);
            p.setString(2, password);
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                String role = rs.getString(1);
                boolean ok = role != null && "APPLICANT".equalsIgnoreCase(role);
                if (ok) {
                    // Keep backup store in sync
                    AccountStorage.addOrUpdateAccount("APPLICANT", username, password, null);
                }
                return ok;
            }
        } catch (SQLException e) {
            System.err.println("Authentication error (DB), falling back to AccountStorage: " + e.getMessage());
            // Fall back to file/ArrayList-based auth when DB is offline
            return AccountStorage.authenticate("APPLICANT", username, password);
        }
        // If not found in DB, also try backup store
        return AccountStorage.authenticate("APPLICANT", username, password);
    }

    /**
     * Minimal view of user accounts for syncing with accounts.txt.
     * Role, username and password are mirrored; timeCreated is set when exporting.
     */
    public static List<AccountStorage.Account> getAllUserAccountsForSync() throws SQLException {
        List<AccountStorage.Account> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement("SELECT role,username,password FROM users")) {
            ResultSet rs = p.executeQuery();
            while (rs.next()) {
                AccountStorage.Account acc = new AccountStorage.Account();
                acc.role = rs.getString(1);
                acc.username = rs.getString(2);
                acc.password = rs.getString(3);
                acc.timeCreated = java.time.LocalDateTime.now().toString();
                list.add(acc);
            }
        }
        return list;
    }

    /**
     * Create a bare user account from file-based backup into H2 when syncing.
     * This does not create a corresponding person/profile row; it can be completed later.
     */
    public static void createUserAccountFromSync(String role, String username, String password) throws SQLException {
        if (username == null || username.isEmpty()) return;
        if (role == null || role.isEmpty()) role = "APPLICANT";
        if (password == null) password = "";
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement("INSERT INTO users(username,password,role) VALUES(?,?,?)")) {
            p.setString(1, username);
            p.setString(2, password);
            p.setString(3, role);
            p.executeUpdate();
        }
    }

    // Get applicant user ID by username
    public static int getApplicantUserId(String username) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement("SELECT user_id FROM users WHERE username=? AND role='APPLICANT'")) {
            p.setString(1, username);
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    // --- APPLICANT NEEDS ---
    public static int createApplicantNeed(int applicantId, String description, String category) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement("INSERT INTO applicant_needs(applicant_id,need_description,category,status,submitted_date) VALUES(?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            p.setInt(1, applicantId);
            p.setString(2, description);
            p.setString(3, category);
            p.setString(4, "PENDING");
            p.setString(5, java.time.LocalDateTime.now().toString());
            p.executeUpdate();
            ResultSet keys = p.getGeneratedKeys();
            keys.next();
            return keys.getInt(1);
        }
    }
    
    public static List<ApplicantNeedRecord> getApplicantNeeds(int applicantId) throws SQLException {
        List<ApplicantNeedRecord> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement("SELECT need_id,need_description,category,status,submitted_date FROM applicant_needs WHERE applicant_id=?")) {
            p.setInt(1, applicantId);
            ResultSet rs = p.executeQuery();
            while (rs.next()) {
                ApplicantNeedRecord n = new ApplicantNeedRecord();
                n.id = rs.getInt(1);
                n.description = rs.getString(2);
                n.category = rs.getString(3);
                n.status = rs.getString(4);
                n.submittedDate = rs.getString(5);
                list.add(n);
            }
        }
        return list;
    }

    // --- SKILLS MANAGEMENT ---
    public static int addApplicantSkill(int applicantId, String skillName, String skillLevel) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement("INSERT INTO applicant_skills(applicant_id,skill_name,skill_level) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            p.setInt(1, applicantId);
            p.setString(2, skillName);
            p.setString(3, skillLevel);
            p.executeUpdate();
            ResultSet keys = p.getGeneratedKeys();
            keys.next();
            return keys.getInt(1);
        }
    }
    
    public static List<SkillRecord> getApplicantSkills(int applicantId) throws SQLException {
        List<SkillRecord> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement("SELECT skill_id,skill_name,skill_level FROM applicant_skills WHERE applicant_id=?")) {
            p.setInt(1, applicantId);
            ResultSet rs = p.executeQuery();
            while (rs.next()) {
                SkillRecord s = new SkillRecord();
                s.id = rs.getInt(1);
                s.skillName = rs.getString(2);
                s.skillLevel = rs.getString(3);
                list.add(s);
            }
        }
        return list;
    }
    
    public static boolean deleteApplicantSkill(int skillId) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement("DELETE FROM applicant_skills WHERE skill_id=?")) {
            p.setInt(1, skillId);
            return p.executeUpdate() > 0;
        }
    }

    // --- TRAINING PROGRAMS ---
    public static int createTrainingProgram(String title, String description, int durationDays, String skillsTaught) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement("INSERT INTO training_programs(title,description,duration_days,skills_taught,status) VALUES(?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, title);
            p.setString(2, description);
            p.setInt(3, durationDays);
            p.setString(4, skillsTaught);
            p.setString(5, "ACTIVE");
            p.executeUpdate();
            ResultSet keys = p.getGeneratedKeys();
            keys.next();
            return keys.getInt(1);
        }
    }
    
    public static List<TrainingProgramRecord> getAllTrainingPrograms() throws SQLException {
        List<TrainingProgramRecord> list = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery("SELECT program_id,title,description,duration_days,skills_taught,status FROM training_programs")) {
            while (rs.next()) {
                TrainingProgramRecord t = new TrainingProgramRecord();
                t.id = rs.getInt(1);
                t.title = rs.getString(2);
                t.description = rs.getString(3);
                t.durationDays = rs.getInt(4);
                t.skillsTaught = rs.getString(5);
                t.status = rs.getString(6);
                list.add(t);
            }
        }
        return list;
    }
    
    public static int enrollInTraining(int applicantId, int programId) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement("INSERT INTO training_enrollments(applicant_id,program_id,enrollment_date,status) VALUES(?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            p.setInt(1, applicantId);
            p.setInt(2, programId);
            p.setString(3, java.time.LocalDateTime.now().toString());
            p.setString(4, "ENROLLED");
            p.executeUpdate();
            ResultSet keys = p.getGeneratedKeys();
            keys.next();
            return keys.getInt(1);
        }
    }
    
    public static List<TrainingEnrollmentRecord> getApplicantTrainings(int applicantId) throws SQLException {
        List<TrainingEnrollmentRecord> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement("SELECT e.enrollment_id,e.program_id,p.title,e.enrollment_date,e.completion_date,e.status FROM training_enrollments e JOIN training_programs p ON e.program_id=p.program_id WHERE e.applicant_id=?")) {
            p.setInt(1, applicantId);
            ResultSet rs = p.executeQuery();
            while (rs.next()) {
                TrainingEnrollmentRecord t = new TrainingEnrollmentRecord();
                t.enrollmentId = rs.getInt(1);
                t.programId = rs.getInt(2);
                t.programTitle = rs.getString(3);
                t.enrollmentDate = rs.getString(4);
                t.completionDate = rs.getString(5);
                t.status = rs.getString(6);
                list.add(t);
            }
        }
        return list;
    }
    
    public static List<TrainingEnrollmentRecord> getAllTrainingEnrollments() throws SQLException {
        List<TrainingEnrollmentRecord> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement("SELECT e.enrollment_id,e.applicant_id,u.username,p.title,e.enrollment_date,e.completion_date,e.status FROM training_enrollments e JOIN training_programs p ON e.program_id=p.program_id JOIN users u ON e.applicant_id=u.user_id ORDER BY e.enrollment_date DESC")) {
            ResultSet rs = p.executeQuery();
            while (rs.next()) {
                TrainingEnrollmentRecord t = new TrainingEnrollmentRecord();
                t.enrollmentId = rs.getInt(1);
                t.applicantId = rs.getInt(2);
                t.applicantUsername = rs.getString(3);
                t.programTitle = rs.getString(4);
                t.enrollmentDate = rs.getString(5);
                t.completionDate = rs.getString(6);
                t.status = rs.getString(7);
                list.add(t);
            }
        }
        return list;
    }
    
    public static boolean deleteTrainingProgram(int programId) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement("DELETE FROM training_programs WHERE program_id=?")) {
            p.setInt(1, programId);
            return p.executeUpdate() > 0;
        }
    }

    // --- EMPLOYMENT STATUS ---
    public static int createEmploymentStatus(int applicantId, int jobId, double monthlyIncome) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement("INSERT INTO employment_status(applicant_id,job_id,start_date,monthly_income,status) VALUES(?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            p.setInt(1, applicantId);
            p.setInt(2, jobId);
            p.setString(3, java.time.LocalDateTime.now().toString());
            p.setDouble(4, monthlyIncome);
            p.setString(5, "EMPLOYED");
            p.executeUpdate();
            ResultSet keys = p.getGeneratedKeys();
            keys.next();
            return keys.getInt(1);
        }
    }
    
    public static List<EmploymentStatusRecord> getAllEmploymentStatus() throws SQLException {
        List<EmploymentStatusRecord> list = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery("SELECT e.employment_id,e.applicant_id,p.name,e.job_id,j.title,e.start_date,e.monthly_income,e.status FROM employment_status e LEFT JOIN persons p ON e.applicant_id=p.user_id LEFT JOIN jobs j ON e.job_id=j.job_id")) {
            while (rs.next()) {
                EmploymentStatusRecord emp = new EmploymentStatusRecord();
                emp.id = rs.getInt(1);
                emp.applicantId = rs.getInt(2);
                emp.applicantName = rs.getString(3);
                emp.jobId = rs.getInt(4);
                emp.jobTitle = rs.getString(5);
                emp.startDate = rs.getString(6);
                emp.monthlyIncome = rs.getDouble(7);
                emp.status = rs.getString(8);
                list.add(emp);
            }
        }
        return list;
    }
    
    public static List<EmploymentStatusRecord> getApplicantEmployment(int applicantId) throws SQLException {
        List<EmploymentStatusRecord> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement("SELECT e.employment_id,e.applicant_id,p.name,e.job_id,j.title,e.start_date,e.monthly_income,e.status FROM employment_status e LEFT JOIN persons p ON e.applicant_id=p.user_id LEFT JOIN jobs j ON e.job_id=j.job_id WHERE e.applicant_id=?")) {
            p.setInt(1, applicantId);
            ResultSet rs = p.executeQuery();
            while (rs.next()) {
                EmploymentStatusRecord emp = new EmploymentStatusRecord();
                emp.id = rs.getInt(1);
                emp.applicantId = rs.getInt(2);
                emp.applicantName = rs.getString(3);
                emp.jobId = rs.getInt(4);
                emp.jobTitle = rs.getString(5);
                emp.startDate = rs.getString(6);
                emp.monthlyIncome = rs.getDouble(7);
                emp.status = rs.getString(8);
                list.add(emp);
            }
        }
        return list;
    }
    
    // --- ATTENDANCE ---
    public static int recordTimeIn(int applicantId) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement("INSERT INTO attendance(applicant_id,date,time_in,status) VALUES(?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            java.time.LocalDate today = java.time.LocalDate.now();
            java.time.LocalTime now = java.time.LocalTime.now();
            p.setInt(1, applicantId);
            p.setString(2, today.toString());
            p.setString(3, now.toString());
            p.setString(4, "PRESENT");
            p.executeUpdate();
            ResultSet keys = p.getGeneratedKeys();
            keys.next();
            return keys.getInt(1);
        }
    }
    
    public static void recordTimeOut(int applicantId) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement("UPDATE attendance SET time_out=?, hours_worked=? WHERE applicant_id=? AND date=? AND time_out IS NULL")) {
            java.time.LocalDate today = java.time.LocalDate.now();
            java.time.LocalTime now = java.time.LocalTime.now();
            
            // Get time_in for today
            try (PreparedStatement p2 = conn.prepareStatement("SELECT time_in FROM attendance WHERE applicant_id=? AND date=? AND time_out IS NULL")) {
                p2.setInt(1, applicantId);
                p2.setString(2, today.toString());
                ResultSet rs = p2.executeQuery();
                if (rs.next()) {
                    String timeInStr = rs.getString(1);
                    java.time.LocalTime timeIn = java.time.LocalTime.parse(timeInStr);
                    long hours = java.time.Duration.between(timeIn, now).toHours();
                    long minutes = java.time.Duration.between(timeIn, now).toMinutes() % 60;
                    double hoursWorked = hours + (minutes / 60.0);
                    
                    p.setString(1, now.toString());
                    p.setDouble(2, hoursWorked);
                    p.setInt(3, applicantId);
                    p.setString(4, today.toString());
                    p.executeUpdate();
                }
            }
        }
    }
    
    public static List<AttendanceRecord> getApplicantAttendance(int applicantId) throws SQLException {
        List<AttendanceRecord> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement("SELECT attendance_id,date,time_in,time_out,status,hours_worked FROM attendance WHERE applicant_id=? ORDER BY date DESC, attendance_id DESC")) {
            p.setInt(1, applicantId);
            ResultSet rs = p.executeQuery();
            while (rs.next()) {
                AttendanceRecord att = new AttendanceRecord();
                att.id = rs.getInt(1);
                att.date = rs.getString(2);
                att.timeIn = rs.getString(3);
                att.timeOut = rs.getString(4);
                att.status = rs.getString(5);
                att.hoursWorked = rs.getDouble(6);
                list.add(att);
            }
        }
        return list;
    }
    
    public static AttendanceRecord getTodayAttendance(int applicantId) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement("SELECT attendance_id,date,time_in,time_out,status,hours_worked FROM attendance WHERE applicant_id=? AND date=? ORDER BY attendance_id DESC LIMIT 1")) {
            p.setInt(1, applicantId);
            p.setString(2, java.time.LocalDate.now().toString());
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                AttendanceRecord att = new AttendanceRecord();
                att.id = rs.getInt(1);
                att.date = rs.getString(2);
                att.timeIn = rs.getString(3);
                att.timeOut = rs.getString(4);
                att.status = rs.getString(5);
                att.hoursWorked = rs.getDouble(6);
                return att;
            }
        }
        return null;
    }

    // --- SKILLS-JOB MATCHING ---
    public static void addJobRequiredSkill(int jobId, String skillName) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement("INSERT INTO job_required_skills(job_id,skill_name) VALUES(?,?)")) {
            p.setInt(1, jobId);
            p.setString(2, skillName);
            p.executeUpdate();
        }
    }
    
    public static List<String> getJobRequiredSkills(int jobId) throws SQLException {
        List<String> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement("SELECT skill_name FROM job_required_skills WHERE job_id=?")) {
            p.setInt(1, jobId);
            ResultSet rs = p.executeQuery();
            while (rs.next()) {
                list.add(rs.getString(1));
            }
        }
        return list;
    }
    
    public static List<JobMatchRecord> getMatchingJobsForApplicant(int applicantId) throws SQLException {
        List<JobMatchRecord> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement(
                "SELECT j.job_id, j.title, j.description, j.salary, " +
                "COUNT(DISTINCT js.skill_name) as required_skills, " +
                "COUNT(DISTINCT CASE WHEN aps.skill_name = js.skill_name THEN 1 END) as matched_skills " +
                "FROM jobs j " +
                "LEFT JOIN job_required_skills js ON j.job_id = js.job_id " +
                "LEFT JOIN applicant_skills aps ON aps.applicant_id = ? " +
                "GROUP BY j.job_id, j.title, j.description, j.salary " +
                "HAVING required_skills > 0 " +
                "ORDER BY matched_skills DESC, required_skills ASC")) {
            p.setInt(1, applicantId);
            ResultSet rs = p.executeQuery();
            while (rs.next()) {
                JobMatchRecord match = new JobMatchRecord();
                match.jobId = rs.getInt(1);
                match.title = rs.getString(2);
                match.description = rs.getString(3);
                match.salary = rs.getDouble(4);
                match.requiredSkills = rs.getInt(5);
                match.matchedSkills = rs.getInt(6);
                match.matchPercentage = match.requiredSkills > 0 ? (match.matchedSkills * 100 / match.requiredSkills) : 0;
                list.add(match);
            }
        }
        return list;
    }

    // --- Simple record holder classes ---
    public static class ApplicantRecord { public int userId; public String username; public int personId; public String name; public int age; public String phone; }
    public static class JobRecord { public int id; public String title; public String description; public double salary; }
    public static class ApplicationRecord { 
        public int id; 
        public int jobId; 
        public int applicantId; 
        public String status; 
        public String jobTitle; 
        public String applicantUsername; 
        public String applicantName;
        public String firstName;
        public String middleName;
        public String lastName;
        public String gender;
        public int age;
        public String address;
        public String experience;
        public String submissionDate;
    }
    public static class HouseholdRecord { public int id; public String name; public String address; public List<Integer> memberIds = new ArrayList<>(); }
    public static class ResourceRecord { public int id; public String name; public int quantity; }
    public static class SimulationRecord { public int id; public String description; public String runAt; public String results; }
    public static class ApplicantNeedRecord { public int id; public String description; public String category; public String status; public String submittedDate; }
    public static class SkillRecord { public int id; public String skillName; public String skillLevel; }
    public static class TrainingProgramRecord { public int id; public String title; public String description; public int durationDays; public String skillsTaught; public String status; }
    public static class TrainingEnrollmentRecord { public int enrollmentId; public int applicantId; public String applicantUsername; public int programId; public String programTitle; public String enrollmentDate; public String completionDate; public String status; }
    public static class EmploymentStatusRecord { public int id; public int applicantId; public String applicantName; public int jobId; public String jobTitle; public String startDate; public String endDate; public double monthlyIncome; public String status; }
    public static class JobMatchRecord { public int jobId; public String title; public String description; public double salary; public int requiredSkills; public int matchedSkills; public int matchPercentage; }
    public static class AttendanceRecord { public int id; public String date; public String timeIn; public String timeOut; public String status; public double hoursWorked; }
    public static class InterviewRecord { 
        public int id; 
        public int applicationId; 
        public int applicantId; 
        public int jobId; 
        public String interviewDate; 
        public String interviewTime; 
        public String status; 
        public String notes; 
        public String createdDate;
        public String jobTitle;
        public String applicantUsername;
        public String applicantName;
    }
}
