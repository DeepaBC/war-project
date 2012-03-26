package ejava.examples.restintro.dmv.dto;

import javax.xml.bind.annotation.XmlType;

/**
 * This enum defines the types of contacts that can exist.
 */
@XmlType(namespace="http://dmv.ejava.info", name="ContactTypeType")
public enum ContactType {
    RESIDENCE,
    WORK,
    OTHER
}
