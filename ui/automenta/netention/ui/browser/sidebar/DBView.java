/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.netention.ui.browser.sidebar;

import automenta.netention.Self;
import automenta.netention.ui.browser.Sidebar.SidebarView;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import java.util.List;
import org.vaadin.appfoundation.persistence.data.AbstractPojo;
import org.vaadin.appfoundation.persistence.facade.FacadeFactory;
import org.vaadin.appfoundation.persistence.facade.IFacade;

/**
 *
 * @author seh
 */
public class DBView extends SidebarView {

    public static class SelfPanelMini extends VerticalLayout {

        public SelfPanelMini(Self s) {
            super();
            addComponent(new Label(s.getId() + " " + s.getName() + " " + s.getPassword()));
        }
            
    }
    
    public static class IconPanel extends VerticalLayout {

        public IconPanel(AbstractPojo o) {
            super();
            addComponent(new Label(o.getId() + " " + o.toString()));
        }
        
    }
    
    public DBView() {
        super("Database");
    }

    @Override
    public void render(AbstractLayout target) {
        
        IFacade db = FacadeFactory.getFacade();
        
//        String query = "SELECT u FROM Self AS u"; // WHERE u.username=:username";
//        Map<String, Object> parameters = new HashMap<String, Object>();
//        //parameters.put("username", "johndoe");
//        Self self = db.find(query, parameters);
        
//        target.addComponent(new Label("<b>AbstractPojo's</b>"));
//        List<AbstractPojo> x = db.list(AbstractPojo.class);
//        for (AbstractPojo o : x)
//            target.addComponent(new IconPanel(o));
        
        target.addComponent(new Label("<b>Self's</b>"));
        List<Self> selves = db.list(Self.class);
        for (Self s : selves)
            target.addComponent(new SelfPanelMini(s));
        
    }
    
    
}
