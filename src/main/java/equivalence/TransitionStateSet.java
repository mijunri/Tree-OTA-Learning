package equivalence;


import lombok.Data;
import ota.Location;

@Data
public class TransitionStateSet extends BaseStateSet {
    private String symbol;


    public TransitionStateSet(Location location, DBM dbm, String symbol) {
        super(location, dbm);
        this.symbol = symbol;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("symbol is ").append(symbol).append("\n");
        sb.append("dbm is :\n").append(getDbm()).append("\n");
        return sb.toString();
    }
}
