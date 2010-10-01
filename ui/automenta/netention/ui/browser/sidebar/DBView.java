/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.netention.ui.browser.sidebar;

import automenta.netention.Detail;
import automenta.netention.Self;
import automenta.netention.ui.browser.Sidebar.SidebarView;
import automenta.netention.ui.view.BigLabel;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
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

    public class DBPanel extends VerticalLayout {

        public DBPanel() {
            super();

            refresh();

        }

        protected synchronized void refresh() {
            removeAllComponents();

            IFacade db = FacadeFactory.getFacade();

            addComponent(new BigLabel("Database"));

            addComponent(new Label("Selfs: " + db.count(Self.class)));
            addComponent(new Label("Details: " + db.count(Detail.class)));

            //        List<Self> selves = db.list(Self.class);
            //        for (Self s : selves)
            //            target.addComponent(new SelfPanelMini(s));

            Button refreshButton = new Button("Refresh");
            refreshButton.addListener(new ClickListener() {

                @Override
                public void buttonClick(ClickEvent event) {
                    refresh();
                }

            });
            addComponent(refreshButton);
        }
    }

    @Override
    public void render(AbstractLayout target) {


//        String query = "SELECT u FROM Self AS u"; // WHERE u.username=:username";
//        Map<String, Object> parameters = new HashMap<String, Object>();
//        //parameters.put("username", "johndoe");
//        Self self = db.find(query, parameters);

//        target.addComponent(new Label("<b>AbstractPojo's</b>"));
//        List<AbstractPojo> x = db.list(AbstractPojo.class);
//        for (AbstractPojo o : x)
//            target.addComponent(new IconPanel(o));

        target.addComponent(new DBPanel());

    }
}
