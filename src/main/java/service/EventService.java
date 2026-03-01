package service;

import domain.*;
import exceptions.EventException;
import exceptions.UserNotFoundException;
import repository.DuckRepository;
import repository.EventsRepository;
import utils.Page;
import validation.Strategy;
import validation.StrategyValidation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EventService {
    private EventsRepository eventsRepository;
    private DuckRepository duckRepository;
    StrategyValidation<RaceEvent> validator;

    public EventService(EventsRepository eventsRepository, DuckRepository duckRepository) {
        this.duckRepository = duckRepository;
        this.eventsRepository = eventsRepository;
        validator = new StrategyValidation<>(Strategy.EVENT);
    }

    public Page<Event> getEvents(int pageNumber, int pageSize) {
        try {
            return eventsRepository.getEvents(pageNumber, pageSize);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void addEvent(String eventName, List<Integer> distances, int M) throws EventException, SQLException {
        if (eventsRepository.findEvent(eventName) != null) {
            throw new EventException("Event already exists", "Error");
        }
        List<SwimmingDuck> ducks = duckRepository.getSwimmingDucks();
        RaceEvent event = new RaceEvent(eventName, ducks, distances, M);
        if (!validator.validate(event)) {
            throw new EventException("Invalid event", "Error");
        }
        eventsRepository.addEvent(event);
    }

    public void subscribeToEvent(String eventName, User user) throws EventException, UserNotFoundException, SQLException {
        Event event = eventsRepository.findEvent(eventName);
        if (event == null) {
            throw new EventException("Event does not exist!", "Error");
        }
        List<User> subscribers = getActiveSubscribers(eventName);
        if (subscribers == null || !subscribers.contains(user)) {
            eventsRepository.subscribeUser(user, event);
            event.registerObserver(user);
        } else throw new EventException("Subscriber already exists!", "Error");
    }

    public void unsubscribeFromEvent(String eventName, Long userId) throws EventException, UserNotFoundException, SQLException {
        Event event = eventsRepository.findEvent(eventName);
        if (event == null) {
            throw new EventException("Event does not exist!", "Error");
        }
        User user = duckRepository.findUser(userId);
        if (user == null) {
            throw new UserNotFoundException("User does not exist!", "Error");
        }
        List<User> subscribers = getActiveSubscribers(eventName);
        if (subscribers.contains(user)) {
            eventsRepository.unsubscribeUser(user, event);
            event.removeObserver(user);
        } else {
            throw new EventException("User is not a subscriber!", "Error");
        }

    }

    public List<Event> getEventsForUser(User user) throws EventException, SQLException {
        List<Event> events = eventsRepository.getAllEvents();

        if (events == null || events.isEmpty()) {
            throw new EventException("No events found!", "Error");
        }

        return events.stream()
                .filter(event -> {
                    try {
                        return isUserSubscribed(event, user);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }

    private boolean isUserSubscribed(Event event, User user) throws SQLException, EventException {
        List<User> subscribers = getSubscribers(event.getName());
        return subscribers != null && subscribers.contains(user);
    }

    public List<User> getSubscribers(String eventName) throws EventException, SQLException {
        Event event = eventsRepository.findEvent(eventName);
        if (event == null) {
            throw new EventException("Event does not exist!", "Error");
        }
        List<Long> ids = eventsRepository.getSubscribers(event);
        if (!ids.isEmpty()) {
            List<User> subscribers = new ArrayList<>();
            for (Long id : ids) {
                User user = duckRepository.findUser(id);
                subscribers.add(user);
            }
            return subscribers;
        }
        return null;
    }

    public List<User> getActiveSubscribers(String eventName) throws EventException, SQLException {
        List<User> subscribers = getSubscribers(eventName);
        if (subscribers == null || subscribers.isEmpty()) return null;
        return subscribers.stream().filter(user -> {
            try {
                return duckRepository.isActive(user);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    public void finishEvent(String eventName) throws EventException, SQLException {
        Event event = eventsRepository.findEvent(eventName);
        if (event == null) {
            throw new EventException("Event does not exist!", "Error");
        }
        List<User> subscribers = getActiveSubscribers(eventName);
        if (subscribers != null) {
            event.setSubscribers(subscribers);
        }
        if (event.start()) {
            try {
                eventsRepository.removeEvent(event);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
