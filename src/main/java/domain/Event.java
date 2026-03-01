package domain;

import java.util.ArrayList;
import java.util.List;

public abstract class Event implements Subject {
    protected String name;
    protected List<User> subscribers = new ArrayList<>();

    public Event(String name) {
        this.name = name;
    }

    @Override
    public void registerObserver(Observer o) {
        if (!subscribers.contains((User)o)) {
            subscribers.add((User)o);
        }
    }

    @Override
    public void removeObserver(Observer o) {
        subscribers.remove((User)o);
    }

    public void notifyObservers(String message) {
        for (User user : subscribers) {
            user.update(this, message);
        }
    }

    public void setSubscribers(List<User> subscribers) {
        this.subscribers = subscribers;
    }

    public String getName() {
        return name;
    }

    public List<User> getSubscribers() {
        return subscribers;
    }

    public abstract boolean start();
}

