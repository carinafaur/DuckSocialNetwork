package validation;

import domain.Duck;
import domain.DuckType;

public class ValidateDuck{
    private Duck duck;

    public ValidateDuck(Duck duck) {
        this.duck=duck;
    }

    public boolean validate() {
        if(duck==null)
            return false;
        if(duck.getType()!= DuckType.SWIMMING && duck.getType()!=DuckType.FLYING && duck.getType()!=DuckType.FLYING_AND_SWIMMING){
            return false;
        }
        return true;
    }

}
