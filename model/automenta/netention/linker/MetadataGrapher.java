/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.netention.linker;

import automenta.netention.Detail;
import automenta.netention.Link;
import automenta.netention.Mode;
import automenta.netention.Node;
import automenta.netention.Pattern;
import automenta.netention.PropertyValue;
import automenta.netention.Schema;
import automenta.netention.graph.ValueEdge;
import automenta.netention.link.CreatedBy;
import automenta.netention.link.HasProperty;
import automenta.netention.link.PatternOf;
import automenta.netention.node.Creator;
import automenta.netention.node.PropertyNode;
import com.syncleus.dann.graph.MutableAdjacencyGraph;
import java.util.Iterator;

/**
 *
 * @author seh
 */
public class MetadataGrapher {

    public static void run(Schema schema, Iterator<Detail> i, MutableAdjacencyGraph<Object, ValueEdge<Object, Link>> target, boolean creators, boolean mode, boolean patterns, boolean properties) {
        while (i.hasNext()) {
            Detail d = i.next();

            target.add(d);

            if (creators) {
                Creator c = new Creator(d);
                target.add(c);
                target.add(new ValueEdge(new CreatedBy(), d, c));
            }

            if (mode) {
                final Object t = getNode(d.getMode());
                target.add(t);
                target.add(new ValueEdge(new PatternOf(), d, t));
            }

            if (patterns) {
                for (String p : d.getPatterns()) {
                    final Pattern pa = schema.getPatterns().get(p);
                    target.add(pa);
                    target.add(new ValueEdge(new PatternOf(), d, pa));
                }
            }

            if (properties) {
                for (PropertyValue p : d.getProperties()) {
                    final PropertyNode pn = new PropertyNode(p);
                    target.add(pn);
                    target.add(new ValueEdge(new HasProperty(), d, pn));
                }
            }
        }
    }

    public static Object getNode(Mode m) {
        if (m == Mode.Real) {
            return new Pattern("real");
        } else if (m == Mode.Imaginary) {
            return new Pattern("imaginary");
        } else /*if (m == Unknown)*/ {
            return new Pattern("thought");
        }
    }
}
