package tree;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import timeword.ResetLogicAction;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorInformation {
    private int index;
    private ResetLogicAction resetLogicAction;
}
