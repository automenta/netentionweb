package automenta.netention.ui.view;

import automenta.netention.Detail;
import automenta.netention.Node;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import java.util.Date;
import org.apache.commons.lang.time.DurationFormatUtils;

public class WideDetailIcon extends VerticalLayout {

    public WideDetailIcon(Detail d) {
        super();
        Label nameLabel = new BigLabel(d.toString());
        //nameLabel.setCaption(n.getID());
        addComponent(nameLabel);
        
        addComponent(new Label(d.getPatterns().toString()));

        Date modified = d.getWhenModified();
        String s = DurationFormatUtils.formatDurationWords(new Date().getTime() - modified.getTime(), true, true);
        addComponent(new SmallLabel("Changed " + s + " ago"));
    }
}
