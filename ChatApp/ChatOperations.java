package ChatApp;


/**
* ChatApp/ChatOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from Chat.idl
* den 24 mars 2015 kl 12:08 CET
*/

public interface ChatOperations 
{
  void say (ChatApp.ChatCallback objref, String message);
  boolean join (ChatApp.ChatCallback objref, String name);
  void leave (ChatApp.ChatCallback objref);
  void list (ChatApp.ChatCallback objref);
} // interface ChatOperations
