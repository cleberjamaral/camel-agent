package auctiontelegram;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import auctiontelegram.App;

import org.apache.camel.component.telegram.TelegramComponent;

import camelagent.*;

public class App {

	public static void main(String[] args) throws Exception {

		AgentContainer container = new AgentContainer(App.class.getClassLoader(), App.class.getPackage());
		final CamelContext camel = new DefaultCamelContext();
		camel.addComponent("agent", new AgentComponent(container));
		camel.addComponent("telegram", new TelegramComponent());
		
		String telegramToken = "botToken1";

		/* Create the routes */
		camel.addRoutes(new RouteBuilder() {
			@Override
			public void configure() {

				from("telegram:bots/" + telegramToken + "?chatId=-274694619")
						.transform(body().convertToString()).to("agent:percept?updateMode=replace");

				from("agent:action?exchangePattern=InOut&actionName=auction")
						.log("action: ${header[actionName]}, Params: ${header[params]}")
						.to("telegram:bots/" + telegramToken + "?chatId=-274694619");

				from("agent:action?actionName=winnerAgent")
						.log("action: ${header[actionName]}, Params: ${header[params]}")
						.to("telegram:bots/" + telegramToken + "?chatId=-274694619");

				from("agent:action?actionName=present").log("action: ${header[actionName]}, Params: ${header[params]}")
						.to("telegram:bots/" + telegramToken + "?chatId=-274694619");

				from("agent:action?actionName=left").log("action: ${header[actionName]}, Params: ${header[params]}")
						.to("telegram:bots/" + telegramToken + "?chatId=-274694619");

				from("agent:action?actionName=iAmTheWinner")
						.log("action: ${header[actionName]}, Params: ${header[params]}")
						.to("telegram:bots/" + telegramToken + "?chatId=-274694619");
			}
		});

		// turn exchange tracing on or off (false is off)
		// camel.setTracing(true);

		// start routing
		camel.start();
		System.out.println("Starting router...");

		// Start the agents after starting the routes
		container.startAllAgents();

		System.out.println("... ready.");
	}
}