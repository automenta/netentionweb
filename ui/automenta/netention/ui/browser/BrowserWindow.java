/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.netention.ui.browser;

import automenta.netention.Detail;
import automenta.netention.Pattern;
import automenta.netention.Self;
import automenta.netention.impl.MemoryDetail;
import automenta.netention.ui.NApplication;
import automenta.netention.ui.ObjectViewer;
import automenta.netention.ui.browser.sidebar.DBView;
import automenta.netention.ui.browser.sidebar.MissingView;
import automenta.netention.ui.browser.sidebar.TypesView;
import automenta.netention.ui.browser.sidebar.WhatView;
import automenta.netention.ui.view.PatternView;
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
        menu.addConceptView(new MissingView("Recent", "Browses a list of recently accessed objects."));
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
            addTab(p, p.getName(), new PatternView(p), new ThemeResource(p.getIconURL()));
        }
    }

    public void newDetail() {
        final Window detailWindow = new Window("Thinking about...");
        
        // ...and make it modal
        detailWindow.setModal(true);
        detailWindow.setWidth("75%");
        detailWindow.setHeight("75%");

        Detail newDetail = new MemoryDetail();
        
//        // Configure the windws layout; by default a VerticalLayout
        VerticalLayout content = (VerticalLayout) detailWindow.getContent();
        content.setMargin(true);
        content.setSpacing(true);

        content.addComponent(new DetailEditPanel(app.getSchema(), newDetail));

//
//        LoginForm login = new LoginForm();
//        login.setWidth("100%");
//        login.setHeight("300px");
//        login.addListener(new LoginForm.LoginListener() {
//            public void onLogin(LoginEvent event) {
//
//                String username = event.getLoginParameter("username");
//                String pw = PasswordUtil.generateHashedPassword(event.getLoginParameter("password"));
//
////                browser.showNotification(
////                        "New Login",
////                        "Username: " + event.getLoginParameter("username")
////                                + ", password: "
////                                + event.getLoginParameter("password"));
//
////                User u = null;
////                try {
////                    u = AuthenticationUtil.authenticate(username, pw);
////                } catch (InvalidCredentialsException ex) {
////                    Logger.getLogger(Header.class.getName()).log(Level.SEVERE, null, ex);
////                } catch (AccountLockedException ex) {
////                    Logger.getLogger(Header.class.getName()).log(Level.SEVERE, null, ex);
////                }
////                if (u == null) {
////                    UserUtil.registerUser(username, pw, pw);
////                }
////                System.out.println("authenticated: " + u);
//
//                String query = "SELECT u FROM User AS u WHERE u.username=:username AND u.password=:password";
//                Map<String, Object> parameters = new HashMap<String, Object>();
//                parameters.put("username", username);
//                parameters.put("password", pw);
//
//                User self = db.find(query, parameters);
//                if (self==null) {
//                    browser.showNotification("Invalid username or password.");
//                }
//                else {
//                    SessionHandler.setUser(self);
//                }
//
//                browser.removeWindow(loginWindow);
//
//                //Header.this.getWindow().open(new ExternalResource(app.getURL()));
//                browser.refresh();
//            }
//        });
//        content.addComponent(login);

        addWindow(detailWindow);
        
    }
}
