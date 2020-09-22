package timeword;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
public class LogicAction{
    private String Symbol;
    private double value;
    private boolean reset;

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(")
                .append(getSymbol())
                .append(",")
                .append(getValue())
                .append(",")
                .append(isReset()?"r":"n")
                .append(")");
        return stringBuilder.toString();
    }

}
