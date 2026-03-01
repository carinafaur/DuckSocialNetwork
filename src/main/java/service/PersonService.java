package service;

import domain.*;
import exceptions.UserAlreadyExistsException;
import exceptions.UserNotFoundException;
import exceptions.ValidationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import repository.FriendshipRepository;
import repository.PersonRepository;
import utils.Page;
import validation.Strategy;
import validation.StrategyValidation;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class PersonService {
    private PersonRepository personRepository;
    private FriendshipRepository friendshipRepository;
    private StrategyValidation<User> validator;
    private BCryptPasswordEncoder passwordEncoder;

    public PersonService(PersonRepository personRepository, FriendshipRepository friendshipRepository) {
        this.personRepository = personRepository;
        this.friendshipRepository = friendshipRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public Page<Person> getAllPersons(int pageNumber, int pageSize){
        try {
            return personRepository.getPersons(pageNumber, pageSize);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void addPerson(Long id, String username, String email, String password, String firstName, String lastName, LocalDate dateOfBirth, String ocupation, int empathyLevel) throws UserAlreadyExistsException, ValidationException, SQLException {
        if (personRepository.findUser(id) != null)
            throw new UserAlreadyExistsException("User already exists!", "Error!");
        String encodedPassword = passwordEncoder.encode(password);
        Person person = new Person(id, username, email, encodedPassword, firstName, lastName, dateOfBirth, ocupation, empathyLevel);
        validator = new StrategyValidation<>(Strategy.PERSON);
        if (!validator.validate(person)) {
            throw new ValidationException("Invalid Person!", "Error!");
        } else
            personRepository.addPerson(person);
    }

    public void removePerson(Long id) throws UserNotFoundException,SQLException {
        User user = personRepository.findUser(id);
        if (user == null)
            throw new UserNotFoundException("User not found!", "Error!");
        personRepository.removePerson((Person)user);
        List<Friendship> friendshipList = friendshipRepository.findFriendships(id);
        if (!friendshipList.isEmpty()) {
            for (Friendship friendship : friendshipList) {
                friendshipRepository.removeFriendship(friendship);
            }
        }
    }

}
