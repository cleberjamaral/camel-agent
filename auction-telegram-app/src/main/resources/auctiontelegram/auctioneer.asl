start.

+start <-
	.drop_all_intentions;
	.abolish(_);
	-+price(1);
	-+winner(no_winner);
	-+total(6).

@b00 +present[source(A)] : .count(present[source(B)],N) & total(T) & N == T  <-
	.print("Participant '", A,"'' was added, total: ",N ,". Let's start the auction!"); 
	!setOffer.

@b01 +present : .print("A participant was added.").

@g00[atomic] +!setOffer : .count(present[source(B)],N) & .count(absent[source(C)],M) & N > M+1 <-
	.print("Number of participants: ", N, ", already left: ", M); 
	?price(P);
	-+price(P+0.5);
	.broadcast(tell, auction(banana, P+0.5));
	auction(banana, P+0.5);
	.wait(1500);
	!!setOffer.

@g01[atomic] +!setOffer <-
	?present[source(S)];
	-+winner(S);
	.print("The winner is ", S);
	.send(S, tell, winnerag);
	winnerAgent(S);
	.wait(1000).

//There is one or none present, so the last should be the winner
@p20[atomic] +absent[source(A)] : winner(no_winner) & count(absent[source(B)],N) & total(T) & N >= T-1 <-
	-+winner(A);	
	.print("Winner of the auction ", product(banana), " is ", A);
	.send(A, tell, winnerag);
	winnerAgent(A).
	
+absent[source(A)] <-
	.print("Participant '", A,"'' has left!").