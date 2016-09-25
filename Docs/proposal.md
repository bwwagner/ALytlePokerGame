Branden Wagner  
Jason Lytle  
9/14/16    

Project Proposal    

Our project is a Java Poker application called A Lytle Poker Game.  It is being developed in JavaFX using NetBeans, 
and we utilize GitHub for version control.  We plan for this to be a lightweight, networked game that is easy to use.   

A poker hand will include an ante and rounds of betting, so the game will end when one player has won the entire pot.  
The size of the pot will be chosen at the start of the game. We plan to offer at least two rule variations to play, 
but the card and suit rankings will follow the standard poker rules. It will support 2-5 players in direct competition.     

Cards will be reshuffled at the beginning of each hand.  The standard 52 card deck will be used without wild cards.  
The game assets (cards, playing table, etc.) are to be created by hand.    

We plan to use an asynchronous model since poker is a turn-based game.  The game state will include: 
all players’ current hand and bank, the overall pot size, size of the ante, current player’s turn, 
and the order of the remaining cards in the deck. If a player’s bank equals the size of the overall pot 
at the end of a hand then the game will end.    

The status of the pot, player’s hands, and game logic will be kept on the server side to avoid cheating.  
The clients connect to a server via LAN in order to play. We will create the Server and a GUI client to play the game.

