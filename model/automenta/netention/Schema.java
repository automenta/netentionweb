/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.netention;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.vaadin.appfoundation.persistence.data.AbstractPojo;

/**
 *
 * @author seh
 */
@Entity
@Table(name = "appschema")
public class Schema extends AbstractPojo {

    /** propertyID -> properties */
    private Map<String, Property> properties = new HashMap();

    /** patternID -> patterns */
    private Map<String, Pattern> patterns = new HashMap();

    public void setProperties(Map<String, Property> properties) {
        this.properties = properties;
    }

    public void setPatterns(Map<String, Pattern> patterns) {
        this.patterns = patterns;
    }
    
    public Map<String, Property> getProperties() {
        return properties;
    }

    public Property getProperty(String propertyID) {
        return properties.get(propertyID);
    }

    public Map<String, Pattern> getPatterns() {
        return patterns;
    }

    public Pattern addPattern(Pattern p) {
        //TODO do not allow adding existing pattern
        patterns.put(p.getID(), p);
        return p;
    }

    public boolean removePattern(Pattern pattern) {
        patterns.remove(pattern.getID());
        return true;
    }
    
    public boolean addProperty(Property p, String... patterns) {
        //TODO do not allow adding existing pattern
        properties.put(p.getID(), p);
        for (String patid : patterns) {
            Pattern pat = getPatterns().get(patid);
            if (pat!=null) {
                pat.put(p.getID(), 1.0);
            }
        }
        return true;
    }

    public void addProperties(Pattern p, Property... properties) {
        addProperties(p.getID(), properties);        
    }

    public void addProperties(String pattern, Property... properties) {
        for (Property p : properties) {
            addProperty(p, pattern);
        }
    }

    
}
