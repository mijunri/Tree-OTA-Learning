package equivalence.ta;

import java.util.Objects;

public class Clock {
    private String name;
    private double value;

    public Clock(String name){
        this.name = name;
    }

    public void delay(double delay){
        value+=delay;
    }

    public double getValue(){
        return value;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Clock)) {
            return false;
        }
        Clock clock = (Clock) o;
        return Objects.equals(getName(), clock.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
