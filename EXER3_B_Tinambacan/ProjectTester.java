class User {
    void login() {
        System.out.println("User logged in.");
    }
}

class Person {
    void eatFood() {
        System.out.println("Person is eating food.");
    }
}

class Applicant {
    void register() {
        System.out.println("Applicant registered.");
    }
    void applyJob() {
        System.out.println("Applicant applied for a job.");
    }
    void viewApplicationStatus() {
        System.out.println("Applicant viewed application status.");
    }
}

class Admin {
    void manageJobs() {
        System.out.println("Admin managing jobs.");
    }
    void approveApplications() {
        System.out.println("Admin approved applications.");
    }
}

class Application {
    void submit() {
        System.out.println("Application submitted.");
    }
    void updateStatus() {
        System.out.println("Application status updated.");
    }
}

class Household {
    void calcNeeds() {
        System.out.println("Household needs calculated.");
    }
}

class Resource {
    void allocate() {
        System.out.println("Resource allocated.");
    }
}

class Job {
    void postJob() {
        System.out.println("Job posted.");
    }
}

class Simulation {
    void distributeResources() {
        System.out.println("Resources distributed in simulation.");
    }
    void trackPoverty() {
        System.out.println("Simulation tracking poverty.");
    }
}

public class ProjectTester {
    public static void main(String[] args) {
        // Create User (base)
        User user = new User();
        user.login();

        // Create Person
        Person person = new Person();
        person.eatFood();

        // Create Applicant
        Applicant applicant = new Applicant();
        applicant.register();
        applicant.applyJob();
        applicant.viewApplicationStatus();

        // Create Admin
        Admin admin = new Admin();
        admin.manageJobs();
        admin.approveApplications();

        // Create Application
        Application application = new Application();
        application.submit();
        application.updateStatus();

        // Create Household
        Household household = new Household();
        household.calcNeeds();

        // Create Resource
        Resource resource = new Resource();
        resource.allocate();

        // Create Job
        Job job = new Job();
        job.postJob();

        // Create Simulation
        Simulation sim = new Simulation();
        sim.distributeResources();
        sim.trackPoverty();
    }
}