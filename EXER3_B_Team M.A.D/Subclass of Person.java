class Applicant extends Person {
    private String resume;
    private String skills;

    public void register() {
        System.out.println("Applicant registered.");
    }

    public void applyJob() {
        System.out.println("Applicant applied for a job.");
    }

    public void viewApplicationStatus() {
        System.out.println("Viewing application status...");
    }
}