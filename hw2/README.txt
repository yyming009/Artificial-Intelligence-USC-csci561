log files:

log_<round>_<player1>_<player2>.txt: player 1 plays 11 times against player 2 on given round
winner_<round>_<player1>_<player2>.txt: the majority winner over those 11 games

all players whose name appears in at least one of the winner_<...> files in one round advance to the next round.

log files are organized into round1/, round2/ etc

round1: all student agents play against our reference random agent
round2: all student agents play against our reference minimax3 agent
later rounds: all student agents play against 2 or 3 other student agents

notes about the log files:

- the start state is always the same. Who plays 'O' first is random for the first game, and O always gets the opening
  move. Then students alternate between playing O or X.

- a game server takes the current state and a move and checks them for validity. It returns FAIL if anything goes wrong,
  and, in such case, that student agent loses that game. Note that if your move failed to parse, we replace it by
  invalid move A0. Note that sometimes your move is valid but the server says invalid, this means that your computed
  next state (after applying your move and gravity) was incorrect.

- timeout: if the log says ##### Killing X after timeout ##### it means that you have exceeded your runtime + grace
  (10s) + an extra 2s to be sure you are above both. Then we kill your agent and you lose that game.

- child exited with value X: this means your program crashed.

  we do not show your printed messages during the runs (some were just way too verbose). To see why you crashed, just
  copy the input.txt as shown in the log file just before the crash and run your program, it should crash again in the
  same way.
