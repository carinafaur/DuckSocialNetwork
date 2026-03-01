package domain;

import java.time.LocalDate;

public class Person extends User{
    private  String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String ocupation;
    private int empathyLevel;

    public Person(Long id,String username,String email, String password,String firstName, String lastName, LocalDate dateOfBirth, String ocupation,int empathyLevel) {
        super(id, username, email, password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.ocupation = ocupation;
        this.empathyLevel = empathyLevel;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getOcupation() {
        return ocupation;
    }

    public int getEmpathyLevel() {
        return empathyLevel;
    }

    @Override
    public String toString(){
        return super.toString()+"PERSON: name- "+firstName+ " "+lastName+", date- "+dateOfBirth+", ocupation- "+ocupation+", empathyLevel- "+empathyLevel;
    }
}
