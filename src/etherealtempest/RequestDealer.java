/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest;

import etherealtempest.Request.RequestType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 */
public class RequestDealer implements Requestable {
    private final List<Request> requests = new ArrayList<>();
    
    public RequestDealer() {}
    
    @Override
    public List<Request> getRequests() {
        return requests;
    }
    
    @Override
    public void attemptToResolveCurrentRequest(float tpf, DataStructure data) {
        int highestIndex = 0;
        for (int i = 0; i < requests.size(); i++) {
            if (requests.get(i).getRequestType() == RequestType.ASAP) {
                requests.get(i).update(tpf, data);
            } else if (requests.get(i).getPriority() > requests.get(highestIndex).getPriority()) {
                highestIndex = i;
            }
        }
        
        boolean finished = requests.get(highestIndex).update(tpf, data);
        
        if (finished) {
            requests.remove(highestIndex);
        }
    }
    
}
