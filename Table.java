
/****************************************************************************************
 * @file  Table.java
 *
 * @author   John Miller
 */

package relation;

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static java.lang.System.arraycopy;
import static java.lang.System.out;

/****************************************************************************************
 * The Table class implements relational database tables (including attribute names, domains
 * and a list of tuples.  Five basic relational algebra operators are provided: project,
 * select, union, minus and join.  The add/insert data manipulation operator is also provided.
 * Missing are update and delete data manipulation operators.
 */
public class Table
       implements Serializable
{
    /** Table name.
     */
    private final String name;

    /** Array of attribute names.
     */
    private final String [] attribute;

    /** Array of attribute domains: a domain may be
     *  integer types: Long, Integer, Short, Byte
     *  real types: Double, Float
     *  string types: Character, String
     */
    private final Class [] domain;

    /** Primary key (the attributes forming the PK). 
     */
    private final String [] key;

    /** Collection of tuples (data storage) stored in a List (e.g., ArrayList).
     */
    private final List <Comparable []> tuples;

    /** Index into tuples (maps key to tuple).
     */
    private final Map <KeyType, Comparable []> index;

    /** Map/Dictionary for mapping attribute names to column numbers
     */
    final HashMap <String, Integer> col;

    /** Counter for naming temporary tables.
     */
    private static int count = 0;

    /************************************************************************************
     * Write a flaw/error message.
     *
     * @param method   the method where the error occurred
     * @param message  the error message to be written
     * @return  false
     */
    public static boolean flaw (String method, String message)
    {
         out.println ("FLAW in " + method + ": " + message);
         return false;
    } // flaw

    /************************************************************************************
     * Make a map (index) of the appropriate type by COMMENTING OUT all but one.
     */
    private static Map <KeyType, Comparable []> makeMap ()
    {
        return new HashMap <> ();
//      return new TreeMap <> ();
//      return new LinHashMap <> (KeyType.class, Comparable [].class);
    } // makeMap

    /************************************************************************************
     * Construct an empty table from the meta-data specifications.
     *
     * @param _name       the name of the relation
     * @param _attribute  the string containing attributes names
     * @param _domain     the string containing attribute domains (data types)
     * @param _key        the primary key
     */  
    public Table (String _name, String [] _attribute, Class [] _domain, String [] _key)
    {
        name      = _name;
        attribute = _attribute;
        domain    = _domain;
        key       = _key;
        tuples    = new ArrayList <> ();
        index     = makeMap ();
        col       = new HashMap <> ();
        for (var i = 0; i < attribute.length; i++) col.put (attribute[i], i);
    } // constructor

    /************************************************************************************
     * Construct an empty table from the raw string specifications.
     *
     * @param _name       the name of the relation
     * @param attributes  the string containing attributes names
     * @param domains     the string containing attribute domains (data types)
     * @param _key        the primary key
     */
    public Table (String _name, String attributes, String domains, String _key)
    {
        this (_name, attributes.split (" "), findClass (domains.split (" ")), _key.split (" "));
    } // constructor

    /************************************************************************************
     * Construct a table from the meta-data specifications and data in the _tuples list.
     *
     * @param _name       the name of the relation
     * @param _attribute  the string containing attributes names
     * @param _domain     the string containing attribute domains (data types)
     * @param _key        the primary key
     * @param _tuples     the list of tuples containing the data
     */
    public Table (String _name, String [] _attribute, Class [] _domain, String [] _key,
                  List <Comparable []> _tuples)
    {
        name      = _name;
        attribute = _attribute;
        domain    = _domain;
        key       = _key;
        tuples    = _tuples;
        index     = makeMap ();
        col       = new HashMap <> ();
        for (var i = 0; i < attribute.length; i++) col.put (attribute[i], i);
    } // constructor

    /************************************************************************************
     * Find the classes in the "java.lang" package with given names.
     *
     * @param className  the array of class name (e.g., {"Integer", "String"})
     * @return  an array of Java classes
     */
    private static Class [] findClass (String [] className)
    {
        out.println ("findClass: className = " + Arrays.toString (className));
        var classArray = new Class [className.length];

        for (var i = 0; i < className.length; i++) {
            try {
                classArray[i] = Class.forName ("java.lang." + className[i]);
            } catch (ClassNotFoundException ex) {
                flaw ("findClass", ex.toString ());
            }
        } // for

        return classArray;
    } // findClass

    /************************************************************************************
     * Add a tuple to the table.
     *
     * @param tup  the array of attribute values forming the tuple
     * @return  the table, enables method chaining
     */
    public Table add (Comparable [] tup)
    {
         flaw ("add", "type checking not implemented yet");
         tuples.add (tup);
         return this;
    } // add

    /************************************************************************************
     * Project the tuples onto a lower dimension by keeping only the given attributes.
     * Check whether the original key is included in the projection.
     * Use INDEX to efficiently eliminate DUPLICATE tuples.
     *
     * #usage deposit.proj ("bname cname")
     *
     * @param attrStr  the attributes (as a string) to project onto
     * @return  a table of projected tuples
     */
    public Table proj (String attrStr)
    {
        var attrs  = attrStr.split (" ");
        var newDom = getDom (attrs);
        var newKey = (Arrays.asList (attrs).containsAll (Arrays.asList (key))) ? key : attrs;

        List <Comparable []> rows = new ArrayList <> ();
        flaw ("proj", "not implemented yet");

        return new Table (name + count++, attrs, newDom, newKey, rows);
    } // proj

    /************************************************************************************
     * Get the domains corresponding the given attributes.
     *
     * @param attrs_  the attributes to project onto
     * @return  the corresponding domains
     */
    private Class [] getDom (String [] attrs)
    {
        out.println ("getDom: col = " + col);
        var obj = new Class [attrs.length];
        for (var j = 0; j < attrs.length; j++) {
            out.println ("getDom: attrs[j] = " + attrs[j]);
            obj[j] = domain [col.get (attrs[j])];
        } // for
        return obj;
    } // getDom

    /************************************************************************************
     * Select the tuples satisfying the given PREDICATE (Boolean function).
     *
     * #usage deposit.selt (t -> t[deposit.col.get ("bname")].equals ("Alps"))
     *
     * @param predicate  the check condition for tuples
     * @return  a table with tuples satisfying the predicate
     */
    public Table selt (Predicate <Comparable []> predicate)
    {
        return new Table (name + count++, attribute, domain, key,
                   tuples.stream ().filter (t -> predicate.test (t))
                                   .collect (Collectors.toList ()));
    } // selt

    /************************************************************************************
     * Select the tuples satisfying the given simple CONDITION on attributes/constants
     * compared using an <op> ==, !=, <, <=, >, >=.
     *
     * #usage deposit.selt ("bname == 'Alps'")
     *
     * @param condition  the check condition as a string for tuples
     * @return  a table with tuples satisfying the condition
     */
    public Table selt (String condition)
    {
        List <Comparable []> rows = new ArrayList <> ();

        var token = condition.split (" ");
        var colNo = col.get (token [0]);
        for (var t : tuples) {
            if (satisfies (t, colNo, token [1], token [2])) rows.add (t);
        } // for

        return new Table (name + count++, attribute, domain, key, rows);
    } // selt

    /************************************************************************************
     * Does tuple t satisfy the condition t[colNo] op value where op is ==, !=, <, <=, >, >=?
     *
     * #usage satisfies (t, 1, "<", "1980")
     *
     * @param colNo  the attribute's column number
     * @param op     the comparison operator
     * @param value  the value to compare with (must be converted, String -> domain type)
     * @return  whether the condition is satisfied
     */
   private boolean satisfies (Comparable [] t, int colNo, String op, String value)
{
    Comparable val;

    // Convert value string to appropriate domain type
    Class<?> domType = domain[colNo];
    try {
        if (domType == Integer.class) val = Integer.valueOf(value);
        else if (domType == Long.class) val = Long.valueOf(value);
        else if (domType == Short.class) val = Short.valueOf(value);
        else if (domType == Byte.class) val = Byte.valueOf(value);
        else if (domType == Double.class) val = Double.valueOf(value);
        else if (domType == Float.class) val = Float.valueOf(value);
        else if (domType == Character.class) val = value.charAt(0);
        else val = value; // Assume String or other Comparable
    } catch (Exception e) {
        flaw("satisfies", "Failed to convert value: " + value);
        return false;
    }

    Comparable attrVal = t[colNo];

    int cmp = attrVal.compareTo(val);

    switch (op) {
        case "==": return cmp == 0;
        case "!=": return cmp != 0;
        case "<":  return cmp < 0;
        case "<=": return cmp <= 0;
        case ">":  return cmp > 0;
        case ">=": return cmp >= 0;
        default:
            flaw("satisfies", "Unknown operator: " + op);
            return false;
    }
}

 /************************************************************************************
 * Select the tuples satisfying the given key predicate (key = value). Use an index
 * (Map) to retrieve the tuple with the given key value using the INDEXED SELECT algorithm.
 *
 * #usage deposit.selt(new KeyType("Alps"))
 *
 * @param keyVal  the given key value
 * @return  a table with the tuple satisfying the key predicate
 */
public Table selt(KeyType keyVal)
{
    out.println("Select on key: " + keyVal);
    List<Comparable[]> rows = new ArrayList<>();

    if (index != null) {
        out.println("[Select] Using index.");
        Comparable[] tuple = index.get(keyVal);
        if (tuple != null) rows.add(tuple);
    } else {
        out.println("[Select] No index. Full scan.");
        // Inline match: get positions of key attributes
        int[] keyPos = new int[key.length];
        for (int i = 0; i < key.length; i++) {
            for (int j = 0; j < attribute.length; j++) {
                if (key[i].equals(attribute[j])) {
                    keyPos[i] = j;
                    break;
                }
            }
        }

        for (Comparable[] tup : tuples) {
            // Inline extract: extract key columns from tuple
            Comparable[] extracted = new Comparable[keyPos.length];
            for (int i = 0; i < keyPos.length; i++) {
                extracted[i] = tup[keyPos[i]];
            }

            KeyType tupleKey = new KeyType(extracted);
            if (tupleKey.equals(keyVal)) {
                rows.add(tup);
            }
        }
    }

    return new Table(name + count++, attribute, domain, key, rows);
}



    /************************************************************************************
     * Union this table and table2.  Check that the two tables are compatible.
     * Use INDEX to efficiently eliminate DUPLICATE tuples.
     *
     * #usage deposit.union (loan)
     *
     * @param table2  the rhs table in the union operation
     * @return  a table representing the union
     */
    public Table union (Table table2)
    {
        if (! compatible (table2)) return null;

        List <Comparable []> rows = new ArrayList <> ();
        flaw ("union", "not implemented yet");

        return new Table (name + count++, attribute, domain, key, rows);
    } // union

   /************************************************************************************
     * Determine whether the two tables (this and table2) are compatible, i.e., have
     * the same number of attributes each with the same corresponding domain.
     *
     * @param table2  the rhs table
     * @return  whether the two tables are compatible
     */
    private boolean compatible (Table table2)
    {
        if (domain.length != table2.domain.length)
            return flaw ("compatible", "tables have different arity");
        for (var j = 0; j < domain.length; j++) {
           if (domain [j] != table2.domain [j])
                return flaw ("compatible", "tables disagree on domain " + j);
        } // for
        return true;
    } // compatible

   /************************************************************************************
 * Take the difference of this table and table2. Check that the two tables are compatible.
 * Use INDEX to speed up the minus operator.
 *
 * #usage deposit.minus(loan)
 *
 * @param table2  The rhs table in the minus operation
 * @return  a table representing the difference
 */
public Table minus(Table table2)
{
    out.println("Minus: " + this.name + " - " + table2.name);
    List<Comparable[]> result = new ArrayList<>();
    Set<KeyType> table2Keys = new HashSet<>();

    // Build key set from table2 using index if available
    if (table2.index != null) {
        out.println("[Minus] Using index on right table.");
        table2Keys.addAll(table2.index.keySet());
    } else {
        for (Comparable[] tup : table2.tuples) {
            Comparable[] keyValues = new Comparable[key.length];
            for (int i = 0; i < key.length; i++) {
                for (int j = 0; j < table2.attribute.length; j++) {
                    if (key[i].equals(table2.attribute[j])) {
                        keyValues[i] = tup[j];
                        break;
                    }
                }
            }
            table2Keys.add(new KeyType(keyValues));
        }
    }

    // Filter tuples from this that are NOT in table2Keys
    for (Comparable[] tup : this.tuples) {
        Comparable[] keyValues = new Comparable[key.length];
        for (int i = 0; i < key.length; i++) {
            for (int j = 0; j < attribute.length; j++) {
                if (key[i].equals(attribute[j])) {
                    keyValues[i] = tup[j];
                    break;
                }
            }
        }
        KeyType k = new KeyType(keyValues);
        if (!table2Keys.contains(k)) {
            result.add(tup);
        }
    }

    return new Table(name + count++, attribute, domain, key, result);
}

    /************************************************************************************
     * Join this table and table2 by performing an EQUI-JOIN.  Tuples from both tables
     * are compared requiring attributes1 to equal attributes2.  Disambiguate attribute
     * names by appending "2" to the end of any duplicate attribute name.  Implement using
     * a Nested Loop Join Algorithm.
     *
     * #usage deposit.join ("cname", "cname", customer)
     *
     * @param attributes1  the attributes of this table to be compared (Foreign Key)
     * @param attributes2  the attributes of table2 to be compared (Primary Key)
     * @param table2       the rhs table in the join operation
     * @return  a table with tuples satisfying the equality predicate
     */
    public Table join (String attributes1, String attributes2, Table table2)
    {   
        var t_attrs = attributes1.split (" ");
        var u_attrs = attributes2.split (" ");
        var rows    = new ArrayList <Comparable []> ();
        flaw ("join", "not implemented yet");
        
        return new Table (name + count++, concat (attribute, table2.attribute),
                                          concat (domain, table2.domain), key, rows);
    } // join

    /************************************************************************************
     * Join this table and table2 by performing a THETA-JOIN.  Tuples from both tables
     * are compared attribute1 <op> attribute2.  Disambiguate attribute names by appending "2"
     * to the end of any duplicate attribute name.  Implement using a Nested Loop Join algorithm.
     *
     * #usage deposit.join ("cname == cname", customer)
     *
     * @param condition  the theta join condition
     * @param table2     the rhs table in the join operation
     * @return  a table with tuples satisfying the condition
     */
    public Table join (String condition, Table table2)
    {   
        var rows = new ArrayList <Comparable []> ();
        flaw ("join", "not implemented yet");
        
        return new Table (name + count++, concat (attribute, table2.attribute),
                                          concat (domain, table2.domain), key, rows);
    } // join

    /************************************************************************************
     * Join this table and table2 by performing an NATURAL JOIN.  Tuples from both tables
     * are compared requiring common attributes to be equal.  The duplicate column is also
     * eliminated.
     *
     * #usage deposit.join (customer)
     *
     * @param table2  the rhs table in the join operation
     * @return  a table with tuples satisfying the equality predicate
     */
    public Table join (Table table2)
    {
        var rows = new ArrayList <Comparable []> ();
        flaw ("join", "not implemented yet");

        // FIX - eliminate duplicate columns
        return new Table (name + count++, concat (attribute, table2.attribute),
                                          concat (domain, table2.domain), key, rows);
    } // join

    /************************************************************************************
     * Join this table and table2 by performing an "equi-join" equating a FOREIGN KEY in
     * the first table (this) with the PRIMARY KEY in the second table (table2).
     * Use the INDEXED JOIN algorithm for efficient execution.
     *
     * #usage deposit.join (customer, "cname")
     *
     * @param table2  the rhs table in the join operation
     * @param fkey    the attributes of this table to be compared (Foreign Key)
     * @return  a table with tuples satisfying the equality predicate
     */
    public Table join (Table table2, String fkey)
    {
    out.println("Join: " + this.name + " ⋈ " + table2.name);

    List<Comparable[]> result = new ArrayList<>();

    // Find foreign key position in this table
    int thisFKeyPos = -1;
    for (int i = 0; i < this.attribute.length; i++) {
        if (this.attribute[i].equals(fkey)) {
            thisFKeyPos = i;
            break;
        }
    }
    if (thisFKeyPos == -1) {
        throw new IllegalArgumentException("Foreign key attribute " + fkey + " not found in this table");
    }

    // Find primary key position in table2 (assume single key)
    if (table2.key == null || table2.key.length != 1) {
        throw new IllegalArgumentException("This join method supports only single attribute primary keys in table2");
    }
    String pkey = table2.key[0];
    int table2PKeyPos = -1;
    for (int i = 0; i < table2.attribute.length; i++) {
        if (table2.attribute[i].equals(pkey)) {
            table2PKeyPos = i;
            break;
        }
    }
    if (table2PKeyPos == -1) {
        throw new IllegalArgumentException("Primary key attribute " + pkey + " not found in table2");
    }

    // Build combined attributes and domain arrays, excluding the duplicate key from table2
    List<String> newAttrList = new ArrayList<>();
    for (String attr : this.attribute) newAttrList.add(attr);
    for (int i = 0; i < table2.attribute.length; i++) {
        if (i != table2PKeyPos) newAttrList.add(table2.attribute[i]);
    }
    String[] newAttr = newAttrList.toArray(new String[0]);

    List<Class> newDomList = new ArrayList<>();
    for (Class d : this.domain) newDomList.add(d);
    for (int i = 0; i < table2.domain.length; i++) {
        if (i != table2PKeyPos) newDomList.add(table2.domain[i]);
    }
    Class[] newDom = newDomList.toArray(new Class[0]);

    // Perform join
    for (Comparable[] t1 : this.tuples) {
        Comparable foreignKeyValue = t1[thisFKeyPos];

        if (table2.index != null) {
            Comparable[] t2 = table2.index.get(new KeyType(new Comparable[]{foreignKeyValue}));
            if (t2 != null) {
                // Build combined tuple inline, excluding table2's key attribute at table2PKeyPos
                Comparable[] combined = new Comparable[t1.length + t2.length - 1];
                // Copy t1 attributes
                System.arraycopy(t1, 0, combined, 0, t1.length);

                // Copy t2 attributes except primary key
                int pos = t1.length;
                for (int i = 0; i < t2.length; i++) {
                    if (i != table2PKeyPos) {
                        combined[pos++] = t2[i];
                    }
                }
                result.add(combined);
            }
        } else {
            for (Comparable[] t2 : table2.tuples) {
                if (t2[table2PKeyPos].equals(foreignKeyValue)) {
                    Comparable[] combined = new Comparable[t1.length + t2.length - 1];
                    System.arraycopy(t1, 0, combined, 0, t1.length);

                    int pos = t1.length;
                    for (int i = 0; i < t2.length; i++) {
                        if (i != table2PKeyPos) {
                            combined[pos++] = t2[i];
                        }
                    }
                    result.add(combined);
                }
            }
        }
    }

    return new Table(name + count++, newAttr, newDom, this.key, result);
    } // join

    /************************************************************************************
     * Concatenate two arrays of type T to form a new wider array.
     *
     * @see http://stackoverflow.com/questions/80476/how-to-concatenate-two-arrays-in-java
     *
     * @param arr1  the first array
     * @param arr2  the second array
     * @return  a wider array containing all the values from arr1 and arr2
     */
    private static <T> T [] concat (T [] arr1, T [] arr2)
    {
        T [] result = Arrays.copyOf (arr1, arr1.length + arr2.length);
        arraycopy (arr2, 0, result, arr1.length, arr2.length);
        return result;
    } // concat

    /************************************************************************************
     * Print the given tuple.
     * @param tup  the tuple to print.
     */
    public void printTup (Comparable [] tup)
    {
        out.print ("| ");
        for (var attr : tup) out.printf ("%15s", attr);
        out.println (" |");
    } // printTup

   public void dropIndex() {
    out.println("Dropping index on " + name);
    if (index != null) {
        index.clear();
    }
}

    /************************************************************************************
     * Show/print this table.
     */
    public void show ()
    {
        out.println ("\n Table " + name);
        out.print ("|-" + "---------------".repeat (attribute.length));
        out.println ("-|");
        out.print ("| ");
        for (var a : attribute) out.printf ("%15s", a);
        out.println (" |");
        out.print ("|-" + "---------------".repeat (attribute.length));
        out.println ("-|");
        for (var tup : tuples) printTup (tup);
        out.print ("|-" + "---------------".repeat (attribute.length));
        out.println ("-|");
    } // show
    
} // Table

