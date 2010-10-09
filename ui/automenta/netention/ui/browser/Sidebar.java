package automenta.netention.ui.browser;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.VerticalLayout;

public class Sidebar extends VerticalLayout implements ValueChangeListener {
    private final BeanItemContainer<SidebarView> list;
    private final NativeSelect qSelect;


    public static abstract class SidebarView {

        private final String name;

        public SidebarView(String name) {
            this.name = name;
        }

        String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
        
        public abstract void render(AbstractLayout target);
    }
    
    
    VerticalLayout view = new VerticalLayout();

    
    public Sidebar(final AppWindow browser) {
        super();
        
        HorizontalLayout topPanel = new HorizontalLayout();
        topPanel.setHeight("50px");
        topPanel.addStyleName("sidebarTopPanel");
        {
            
            Button newButton = new Button("+");
            newButton.addListener(new ClickListener() {

                @Override
                public void buttonClick(ClickEvent event) {
                    browser.newDetail();
                }

            });
            //newButton.setIcon(new ThemeResource("tango32/actions/document-new.png"));
            newButton.setDescription("New Object...");
            topPanel.addComponent(newButton);
            
            list = new BeanItemContainer<SidebarView>(SidebarView.class);

            qSelect = new NativeSelect("", list);
            qSelect.setNullSelectionAllowed(false);
            qSelect.addStyleName("v-button");
            qSelect.setImmediate(true);
            topPanel.addComponent(qSelect);
        }
        
        addComponent(topPanel);
        setExpandRatio(topPanel, 0f);
       
        addComponent(view);
        setExpandRatio(view, 1.0f);

        qSelect.addListener(this);

    }

    
    @Override
    public void valueChange(ValueChangeEvent event) {
        
        view.removeAllComponents();
        
        SidebarView v = (SidebarView)qSelect.getValue();
        if (v != null) {
            v.render(view);
        }
    }

    public synchronized void addConceptView(SidebarView view) {
        list.addItem(view);

        if (list.size() == 1) {
            //first by default
            qSelect.setValue(view);
        }

    }
    
}
