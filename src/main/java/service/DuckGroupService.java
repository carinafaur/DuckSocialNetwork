package service;

import domain.*;
import exceptions.DuckGroupException;
import exceptions.UserNotFoundException;
import repository.DuckGroupRepository;
import repository.DuckRepository;
import utils.Page;
import validation.Strategy;
import validation.StrategyValidation;

import java.sql.SQLException;
import java.util.List;

public class DuckGroupService {
    private DuckGroupRepository duckGroupRepository;
    private DuckRepository userRepository;
    private StrategyValidation<DuckGroup<? extends Duck>> validator;

    public DuckGroupService(DuckGroupRepository duckGroupRepository, DuckRepository userRepository) {
        this.duckGroupRepository = duckGroupRepository;
        this.userRepository = userRepository;
    }

    public Page<DuckGroup<? extends Duck>> getDuckGroups(int pageNumber, int pageSize) throws SQLException{
       return duckGroupRepository.getDuckGroups(pageNumber,pageSize);
    }

    public void addDuckGroup(Long id, String name, List<Duck> ducks, DuckType type) throws DuckGroupException ,SQLException{
        if (duckGroupRepository.findDuckGroup(id) == null) {
            DuckGroup<? extends Duck> duckGroup = new DuckGroup<>(id, name, ducks, type);
            validator = new StrategyValidation<>(Strategy.DUCKGROUP);
            if (!validator.validate(duckGroup))
                throw new DuckGroupException("Invalid DuckGroup!", "Error");
            else
                duckGroupRepository.addDuckGroup(duckGroup);
        } else {
            throw new DuckGroupException("The DuckGroup already exists!", "Error");
        }
    }

    public <T extends Duck> void addMemberDuckGroup(Long idGroup, Long idDuck) throws DuckGroupException, UserNotFoundException, SQLException {
        DuckGroup<Duck> duckGroup = duckGroupRepository.findDuckGroup(idGroup);
        if (duckGroup == null)
            throw new DuckGroupException("DuckGroup not found!", "Error");
        else {
            Duck duck=(Duck)userRepository.findUser(idDuck);
            if (duck == null)
                throw new UserNotFoundException("User not found!", "Error");
            else if (duck.getGroup() != 0)
                throw new DuckGroupException("The duck is already in a group!", "Error");
            else {
                if (duck.getType() == DuckType.FLYING) {
                    if (duckGroup.hasMembers()) {
                        if (duck.getType() != duckGroup.getDuckType())
                            throw new DuckGroupException("Invalid Duck type for the group!", "Error");
                        else {
                            duckGroupRepository.addDuckToGroup(duckGroup, new FlyingDuck(duck.getId(), duck.getUsername(), duck.getEmail(), duck.getPassword(), duck.getType(), duck.getSpeed(), duck.getResistance()));
                        }
                    } else
                        duckGroupRepository.addDuckToGroup(duckGroup, new FlyingDuck(duck.getId(), duck.getUsername(), duck.getEmail(), duck.getPassword(), duck.getType(), duck.getSpeed(), duck.getResistance()));
                } else if (duck.getType() == DuckType.SWIMMING) {
                    if (duckGroup.hasMembers()) {
                        if (duck.getType() != duckGroup.getDuckType())
                            throw new DuckGroupException("Invalid Duck type for the group!", "Error");
                        else
                            duckGroupRepository.addDuckToGroup(duckGroup, new SwimmingDuck(duck.getId(), duck.getUsername(), duck.getEmail(), duck.getPassword(), duck.getType(), duck.getSpeed(), duck.getResistance()));

                    } else
                        duckGroupRepository.addDuckToGroup(duckGroup, new SwimmingDuck(duck.getId(), duck.getUsername(), duck.getEmail(), duck.getPassword(), duck.getType(), duck.getSpeed(), duck.getResistance()));
                } else {
                    if (duckGroup.hasMembers()) {
                        if (duck.getType() != duckGroup.getDuckType())
                            throw new DuckGroupException("Invalid Duck type for the group!", "Error");
                        else
                            duckGroupRepository.addDuckToGroup(duckGroup, new FlyingAndSwimmingDuck(duck.getId(), duck.getUsername(), duck.getEmail(), duck.getPassword(), duck.getType(), duck.getSpeed(), duck.getResistance()));
                    } else
                        duckGroupRepository.addDuckToGroup(duckGroup, new FlyingAndSwimmingDuck(duck.getId(), duck.getUsername(), duck.getEmail(), duck.getPassword(), duck.getType(), duck.getSpeed(), duck.getResistance()));
                }
            }
        }
    }
}
