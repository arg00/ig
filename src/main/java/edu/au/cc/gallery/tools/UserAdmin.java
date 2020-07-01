package edu.au.cc.gallery.tools;

import edu.au.cc.gallery.DB;
import java.util.Scanner;
import java.util.ArrayList;

public class UserAdmin {
	
	private DB db;

	public UserAdmin() throws Exception {
		db = new DB();
		db.initDB();
	}

	public boolean addUser(String uname, String pwd, String fname) throws Exception {
		ArrayList<String> currUserList = db.getUsers();
		if (currUserList.contains(uname)) return false;
		
		db.addUser(uname, pwd, fname);
		return true;
	}

	public boolean editUser(String uname, String pwd, String fname) throws Exception {
		ArrayList<String> currUserList = db.getUsers();
		if (!currUserList.contains(uname)) return false;

		db.updateUser(uname, pwd, fname);
		return true;
	}

	public boolean deleteUser(String uname) throws Exception {
		ArrayList<String> currUserList = db.getUsers();
		if (!currUserList.contains(uname)) return false;

		db.deleteUser(uname);
		return true;	
	}

	public ArrayList<String> getUsers() throws Exception {
		return db.getUsers();
	}

	public ArrayList<String> getUserInfo(String uname) throws Exception {
		return db.getUserInfo(uname);
	}

        public ArrayList<String> getUsersImages(String uname) throws Exception {
	    return db.getUsersImages(uname);
        }
    
	public void runCLAdmin() throws Exception {
		Scanner sc = new Scanner(System.in);
                int input = -1;

                do {
			ArrayList<String> currUserList = db.getUsers();
			printOptions();
			input = Integer.parseInt(sc.nextLine());
			switch (input) {
				case 1:
					db.listAll();
					break;
				case 2: 
					System.out.println("Username> ");
					String userAddName = sc.nextLine();
					if (currUserList.contains(userAddName)) {
						System.out.println("Error: user with username "+userAddName+" already exists.");
						break;
					}
					String[] addVals = addOptions();
					db.addUser(userAddName, addVals[0], addVals[1]);
					break;
				case 3:
					System.out.print("Username to edit> ");
					String toEdit = sc.nextLine();
					if (!currUserList.contains(toEdit)) {
						System.out.println("No such user.");
						break;
					}
					String[] updateVals = updateOptions();
					db.updateUser(toEdit, updateVals[0], updateVals[1]);
					break;
				case 4:
					System.out.println("Enter username to delete> ");
					String toDelete = sc.nextLine();
					if (!currUserList.contains(toDelete)) {			
						System.out.println("No such user.");
						break;
					}
					db.deleteUser(toDelete);
					break;
			}
		
		} while (input != 5);
		
		System.out.println("Bye.");

		db.close();
	}	

	private static void printOptions() {
		String output = "1) List Users\n";
		output += "2) Add user\n";
		output += "3) Edit user\n";
		output += "4) Delete user\n";
		output += "5) Quit\n";
		output += "Enter command> ";

		System.out.println(output); 
	}

	public static String[] addOptions() {
		Scanner sc2 = new Scanner(System.in);

		String[] vals = new String[2];
		System.out.println("Password> ");
		vals[0] = sc2.nextLine();
		System.out.println("Full name> ");
		vals[1] = sc2.nextLine();
		return vals;		
	}

	public static String[] updateOptions() {
		Scanner sc3 = new Scanner(System.in);

		String[] vals = new String[2];
		System.out.println("New password (press enter to keep current)>");
		vals[0] = sc3.nextLine();
		System.out.println("New full name (press enter to keep current)>");
		vals[1] = sc3.nextLine();
		return vals;
	}
}
