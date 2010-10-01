package automenta.netention.linker.hueristic;

import automenta.netention.Link;
import automenta.netention.Mode;
import automenta.netention.Detail;
import automenta.netention.graph.ValueEdge;
import java.util.HashSet;
import java.util.Set;

import automenta.netention.linker.Linker;
import com.syncleus.dann.graph.MutableBidirectedGraph;
import com.syncleus.dann.graph.MutableDirectedAdjacencyGraph;
import java.util.Collection;

abstract public class HueristicLinker implements Linker {

    private Set<Detail> Details = new HashSet();

    public HueristicLinker() {
        super();
    }

    public MutableBidirectedGraph<Detail,ValueEdge<Detail, Link>> run(Collection<Detail> details) {
        MutableBidirectedGraph<Detail,ValueEdge<Detail, Link>> graph = new MutableDirectedAdjacencyGraph<Detail, ValueEdge<Detail, Link>>();
        for (Detail d : details) {
            for (Detail n : details) {
                if (n == d) {
                    continue;
                }

                if (d.getMode() == Mode.Real) {
                    if (n.getMode() == Mode.Imaginary) {

                        Link link = compareSatisfying(n, d);
                        if (link != null) {
                            //if (link.getStrength() > getStrengthThreshold()) {
                                graph.add(d);
                                graph.add(n);
                                graph.add(new ValueEdge(link, d, n));
                            //}
                        }
                    }
                }
            }
        }

        return graph;

    }

//    private double getStrengthThreshold() {
//        return 0.0;
//    }

    abstract public Link compareSatisfying(Detail real, Detail imaginary);

    protected void addDetail(Detail n) {
        synchronized (Details) {
            Details.add(n);
        }
    }

    protected boolean containsDetail(Detail n) {
        boolean b;
        synchronized (Details) {
            b = Details.contains(n);
        }
        return b;
    }

}
