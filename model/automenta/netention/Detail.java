package automenta.netention;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import org.eclipse.persistence.annotations.PrimaryKey;
import org.vaadin.appfoundation.persistence.data.AbstractPojo;


@Entity
@Table(name = "appdetail", uniqueConstraints = { @UniqueConstraint(columnNames = {"id"})})

public class Detail extends AbstractPojo {
        
    protected String uuid;
    
    protected String name;

    private Mode mode;
    private List<String> patterns = new LinkedList();
    private List<PropertyValue> properties = new LinkedList();
    private String creator;
    @Temporal(TemporalType.TIMESTAMP) private Date whenCreated;
    @Temporal(TemporalType.TIMESTAMP) private Date whenModified;
    private String iconURL = null;

    public Detail() {
        this("");
    }

    public Detail(String name) {
        this(name, Mode.Unknown);
    }

    public Detail(String name, Mode mode, String... initialPatterns) {
        super();
        this.uuid = UUID.randomUUID().toString();
        this.name = name;
        this.mode = mode;
        this.creator = "Me";
        this.whenCreated = this.whenModified = new Date();

        for (String p : initialPatterns) {
            addPattern(p);
        }
    }

    public String getUuid() {
        return uuid;
    }

    /** universally unique ID */
    public String getID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String nextName) {
        this.name = nextName;
    }

    public String toString() {
        return getID() + " (" + getName() + ")";
    }

    public int hashCode() {
        return getID().hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof Node) {
            Node an = (Node) obj;
            return an.getID().equals(getID());
        }
        return false;
    }

    public void setMode(Mode newMode) {
        this.mode = newMode;
    }


    
    public Mode getMode() {
        return mode;
    }

    
    public List<String> getPatterns() {
        return patterns;
    }

    
    public List<PropertyValue> getProperties() {
        return properties;
    }
    

    public boolean addProperty(String propID, PropertyValue p) {
        p.setProperty(propID);
        return getProperties().add(p);
    }

    //TODO impl
//    public boolean removeProperty(String propID) {
//        return false;
//    }

    public boolean addPattern(String... patternID) {
        for (String p : patternID)
            getPatterns().add(p);
        return true;
    }

    public boolean removePattern(String patternID) {
        return getPatterns().remove(patternID);
    }
    
    public String getCreator() {
        return creator;
    }

    
    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String u) {
        this.iconURL = u;
    }

    public Date getWhenCreated() {
        return whenCreated;
    }

    public void setWhenCreated(Date whenCreated) {
        this.whenCreated = whenCreated;
    }

    public Date getWhenModified() {
        return whenModified;
    }

    public void setWhenModified(Date whenModified) {
        this.whenModified = whenModified;
    }




}
