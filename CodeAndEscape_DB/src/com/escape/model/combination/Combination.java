package main.java.com.escape.model.combination;

public class Combination {
    private String itemA;
    private String itemB;
    private String result;
public Combination(String itemA ,String itemB,String result){
    this.itemA=itemA;
    this.itemB=itemB;
    this.result=result;
}

    public boolean matches(String id1,String id2){
    if(id1.equals(itemA) && id2.equals(itemB)){
        return true;
    }

    if(id2.equals(itemA) && id1.equals(itemB)){
        return true;
    }

    return false;
    }

    public String getItemA() {
        return itemA;
    }

    public String getItemB() {
        return itemB;
    }

    public String getResult() {
        return result;
    }
}
