package domain;

import exceptions.EventException;

import java.util.*;

public class RaceEvent extends Event {
    private List<SwimmingDuck> allDucks;
    private List<SwimmingDuck> selectedDucks = new ArrayList<>();
    private List<Integer> distances;
    private int M;

    public RaceEvent(String name, List<SwimmingDuck> allDucks,List<Integer> distances,int M)  {
        super(name);
        this.allDucks = allDucks;
        this.distances = distances;
        this.M = M;
    }

    @Override
    public boolean start() {
        try {
            selectDucks();
        }catch(EventException e){
            System.out.println(e.getMessage());
            return false;
        }
        if (!isValidRace()) {
            notifyObservers("The race " + name + " is invalid.");
            return false;
        }
        double raceTime = computeRaceTime();

        notifyObservers("The race " + name + " ended! Total time: " + raceTime + " seconds.");
        return true;
    }

    public void selectDucks() throws  EventException {
        if(allDucks.isEmpty()){
            throw new EventException("There are no ducks in the race!","Error");
        }
        if(allDucks.size()<M){
            throw new EventException("There are not enough ducks in the race!","Error");
        }
        allDucks.sort(Comparator.comparingDouble(Duck::getResistance).reversed());
        selectedDucks = allDucks.subList(0, (int) Math.min(M, allDucks.size()));
    }

    public List<Integer> getDistances() {
        return distances;
    }

    private boolean isValidRace() {
        for (int i = 0; i < selectedDucks.size() - 1; i++) {
            if (selectedDucks.get(i).getResistance() < selectedDucks.get(i + 1).getResistance()) {
                return false;
            }
        }
        return true;
    }

    private double computeRaceTime() {
        double maxTime = 0.0;
        for (int i = 0; i < selectedDucks.size() && i < distances.size(); i++) {
            double time = selectedDucks.get(i).getTime(distances.get(i));
            if (time > maxTime) {
                maxTime = time;
            }
        }
        return maxTime;
    }

    public int getNrDucks() {
        return M;
    }

    public List<SwimmingDuck> getAllDucks() {
        return allDucks;
    }

    @Override
    public String toString() {
        return "RACE EVENT: "+name+"\n DUCKS: "+allDucks.toString()+"\n DISTANCES: "+distances.toString();
    }
}
