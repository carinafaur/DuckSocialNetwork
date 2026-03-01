package validation;

import domain.Event;
import domain.RaceEvent;

import java.util.List;

public class ValidateEvent {
    private RaceEvent event;

    public ValidateEvent(RaceEvent event) {
        this.event = event;
    }

    public boolean validate() {
        if (event == null)
            return false;
        if(event.getName() == null)
            return false;
        List<Integer> dist=event.getDistances();
        for(int i=0;i<dist.size()-1;i++)
            if(dist.get(i)>dist.get(i+1))
                return false;
        if(event.getNrDucks()>event.getAllDucks().size())
            return false;
        return true;
    }
}
