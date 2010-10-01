/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.netention.impl;

import automenta.netention.Detail;
import automenta.netention.Mode;
import automenta.netention.value.string.StringIs;
import java.util.Date;

/**
 *
 * @author seh
 */
public class MessageDetail extends Detail {
    private final String content;

    public MessageDetail(String subject, String content) {
        this(subject, content, new Date());
    }

    public MessageDetail(String subject, String content, Date date) {
        super(subject, Mode.Real, "Message");
        this.content = content;

        addProperty("content", new StringIs(content));
        addProperty("whenSent", new StringIs(date.toString()));
    }
    
    public String getSubject() { return getName(); }


}
