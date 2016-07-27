import java.util.*;
import java.sql.*;
import java.io.*;

//ResultSet toString for Log


public class TrapSwitch {
	public String virtualMachine = null;
    public String sqlQuery = null;
    public String receivedInput = null;
    public String dbLogs = null;

    //receive input
    public void getInput(){
        try (BufferedReader br = new BufferedReader(new FileReader("trap_commands/log.txt"))) {
            String last = "";
            String line;
            while ((line = br.readLine()) != null) {
                last = line;
             }
             this.receivedInput = last;
             //parse the received input
             parseInput();
        } catch(Exception e){
            System.out.println("Error: " + e);
        }
    }

    /*parse the input into which virtual machine
      sent the command, and what command that vm
      sent. */
    private void parseInput(){
        String[] parsedInput = this.receivedInput.split("\\,", 2);
        this.virtualMachine = parsedInput[0];
        this.sqlQuery = parsedInput[1];

        //Now that input is parsed, we can check validity
        checkValidity(this.virtualMachine);
    }

    private void checkValidity(String vm){
    	try (BufferedReader br = new BufferedReader(new FileReader("vm_validity.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(line.substring(0, 3).equalsIgnoreCase(vm)){
                	if(Integer.parseInt(line.substring(5, 6)) == 0){
                		//real data
                		//Semih connect this
                		System.out.println("Real Data");
                	} else {
                		//fake data
                		//Jaken connect this
                		System.out.println("Fake Data");
                        shuffleData();
                	}
                }
             }
        } catch(Exception e){
            System.out.println("Error: " + e);
        }
    }

    private void shuffleData(){
        try (BufferedReader br = new BufferedReader(new FileReader("dblog.txt"))){
            String line;
            int bLinecount = 0;
            int pLinecount = 0;
            ArrayList<String> eachLine = new ArrayList<String>();
            ArrayList<String> bPolicyNumbers = new ArrayList<String>();
            ArrayList<String> pPolicyNumbers = new ArrayList<String>();
            ArrayList<String> businessNames = new ArrayList<String>();
            ArrayList<String> personalNames = new ArrayList<String>();
            ArrayList<String> taxIDNumbers = new ArrayList<String>();
            ArrayList<String> socialSecurities = new ArrayList<String>();
            ArrayList<String> bAccountNumbers = new ArrayList<String>();
            ArrayList<String> pAccountNumbers = new ArrayList<String>();
            while((line = br.readLine()) != null){
                eachLine.add(line);
            }

            for(String element : eachLine){
                if(element.split(",")[0].equalsIgnoreCase("B")){
                    bLinecount++;
                    bPolicyNumbers.add(element.split(",")[1]);
                    businessNames.add(element.split(",")[2]);
                    taxIDNumbers.add(element.split(",")[3]);
                    bAccountNumbers.add(element.split(",")[4]);
                }
                else if(element.split(",")[0].equalsIgnoreCase("P")){
                    pLinecount++;
                    pPolicyNumbers.add(element.split(",")[1]);
                    personalNames.add(element.split(",")[2]);
                    socialSecurities.add(element.split(",")[3]);
                    pAccountNumbers.add(element.split(",")[4]);
                }
                
            }

            populateHoneypot(bPolicyNumbers, businessNames, taxIDNumbers, bAccountNumbers, pPolicyNumbers,
                          personalNames, socialSecurities, pAccountNumbers, bLinecount, pLinecount);
        } catch(Exception e){
            System.out.println("Error " + e);
        }
    }

    private void populateHoneypot(ArrayList<String> bPolicyNumbers,
                                  ArrayList<String> businessNames, 
                                  ArrayList<String> taxIDNumbers,
                                  ArrayList<String> bAccountNumbers,
                                  ArrayList<String> pPolicyNumbers,
                                  ArrayList<String> personalNames,
                                  ArrayList<String> socialSecurities,
                                  ArrayList<String> pAccountNumbers,
                                  int bLinecount, int pLinecount){
        //connect to honey
        //insert into these statements
        Random rand = new Random();
        int totalLineCount = pLinecount + bLinecount;
        for(int i = 0; i <= totalLineCount; i++){ 
            int j = rand.nextInt(2) + 0;
            if(j == 0){
                Random bRand = new Random();
                int b = bRand.nextInt(bLinecount) + 0;
                int c = bRand.nextInt(bLinecount) + 0;
                int d = bRand.nextInt(bLinecount) + 0;
                int e = bRand.nextInt(bLinecount) + 0;
                System.out.println("B"+","+bPolicyNumbers.get(b)+
                                ","+businessNames.get(c)+","+taxIDNumbers.get(d)+
                                ","+bAccountNumbers.get(e));
                
            } 
            else{
                Random pRand = new Random();
                int b = pRand.nextInt(pLinecount) + 0;
                int c = pRand.nextInt(pLinecount) + 0;
                int d = pRand.nextInt(pLinecount) + 0;
                int e = pRand.nextInt(pLinecount) + 0;
                System.out.println("P"+","+pPolicyNumbers.get(b)+
                                ","+personalNames.get(c)+","+socialSecurities.get(d)+
                                ","+pAccountNumbers.get(e));
            }
        }
    }
}
