package timeword;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Action {
    private String Symbol;
    private double value;

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(")
            .append(getSymbol())
            .append(",")
            .append(getValue())
            .append(")");
        return stringBuilder.toString();
    }
}
