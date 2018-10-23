start.

+start <-
	.drop_all_intentions;
	.abolish(_);
	.wait(1000);
	-+my_price(1+math.random*10);
	!presentYourself.

@p10[atomic] +!presentYourself[source(self)] : my_price(MP) <-
	.broadcast(tell, present);
	.my_name(N);
	present(N);
	.print("I am ", N, " the max price I would pay is ", MP).

@p20[atomic] +auction(D, P)[source(S)] : my_price(MP) & P > MP & not iAmOut<-
	.my_name(N);
	.broadcast(tell, absent);
	left(N);
	-+iAmOut.	
	
+winnerag[source(Z)]: true <- 
	.print("I am so so Happy because I am the winner");
	.my_name(N);
	iAmTheWinner(N);
	.stopMAS.