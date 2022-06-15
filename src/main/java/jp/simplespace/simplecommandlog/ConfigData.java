package jp.simplespace.simplecommandlog;

import java.util.List;
import java.util.Map;

public class ConfigData {
    public Map<String, List<String>> cmdlog;
    public boolean eval;

    public Map<String,List<String>> getCmdlog(){
        return cmdlog;
    }
    public void setCmdlog(Map<String,List<String>> cmdlog){
        this.cmdlog=cmdlog;
    }
    public boolean isEval(){
        return eval;
    }
    public void setEval(boolean eval){
        this.eval=eval;
    }
}
