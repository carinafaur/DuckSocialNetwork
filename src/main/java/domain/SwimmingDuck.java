package domain;

public class SwimmingDuck extends Duck implements Swimmer {
    public SwimmingDuck(Long id, String username, String email, String password, DuckType type, double speed, double resistance) {
        super(id, username, email, password, type, speed, resistance);
    }

    @Override
    public void swim() {
        System.out.println("Swimming Duck" +super.getUsername()+ "is swimming");
    }

    @Override
    public void print() {
        System.out.println("Swimming Duck"+super.getUsername());
    }
}
