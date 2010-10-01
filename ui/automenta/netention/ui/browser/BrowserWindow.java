/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.netention.ui.browser;

import automenta.netention.ui.view.DetailEditPanel;
import automenta.netention.ui.view.NewPatternPanel;
import automenta.netention.Detail;
import automenta.netention.Pattern;
import automenta.netention.Self;
import automenta.netention.ui.NApplication;
import automenta.netention.ui.ObjectViewer;
import automenta.netention.ui.browser.sidebar.DBView;
import automenta.netention.ui.browser.sidebar.MissingView;
import automenta.netention.ui.browser.sidebar.RecentView;
import automenta.netention.ui.browser.sidebar.TypesView;
import automenta.netention.ui.browser.sidebar.WhatView;
import automenta.netention.ui.view.PatternPanel;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.SplitPanel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.util.HashMap;
import java.util.Map;
import org.vaadin.appfoundation.authentication.SessionHandler;
import org.vaadin.appfoundation.authentication.data.User;

/**
 *
 * @author seh
 */
public class BrowserWindow extends Window implements ObjectViewer {

    private TabSheet contentTabs;
    Map<Object, Component> componentTabs = new HashMap();
    private Self self;
    private final NApplication app;

    public BrowserWindow(NApplication app) {
        super();
        
        this.app = app;

        refresh();

        viewObject(new HTMLContent("What is Netention?", "content/about.html"));
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

        SplitPanel sp = new SplitPanel();
        sp.setOrientation(SplitPanel.ORIENTATION_HORIZONTAL);
        sp = new SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL);
        sp.setSizeFull();
        //sp.setStyleName("main-split");
        mainExpand.addComponent(sp);
        mainExpand.setExpandRatio(sp, 1);

        Sidebar menu = new Sidebar(this);
        menu.addConceptView(new WhatView());
        menu.addConceptView(new MissingView("Who", "Browses people and groups."));
        menu.addConceptView(new MissingView("When", "Browses a timeline of events."));
        menu.addConceptView(new MissingView("Where", "Browses a map of geolocated objects."));
        menu.addConceptView(new MissingView("Why", "Browses a list of things tagged as 'reasons'"));
        menu.addConceptView(new MissingView("Frequent", "Browses a list of frequently accessed objects."));
        menu.addConceptView(new RecentView());
        menu.addConceptView(new TypesView(this, app.getSchema()));
        menu.addConceptView(new DBView());
        menu.setSizeFull();

        VerticalLayout content = new VerticalLayout();

        contentTabs = new TabSheet();
        //remove entries from Map componentTabs when a tab is detached
        contentTabs.addListener(new ComponentDetachListener() {

            @Override
            public void componentDetachedFromContainer(ComponentDetachEvent event) {
                Component d = event.getDetachedComponent();
                Object toRemove = null;

                for (Object o : componentTabs.keySet()) {
                    if (componentTabs.get(o) == d) {
                        toRemove = o;
                        break;
                    }
                }

                if (toRemove != null) {
                    componentTabs.remove(toRemove);
                }
            }
        });
        contentTabs.setSizeFull();
        
        content.addComponent(contentTabs);
        content.setExpandRatio(contentTabs, 1);
        content.setSizeFull();

        sp.setFirstComponent(menu);
        sp.setSecondComponent(content);
        sp.setSizeFull();
        sp.setHeight("100%");
        sp.setSplitPosition(25, SplitPanel.UNITS_PERCENTAGE);

//            TextField fLogin = new TextField("");
//            addComponent(fLogin);
//
//            TextField fPass = new TextField("");
//            addComponent(fPass);
//
//            Button b = new Button("Login");
//
//            b.addListener(new ClickListener() {
//
//                public void buttonClick(ClickEvent event) {
//                    label.setValue("Clicked");
//                    
//                    MemorySelf defaultUser = new MemorySelf();
//                    new SeedSelfBuilder().build(defaultUser);
//                    SessionHandler.setUser((User)defaultUser);
//                }
//                
//            });
//            addComponent(b);

    }
    
    public String getTitle() {
        return /*getSelf().getName() +*/ "Netention";
    }
    
    public class HTMLContent {

        public final String path;
        public final String title;

        public HTMLContent(String title, String path) {
            this.path = path;
            this.title = title;
        }

        public Component newComponent() {
            VerticalLayout newTab = new VerticalLayout();

            Label x = new Label(app.getClassContent("content/about.html"));
            x.addStyleName("htmlContent");
            x.setContentMode(Label.CONTENT_RAW);
            newTab.addComponent(x);

            return newTab;
        }

        public Resource getIcon() {
            return new ThemeResource("tango32/mimetypes/text-html.png");
        }
    }

    protected void addTab(Object o, String label, Component c) {
        addTab(o, label, c, null);
    }

    protected synchronized void addTab(Object o, String label, Component c, Resource icon) {
        if (componentTabs.get(o) != null) {
            //replace component
            Component oldComponent = componentTabs.get(o);
            contentTabs.removeComponent(oldComponent);
        }

        contentTabs.addTab(c);
        contentTabs.getTab(c).setCaption(label);
        contentTabs.getTab(c).setClosable(true);

        if (icon != null) {
            contentTabs.getTab(c).setIcon(icon);
        }

        contentTabs.setSelectedTab(c);

        componentTabs.put(o, c);
    }

    @Override
    public void viewObject(Object o) {
        if (o instanceof HTMLContent) {
            HTMLContent h = (HTMLContent) o;
            addTab(h, h.title, h.newComponent(), h.getIcon());
        } else if (o instanceof Pattern) {
            Pattern p = (Pattern) o;
            addTab(p, p.getName(), new PatternPanel(p), new ThemeResource(p.getIconURL()));
        }
    }

    public void newDetail() {
        final Window detailWindow = new Window("Thinking about...");
        

        Detail newDetail = app.newDetail();
        
        VerticalLayout content = (VerticalLayout) detailWindow.getContent();
        content.setMargin(true);
        content.setSpacing(true);

        content.addComponent(new DetailEditPanel(app.getSchema(), newDetail) {

            @Override
            public void createNewPattern() {
                final Window w = new Window("New Pattern...");
                VerticalLayout content = (VerticalLayout) w.getContent();
                content.setMargin(true);
                content.setSpacing(true);
                content.addComponent(new NewPatternPanel());
                openModalWindow(w, "50%", "50%");
            }

            @Override
            public void cancel() {
                close();
            }

            @Override
            public void save() {
                app.addDetail(detail);
                close();
            }

            protected void close() {
                BrowserWindow.this.removeWindow(detailWindow);
            }
        });

        openModalWindow(detailWindow, "75%", "75%");
        
    }

    public void openModalWindow(Window window, String width, String height) {
        window.setModal(true);
        window.setWidth(width);
        window.setHeight(height);
        addWindow(window);

    }
}
