class Person extends User {
    private int personID;
    private String name;
    private int age;
    private int healthStatus;
    private int hungerLevel;

    public void applyJob() {
        System.out.println(name + " applied for a job.");
    }

    public void eatFood() {
        System.out.println(name + " ate food.");
    }
}