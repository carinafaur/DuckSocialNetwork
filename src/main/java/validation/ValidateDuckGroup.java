package validation;

import domain.Duck;
import domain.DuckGroup;
import domain.DuckType;

import java.util.Objects;

public class ValidateDuckGroup {
    private DuckGroup<? extends Duck> duckGroup;

    public ValidateDuckGroup(DuckGroup<? extends Duck> duckGroup) {
        this.duckGroup = duckGroup;
    }

    public boolean validate() {
        if (Objects.equals(duckGroup.getGroupName(), ""))
            return false;
        if (!duckGroup.getMembers().isEmpty()) {
            DuckType duckType = duckGroup.getDuckType();
            for (Duck duck : duckGroup.getMembers()) {
                if (!duck.getType().equals(duckType))
                    return false;
            }
        }
        return true;
    }
}
