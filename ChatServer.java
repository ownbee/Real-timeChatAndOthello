import ChatApp.*;          // The package containing our stubs. 
import org.omg.CosNaming.*; // HelloServer will use the naming service. 
import org.omg.CosNaming.NamingContextPackage.*; // ..for exceptions. 
import org.omg.CORBA.*;     // All CORBA applications need these classes. 
import org.omg.PortableServer.*;   
import org.omg.PortableServer.POA;
import java.util.*;

class Othello{
    private Vector<Vector<Character>> gameArea = new Vector<Vector<Character>>(8);
    private Map players = new Hashtable();
    public Othello(){
	;
    } 
    public String Join(String username, char color){
	if(players.containsKey(username)){
	    return "You're aleady playing!";
	}
	
	if(color != 'x' || color != 'o'){
	    return "Choose between color 'x' or 'o'";
	}
	players.put(username, color);
	return "";
    }
    public Boolean Leave(String username){
	if(players.containsKey(username)){
	    players.remove(username);
	    return true;
	}
	return false;
    }
    public String Insert(String username, Integer x, Integer y){
	if(!players.containsKey(username)){
	    return "You are not part of the game!";
	}
	if(x > 8 || y > 8 || x < 0 || y < 0){
	    return "Out of game area!";
	}
	if(gameArea.get(x).get(y) == 'x' || gameArea.get(x).get(y) == 'o'){
	    return "Position already taken!";
	}
	gameArea.get(x).add(y, players.get(username));
	return "";
    }
    private Boolean Check(Integer x, Integer y){
	return true;
    }
    public void Reset(){
	;
    }
}

class User<L,R> {
    private L l;
    private R r;
    public User(L l, R r){
	this.l = l;
	this.r = r;
    }
    public L getl(){ return l; }
    public R getr(){ return r; }
    public boolean comparel(String otherName){
	if(otherName.equals(this.l))
	    return true;
	return false;
    }
}

class ChatImpl extends ChatPOA
{
    private ORB orb;
    private Vector<User<String,ChatCallback>> users = new Vector<User<String,ChatCallback>>();

    public void setORB(ORB orb_val) {
        orb = orb_val;
    }
    private int IORlookup(ChatCallback objref){
	for(int i = 0; i < users.size(); ++i){
	    if(users.get(i).getr().equals(objref)){
		return i;
	    }
	} 
	return -1;
    }
    
    public void say(ChatCallback callobj, String msg)
    {
	int userIndex = IORlookup(callobj);
	if(userIndex == -1){
	    callobj.callback("You have to join first!");
	    return;
	}
	post(callobj, users.get(userIndex).getl() + " said:" + msg); // Send to all but sender.
	callobj.callback(users.get(userIndex).getl() + " said:" + msg); // Send to sender.
    }
    private void post(ChatCallback callobj, String msg){
	int userIndex = IORlookup(callobj);
	for(int i = 0; i < users.size(); ++i){
	    if(userIndex != i) // If not sender.
		users.get(i).getr().callback(msg);
	}
    }
    public boolean join(ChatCallback callobj, String name){
	if(IORlookup(callobj) != -1){
	    callobj.callback("You have already joined!");
	    return false;
	}
	if(name.length() < 3){ // A minimum username size
	    callobj.callback("Name is too short. Choose another one.\n");
	    return false;
	}
	
	for(int i = 0; i < users.size(); ++i){ // Check if username already exists 
	    if(users.get(i).comparel(name)){
		callobj.callback("Name already exist!\n");  
		return false;
	    }
	}
	users.add(new User(name, callobj)); // If all went fine, add username to list
	callobj.callback("Welcome " + name  + "!\n");
	post(callobj, name + " joined");
	return true;
    }

    public void leave(ChatCallback objref){
	int userIndex = IORlookup(objref);
	if( userIndex == -1 ){
	    objref.callback("You dont have anything to leave");
	    return;
	}
	post(objref, users.get(userIndex).getl() + " left");
	objref.callback("Goodbye " + users.get(userIndex).getl());
	users.remove(userIndex);
    }

    public void list(ChatCallback objref){
	int userIndex = IORlookup(objref);
	if(userIndex == -1){
	    objref.callback("Register first!");
	    return;
	}
	if(users.size() == 0){
	    return;
	}
	StringBuilder stringBuilder = new StringBuilder();
	stringBuilder.append("List of registered users:\n");
	for(int i = 0; i < users.size(); ++i){
	    stringBuilder.append(users.get(i).getl() + "\n");
	}
	String list = stringBuilder.toString();
	objref.callback(list);
    }
    public void play(){
	;
    }
}

public class ChatServer 
{
    public static void main(String args[]) 
    {
	try { 
	    // create and initialize the ORB
	    ORB orb = ORB.init(args, null); 

	    // create servant (impl) and register it with the ORB
	    ChatImpl chatImpl = new ChatImpl();
	    chatImpl.setORB(orb); 

	    // get reference to rootpoa & activate the POAManager
	    POA rootpoa = 
		POAHelper.narrow(orb.resolve_initial_references("RootPOA"));  
	    rootpoa.the_POAManager().activate(); 

	    // get the root naming context
	    org.omg.CORBA.Object objRef = 
		           orb.resolve_initial_references("NameService");
	    NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

	    // obtain object reference from the servant (impl)
	    org.omg.CORBA.Object ref = 
		rootpoa.servant_to_reference(chatImpl);
	    Chat cref = ChatHelper.narrow(ref);

	    // bind the object reference in naming
	    String name = "Chat";
	    NameComponent path[] = ncRef.to_name(name);
	    ncRef.rebind(path, cref);

	    // Application code goes below
	    System.out.println("ChatServer ready and waiting ...");
	    
	    // wait for invocations from clients
	    orb.run();
	}
	    
	catch(Exception e) {
	    System.err.println("ERROR : " + e);
	    e.printStackTrace(System.out);
	}

	System.out.println("ChatServer Exiting ...");
    }

}
