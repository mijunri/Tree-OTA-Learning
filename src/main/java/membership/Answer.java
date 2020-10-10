package membership;

import lombok.AllArgsConstructor;
import lombok.Data;
import timeword.ResetLogicTimeWord;

@Data
@AllArgsConstructor
public class Answer {
    private ResetLogicTimeWord resetLogicTimeWord;
    private boolean accept;
}
