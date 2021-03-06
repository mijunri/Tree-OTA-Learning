package timeword;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetLogicAction {
    private String symbol;
    private double value;
    private boolean reset;

    public ResetLogicAction(LogicAction logicAction, boolean reset) {
        this.symbol = logicAction.getSymbol();
        this.value = logicAction.getValue();
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
