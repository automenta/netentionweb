/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.netention.dialog;

import automenta.netention.Detail;

/**
 * a step or interaction which is either incomplete or completed
 * @param S state object that it can operate upon
 */
abstract public class Step extends Detail {

    public Step(String name) {
        super(name);
    }


//    //public void setState(S s);
//
//
//    public List<Step> getNextSteps();
//
//    public void addStepWatcher(StepWatcher w);
//    public void removeStepWatcher(StepWatcher w);

}
