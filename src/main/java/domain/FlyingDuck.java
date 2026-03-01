package domain;

public class FlyingDuck extends Duck implements Flyer{
    public FlyingDuck(Long id, String username, String email, String password, DuckType type, double speed, double resistance){
        super(id, username, email, password, type, speed, resistance);
    }

    @Override
    public void fly() {
        System.out.println("Flying Duck"+super.getUsername()+"is flying");
    }

    @Override
    public void print(){
        System.out.println("Flying duck"+super.getUsername());
    }
}
