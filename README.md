# Real-timeChatAndOthello
## Very simple real-time chat and Othello using Corba

_How to use Makefile:_

     	$ make target             - build the project
     	$ make orbd|client|server - run the individual components
     	$ make clean              - clean temporary files
     	$ make clobber            - wipe everything that is generated

 _Do the following steps to start application:_
 _(1) Compile project:_
       
       $ make target 

_(2) Start the ordb:_

       $ make orbd
       orbd -ORBInitialPort 1057 -ORBInitialHost localhost

_(3) Start the chat server in the second terminal window:_

       $ make server
       /usr/bin/java ChatServer -ORBInitialPort 1057 -ORBInitialHost localhost
       ChatServer ready and waiting ...

_(4) Finally, start up the chat client in the third window:_
_(And maybe a few more if you want to test it alone)_

       $ make client
       /usr/bin/java ChatClient -ORBInitialPort 1057 -ORBInitialHost localhost

_Commands ingame:_

       join <username>  - join chat and all
       post <message> 	- write to anyone online
       leave 		- leave chat or othello
       othello <color>	- o = team o, x = team x
       insert x y	- x,y => coorinates to place mark

Node: To win othello, you have to get 4 in a row...