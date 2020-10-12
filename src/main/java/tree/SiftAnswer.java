package tree;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SiftAnswer {
    private Node node;
    private boolean refine;
}
