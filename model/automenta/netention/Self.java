/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.netention;

import automenta.netention.graph.ValueEdge;
import com.syncleus.dann.graph.MutableBidirectedGraph;
import com.syncleus.dann.graph.MutableDirectedAdjacencyGraph;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import org.vaadin.appfoundation.authentication.data.User;

/**
 * Self = Agent = Client = User
 * @author seh
 */
@Entity
@Table(name = "appself" , uniqueConstraints = { @UniqueConstraint(columnNames = { "id" }) })
public class Self extends User implements Serializable {

    //private String email;

    private int failedLoginAttempts = 0;

    private boolean accountLocked = false;

    private String reasonForLockedAccount;

    @Transient private int failedPasswordChanges = 0;    


    /* detail -> detail link graph */
    //transient private DirectedSparseMultigraph<Detail, Link> links = new DirectedSparseMultigraph<Detail, Link>();
    @Transient transient private MutableBidirectedGraph<Node, ValueEdge<Node, Link>> graph;

    public Self() {
        super();
    }

    
    public Self(String id, String name) {
        this();
        setUsername(id);
        setName(name);
    }
       
    public MutableBidirectedGraph<Node, ValueEdge<Node, Link>> getGraph() {
        return graph;
    }


    public Collection<String> getAvailablePatterns(Schema s, Detail d) {
        //TODO use dependency information to find all available patterns applicable for d
        //for now, just use all patterns minus existing patterns in 'd'

        Set<String> patterns = new HashSet<String>(s.getPatterns().keySet());
        for (String p : d.getPatterns()) {
            patterns.remove(p);
        }        

        return patterns;
    }

    public Map<Property, Double> getAvailableProperties(Schema s, Detail d, String... patternID) {
        Map<Property, Double> a = new HashMap();
        for (String pid : patternID) {
            Pattern pat = s.getPatterns().get(pid);
            if (pat != null) {
                for (String propid : pat.keySet()) {
                    Property prop = s.getProperties().get(propid);
                    if (!containsProperty(d, prop)) {
                        Double propStrength = pat.get(propid);
                        a.put(prop, propStrength);
                    }
                }

            }
        }
        return a;
    }

    public boolean containsProperty(Detail d, Property p) {
        for (PropertyValue pv : d.getProperties()) {
            if (p.getID().equals(pv.getProperty())) {
                return true;
            }
        }
        return false;
    }

/*    public void link(Linker l) {
        MutableBidirectedGraph<Node, ValueEdge<Node, Link>> g = l.run(IteratorUtils.toList(iterateDetails()));
        for (Node n : g.getNodes()) {
            graph.add(n);
        }
        for (ValueEdge<Node,Link> e : g.getEdges()) {
            graph.add(new ValueEdge(e.getValue(), e.getSourceNode(), e.getDestinationNode()));
        }
    }
*/
    public void clearGraph() {
        //links = new SimpleDynamicDirectedGraph<Node, Link>();
        
        //graph.clear();
        graph = new MutableDirectedAdjacencyGraph<Node, ValueEdge<Node, Link>>();
    }

//    public void save(String path) throws Exception {
//        FileOutputStream fout = new FileOutputStream(path);
//        ObjectOutputStream oos = new ObjectOutputStream(fout);
//        oos.writeObject(this);
//        oos.close();
//    }
//
//    public static MemorySelf load(String path) throws Exception {
//        FileInputStream fout = new FileInputStream(path);
//        ObjectInputStream oos = new ObjectInputStream(fout);
//        MemorySelf ms = (MemorySelf)oos.readObject();
//        oos.close();
//        return ms;
//    }

    public boolean acceptsAnotherProperty(Detail d, String propid) {
        int existing = 0;
        for (PropertyValue v : d.getProperties()) {
            if (v.getProperty().equals(propid)) {
                existing++;
            }
        }

        //TODO consider the property's cardinality properties
        if (existing == 1)
            return false;

        return true;
    }

//    public void updateLinks(Runnable whenFinished, Detail... details) {
//        clearGraph();
//        link(new DefaultHeuristicLinker());
//
//        if (whenFinished!=null)
//            whenFinished.run();
//    }
    
//    public void addPlugin(SelfPlugin p) {
//        if (plugins == null)
//            plugins = new LinkedList();
//        plugins.add(p);
//    }

   /**
     * Increments the amount of failed login attempts by one
     */
    public void incrementFailedLoginAttempts() {
        failedLoginAttempts++;
    }

    /**
     * Define if the account should be locked
     * 
     * @param accountLocked
     *            true if account should be locked, false if account should be
     *            activated
     */
    public void setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    /**
     * Is the current user account locked
     * 
     * @return true if account is locked, otherwise false
     */
    public boolean isAccountLocked() {
        return accountLocked;
    }

    /**
     * Resets the number of failed login attempts back to zero
     */
    public void clearFailedLoginAttempts() {
        failedLoginAttempts = 0;
    }

    /**
     * Get the number of failed login attempts
     * 
     * @return The number of failed login attempts
     */
    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    /**
     * Sets the reason why the account has been locked
     * 
     * @param reasonForLockedAccount
     *            Reason for account being locked
     */
    public void setReasonForLockedAccount(String reasonForLockedAccount) {
        this.reasonForLockedAccount = reasonForLockedAccount;
    }

    /**
     * Get the reason why the account has been locked
     * 
     * @return Reason for account being locked
     */
    public String getReasonForLockedAccount() {
        return reasonForLockedAccount;
    }

    /**
     * Increments the number of failed password change attempts. This value is
     * not persisted.
     */
    public void incrementFailedPasswordChangeAttempts() {
        failedPasswordChanges++;
    }

    /**
     * Clears the number of failed password change attempts
     */
    public void clearFailedPasswordChangeAttempts() {
        failedPasswordChanges = 0;
    }

    /**
     * Returns the number of failed password change attempts. This value is not
     * persisted.
     * 
     * @return Number of failed password change attempts for this object
     *         instance
     */
    public int getFailedPasswordChangeAttemps() {
        return failedPasswordChanges;
    }
    
}
//
///**
// * A session managed by a certain user/agent.
// * @author seh
// */
//public interface Self {
//
//    /** all available properties */
//    public Map<String, Property> getProperties();
//
//    public boolean addDetail(Detail d);
//    public Detail getDetail(String id);
//    public Iterator<Node> iterateDetails();
//    //public Map<String, Detail> getDetails();
//
//    /** all available patterns */
//    public Map<String, Pattern> getPatterns();
//
//    public MutableBidirectedGraph<Node, ValueEdge<Node, Link>> getGraph();
//
//
//    /** gets available patterns that may be added to a detail */
//    public Collection<String> getAvailablePatterns(Detail d);
//    public Pattern addPattern(Pattern p);
//    public boolean removePattern(Pattern pattern);
//
//    /** gets available properties that may be added to a detail */
//    public Map<Property, Double> getAvailableProperties(Detail d, String... patternID);
//    public boolean acceptsAnotherProperty(Detail d, String propid);
//
//    public boolean addProperty(Property p, String... patterns);
//    public Property getProperty(String propertyID);
//
//    /** signals that certain details have changed, causing the system to update the links for them */
//    public void updateLinks(Runnable whenFinished, Detail... details);
//
//    public void link(Linker l);
//
//    public void clearGraph();
//
//}
