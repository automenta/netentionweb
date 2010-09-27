/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.netention.ui.browser.sidebar;

import automenta.netention.ui.browser.Sidebar.SidebarView;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Label;

/**
 *
 * @author seh
 */
public class MissingView extends SidebarView {
    private final String description;

    public MissingView(String title, String description) {        
        super(title);
        this.description = description;
    }

    
    @Override
    public void render(AbstractLayout target) {
        target.addComponent(new Label(description));
    }

}
