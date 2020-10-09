package tree;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import timeword.LogicTimeWord;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GamaAnswer {
    private boolean sucess;
    private LogicTimeWord logicTimeWord;
}
