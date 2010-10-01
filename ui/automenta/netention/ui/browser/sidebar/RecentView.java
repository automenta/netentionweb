/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.netention.ui.browser.sidebar;

import automenta.netention.Detail;
import automenta.netention.ui.browser.Sidebar.SidebarView;
import automenta.netention.ui.view.WideDetailIcon;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.VerticalLayout;
import java.util.List;
import org.vaadin.appfoundation.persistence.data.AbstractPojo;
import org.vaadin.appfoundation.persistence.facade.FacadeFactory;
import org.vaadin.appfoundation.persistence.facade.IFacade;

/**
 * Browses a list of recently accessed objects
 * @author seh
 */
public class RecentView extends SidebarView {

    public RecentView() {
        super("Recent");
    }

    public static class RecentPanel extends VerticalLayout {

        public RecentPanel() {
            super();


            IFacade db = FacadeFactory.getFacade();
            String query = "SELECT u FROM Detail AS u ORDER BY u.whenModified DESC";

            List<AbstractPojo> result = db.list(query, null);
            for (AbstractPojo pj : result) {
                if (pj instanceof Detail) {
                    addComponent(new WideDetailIcon(((Detail)pj)));
                }
            }

        }


    }

    @Override
    public void render(AbstractLayout target) {
        target.addComponent(new RecentPanel());
    }

}
