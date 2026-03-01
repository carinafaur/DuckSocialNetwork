package repository;


import domain.*;
import exceptions.EventException;
import javafx.util.Pair;
import utils.Page;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventsRepository {
    DuckRepository userRepository;
    DataBaseLoaderEvent dataBaseLoader;

    public EventsRepository(DuckRepository userRepository) {
        this.userRepository = userRepository;
        dataBaseLoader = new DataBaseLoaderEvent();
    }

    private List<Event> loadFromDataBase(int pageNumber, int pageSize) throws SQLException {
        List<Event> events = new ArrayList<>();
        Map<String, List<Integer>> dataEvents;
        int offset = pageNumber * pageSize;
        dataEvents = dataBaseLoader.getEvents(pageSize, offset);
        for (Map.Entry<String, List<Integer>> entry : dataEvents.entrySet()) {
            List<SwimmingDuck> swimmingDucks = userRepository.getSwimmingDucks();
            String eventName = entry.getKey();
            List<Integer> distances = entry.getValue();
            int M = distances.get(distances.size() - 1);
            distances.remove(distances.size() - 1);
            RaceEvent raceEvent = new RaceEvent(eventName, swimmingDucks, distances, M);
            events.add(raceEvent);
        }

        return events;
    }

    private List<Event> loadFromDataBaseAll() throws SQLException {
        List<Event> events = new ArrayList<>();
        Map<String, List<Integer>> dataEvents;
        dataEvents = dataBaseLoader.getAllEvents();
        for (Map.Entry<String, List<Integer>> entry : dataEvents.entrySet()) {
            List<SwimmingDuck> swimmingDucks = userRepository.getSwimmingDucks();
            String eventName = entry.getKey();
            List<Integer> distances = entry.getValue();
            int M = distances.get(distances.size() - 1);
            distances.remove(distances.size() - 1);
            RaceEvent raceEvent = new RaceEvent(eventName, swimmingDucks, distances, M);
            events.add(raceEvent);
        }

        return events;
    }

    public List<Event> getAllEvents() throws SQLException {
        return loadFromDataBaseAll();
    }

    public List<Long> getSubscribers(Event event) throws SQLException {
        return dataBaseLoader.getSubscribers(event.getName());
    }

    public Page<Event> getEvents(int pageNumber, int pageSize) throws SQLException {
        int offset = pageNumber * pageSize;
        int totalElements = dataBaseLoader.getEventsCount();

        if (offset >= totalElements && totalElements > 0) {
            return new Page<>(new ArrayList<>(), 0, totalElements);
        }
        List<Event> pageContent = loadFromDataBase(pageNumber, pageSize);
        return new Page<>(pageContent, pageContent.size(), totalElements);
    }

    public Event findEvent(String eventName) throws SQLException,EventException {
        Pair<String, List<Integer>> pair = dataBaseLoader.findEventByName(eventName);
        if(pair==null)
            return null;
        List<SwimmingDuck> swimmingDucks = userRepository.getSwimmingDucks();
        String event = pair.getKey();
        List<Integer> distances = pair.getValue();
        int M = distances.get(distances.size() - 1);
        distances.remove(distances.size() - 1);
        RaceEvent raceEvent = new RaceEvent(event, swimmingDucks, distances, M);
        return raceEvent;
    }

    public void addEvent(Event event) throws SQLException {
        dataBaseLoader.saveEvent((RaceEvent) event);
    }

    public void removeEvent(Event event) throws SQLException {
        dataBaseLoader.deleteEvent((RaceEvent) event);
    }

    public void subscribeUser(User user,Event event) throws SQLException {
        if(user instanceof Duck){
            dataBaseLoader.subscribeDuck(user.getId(), event.getName());
        }
        else dataBaseLoader.subscribePerson(user.getId(), event.getName());
    }

    public void unsubscribeUser(User user,Event event) throws SQLException {
        if(user instanceof Duck){
            dataBaseLoader.unsubscribeDuck(user.getId(), event.getName());
        }else dataBaseLoader.unsubscribePerson(user.getId(), event.getName());
    }

}
