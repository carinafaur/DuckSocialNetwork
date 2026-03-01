package domain;

public class FlyingAndSwimmingDuck extends Duck implements Flyer ,Swimmer{
    public FlyingAndSwimmingDuck(Long id, String username, String email, String password, DuckType type, double speed, double resistance){
        super(id, username, email, password, type, speed, resistance);
    }

    @Override
    public void fly() {
        System.out.println("Flying Duck"+super.getUsername()+"is flying");
    }

    @Override
    public void swim(){
        System.out.println("Swimming Duck"+super.getUsername()+"is swimming");
    }

    @Override
    public void print() {
        System.out.println("Flying and Swimming Duck"+super.getUsername()+"is printing");
    }
}
