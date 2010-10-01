package automenta.netention.ui;

import automenta.netention.Self;
import automenta.netention.impl.MessageDetail;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.vaadin.appfoundation.authentication.util.PasswordUtil;
import org.vaadin.appfoundation.authentication.util.UserUtil;
import org.vaadin.appfoundation.persistence.facade.FacadeFactory;

/**
 * called before application starts, and after it's stopped
 */
public class NContextListener implements ServletContextListener {

    public static final String PersistenceModel = "mysql";




    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        try {
            // Register facade
            FacadeFactory.registerFacade(PersistenceModel, true);
        } catch (InstantiationException ex) {
            Logger.getLogger(NApplication.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(NApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Set the salt for passwords
        Properties prop = new Properties();
        prop.setProperty("password.salt", "tld04\u2334\u2131#fawef@99923fsd");
        PasswordUtil.setProperties(prop);
        // Set the properties for the UserUtil
        prop.setProperty("password.length.min", "4");
        prop.setProperty("username.length.min", "4");
        UserUtil.setProperties(prop);
                
        //File file = new File(translationsPath);
        //InternationalizationServlet.loadTranslations(file);


        //Init Anonymous (public) Self
        Self a = new Self();
//        addDetail(new MessageDetail(a, "Subject", "Body"));
//        addDetail(new MessageDetail(a, "Subject", "Body"));
//        addDetail(new MessageDetail(a, "Subject", "Body"));
        NApplication.setAnonymousSelf(a);

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
