This is a Java tool for testing bots for CodinGame multiplayer <a href="https://www.codingame.com/multiplayer/bot-programming/tic-tac-toe">Ultimate Tic-Tac-Toe</a>.
<h3>How does it works?</h3>
Compile sources. Run Tester.class with params:
<ul>
<li>Command line to start a new version of your bot process 
<li>Command line to start an old version of your bot process 
<li>Number of games
</ul>
As example: 
<pre><code>java Tester "java -cp C:\Documents\CodinGame\TicTacToe\out\ Player" "java -cp C:\Documents\CodinGame\TicTacToe\out\ PlayerOld" 100
</code></pre>
For this example 100 games will be played, where new version of your bot will play on the first and then second positions consistently.

<h4>OR</h4>
Download and run TicTacToeTester.jar with same params:
<pre><code>java - jar TicTacToeTester.jar "java -cp C:\Documents\CodinGame\TicTacToe\out\ Player" "java -cp C:\Documents\CodinGame\TicTacToe\out\ PlayerOld" 100</code></pre>

During the testing you will see result of testing of current game:
<code><pre>Game 192: frame 41; WIN; avg 1,41; wins 58,85%</pre></code>

At the end of the run you will see a result of testing. Something like:
<pre><code>Results for bot java -cp C:\Documents\CodinGame\TicTacToe\out\ Player
Wins: 117; Loses: 83; Draws: 0
Average place 1.415
Wins 58.5 %
</code></pre>

This means that new version is better than the old one.

The result like “Average place 1.5” means that both bots are equal.

<b>NOTE</b>: There is no any timelimits for testing bots.
