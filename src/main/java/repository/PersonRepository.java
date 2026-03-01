package repository;

import domain.*;
import utils.Page;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PersonRepository extends UserRepository {
    private DataBaseLoaderPerson dataBaseLoader;

    public PersonRepository() {
        this.dataBaseLoader = new DataBaseLoaderPerson();
    }

    public Page<Person> getPersons(int pageNumber, int pageSize) throws SQLException, ClassNotFoundException {
        int offset = pageNumber * pageSize;
        int totalElements = dataBaseLoader.getTotalPersonCount();

        if (offset >= totalElements && totalElements > 0) {
            return new Page<>(new ArrayList<>(), 0, totalElements);
        }
        List<Person> pageContent = dataBaseLoader.getPersonsPage(pageSize, offset);
        return new Page<>(pageContent, pageContent.size(), totalElements);
    }

    public void addPerson(Person person) throws SQLException {
        dataBaseLoader.savePerson(person);
    }

    public void removePerson(Person person) throws SQLException {
        dataBaseLoader.removePerson(person);
    }


}
