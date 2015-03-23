# Real-timeChatAndOthello
Real-time chat and Othello using Corba

_How to use Makefile:_

     	$ make target             - build the project
     	$ make orbd|client|server - run the individual components
     	$ make clean              - clean temporary files
     	$ make clobber            - wipe everything that is generated

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

       $ make orbd
       $ orbd -ORBInitialPort 1057 -ORBInitialHost localhost

_Start the chat server in the second terminal window:_

       $ make server
       $ /usr/bin/java ChatServer -ORBInitialPort 1057 -ORBInitialHost localhost
       ChatServer ready and waiting ...

_Finally, start up the chat client in the third window:_

       $ make client
       $ /usr/bin/java ChatClient -ORBInitialPort 1057 -ORBInitialHost localhost
