/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.netention.ui.browser;

import automenta.netention.ui.NApplication;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import org.vaadin.appfoundation.authentication.data.User;

/**
 *
 * @author seh
 */
public class Header extends HorizontalLayout {
    private final User user;
    private final NApplication app;
    private final AppWindow browser;

    public Header(final NApplication app, final AppWindow browser, User user) {
        super();
    
        setWidth("100%");
        
        this.app = app;
        this.user = user;
        this.browser = browser;
        
        String userString = (user == null) ? "Unidentified Agent" : user.getName();

        final Label nameLabel = new Label(userString);
        nameLabel.addStyleName("headerRealName");
        addComponent(nameLabel);
        setExpandRatio(nameLabel, 0.9f);
        setComponentAlignment(nameLabel, Alignment.TOP_LEFT);
        
        VerticalLayout userPanel = new VerticalLayout();
        if (user == null) {
            Button loginButton = new Button("Login...");
            loginButton.addListener(new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    app.login(browser);
                }                
            });
            loginButton.setStyleName(Button.STYLE_LINK);
            userPanel.addComponent(loginButton);
            
            Button registerButton = new Button("Register");
            registerButton.addListener(new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    app.register(browser);
                }                
            });
            registerButton.setStyleName(Button.STYLE_LINK);            
            userPanel.addComponent(registerButton);
        }
        else {
            Button logoutButton = new Button("Logout");
            logoutButton.addListener(new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    app.logout(browser);
                }                
            });
            logoutButton.setStyleName(Button.STYLE_LINK);
            userPanel.addComponent(logoutButton);
        }
        
        addComponent(userPanel);    
        setExpandRatio(userPanel, 0.05f);
        setComponentAlignment(userPanel, Alignment.TOP_RIGHT);
    
    }
    
}
