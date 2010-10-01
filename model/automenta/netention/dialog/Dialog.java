/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.netention.dialog;

import automenta.netention.Node;

/**
 * an instance of an interaction
 * @author seh
 */
public class Dialog extends Node {

    final Step start;
    Step step;
    
    public Dialog(Step start) {
        super("", "");
        this.step = this.start = start;
    }

    
    
}
