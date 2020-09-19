package ota;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Location{
    private int id;
    private String name;
    private boolean init;
    private boolean accept;

    public Location(int id,String name){
        this.id = id;
        this.name = name;
    }

    public Location(int id){
        this.id = id;
    }

}

