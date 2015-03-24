import ChatApp.*;          // The package containing our stubs
import org.omg.CosNaming.*; // HelloClient will use the naming service.
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;     // All CORBA applications need these classes.
import org.omg.PortableServer.*;   
import org.omg.PortableServer.POA;
import java.util.Scanner;
import java.util.*;
 
class ChatCallbackImpl extends ChatCallbackPOA
{
    private ORB orb;

    public void setORB(ORB orb_val) {
        orb = orb_val;
    }

    public void callback(String notification)
    {
        System.out.println(notification);
    }
    public void printGameArea(char gameArea[][], String xTeam, String oTeam) {
	System.out.println("Team 'x': " + xTeam + " Team 'o': " + oTeam);
	 System.out.println("... 0 . 1 . 2 . 3 . 4 . 5 . 6 . 7 .");
	for(int col = 0; col < 8; ++col){
	    System.out.println(". ---------------------------------");
	    System.out.print(col + " | ");
	    for(int row = 0; row < 8; ++row){
		if(gameArea[row][col] != 'x' && gameArea[row][col] != 'o' ){
		    System.out.print("- | ");
		}
		else{
		    System.out.print(gameArea[row][col]+" | ");
		}
	    }
	    System.out.println();
	}
	System.out.println(". ---------------------------------");
    }
    public void printWinners(String winners){
	System.out.println("WINNER(S): " + winners);
    }
}

public class ChatClient
{
    static Chat chatImpl;
    String myName;
    public static void main(String args[])
    {
	try {
	    // create and initialize the ORB
	    ORB orb = ORB.init(args, null);

	    // create servant (impl) and register it with the ORB
	    ChatCallbackImpl chatCallbackImpl = new ChatCallbackImpl();
	    chatCallbackImpl.setORB(orb);

	    // get reference to RootPOA and activate the POAManager
	    POA rootpoa = 
		POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();
	    
	    // get the root naming context 
	    org.omg.CORBA.Object objRef = 
		orb.resolve_initial_references("NameService");
	    NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
	    
	    // resolve the object reference in naming
	    String name = "Chat";
	    chatImpl = ChatHelper.narrow(ncRef.resolve_str(name));
	    
	    // obtain callback reference for registration w/ server
	    org.omg.CORBA.Object ref = 
		rootpoa.servant_to_reference(chatCallbackImpl);
	    ChatCallback cref = ChatCallbackHelper.narrow(ref);
	    
	    // Application code goes below
	    System.out.println("Hello and welcome to this fantastic program!\nAvailable commands:\njoin <your prefered nickname> - Create a user\npost <whatever you want to post> - Post to everybody who is online\n ");
	    boolean quit = false;
	    String username = "";
	    while(!quit){
		Scanner sc = new Scanner(System.in);
		String prefix = sc.next();
		prefix = prefix.toLowerCase();
		if(prefix.equals("join")){
		    String namerequest = sc.next();
		    if(chatImpl.join(cref, namerequest)){
			username = namerequest;
		    }
		}
		else if(prefix.equals("leave")){
		    chatImpl.leave(cref);
		}
		else if(prefix.equals("list")){
		    chatImpl.list(cref);
		}
		else if(prefix.equals("post")){
		    chatImpl.say(cref, sc.nextLine());
		    //System.out.println(sc.nextLine());
		}
		else if(prefix.equals("quit")){
		    chatImpl.leave(cref);
		    quit = true;
		}
		else if(prefix.equals("othello")){
		    chatImpl.othello(cref, sc.next().charAt(0));
		}
		else if(prefix.equals("insert")){
		    chatImpl.insert(cref, sc.nextInt(), sc.nextInt());
		}
		else if(prefix.equals("reset")){
		    chatImpl.reset(cref);
		}
		else{
		    System.out.println("Invalid prefix!");
		}
	    }
	    
	} catch(Exception e){
	    System.out.println("ERROR : " + e);
	    e.printStackTrace(System.out);
	}
    }
}
