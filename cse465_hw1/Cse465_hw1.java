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
						if(db.get(id).equals(getSeed(id))){
							System.out.println("All passwords used. Register new user.");
							db.remove(id);
							skeys.remove(id);
							mainMenu();
							continue;
						}
					} 
					else{ 
						System.out.println("Attempt #: " + challenge + " out of 3");
						challenge++;
						System.out.println("Enter password (i="+ index  +"): \n");
					}					
				}//end while
			}else{
				//register(id);
				System.out.println("ID does not exist.");
				mainMenu();
			}
		}	
	}//end main 
	
	public void register(String id) throws Exception{
            boolean validCheck = true;
            
            // set n value to 5
            int n=5;
            Scanner sc = new Scanner(System.in);
            
            while(validCheck){		
                System.out.println("N value is 5, Enter secret seed: ");
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
                db.put(id,pChain);
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
					System.out.println("Enter new User: ");
					@SuppressWarnings("resource")
					Scanner scanner = new Scanner(System.in);
					String id = scanner.nextLine();
					temp.register(id);
                                        break;
				}
				else if(menuInt == 2){
					System.exit(0);
				}
                                else if(menuInt == 3) {
                                    break;
                                }
				else{
					System.out.println("\nWrong!\n");
					//flag = true;
				}
			}
		}
	}
	
	//hashing algorithm(SHA-256)
	public static String secret(String plain) throws Exception{
		String result="";
		byte[] hashBytes = md.digest(plain.getBytes("UTF-8"));
		result = DatatypeConverter.printHexBinary(hashBytes);
		return result;
	}
	
	public static Object[] reverse(Object[] str){
		Object[] result = new String[str.length];
		int rts = 0;
		for( int i = str.length-1; i >= 0; i-- ){
			result[rts] = str[i];
			rts++;
		}
		return result;
	}
	
        // deletion works by replacement, in the event correct password entered.
	public static boolean authenticate(String id, String password) throws Exception{
		if(secret(password).equals(db.get(id))){
			db.replace(id, password);
			return true;
		}
		return false;
	}
	
	public static String getSeed(String id){
		int size = skeys.get(id).length;
		return skeys.get(id)[size-1];
        }
}