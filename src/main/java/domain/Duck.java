package domain;

public abstract class Duck extends User {
    private DuckType type;
    private double speed;
    private double resistance;
    private long groupId;

    public Duck(Long id, String username, String email, String password, DuckType type, double speed, double resistance) {
        super(id, username, email, password);
        this.type = type;
        this.speed = speed;
        this.resistance = resistance;
        this.groupId = 0;
    }

    @Override
    public String toString() {
        return super.toString() + "DUCK: type- " + type + ", speed- " + speed + ", resistance- " + resistance;

    }

    public DuckType getType() {
        return type;
    }

    public double getSpeed() {
        return speed;
    }

    public double getResistance() {
        return resistance;
    }

    public long getGroup() {
        return groupId;
    }

    public void setGroup(long groupId) {
        this.groupId = groupId;
    }

    public double getTime(int distance){
        return 2.0*distance/speed;
    }

    public abstract void print();

}
