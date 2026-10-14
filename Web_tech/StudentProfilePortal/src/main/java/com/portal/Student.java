package com.portal;
public class Student {
    private int    id;
    private String rollNo;
    private String name;
    private String department;
    private String email;
    private String photoPath;
    private String documentPath;    public Student(int id, String rollNo, String name,
                   String department, String email,
                   String photoPath, String documentPath) {
        this.id           = id;
        this.rollNo       = rollNo;
        this.name         = name;
        this.department   = department;
        this.email        = email;
        this.photoPath    = photoPath;
        this.documentPath = documentPath;
    }    public int    getId()           { return id; }
    public String getRollNo()       { return rollNo; }
    public String getName()         { return name; }
    public String getDepartment()   { return department; }
    public String getEmail()        { return email; }
    public String getPhotoPath()    { return photoPath; }
    public String getDocumentPath() { return documentPath; }
}
