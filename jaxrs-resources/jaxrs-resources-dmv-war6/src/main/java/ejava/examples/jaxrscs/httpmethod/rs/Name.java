package ejava.examples.jaxrscs.httpmethod.rs;

/**
 * This class is used as an example of mapping custom types to resource 
 * method parameters
 */
public class Name {
    private String name;
    public static Name valueOf(String value) {
        Name name = new Name();
        name.name = value;
        return name;
    }
    public String toString() {
        return String.format("Name[%s]", name);
    }
}
