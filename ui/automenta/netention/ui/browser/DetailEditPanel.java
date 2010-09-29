/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.netention.ui.browser;

import automenta.netention.Detail;
import automenta.netention.Pattern;
import automenta.netention.Property;
import automenta.netention.PropertyValue;
import automenta.netention.Schema;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.VerticalLayout;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;

/**
 *
 * @author seh
 */
abstract public class DetailEditPanel extends VerticalLayout {

    public final Detail detail;
    private MenuItem patternMenu;
    private final Schema schema;
    private final MenuBar patternBar;
    private final VerticalLayout propertiesPanel;

    public DetailEditPanel(Schema schema, Detail d) {
        super();
        this.detail = d;
        this.schema = schema;

        final RichTextArea text = new RichTextArea();
        text.setWidth("100%");
        addComponent(text);


        HorizontalLayout typePanel = new HorizontalLayout();

        patternBar = new MenuBar();
        patternBar.setWidth("100%");
        typePanel.addComponent(patternBar);

        refreshPatternBar();

        addComponent(typePanel);

        propertiesPanel = new VerticalLayout();
        propertiesPanel.setWidth("100%");
        addComponent(propertiesPanel);

        HorizontalLayout bottomPanel = new HorizontalLayout();

        Button cancelButton = new Button("Cancel");
        cancelButton.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                cancel();
            }
        });
        bottomPanel.addComponent(cancelButton);

        Button saveButton = new Button("Save");
        saveButton.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                save();
            }
        });
        bottomPanel.addComponent(saveButton);
        addComponent(bottomPanel);
        setComponentAlignment(bottomPanel, Alignment.TOP_CENTER);

        //setComponentAlignment(bottomPanel, Alignment.TOP_RIGHT);
    }

    abstract public void cancel();

    abstract public void save();

    protected void refreshPatternBar() {
        patternBar.removeItems();
        patternMenu = patternBar.addItem(">>>", null, null);
        
        final Collection<String> allPatterns = schema.getPatterns().keySet();
        final List<String> presentPatterns = detail.getPatterns();

        Collection<String> missingPatterns = new LinkedList(allPatterns);
        CollectionUtils.filter(missingPatterns, new Predicate<String>() {

            @Override
            public boolean evaluate(String t) {
                return !presentPatterns.contains(t);
            }
        });

        //1. add all non-present patterns to 'patternMenu'
        for (final String p : missingPatterns) {
            Pattern pattern = schema.getPatterns().get(p);
            String pName = pattern.getName();
            patternMenu.addItem(pName, new Command() {

                @Override
                public void menuSelected(MenuItem selectedItem) {
                    addNewPattern(p);
                }
            });
        }
        patternMenu.addSeparator();
        patternMenu.addItem("New Pattern...", new Command() {
            @Override
            public void menuSelected(MenuItem selectedItem) {
                createNewPattern();
            }
        });


        //2. add all present patterns to 'patternBar' as menus
        for (final String p : presentPatterns) {
            Pattern pattern = schema.getPatterns().get(p);
            MenuItem i = patternBar.addItem(pattern.getName() + " (" + getPropertiesPresent(pattern) + "/" + getPropertiesTotal(pattern) + ")", null, null);

            for (String property : pattern.keySet()) {
                if (supportsAnotherProperty(property)) {
                    final Property pr = schema.getProperty(property);
                    i.addItem(pr.getName(), new Command() {
                        @Override
                        public void menuSelected(MenuItem selectedItem) {
                            addProperty(pr.getID());
                        }
                    });
                }
            }

            i.addSeparator();

            i.addItem("Remove", new Command() {
                @Override
                public void menuSelected(MenuItem selectedItem) {
                    removePattern(p);
                }
            });
        }
    }

    public void addProperty(String id) {
        Property p = schema.getProperty(id);
        PropertyValue propertyValue = p.newDefaultValue(detail.getMode());
        propertyValue.setProperty(id);

        detail.getProperties().add(propertyValue);

        refreshProperties();
    }

    public class PropertyPanel extends HorizontalLayout {

        private PropertyPanel(Property p, PropertyValue pv) {
            super();

            Label propertyName = new Label(p.getName());
            addComponent(propertyName);
        }

    }

    public class MissingPropertyPanel extends HorizontalLayout {

        public MissingPropertyPanel(PropertyValue pv) {
            String lt = "Missing Property for Value: " + pv.toString();
            addComponent(new Label(lt));
        }


    }

    protected void refreshProperties() {
        propertiesPanel.removeAllComponents();

        for (PropertyValue pv : detail.getProperties()) {
            Property p = schema.getProperty(pv.getProperty());
            if (p!=null) {
                propertiesPanel.addComponent(new PropertyPanel(p, pv));
            }
             else {
                propertiesPanel.addComponent(new MissingPropertyPanel(pv));

             }
        }

    }

    public int getPropertiesPresent(Pattern p) {
        return 0;
    }
    public int getPropertiesTotal(Pattern p) {
        return p.size();
    }

    abstract public void createNewPattern();

    public void addNewPattern(String patternID) {
        if (!detail.getPatterns().contains(patternID)) {
            detail.getPatterns().add(patternID);
            refreshPatternBar();
        }        
    }

    public void removePattern(String patternID) {
        if (detail.getPatterns().contains(patternID)) {
            detail.getPatterns().remove(patternID);
            refreshPatternBar();
        }
    }

    public boolean supportsAnotherProperty(String propertyID) {
        return true;
    }

}
