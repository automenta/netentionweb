/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.netention.ui.browser;

import automenta.netention.Detail;
import automenta.netention.Pattern;
import automenta.netention.Property;
import automenta.netention.Schema;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
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
public class DetailEditPanel extends VerticalLayout {

    public final Detail detail;
    private MenuItem patternMenu;
    private final Schema schema;
    private final MenuBar patternBar;

    public DetailEditPanel(Schema schema, Detail d) {
        super();
        this.detail = d;
        this.schema = schema;

        final RichTextArea text = new RichTextArea();
        addComponent(text);


        HorizontalLayout typePanel = new HorizontalLayout();

        patternBar = new MenuBar();
        typePanel.addComponent(patternBar);

        refreshPatternBar();


        addComponent(typePanel);

        HorizontalLayout bottomPanel = new HorizontalLayout();
        bottomPanel.addComponent(new Button("Cancel"));
        bottomPanel.addComponent(new Button("Save"));
        addComponent(bottomPanel);

        //setComponentAlignment(bottomPanel, Alignment.TOP_RIGHT);
    }

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

        //2. add all present patterns to 'patternBar' as menus
        for (final String p : presentPatterns) {
            Pattern pattern = schema.getPatterns().get(p);
            MenuItem i = patternBar.addItem(pattern.getName() + " (" + getPropertiesPresent(pattern) + "/" + getPropertiesTotal(pattern) + ")", null, null);

            for (String property : pattern.keySet()) {
                if (supportsAnotherProperty(property)) {
                    Property pr = schema.getProperty(property);
                    i.addItem(pr.getName(), null);
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

    public int getPropertiesPresent(Pattern p) {
        return 0;
    }
    public int getPropertiesTotal(Pattern p) {
        return p.size();
    }

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
