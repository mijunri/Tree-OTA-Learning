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
}
