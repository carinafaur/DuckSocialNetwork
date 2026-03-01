package validation;

import domain.Person;

public class ValidatePerson {
    private Person person;

    public ValidatePerson(Person person) {
        this.person = person;
    }

    public boolean validate(){
        if(person==null){
            return false;
        }
        if(person.getDateOfBirth()==null){
            return false;
        }
        return true;
    }
}
