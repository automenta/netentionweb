/*
 * DemoApplication.java
 *
 * Created on September 15, 2010, 11:17 AM
 */
package automenta.netention.ui;

import automenta.netention.Schema;
import automenta.netention.Self;
import automenta.netention.ui.browser.BrowserWindow;
import com.vaadin.Application;
import com.vaadin.terminal.ClassResource;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.LoginForm.LoginEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.vaadin.appfoundation.authentication.SessionHandler;
import org.vaadin.appfoundation.authentication.data.User;
import org.vaadin.appfoundation.authentication.util.PasswordUtil;
import org.vaadin.appfoundation.authorization.Permissions;
import org.vaadin.appfoundation.authorization.jpa.JPAPermissionManager;
import org.vaadin.appfoundation.i18n.Lang;
import org.vaadin.appfoundation.persistence.facade.FacadeFactory;
import org.vaadin.appfoundation.persistence.facade.IFacade;
import org.vaadin.appfoundation.view.ViewHandler;

/** 
 *
 * @author me
 * @version 
 */
abstract public class NApplication extends Application implements Netention {

    protected Logger logger = Logger.getLogger(NApplication.class);
    protected static Schema schema;
    private final IFacade db;

    public NApplication() {
        super();

        db = FacadeFactory.getFacade();

        if (schema == null) {
            schema = new Schema();
            SeedSelfBuilder.build(schema);
            
            db.store(schema);
        }
        

    }

    @Override
    public void init() {
        SessionHandler.initialize(this);
        ViewHandler.initialize(this);
        Lang.initialize(this);
        Permissions.initialize(this, new JPAPermissionManager());
    }


    //TODO extract LoginWindow class
    public void login(final BrowserWindow browser) {
        //show a dialog window w/ login form
          // Create the window...
        final Window loginWindow = new Window("Login");
        // ...and make it modal
        loginWindow.setModal(true);
        loginWindow.setWidth("25%");
        loginWindow.setHeight("50%");

        // Configure the windws layout; by default a VerticalLayout
        VerticalLayout content = (VerticalLayout) loginWindow.getContent();
        content.setMargin(true);
        content.setSpacing(true);
               
        LoginForm login = new LoginForm();
        login.setWidth("100%");
        login.setHeight("300px");
        login.addListener(new LoginForm.LoginListener() {
            public void onLogin(LoginEvent event) {

                String username = event.getLoginParameter("username");
                String pw = PasswordUtil.generateHashedPassword(event.getLoginParameter("password"));
                
//                browser.showNotification(
//                        "New Login",
//                        "Username: " + event.getLoginParameter("username")
//                                + ", password: "
//                                + event.getLoginParameter("password"));
                
//                User u = null;
//                try {
//                    u = AuthenticationUtil.authenticate(username, pw);
//                } catch (InvalidCredentialsException ex) {
//                    Logger.getLogger(Header.class.getName()).log(Level.SEVERE, null, ex);
//                } catch (AccountLockedException ex) {
//                    Logger.getLogger(Header.class.getName()).log(Level.SEVERE, null, ex);
//                }
//                if (u == null) {
//                    UserUtil.registerUser(username, pw, pw);
//                }                
//                System.out.println("authenticated: " + u);
                                
                String query = "SELECT u FROM User AS u WHERE u.username=:username AND u.password=:password";
                Map<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("username", username);
                parameters.put("password", pw);
                
                User self = db.find(query, parameters);
                if (self==null) {
                    browser.showNotification("Invalid username or password.");
                }
                else {                
                    SessionHandler.setUser(self);                
                }
                
                browser.removeWindow(loginWindow);
                
                //Header.this.getWindow().open(new ExternalResource(app.getURL()));
                browser.refresh();
            }
        });
        content.addComponent(login);

        browser.addWindow(loginWindow);
    }

    public void register(final BrowserWindow root) {
        //show a dialog window w/ login form
          // Create the window...
        final Window regWindow = new Window("Register");
        // ...and make it modal
        regWindow.setModal(true);
        regWindow.setWidth("25%");
        regWindow.setHeight("50%");

        // Configure the windws layout; by default a VerticalLayout
        VerticalLayout content = (VerticalLayout) regWindow.getContent();
        content.setMargin(true);
        content.setSpacing(true);

        LoginForm login = new LoginForm();
        login.setWidth("100%");
        login.setHeight("300px");
        
        login.addListener(new LoginForm.LoginListener() {
            public void onLogin(LoginEvent event) {
                
                Self newSelf = new Self();
                newSelf.setUsername(event.getLoginParameter("username"));
                newSelf.setName(event.getLoginParameter("username"));
                                
                newSelf.setPassword(PasswordUtil.generateHashedPassword(event.getLoginParameter("password")));
                
                SeedSelfBuilder.build(newSelf);
                                
                db.store(newSelf);                
                
                SessionHandler.setUser(newSelf);
        
                root.removeWindow(regWindow);
                
                root.refresh();
            }
        });
        content.addComponent(login);

        root.addWindow(regWindow);
    }
    
    public void logout(BrowserWindow browser) {
        SessionHandler.setUser(null);
        browser.refresh();
    }
    
    /** gets class resource relative to NApplication.class, which is in package automenta.netention.ui */
    public String getClassContent(String path) {
        ClassResource fr = new ClassResource(NApplication.class, path, this);
        InputStream is = fr.getStream().getStream();

        try {
            final char[] buffer = new char[is.available()];
            StringBuilder out = new StringBuilder();
            Reader in = new InputStreamReader(is, "UTF-8");
            int read;
            do {
                read = in.read(buffer, 0, buffer.length);
                if (read > 0) {
                    out.append(buffer, 0, read);
                }
            } while (read >= 0);

            return out.toString();
        } catch (Exception ex) {
            logger.error("getHTML: ", ex);
            return "";
        }


    }

    @Override
    public Schema getSchema() {
        return schema;
    }


//    public String getThemeContent(String path) {
//        try {
//            URL url = new URL(getURL() + "/VAADIN/themes/" + getTheme() + "/" + path);
//            URLConnection connection = url.openConnection();
//
//            DataInputStream inStream = new DataInputStream(connection.getInputStream());
//            StringBuffer b = new StringBuffer();
//            String inputLine;
//
//            while ((inputLine = inStream.readLine()) != null) {
//                b.append(inputLine);
//            }
//            
//            inStream.close();
//
//            return b.toString();
//        }
//        catch (Exception e) {
//            logger.error("getThemeContent", e);
//            return "";         
//        }
//    }

}
