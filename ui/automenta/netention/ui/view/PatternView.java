/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.netention.ui.view;

import automenta.netention.Pattern;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.IntegerValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.util.Arrays;

/**
 *
 * @author seh
 */
public class PatternView extends VerticalLayout {

    public final Pattern pattern;
    
    private static final String COMMON_FIELD_WIDTH = "12em";
 
    private class PatternFieldFactory extends DefaultFieldFactory {

        public PatternFieldFactory() {
            super();
        }

        @Override
        public Field createField(Item item, Object propertyId, Component uiContext) {
            Field f = super.createField(item, propertyId, uiContext);
            if ("name".equals(propertyId)) {
                TextField tf = (TextField) f;
                tf.setRequired(true);
                tf.setRequiredError("Enter a pattern name");
                tf.setWidth(COMMON_FIELD_WIDTH);
                tf.addValidator(new StringLengthValidator("Pattern name must be 3-25 characters", 3, 25, false));
            } else if ("description".equals(propertyId)) {
                TextField tf = (TextField) f;
                tf.setRequired(true);
                tf.setRequiredError("Enter a description");
                tf.setWidth(COMMON_FIELD_WIDTH);
                tf.addValidator(new StringLengthValidator("Description must be 3-50 characters", 3, 50, false));
            }
//            } else if ("password".equals(propertyId)) {
//                TextField tf = (TextField) f;
//                tf.setSecret(true);
//                tf.setRequired(true);
//                tf.setRequiredError("Please enter a password");
//                tf.setWidth("10em");
//                tf.addValidator(new StringLengthValidator(
//                        "Password must be 6-20 characters", 6, 20, false));
//            } else if ("shoesize".equals(propertyId)) {
//                TextField tf = (TextField) f;
//                tf.addValidator(new IntegerValidator(
//                        "Shoe size must be an Integer"));
//                tf.setWidth("2em");
//            } else if ("uuid".equals(propertyId)) {
//                TextField tf = (TextField) f;
//                tf.setWidth("20em");
//            }

            return f;
        }
    }

    public PatternView(Pattern p) {
        super();
        this.pattern = p;

        Label nameLabel = new Label(p.getName());
        nameLabel.setIcon(new ThemeResource(p.getIconURL()));
        addComponent(nameLabel);

        Label descLabel = new Label(p.getDescription());
        addComponent(descLabel);


        BeanItem patternItem = new BeanItem(pattern); // item from POJO

        // Create the Form
        final Form patternForm = new Form();
        patternForm.setCaption("Edit Pattern");
        patternForm.setWriteThrough(false); // we want explicit 'apply'
        patternForm.setInvalidCommitted(false); // no invalid values in datamodel

        // FieldFactory for customizing the fields and adding validators
        patternForm.setFormFieldFactory(new PatternFieldFactory());
        patternForm.setItemDataSource(patternItem); // bind to POJO via BeanItem

        // Determines which properties are shown, and in which order:
        patternForm.setVisibleItemProperties(Arrays.asList(new String[]{
                    "name", "description" /*, "countryCode", "password",
                    "birthdate", "shoesize", "uuid"*/}));

        // Add form to layout
        addComponent(patternForm);

        // The cancel / apply buttons
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);
        Button discardChanges = new Button("Discard changes",
                new Button.ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        patternForm.discard();
                    }
                });
        discardChanges.setStyleName(Button.STYLE_LINK);
        buttons.addComponent(discardChanges);
        buttons.setComponentAlignment(discardChanges, "middle");

        Button apply = new Button("Save", new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                try {
                    patternForm.commit();
                } catch (Exception e) {
                    // Ignored, we'll let the Form handle the errors
                }
            }
        });
        buttons.addComponent(apply);
        patternForm.getFooter().addComponent(buttons);
        patternForm.getFooter().setMargin(false, false, true, true);

//        // button for showing the internal state of the POJO
//        Button showPojoState = new Button("Show POJO internal state",
//                new Button.ClickListener() {
//
//                    public void buttonClick(ClickEvent event) {
//                        showPojoState();
//                    }
//                });
//        addComponent(showPojoState);


    }

    protected void showPojoState() {
        Window.Notification n = new Window.Notification("POJO state",
                Window.Notification.TYPE_TRAY_NOTIFICATION);
        n.setPosition(Window.Notification.POSITION_CENTERED);
        n.setDescription("Name: " + pattern.getName()
                + "<br/>Description: " + pattern.getDescription());
        getWindow().showNotification(n);
    }
}
