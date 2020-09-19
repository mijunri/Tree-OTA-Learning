package timeword;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

public class LogicAction extends Action{

    private boolean reset;

    public LogicAction(String Symbol, double value, boolean reset) {
        super(Symbol, value);
        this.reset = reset;
    }


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
