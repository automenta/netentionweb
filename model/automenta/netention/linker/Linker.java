package automenta.netention.linker;

import automenta.netention.Link;
import automenta.netention.Detail;
import automenta.netention.graph.ValueEdge;
import com.syncleus.dann.graph.MutableBidirectedGraph;
import java.util.Collection;


/** a weaver is a process that semantically links stories in real-time */
public interface Linker {

    public MutableBidirectedGraph<Detail, ValueEdge<Detail, Link>> run(Collection<Detail> details);

}
