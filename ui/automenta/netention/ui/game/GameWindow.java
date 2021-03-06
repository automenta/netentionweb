/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.netention.ui.game;

import automenta.netention.Self;
import automenta.netention.ui.NApplication;
import automenta.netention.ui.browser.AppWindow;
import automenta.netention.ui.browser.Header;
import automenta.netention.ui.browser.Sidebar;
import automenta.netention.ui.browser.sidebar.DBView;
import automenta.netention.ui.browser.sidebar.MissingView;
import automenta.netention.ui.browser.sidebar.RecentView;
import automenta.netention.ui.browser.sidebar.TypesView;
import automenta.netention.ui.browser.sidebar.WhatView;
import automenta.netention.ui.view.BigLabel;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.SplitPanel;
import com.vaadin.ui.VerticalLayout;
import java.awt.geom.Point2D;
import org.vaadin.appfoundation.authentication.SessionHandler;
import org.vaadin.appfoundation.authentication.data.User;
import org.vaadin.hezamu.googlemapwidget.GoogleMap;

/**
 *
 * @author seh
 */
public class GameWindow extends AppWindow {


    public static class MockNowView extends Sidebar.SidebarView {

        public MockNowView() {
            super("Humanity");
        }

        @Override
        public void render(AbstractLayout t) {

            t.addComponent(new BigLabel("Population"));
            t.addComponent(new Label("6,697,254,041"));
            t.addComponent(new BigLabel("Birth Rate"));
            t.addComponent(new Label("13.4 CBR"));
            t.addComponent(new BigLabel("Death Rate"));
            t.addComponent(new Label("10 CDR"));
            t.addComponent(new BigLabel("Happiness"));
            t.addComponent(new Label("Unknown"));
        }


    }
    
    public GameWindow(NApplication app) {
        super(app);
    }


    public void refresh() {
        removeAllComponents();

        User user = SessionHandler.get();
        this.self = (Self) user;

        
        System.out.println(this + " loggedin user: " + self + " " + SessionHandler.get());

        VerticalLayout mainExpand = new VerticalLayout();
        mainExpand.setSizeFull();
        setContent(mainExpand);
        setSizeFull();
        setCaption(getTitle());

        addComponent(new Header(app, this, user));

        NativeSelect modeSelect = new NativeSelect();
        modeSelect.addItem("Visitor");
        modeSelect.addItem("Builder");
        modeSelect.addItem("Soldier");
        modeSelect.addItem("Journalist");
        modeSelect.addItem("Engineer");
        modeSelect.addItem("Teacher");
        modeSelect.addItem("Student");
        modeSelect.addItem("Communicator");
        modeSelect.addItem("Artist");
        modeSelect.addItem("Hacker");
        modeSelect.addItem("Leader");
        modeSelect.setNullSelectionAllowed(false);
        modeSelect.setValue("Visitor");
        addComponent(modeSelect);

        SplitPanel sp = new SplitPanel();
        sp.setOrientation(SplitPanel.ORIENTATION_HORIZONTAL);
        sp = new SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL);
        sp.setSizeFull();
        //sp.setStyleName("main-split");
        mainExpand.addComponent(sp);
        mainExpand.setExpandRatio(sp, 1);

        Sidebar menu = new Sidebar(this);
        menu.addConceptView(new MockNowView());
        menu.addConceptView(new WhatView());
        menu.addConceptView(new MissingView("Who", "Browses people and groups."));
        menu.addConceptView(new MissingView("When", "Browses a timeline of events."));
        menu.addConceptView(new MissingView("Where", "Browses a map of geolocated objects."));
        menu.addConceptView(new MissingView("Why", "Browses a list of things tagged as 'reasons'"));
        menu.addConceptView(new MissingView("Frequent", "Browses a list of frequently accessed objects."));
        menu.addConceptView(new RecentView(this));
        menu.addConceptView(new TypesView(this, app.getSchema()));
        menu.addConceptView(new DBView());
        menu.setSizeFull();

        VerticalLayout content = new VerticalLayout();

        content.addComponent(new HTMLContent("Misson Control", "content/mission.html").newComponent());

//        Console c = new Console();
//        c.setPs("}> ");
//        c.setCols(68);
//        c.setRows(20);
//        c.setMaxBufferSize(40);
//        c.setGreeting("Universal Console");
//        c.reset();
//        content.addComponent(c);

        String apikey = "ABQIAAAA3D2mD_qMSK4fmuGtL57T-xRRsdDM_wdow03Is4OuEBRy5_0BrBSrOyGFe-LrjBmXfxdbUk1PszqMRw";
        GoogleMap m = new GoogleMap(app, apikey);
        m.setCenter(new Point2D.Double(22.3, 60.45));
        m.setZoom(8);
        m.setWidth("800px");
        m.setHeight("600px");


        content.addComponent(m);
        content.setExpandRatio(m, 1);
        content.setSizeFull();

        sp.setFirstComponent(menu);
        sp.setSecondComponent(content);
        sp.setSizeFull();
        sp.setHeight("100%");
        sp.setSplitPosition(25, SplitPanel.UNITS_PERCENTAGE);


    }

    @Override
    public void viewObject(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
