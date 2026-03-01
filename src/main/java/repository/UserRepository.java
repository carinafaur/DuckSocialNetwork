package repository;

import domain.Duck;
import domain.Person;
import domain.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private DataBaseLoaderDuck dataBaseLoaderDuck;
    private DataBaseLoaderPerson dataBaseLoaderPerson;

    public UserRepository() {
        this.dataBaseLoaderDuck = new  DataBaseLoaderDuck();
        this.dataBaseLoaderPerson = new  DataBaseLoaderPerson();
    }

    public User findUser(Long id) throws SQLException{
        Duck duck = dataBaseLoaderDuck.findDuckById(id);
        if (duck == null) {
            Person person = dataBaseLoaderPerson.findPersonById(id);
            if (person == null)
                return null;
            else
                return person;
        } else
            return duck;
    }

    public User findUserByUsername(String username) throws SQLException{
        Duck duck = dataBaseLoaderDuck.findDuckByUsername(username);
        if (duck == null) {
            Person person = dataBaseLoaderPerson.findPersonByUsername(username);
            if (person == null)
                return null;
            else
                return person;
        } else
            return duck;
    }

    public List<User> getAllUsers() throws SQLException{
        List<Duck> ducks=dataBaseLoaderDuck.getAllDucks();
        List<Person> persons=dataBaseLoaderPerson.getAllPersons();
        List<User> users=new ArrayList<>();
        users.addAll(ducks);
        users.addAll(persons);
        return users;
    }

    public boolean isActive(User user) throws SQLException {
        if(user instanceof Duck)
             return dataBaseLoaderDuck.isActive(user.getId());
        else return dataBaseLoaderPerson.isActive(user.getId());
    }

    public void updateStatus(User user,boolean status) throws SQLException {
        if(user instanceof Duck)
            dataBaseLoaderDuck.updateActive(user.getId(), status);
        else dataBaseLoaderPerson.updateActive(user.getId(), status);
    }
}
