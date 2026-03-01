package validation;

import domain.*;

public class StrategyValidation<T> {
    Strategy strategy;

    public StrategyValidation(Strategy strategy) {
        this.strategy = strategy;
    }

    public boolean validate(T toBeValidated) {
        if (this.strategy == Strategy.DUCK) {
            ValidateDuck validateDuck = new ValidateDuck((Duck) toBeValidated);
            return validateDuck.validate();
        } else if (this.strategy == Strategy.PERSON) {
            ValidatePerson validatePerson = new ValidatePerson((Person) toBeValidated);
            return validatePerson.validate();
        }else if (this.strategy == Strategy.FRIENDSHIP) {
            ValidateFriendship validateFriendship=new ValidateFriendship((Friendship) toBeValidated);
            return validateFriendship.validate();
        }else if(this.strategy == Strategy.DUCKGROUP){
            ValidateDuckGroup validateDuckGroup=new ValidateDuckGroup((DuckGroup<? extends Duck>) toBeValidated);
            return validateDuckGroup.validate();
        }else if(this.strategy==Strategy.EVENT)
        {
            ValidateEvent validateEvent=new ValidateEvent((RaceEvent) toBeValidated);
            return validateEvent.validate();
        }
        return false;
    }
}
