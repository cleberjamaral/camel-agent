my_price(1+math.random*10).
presentYourself.

@p10[atomic] +presentYourself : my_price(MP) <-
	.broadcast(tell, present);
	.my_name(N);
	present(N);
	.print("I am ", N, " the max price I would pay is ", MP).

@p20[atomic] +auction(D, P)[source(S)] : my_price(MP) & P > MP & presentYourself <-
	.my_name(N);
	left(N);
	!!surelyLeft;
	-presentYourself.
	
+!surelyLeft : not agentLeft <-
	.broadcast(untell, present);
	.wait(500);
	!!surelyLeft.

+!surelyLeft.

+winnerag[source(Z)]: true <- 
	.print("I am so so Happy because I am the winner");
	.my_name(N);
	iAmTheWinner(N);
	.stopMAS.