JPE 0.9.** - bayard@generationjava.com
(http://www.generationjava.com/java/JPE.shtml)

Licenced under GPL.

A series of patches and functionality enhancements to version 
0.9b.

New functionalities:

  JStyle added in. Let's you reformat your java code.
  Autoindentation. Makes your life easier.
  Regexp search and replace.
  Regexp search, hidden behind Find. Start your find with / and 
  	it is automatically a regexp find.
  Import text.
  Create from templates.
  Revert to saved file.
  Improved shortcut keys. IMO :)
  Recently opened files list.
  Run Garbage Collector. 
  Separate JPE application error log.
  Imports fixing. Searches your source and works out the import 
  	statements for you.
  Plus many bug-fixes.
  
Recent new things

To upgrade JPE v0.9.** on Epoc:
	Copy jpe.jar over the top of your existing:
		C:\System\Apps\JPE\jpe.jar
	Copy jstyle.jar and gnu-regexp-1.0.8.jar into the same directory.
	Amend the jpe.txt file to include the above two jar's in the classpath.
	Run JPE! :)

********** ORIGINAL readme.txt ***********

				JPE 0.9b - Java Psion Editor / Readme
			-----------------------------------------------------------

	Released under the OpenSource GNU-Licence 
	(c) Submarine - Daniel Ockeloen, Thanks to psion netherlands

	The Epoc parts (loader, logo, installer) where made by Oliver
	Toelker for which many thanks.

	This is a beta release of a programming tool that allows me
	to use my netbook as a on the road programming tool. Im normally
	not into writing frontend code (like a editor) so since this is a
	opensource project please fix my mistakes. My first goals are very
	simple i want a editor that allows me to work on multiple files switch
	between them, compile them using javac and run them. I started out 
	with a good look at MiniEdit from M.Tjioe based on a example made 
	in 1997 bij John Jensen not much code is left of that but i like to 
	thank them for a running example that i could base this work	on.

	How to install
	------------------

	You probably allready used the installed and started JPE
	using the application bar. Ifso then only make sure you have
	installed the missing.jar file correctly (so you can compile).

	To run and use JPE you need to have a epoc32 release 5 with
	the java runtime installed. Last but not least you also need a 
	new classes.zip that holds the missing classes not installed by 
	the symbian jdk.

	You can download the new classes.zip (thanks to John Davies) 
	from	http://www.davies.lu/Java.

	Install the zip file as d:\Classes\lib\missing.jar , if you don't
	jpe will give a error ones you try to compile something. You
	can edit the classpath in jpe.txt.

	you can also start JPE from inside eshell/JShell with the following
	command line :

	java -cp d:\Classes\lib\missing.jar;c:\MJC;c:\JPE JPE

	optional commands

	-Djpe.home=e:\prg\jpe (override auto find jpe.home)
	
	Using JPE
	-------------

	The idea of JPE is to keep the number of actions needed to load/edit
	compile and run your programs with as low as possible. One of the
	ways to do this is to try to be stateless between uses JPE. This
	means that JPE remembers what files you had you had open and what
	lines you where working on. It saves the state of JPE and the files in 
	XML format in a file called config.xml. 

	The menu's are explained below but i use it in the following way :
	I open files that will be places in my 'Opened' menu i can
	switch to them at any time. JPE remembers the state of each file once
	i switch. With crlt-m i can compile the file i now have 
	selected when done the title bar tells me if i have errors ifso 
	i can switch	(toggle infact) with crtl-h to the error page and back. 
	Compile also autosaves before the compile. I can close files ones 
	i don't need them in your current set. Well thats about it the rest 
	should make sense.The font/fontsize and other setups are also 
	remembered by JPE.


	TitleBar
	----------

	The titlebar inside JPE is used (to save space) as feedback and
	input system. Most command will give feedback using this bar so
	keep your eye on it. Also command like find, run, goto etc etc use
	it to obtain input from you.

		feedback - general feedback on actions inside the bar, default
					   it shows you the filename of the selected file.

		input - it can ask you to input a string for example when doing
				  a search. it can also ask you for one key dialog like
				  on exit or save etc etc.

		status - at the end of the titlebar it shows you the current line
				   number and 3 possible indicators :
					
						s for saved/unchanged
						c for the file is compiled without errors
						l file is locked (readonly mode).

	Ctrl-keys
	------------

		JPE provides alot of key bindings for most used command. for
		example using the crtl-m, crtl-h, crtl-g , crtl-d and crtl-r combo you 
		can edit, compile, debug run and see output all without using any
		menu's. A few handy ones are :

			ctrl-e - Exit JPE
			ctrl-x - Cut
			ctrl-c - Copy
			ctrl-v - Paste
			ctrl-f - Find
			ctrl-shift-f Find again
			ctrl-g - Goto first compile error
			ctrl-shift-g Goto line number
			ctrl-d - Goto/Toggle debug window
			ctrl-h - Goto/Toggle error window
			ctrl-m - Compile file
			ctrl-r - Run as application
			ctrl-shift-r Run as application with params
			ctrl-p - Font size up
			ctrl-o -Font size down

	File menu
	-------------
		
		new - Creates a new file under the name untitled*.txt
		
		Open - Opens a file using a file requester

		Close - Close a file (unload it from JPE and the Opened menu)

		Lock/unlock - A toggle that makes the file read only or not 
						  inside JPE. This is usefull for heaving files open
						  to look at them but makes sure you can't change
						  them (happens alot on a small keyboard).

		Save - saves a file under its old name

		Save as - ask user for new name and save it as such

		Exit - check if all files are saved and exit JPE


	Edit Menu
	-------------

		Cut - Copy the selected text area to the clipboard and remove
				it from the selected text file.	

		Copy - Copy the selected test area to the clipboard

		Paste - Insert the text on the clipboard in this text area at
				  the current pos.

		Find - Find a given text in the current text

		Find Again - Find the text again from the current cursor position

		Goto Line - Ask for a line number and jump to it

		Goto Error - Jump to the first compile error if possible, file
						 must be 'compiled' to make this work

		Select All - Select the whole text Area


	Log Menu
	-------------

		StdOut - Show the standard output of JPE, the compiler and
					the running applications started by JPE.

		StdErr - Shows the standard error output of JPE, the compiler
				    and running applications started by JPE.


	Extra Menu
	---------------
	
		Compile - Save and compile the selected file using the java
					  compiler, output if any in stdErr (ctrl-h)

		Run as App - Run the current file as a java application

		Run as with param - Run like application with parameter line			

		Font up - Make the current font bigger
	
		Font down - Make the current fonr smaller

		Arial - Switch to the Arial font

		Courier - Switch to the Courier font

		Times - Switch to the New Times Roman font

		Machine - Switch to prefered machine setup


	Opened
	-----------
	
		Well it holds the opened files and you can switch to them
		using this pulldown.



	have Fun and remember this is a beta-release,

	Daniel Ockeloen
	daniel@submarine.nl

	
	
	