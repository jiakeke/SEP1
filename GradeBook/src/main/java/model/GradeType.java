package model;

public class GradeType {
    private int id;
    private String name;
    private double weight;
    private int groupId;

    public GradeType(int id, String name, double weight, int groupId) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.groupId = groupId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getWeight() {
        return weight;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}
