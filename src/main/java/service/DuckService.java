package service;

import domain.*;
import exceptions.UserAlreadyExistsException;
import exceptions.UserNotFoundException;
import exceptions.ValidationException;
import repository.DuckRepository;
import repository.FriendshipRepository;
import utils.Page;
import validation.Strategy;
import validation.StrategyValidation;

import java.sql.SQLException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.List;

public class DuckService {
    private DuckRepository duckRepository;
    private FriendshipRepository friendshipRepository;
    private StrategyValidation<User> validator;
    private BCryptPasswordEncoder passwordEncoder;

    public DuckService(DuckRepository duckRepository, FriendshipRepository friendshipRepository) {
        this.duckRepository = duckRepository;
        this.friendshipRepository = friendshipRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public Page<Duck> getAllDucks(int pageNumber, int pageSize) {
        try {
            return duckRepository.getDucks(pageNumber, pageSize);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Page<Duck> getDucksByType(int pageNumber,int pageSize,DuckType type) throws SQLException {
        return duckRepository.getDucksByType(pageNumber, pageSize,type);
    }

    public void addDuck(Long id, String username, String email, String password, DuckType type, double speed, double resistance) throws ValidationException, UserAlreadyExistsException,SQLException {
        if (duckRepository.findUser(id) != null) {
            throw new UserAlreadyExistsException("User already exists!", "Error!");
        }
        String crPassword=passwordEncoder.encode(password);
        Duck duck;
        if (type==DuckType.SWIMMING)
            duck = new SwimmingDuck(id, username, email, crPassword, type, speed, resistance);
        else if(type==DuckType.FLYING)
            duck = new FlyingDuck(id, username, email,crPassword,type, speed, resistance);
        else
            duck=new FlyingAndSwimmingDuck(id, username, email, crPassword, type, speed, resistance);
        validator = new StrategyValidation<User>(Strategy.DUCK);
        if (!validator.validate(duck)) {
            throw new ValidationException("Invalid Duck!", "Error!");
        } else
            duckRepository.addDuck(duck);

    }

    public void removeDuck(Long id) throws UserNotFoundException , SQLException{
        User user= duckRepository.findUser(id);
        if (user == null)
            throw new UserNotFoundException("User not found!", "Error!");
        duckRepository.removeDuck((Duck)user);
        List<Friendship> friendshipList = friendshipRepository.findFriendships(id);
        if (!friendshipList.isEmpty()) {
            for (Friendship friendship : friendshipList) {
                friendshipRepository.removeFriendship(friendship);
            }
        }
    }

    public User findUserByUsernameAndPassword(String username, String password) throws UserNotFoundException, SQLException{
         User user= duckRepository.findUserByUsername(username);
        if (user == null)
            throw new UserNotFoundException("User not found!", "Error!");
        if(!passwordEncoder.matches(password,user.getPassword()))
            throw new UserNotFoundException("Wrong password!", "Error!");
        return user;
    }

    public User findUserByUsername(String username) throws UserNotFoundException, SQLException{
        User user= duckRepository.findUserByUsername(username);
        if(user == null)
            throw new UserNotFoundException("User not found!", "Error!");
        return user;
    }

    public List<User> getAllUsers() throws SQLException{
        return duckRepository.getAllUsers();
    }

    public User findUser(Long id) throws SQLException, UserNotFoundException{
       User user=duckRepository.findUser(id);
       if(user==null)
           throw new UserNotFoundException("User not found!","Error!");
       return user;
    }

    public boolean isActive(User user) throws SQLException{
        return duckRepository.isActive(user);
    }

    public void updateStatus(User user,boolean status) throws SQLException {
        duckRepository.updateStatus(user, status);
    }

}
