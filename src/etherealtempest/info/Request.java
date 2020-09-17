/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest.info;

/**
 *
 * @author night
 */
public abstract class Request {
    public enum RequestType {
        Ordinal, //linear
        ASAP; //happens ASAP
    }
    
    private final RequestType requestType;
    private final boolean isWall; //meaning that this request will not let everything else around it happen until it is done
    private final int priority;
    
    public Request(RequestType requestType, int priority, boolean isWall) {
        this.requestType = requestType;
        this.priority = priority;
        this.isWall = isWall;
    }
    
    public boolean update(float tpf, DataStructure data) { //returns if the update is done or not
        boolean finished = update(data, tpf);
        
        if (finished) {
            onFinish(data);
            return true;
        }
        
        return false;
    }
    
    protected abstract boolean update(DataStructure data, float tpf);
    protected abstract void onFinish(DataStructure data);
    
    public RequestType getRequestType() { return requestType; }
    public boolean getIsWall() { return isWall; }
    public int getPriority() { return priority; }
    
}
