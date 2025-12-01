
/*****************************************************************************************
 * @file  Bank.java
 *
 * @author   John Miller
 */

package relation;

import static java.lang.System.out;

/*****************************************************************************************
 * The Bank class makes a Bank Database.  It serves as a template for making other databases.
 */
class Bank
{
    /*************************************************************************************
     * Main method for creating, populating and querying the Bank Database.
     * @param args  the command-line arguments
     */
    public static void main (String [] args)
    {
        var branch   = new Table ("branch", "bname assets bcity",
                                  "String Double String", "bname");
        var customer = new Table ("customer", "cname street ccity",
                                  "String String String", "cname");
        var deposit  = new Table ("deposit", "bname accno cname balance",
                                  "String Integer String Double", "accno");
        var loan     = new Table ("loan", "bname loanno cname amount",
                                  "String Integer String Double", "loanno");

        branch.add (new Comparable [] {"Main",     15000000.0, "Athens"})
              .add (new Comparable [] {"Lake",     20000000.0, "Gainesville"})
              .add (new Comparable [] {"Downtown", 10000000.0, "Winder"})
              .add (new Comparable [] {"Alps",     11000000.0, "Athens"});

        customer.add (new Comparable [] {"Peter", "Maple St", "Athens"})
                .add (new Comparable [] {"Paul",  "Oak St",   "Athens"})
                .add (new Comparable [] {"Mary",  "Elm St",   "Winder"})
                .add (new Comparable [] {"Joe",   "Pine St",  "Athens"});

        deposit.add (new Comparable [] {"Downtown", 901, "Peter", 1000.0})
               .add (new Comparable [] {"Main",     902, "Paul",  2000.0})
               .add (new Comparable [] {"Alps",     903, "Paul",  3000.0})
               .add (new Comparable [] {"Lake",     904, "Paul",  1000.0})
               .add (new Comparable [] {"Main",     905, "Mary",  1000.0})
               .add (new Comparable [] {"Alps",     906, "Mary",  2000.0})
               .add (new Comparable [] {"Lake",     907, "Joe",   1500.0});

        loan.add (new Comparable [] {"Lake",     1001, "Peter", 1000.0})
            .add (new Comparable [] {"Alps",     1002, "Peter", 2000.0})
            .add (new Comparable [] {"Main",     1003, "Paul",  1000.0})
            .add (new Comparable [] {"Alps",     1004, "Paul",  2000.0})
            .add (new Comparable [] {"Main",     1005, "Mary",  1500.0})
            .add (new Comparable [] {"Downtown", 1006, "Mary",  2000.0});

        // Perform queries

        out.println ("\n RA1> deposit.proj (\"bname cname\")");
        deposit.proj ("bname cname").show ();

        out.println ("\n RA2> deposit.selt (t -> t[deposit.col.get (\"bname\")].equals (\"Alps\"))");
        deposit.selt (t -> t[deposit.col.get ("bname")].equals ("Alps")).show ();

        out.println ("\n RA3> deposit.selt (\"bname == 'Alps'\")");
        deposit.selt ("bname == 'Alps'").show ();

        out.println ("\n RA4> deposit.selt (new KeyType (\"Alps\"))");
        deposit.selt (new KeyType ("Alps")).show ();

        out.println ("\n RA5> deposit.union (loan)");
        deposit.union (loan).show ();

        out.println ("\n RA6> deposit.minus (loan)");
        deposit.minus (loan).show ();

        out.println ("\n RA7> deposit.join (\"cname\", \"cname\", customer)");
        deposit.join ("cname", "cname", customer).show ();

        out.println ("\n RA8> deposit.join (\"cname == cname\", customer)");
        deposit.join ("cname == cname", customer).show ();

        out.println ("\n RA9> deposit.join (customer)");
        deposit.join (customer).show ();
        
        out.println ("\n RA10> deposit.join (customer, \"cname\")");
        deposit.join (customer, "cname").show ();

    } // main

} // Bank

