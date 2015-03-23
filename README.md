# Real-timeChatAndOthello
Real-time chat and Othello using Corba

 _Here is an example of how you compile the chat system:_

       $ make target
       $ make clobber
       $ make idl
       $ /usr/bin/idlj -fall Chat.idl
       $ make c
       $ /usr/bin/javac ChatClient.java ChatApp/
       $ *.java
       $ make s
       $ /usr/bin/javac ChatServer.java ChatApp/*.java
