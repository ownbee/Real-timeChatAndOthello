import ChatApp.*;          // The package containing our stubs. 
import org.omg.CosNaming.*; // HelloServer will use the naming service. 
import org.omg.CosNaming.NamingContextPackage.*; // ..for exceptions. 
import org.omg.CORBA.*;     // All CORBA applications need these classes. 
import org.omg.PortableServer.*;   
import org.omg.PortableServer.POA;
import java.util.*;

class Othello{
    private int size = 8;
    private char gameArea[][] = new char[size][size];
    private Map players = new Hashtable();
    public Othello(){
	;
    } 
    public char[][] getGameArea(){
	return gameArea;
    }
    public Character getTeam(String username){
	return (Character)players.get(username);
    }
    public Map getAllPlayers(){
	return players;
    }
    public String getXPlayers(){
	StringBuilder stringBuilder = new StringBuilder();
	Set<String> keys = players.keySet();
	for(String key: keys){
	    if(players.get(key) == 'x'){
		stringBuilder.append(key + " ");
	    }
	}
	return stringBuilder.toString();
    }
    public String getOPlayers(){
	StringBuilder stringBuilder = new StringBuilder();
	Set<String> keys = players.keySet();
	for(String key: keys){
	    if(players.get(key) == 'o'){
		stringBuilder.append(key + " ");
	    }
	}
	return stringBuilder.toString();
    }
    
    public Integer join(String username, char color){
	if(players.containsKey(username)){
	    return 1;
	}
	
	if(color != 'x' && color != 'o'){
	    return 2;
	}
	players.put(username, color);
	return 0;
    }
    public Boolean leave(String username){
	if(players.containsKey(username)){
	    players.remove(username);
	    return true;
	}
	return false;
    }
    public Boolean inGame(String username){
	if(players.containsKey(username)){
	    return true;
	}
	return false;
    }
    public Integer insert(String username, int x, int y){
	if(!inGame(username)){
	    return 1;
	}
	if(x > size-1 || y > size-1 || x < 0 || y < 0){
	    return 2; 
	}
	if(gameArea[x][y] == 'x' || gameArea[x][y] == 'o'){
	    return 3;
	}
	gameArea[x][y] = (Character)players.get(username);
	return 0;
    }
    public Boolean winningMove(String username, int x, int y){
	// Check all possibilities???!!
        char color = (char)players.get(username); // Get color
	int win = 3; //num in row to win (3 = |0,1,2,3| = 4)
	// TO THE LEFT
	int array[] = new int[8];  
	for(int i = 0; i <= win; ++i){
	    if((x-win)>=0){
		if(gameArea[x-i][y] == color){ //LEFT
		    ++array[0];
		}
		if((y-win)>=0){
		    if(gameArea[x-i][y-i] == color){ //UPLEFT
			++array[1];
		    }
		}
		if((y+win)<size){
		    if(gameArea[x-i][y+i] == color){ //DOWNLEFT
			++array[2];
		    }
		}
	    }
	    if((y-win)>=0){
	    	if(gameArea[x][y-i] == color){ //UP
	    	    ++array[3];
	    	}
	    }
	    if((y+win)<size){
	    	if(gameArea[x][y+i] == color){ //DOWN
	    	    ++array[4];
	    	}
	    }
	    if((x+win)<size){
	    	if(gameArea[x+i][y] == color){ //RIGHT
	    	    ++array[5];
	    	}
	    	if((y-win)>=0){
	    	    if(gameArea[x+i][y-i] == color){ //UPRIGHT
	    		++array[6];
	    	    }
	    	}
	    	if((y+win)<size){
	    	    if(gameArea[x+i][y+i] == color){ //DOWNRIGHT
	    		++array[7];
	    	    }
	    	}
	    }
	}
	for(int p = 0; p < 8; ++p){
	    if(array[p] >= win){
		++array[p];
		return true;
	    }
	}
	
	return false;
    }
    public void reset(){
	gameArea = new char[size][size];
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
    private Othello othello = new Othello();
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
	if(othello.inGame(users.get(userIndex).getl())){
	    if(othello.leave(users.get(userIndex).getl())){
		post(objref, users.get(userIndex).getl() + " left othello.");
		objref.callback("You left othello.");
		return;
	    }
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
    private void updateGameArea(){
	Set<String> keys = othello.getAllPlayers().keySet();
	for(String key: keys){ // For all players in othello
	    for(int i = 0; i < users.size(); ++i){ // For all regisered users 
		if(users.get(i).getl().equals(key)){ // If two mathces, send new gamearea
		    users.get(i).getr().printGameArea(othello.getGameArea(),othello.getXPlayers(),othello.getOPlayers());
		}
	    }
	}
    }
    public void othello(ChatCallback objref, char color){
	int userIndex = IORlookup(objref);
	if(userIndex == -1){
	    objref.callback("Register first!");
	    return;
	}
	int error = othello.join(users.get(userIndex).getl(),color);
	if(error == 0){
	    objref.callback("You have successfully joined Othello!");
	    updateGameArea();
	}
	else if(error == 1){
	    objref.callback("You're aleady playing!");
	}
	else if(error == 2){
	    objref.callback("Choose between color 'x' or 'o'");
	}
    }
    public void insert(ChatCallback objref, int x, int y){
	int userIndex = IORlookup(objref);
	if(userIndex == -1){
	    objref.callback("Register first!");
	    return;
	}
	int error = othello.insert(users.get(userIndex).getl(), x, y);
	if(error == 0){
	    objref.callback("Your move: (" + x + "," + y + "), placed." );
	    post(objref, users.get(userIndex).getl() + "'s move: (" + x + "," + y + ").");
	}
	if(error == 1){
	    objref.callback("You are not part of the game!");
	    return;
	}
	if(error == 2){
	    objref.callback("Out of game area!");
	    return;
	}
	if(error == 3){
	    objref.callback("Position already taken!");
	    return;
	}
	if(othello.winningMove(users.get(userIndex).getl(), x, y)){
	    char color = othello.getTeam(users.get(userIndex).getl());
	    String winners;
	    if(color == 'x'){
		winners = othello.getXPlayers();
	    }
	    else{
		winners = othello.getOPlayers();
	    }
	    updateGameArea();
	    post(objref, "WINNER(S):" + winners);
	    objref.callback("WINNER(S):" + winners);
	    othello.reset();
	    return;
	}
	updateGameArea();
    }
    public void reset(ChatCallback objref){
	int userIndex = IORlookup(objref);
	if(userIndex == -1){
	    objref.callback("Register first!");
	    return;
	}
	if(othello.inGame(users.get(userIndex).getl())){
	    othello.reset();
	    updateGameArea();
	    post(objref, users.get(userIndex).getl() + " reset game.");
	    objref.callback("You have made a reset.");
	}
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
