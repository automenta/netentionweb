package automenta.netention.ui.view;

import automenta.netention.Detail;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import java.util.Date;
import org.apache.commons.lang.time.DurationFormatUtils;

public class WideDetailIcon extends VerticalLayout {

    public static class PatternsPanel extends HorizontalLayout {

        public PatternsPanel(Detail d) {
            super();

            for (String p : d.getPatterns()) {
                addComponent(new Button(p));
            }

        }


    }
    
    public WideDetailIcon(Detail d) {
        this(d, null);
    }

    public WideDetailIcon(Detail d, ClickListener nameClicked) {
        super();

        addStyleName("WideDetailIcon");

        if (nameClicked==null) {
            Label nameLabel = new BigLabel(d.getName());
            addComponent(nameLabel);
        }
         else {

            Button nameButton = new Button(d.getName());
            nameButton.addStyleName(Button.STYLE_LINK);
            nameButton.addListener(nameClicked);
            addComponent(nameButton);
         }
    
        if (d.getPatterns()!=null)
            if (d.getPatterns().size() > 0)
                addComponent(new PatternsPanel(d));

        Date modified = d.getWhenModified();
        String s = DurationFormatUtils.formatDurationWords(new Date().getTime() - modified.getTime(), true, true);
        addComponent(new SmallLabel("Updated " + s + " ago"));
    }
}
