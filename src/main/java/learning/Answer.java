package learning;

import lombok.AllArgsConstructor;
import lombok.Data;
import timeword.LogicTimeWord;

@Data
@AllArgsConstructor

public class Answer {
    private LogicTimeWord logicTimeWord;
    private boolean accept;
}
