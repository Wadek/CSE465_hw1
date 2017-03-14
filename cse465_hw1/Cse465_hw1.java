package cse465_hw1;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;
import javax.xml.bind.DatatypeConverter;

public class Cse465_hw1 {
        Object[] passwordList;
        int passwordLength = 0;
	
        static Hashtable< String, String > db = new Hashtable< String,String >();
	static Hashtable<String,String[]> skeys = new Hashtable<String, String[]>();
	
	//Initialize SHA-256 instance
	static MessageDigest md;

	public static void main(String[] args)throws Exception {
            Cse465_hw1 temp = new Cse465_hw1();
		while(true){
			//Set encryption method to SHA-256
			md = MessageDigest.getInstance("SHA-256");
			mainMenu();

			System.out.println();
			System.out.println("Enter ID name: ");
			
			//Get ID info from user
			Scanner scanner = new Scanner(System.in);
			Scanner sc = new Scanner(System.in);
			String id = scanner.nextLine();
			int index =0;
			//if user exists in the db
			if(db.containsKey(id)){ 
				//Calculate password chain position, i
				for(int i = 0; i < skeys.get(id).length;i++) {
					if(skeys.get(id)[i].equals(db.get(id)))
						index = i+1;
				}
				System.out.println("Enter password (i="+ index  +"): ");
				boolean isPassCorrect=false;
				//challenge response
				int challenge = 1;
				while(!isPassCorrect){
					if(challenge>3){
						System.out.println("3 failed attmepts. Logging out.");
						mainMenu();
						break;
					}
					sc = new Scanner(System.in);
					String passwordAttempt = sc.nextLine();
					
					if(authenticate(id,passwordAttempt)){
						isPassCorrect =true;
						for(int i = index; i < skeys.get(id).length;i++) {
                                                    System.out.println("(i = "+ i +") "+skeys.get(id)[i]);
                                                    
                                                }
                                                System.out.println("\n"+id + " has been authenticated");
						System.out.println("\nWelcome back to the simulated db\n");
						System.out.println("logging out...");
						if(db.get(id).equals(getSeed(id))){
							System.out.println("\nNote: You have no more authentifications remaining. Please Re-register.");
							db.remove(id);
							skeys.remove(id);
							mainMenu();
							continue;
						}
					} 
					else{
						System.out.println("\nIncorrect password, Try again."); 
						System.out.println("Attempt #: " + challenge + " out of 3");
						challenge++;
						System.out.println("Enter password (i="+ index  +"): \n");
					}
					
				}//end while
			}else{
				//register(id);
				System.out.println("ID does not exist. Please Register.");
				mainMenu();
			}
		}	
	}//end main 
	
	public void register(String id) throws Exception{
            boolean validCheck = true;
            int n=0;
            Scanner sc = new Scanner(System.in);
            System.out.println("N value: ");
            String n_str = sc.nextLine();
            if(n_str.equals("")){
                    System.out.println("\nInvalid response. Try Agian.\n");
            }
            else if(isInt(n_str)){
                    n = Integer.parseInt(n_str);
                    if(n <= 1){
                        System.out.println("\nInvalid response. Try Agian.\n");
                    } else {}
            }
            else {
                    System.out.println("\nInvalid response. Try Agian.\n");
            }
            
            System.out.println(validCheck);
            while(validCheck){		
                System.out.println("Enter secret seed: ");
                sc = new Scanner(System.in);
                String seed = sc.nextLine();
                ArrayList<String> pList = new ArrayList<String>();
                for(int i = 0; i < n; i++){
                        pList.add(seed);
                        seed = secret(seed);	
                }

                //reverse password chain
                passwordList =  pList.toArray();
                passwordList = reverse(passwordList);
                String pChain = (String) passwordList[0];
                //only save the the nth encrypted password to db
                db.put(id,pChain);
                //Used for debug, not a good idea to keep password chain on the system
                skeys.put(id, (String[]) passwordList);
                printList(passwordList);
                passwordLength = passwordList.length;
                validCheck = false;
                mainMenu();
            }
           }// end method register
	
        public void printList(Object[] passwordList) {
        //Print password chain for debug purpose. This would not be needed in real life applications
                for(int i = 1; i < passwordList.length;i++) System.out.println("(i = "+ i +") "+passwordList[i]);
        }
        
	public static boolean isInt(String s) {
		for(int i =0; i < s.length();i++){
			if(!Character.isDigit(s.charAt(i))) return false;
		}
	    return true;
	}
	
	public static void mainMenu()throws Exception{
                Cse465_hw1 temp = new Cse465_hw1();
		boolean flag = false;
		Scanner sc = new Scanner(System.in);
		while(!flag){
			System.out.println("1 Register new User, 2 Exit, or 3 log in");
			
			String menu = sc.nextLine();
			if(menu.equals("")) menu = "0";
			int menuInt = 0;

			sc = new Scanner(System.in);
			if(isInt(menu)){
				menuInt = Integer.parseInt(menu);
				if(menuInt == 1){
					System.out.println("Enter new ID name: ");
					@SuppressWarnings("resource")
					Scanner scanner = new Scanner(System.in);
					String id = scanner.nextLine();
					temp.register(id);
                                        break;
				}
				else if(menuInt == 2){
					System.out.println("Goodbye\n");
					System.exit(0);
				}
                                else if(menuInt == 3) {
                                    break;
                                }
				else{
					System.out.println("\nInvalid response.Try again\n");
					//flag = true;
				}
			}
		}
	}//End method mainMenu
	
	//The secret cryptographic hashing algorithm(SHA-256)
	public static String secret(String plain) throws Exception{
		String result="";
		byte[] hashBytes = md.digest(plain.getBytes("UTF-8"));
		result = DatatypeConverter.printHexBinary(hashBytes);
		return result;
	}//end method secret
	
	public static Object[] reverse(Object[] str){
		Object[] result = new String[str.length];
		int ctr = 0;
		for( int i = str.length-1; i >= 0; i-- ){
			result[ctr] = str[i];
			ctr++;
		}
		return result;
	}// end method reverse
	
	public static boolean authenticate(String id, String password) throws Exception{
		//After running the attempted password through the secret cryptographic hashing algorithm, if it equals the previous
		//password then return true and replace previous password with attempted password,moving down the password chain.
		if(secret(password).equals(db.get(id))){
			db.replace(id, password);
			return true;
		}
		return false;
	}//end method authenticate
	
	public static String getSeed(String id){
		int size = skeys.get(id).length;
		return skeys.get(id)[size-1];
	}// end method getSeed
	
}//end class Skey 