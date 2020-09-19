package learning;

public class Value implements Comparable<Value>{
    int value;
    boolean equal;

    public Value(int value, boolean equal) {
        this.value = value;
        this.equal = equal;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isEqual() {
        return equal;
    }

    public void setEqual(boolean equal) {
        this.equal = equal;
    }

    public static Value add(Value v2, Value v3){
        int value = v2.getValue()+v3.getValue();
        boolean equal = v2.isEqual() && v3.isEqual();
        Value v1 = new Value(value,equal);
        return v1;
    }

    @Override
    public int compareTo(Value o) {
        if(this.getValue() < o.getValue()){
            return -1;
        }
        if(this.getValue() == o.getValue()){
            if(this.equal == false && o.equal == true){
                return -1;
            }
            if(this.equal == false && o.equal == false){
                return 0;
            }
            if(this.equal == true && o.equal == true){
                return 0;
            }
        }
        return 1;
    }
}
