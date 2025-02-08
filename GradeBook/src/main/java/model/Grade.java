package model;

public class Grade {
    private int id;
    private double grade;
    private int studentId;
    private int groupId;
    private int gradeTypeId;

    public Grade(int id, double grade, int studentId, int groupId, int gradeTypeId) {
        this.id = id;
        this.grade = grade;
        this.studentId = studentId;
        this.groupId = groupId;
        this.gradeTypeId = gradeTypeId;
    }

    public int getId() {
        return id;
    }

    public double getGrade() {
        return grade;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getGroupId() {
        return groupId;
    }

    public int getGradeTypeId() {
        return gradeTypeId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public void setGradeTypeId(int gradeTypeId) {
        this.gradeTypeId = gradeTypeId;
    }
}
