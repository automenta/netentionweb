package automenta.netention;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.vaadin.appfoundation.persistence.data.AbstractPojo;

/** analogous to an RDF resource */
//@Entity
//@Table(name = "appnode", uniqueConstraints = { @UniqueConstraint(columnNames = {"uuid"})})
public class Node implements Serializable {

    @Id
    protected String uuid;

    protected String name;

    public Node() {
        this("", "");
    }

    public Node(String id) {
        this(id, id);
    }

    public Node(String id, String name) {
        super();

        this.uuid = id;
        this.name = name;
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

    public static class StringNode extends Node {

        public StringNode(String id) {
            super(id);
        }

        @Override
        public String toString() {
            return getName();
        }
    }
}
