# assignment4

This is assignment 4 for Prof. Madden's CS 441 class during Spring 2018.

This is an app where you can play checkers online with your friends.
You create an account, and after you do, you can challenge anybody
else to a game of checkers by typing their name into the search. If
you type a valid user, you'll start a new game with them. Additionally,
if you try to start a game with someone who you've already started
an incomplete game with, it'll go back to that game - you can only
have one active game at a time with anybody. Other than that, it's
standard checkers.

I have a database on mysql.cs.binghamton.edu that holds all the user
and game data. I can't access this directly unless I'm on school wifi,
so I have a few PHP scripts that query the database for me - these 
scripts are what the app communicates with to check the database. Since
I use the scripts which are hosted on cs.binghamton.edu, you can play
the game on any wifi. I spent most of my time learning PHP and MySQL,
as well as how to communicate between Android <-> PHP and PHP <-> SQL.

Fun facts about the game:

    - You can see this in the code, but this was originally going to be
      a joke social media site which involved bullying people to get points.
      I still want to make this, but I didn't have time to do it for this
      project, so I'll have to do it later. I already had the PHP/MySQL
      backend finished when I made this decision, so I went with something
      else that could be played with multiple people.
    - You can absolutely use SQL injection (and probably several other
      web exploits) to mess with this app. Please be nice.
    - I hosted my PHP scripts on cs.binghamton.edu/~kfranke1 - this is
      something that everybody can do. Just set up a public_html folder in
      your home directory and make sure it and everything in it have public
      access with chmod.
    - If you win 2147483648 or more games, nothing special will happen
      because I use longs in my app and 11-factor integers in my database, 
      but you should probably go outside.
