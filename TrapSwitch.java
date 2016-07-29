import java.util.*;
import java.sql.*;
import java.io.*;



public class TrapSwitch {
    //Initialize all variables to null
    public String virtualMachine = null;
    public String sqlQuery = null;
    public String receivedInput = null;
    public String dbLogs = null;

    //receive input
    public void getInput() {
        //The last line of the log.txt file will be the input we're interested in.
        try (BufferedReader br = new BufferedReader(new FileReader("trap_commands/log.txt"))) {
            String last = "";
            String line;
            while ((line = br.readLine()) != null) {
                last = line;
            }
            this.receivedInput = last;
            //parse the received input
            parseInput();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    /*parse the input into which virtual machine
      sent the command, and what command that vm
      sent. */
    private void parseInput() {
        String[] parsedInput = this.receivedInput.split("\\,", 2);
        this.virtualMachine = parsedInput[0];
        this.sqlQuery = parsedInput[1];

        //Now that input is parsed, we can check validity
        checkValidity(this.virtualMachine);
    }

    private void checkValidity(String vm) {
        //vm_validity.txt has vmname, 0 or vmname, 1. If 0, vm is valid. If 1, vm invalid.
        try (BufferedReader br = new BufferedReader(new FileReader("vm_validity.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.substring(0, 3).equalsIgnoreCase(vm)) {
                    if (Integer.parseInt(line.substring(5, 6)) == 0) {
                        //real data
                        Class.forName("org.sqlite.JDBC");
                        Connection connection = null;
                        try {
                            connection = DriverManager.getConnection("jdbc:sqlite:real.db");
                            Statement statement = connection.createStatement();
                            statement.setQueryTimeout(30);
                            ResultSet rs = statement.executeQuery(this.sqlQuery);
                            ResultSetMetaData rsmd = rs.getMetaData();
                            System.out.printf("querying %s/n", this.sqlQuery);
                            int columnsNumber = rsmd.getColumnCount();
                            while (rs.next()) {
                                // read the result set
                                for (int i = 1; i <= columnsNumber; i++) {
                                    if (i > 1) System.out.print(",  ");
                                    String columnValue = rs.getString(i);
                                    System.out.print(columnValue + " " + rsmd.getColumnName(i));
                                }
                                System.out.println("");
                            }
                        }
                    } catch (Exception e) {
                        System.err.println(e.getClass().getName() + ": " + e.getMessage());
                        System.exit(0);
                    }

                } else {
                    /*because we need fake data that looks real,
                    we shuffle up the real data to populate the honeypot */
                    shuffleData();
                }
            }
        }
    } catch (Exception e) {
        System.out.println("Error: " + e);
    }
}

private void shuffleData() {
    /*Save each "Field" to it's own ArrayList */
    try (BufferedReader br = new BufferedReader(new FileReader("dblog.txt"))) {
        String line;
        int bLinecount = 0;
        int pLinecount = 0;
        ArrayList < String > eachLine = new ArrayList < String > ();
        ArrayList < String > bPolicyNumbers = new ArrayList < String > ();
        ArrayList < String > pPolicyNumbers = new ArrayList < String > ();
        ArrayList < String > businessNames = new ArrayList < String > ();
        ArrayList < String > personalNames = new ArrayList < String > ();
        ArrayList < String > taxIDNumbers = new ArrayList < String > ();
        ArrayList < String > socialSecurities = new ArrayList < String > ();
        ArrayList < String > bAccountNumbers = new ArrayList < String > ();
        ArrayList < String > pAccountNumbers = new ArrayList < String > ();
        while ((line = br.readLine()) != null) {
            eachLine.add(line);
        }

        for (String element: eachLine) {
            if (element.split(",")[0].equalsIgnoreCase("B")) {
                bLinecount++;
                bPolicyNumbers.add(element.split(",")[1]);
                businessNames.add(element.split(",")[2]);
                taxIDNumbers.add(element.split(",")[3]);
                bAccountNumbers.add(element.split(",")[4]);
            } else if (element.split(",")[0].equalsIgnoreCase("P")) {
                pLinecount++;
                pPolicyNumbers.add(element.split(",")[1]);
                personalNames.add(element.split(",")[2]);
                socialSecurities.add(element.split(",")[3]);
                pAccountNumbers.add(element.split(",")[4]);
            }

        }
        //Send over each ArrayList into the populateHoneypot method to randomly generate tuples.
        populateHoneypot(bPolicyNumbers, businessNames, taxIDNumbers, bAccountNumbers, pPolicyNumbers,
            personalNames, socialSecurities, pAccountNumbers, bLinecount, pLinecount);
    } catch (Exception e) {
        System.out.println("Error " + e);
    }
}

private void populateHoneypot(ArrayList < String > bPolicyNumbers,
    ArrayList < String > businessNames,
    ArrayList < String > taxIDNumbers,
    ArrayList < String > bAccountNumbers,
    ArrayList < String > pPolicyNumbers,
    ArrayList < String > personalNames,
    ArrayList < String > socialSecurities,
    ArrayList < String > pAccountNumbers,
    int bLinecount, int pLinecount) {
    //connect to honey
    //insert into these statements
    Class.forName("org.sqlite.JDBC");
    Connection connection = null;
    try {
        connection = DriverManager.getConnection("jdbc:sqlite:real.db");
        Statement statement = connection.createStatement();
        statement.setQueryTimeout(30);

        Random rand = new Random();
        int totalLineCount = pLinecount + bLinecount;
        for (int i = 0; i <= totalLineCount; i++) {
            int j = rand.nextInt(2) + 0;
            if (j == 0) {
                Random bRand = new Random();
                int b = bRand.nextInt(bLinecount) + 0;
                int c = bRand.nextInt(bLinecount) + 0;
                int d = bRand.nextInt(bLinecount) + 0;
                int e = bRand.nextInt(bLinecount) + 0;
                String honeyPopulateQuery = "insert into business values (B," + bPolicyNumbers.get(b) +
                    "," + businessNames.get(c) + "," + taxIDNumbers.get(d) + "," + bAccountNumber.get(e));
            //insert random business values into honeypot
            ResultSet rs = statement.executeQuery(honeyPopulateQuery);


        } else {
            Random pRand = new Random();
            int b = pRand.nextInt(pLinecount) + 0;
            int c = pRand.nextInt(pLinecount) + 0;
            int d = pRand.nextInt(pLinecount) + 0;
            int e = pRand.nextInt(pLinecount) + 0;
            System.out.println("P" + "," + pPolicyNumbers.get(b) +
                "," + personalNames.get(c) + "," + socialSecurities.get(d) +
                "," + pAccountNumbers.get(e));
            String honeyPopulateQuery = "insert into personal values (P," + pPolicyNumbers.get(b) +
                "," + personalNames.get(c) + "," + socialSecurities.get(d) + "," + pAccountNumber.get(e));
        //insert random personal values into honeypot        
        ResultSet rs = statement.executeQuery(honeyPopulateQuery);
    }

}
} catch (Exception e) {
    System.err.println(e.getClass().getName() + ": " + e.getMessage());
    System.exit(0);
}
}
}
