package equivalence;

import lombok.Data;
import ota.Location;

@Data
public abstract class BaseStateSet {
    private Location location;
    private DBM dbm;
    private BaseStateSet preStateSet;

    public BaseStateSet(Location location, DBM dbm) {
        this.location = location;
        this.dbm = dbm;
    }

    public boolean include(BaseStateSet stateSet){
        if(stateSet.getLocation() != getLocation()){
            return false;
        }
        if(!getDbm().include(stateSet.getDbm())){
            return false;
        }
        return true;
    }


}
