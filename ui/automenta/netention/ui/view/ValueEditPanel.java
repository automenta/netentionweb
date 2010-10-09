/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.netention.ui.view;

import automenta.netention.Property;
import automenta.netention.PropertyValue;
import automenta.netention.value.string.StringIs;
import automenta.netention.value.string.StringProp;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;

/**
 *
 * @author seh
 */
abstract public class ValueEditPanel extends HorizontalLayout {

    final public HorizontalLayout modePanel = new HorizontalLayout();
    final public HorizontalLayout valuePanel = new HorizontalLayout();
    private final Property property;
    private ValueEditor editor;

    public static interface ValueEditor {
        public void setValue(PropertyValue pv);
        public PropertyValue getValue();
    }

    public static class StringValueEditor implements ValueEditor {
        private PropertyValue value;
        private TextField isEdit = new TextField();
        private final ValueEditPanel panel;
        private final NativeSelect modeSelect;

        public StringValueEditor(ValueEditPanel panel, Property p, PropertyValue value) {
            super();

            modeSelect = new NativeSelect("");
            modeSelect.removeAllItems();
            modeSelect.setNullSelectionAllowed(false);
            modeSelect.addItem("is");
            modeSelect.addItem("equals");
            modeSelect.addItem("doesn't equal");
            modeSelect.setValue("is");

            this.panel = panel;

            panel.modePanel.removeAllComponents();
            panel.modePanel.addComponent(modeSelect);

            setValue(value);
        }

        @Override
        public void setValue(PropertyValue pv) {
            if (value instanceof StringIs)
                set((StringIs)value);
        }

        @Override
        public PropertyValue getValue() {
            return value;
        }

        protected void set(StringIs s) {
            this.value = s;

            panel.valuePanel.removeAllComponents();
            isEdit.setValue(s.getValue());
            panel.valuePanel.addComponent(isEdit);
        }

    }

    public ValueEditPanel(Property p, PropertyValue initialValue) {
        super();

        this.property = p;
        
        Label propertyName = new Label(p.getName());
        addComponent(propertyName);

        addComponent(modePanel);
        addComponent(valuePanel);

        Button removeButton = new Button("X");
        removeButton.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                removeThis();
            }
        });
        addComponent(removeButton);

        setValue(initialValue);
    }

    protected void setValue(PropertyValue value) {
        if (property instanceof StringProp) {
            this.editor = new StringValueEditor(this, property, value);
        }

    }

    abstract public void removeThis();
}
