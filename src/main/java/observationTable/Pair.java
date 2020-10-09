package observationTable;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import timeword.LogicTimeWord;
import util.TimeWordUtil;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pair {
    private LogicTimeWord prefix;
    private LogicTimeWord suffix;


    public LogicTimeWord timeWord(){
        return TimeWordUtil.concat(prefix,suffix);
    }
}
