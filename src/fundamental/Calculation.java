/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental;

import java.util.List;

/**
 *
 * @author night
 * @param <InfoCarrier> this is a carrier of info (e.g. Conveyor)
 * @param <ValueCarrier> this is a carrier of units (e.g. Bonus carries stats)
 */
public interface Calculation<InfoCarrier, ValueCarrier> {
    public List<ValueCarrier> calculate(InfoCarrier info);
    public String description();
}
