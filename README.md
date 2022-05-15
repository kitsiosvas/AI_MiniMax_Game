# AI_MiniMax_Game
A Java 2-player game, where the player plays against the algorithm. Each party tries to make the opposing one to run out of moves.
The one that is about to play and has no moves left, loses.

Each player moves across the board. After one player moves through a tile, the tile becomes "black"  which means it's no longer available. The goal is to move 
optimaly in order to make the enemy have 0 available moves left when it's his turn.

First the progamm asks for the dimensions of the board. A 4x4 or 5x5 size is good for demonstration since larger sizes will take longer time to calculate the moves.
Valid moves are: "up", "right", "down", "left", "up_right", "up_left", "down_right", "down_left". An integer (max is 2 for complexity reduction) is also required to specify how many spaces you want to travel
towards that direction.
