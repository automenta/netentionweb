/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.netention.ui.view;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;

/**
 *
 * @author seh
 */
public class PushButton extends Button implements ClickListener {

    private boolean pushed = false;

    public PushButton(String label, boolean pushed) {
        super(label);
        setPushed(pushed);
        addListener((ClickListener)this);
    }

    public void setPushed(boolean b) {
        this.pushed = b;

        if (pushed) {
            addStyleName("pushButtonPushed");
            removeStyleName("pushButtonUnpushed");
        } else {
            removeStyleName("pushButtonPushed");
            addStyleName("pushButtonUnpushed");
        }

    }

    public boolean isPushed() {
        return pushed;
    }

    @Override
    public void buttonClick(ClickEvent event) {
        setPushed(!isPushed());
    }
}
