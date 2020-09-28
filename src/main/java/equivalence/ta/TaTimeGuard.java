package equivalence.ta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ota.TimeGuard;

import java.sql.Time;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor

public class TaTimeGuard {
    private Map<TimeGuard, Clock> timeGuardClockMap;

    public TaTimeGuard() {
        timeGuardClockMap = new HashMap<>();
    }

    public Set<Map.Entry<TimeGuard, Clock>> entrySet(){
        return timeGuardClockMap.entrySet();
    }

    public void add(TimeGuard timeGuard, Clock c){
        timeGuardClockMap.put(timeGuard, c);
    }
}
