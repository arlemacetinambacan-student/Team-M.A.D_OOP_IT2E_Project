class Admin extends User {
    public void manageJobs() {
        System.out.println("Admin managing jobs.");
    }

    public void viewApplications() {
        System.out.println("Admin viewing applications.");
    }

    public void approveApplications() {
        System.out.println("Application approved.");
    }

    public void rejectApplications() {
        System.out.println("Application rejected.");
    }
}