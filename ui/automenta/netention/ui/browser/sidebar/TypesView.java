/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.netention.ui.browser.sidebar;

import automenta.netention.Pattern;
import automenta.netention.Schema;
import automenta.netention.ui.browser.Sidebar.SidebarView;
import automenta.netention.ui.ObjectViewer;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author seh
 */
public class TypesView extends SidebarView {
    private final Schema schema;
    private final ObjectViewer viewer;
    
    public TypesView(ObjectViewer viewer, Schema schema) {
        super("Type");
        this.schema = schema;
        this.viewer = viewer;
    }

    @Override
    public void render(AbstractLayout target) {
        Map<String, Pattern> patterns = schema.getPatterns();
        List<Pattern> p = new ArrayList(patterns.values());
        Collections.sort(p, new Comparator<Pattern>() {

            @Override
            public int compare(Pattern a, Pattern b) {
                return a.getName().compareTo(b.getName());
            }
            
        });
        for (final Pattern x : p) {
            Button b = new Button(x.getName());
            b.setIcon(new ThemeResource(x.getIconURL()));
            
            b.addListener(new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    viewer.viewObject(x);
                }                
            });
            
            target.addComponent(b);            
        }
    }
    
    

    
}
