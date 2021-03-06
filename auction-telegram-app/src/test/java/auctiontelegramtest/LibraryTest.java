package auctiontelegramtest;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
import org.junit.Test;

import org.apache.camel.component.telegram.TelegramService;
import org.apache.camel.component.telegram.TelegramServiceProvider;
import org.apache.camel.component.telegram.model.OutgoingTextMessage;

import auctiontelegram.App;

public class LibraryTest {
	@Test
	public void testSomeLibraryMethod() {
		// Library classUnderTest = new Library();
		try {
			System.out.println("Sending a simple message by telegram service provider!");
			testSendFull();
			System.out.println("Check on telegram referred chat if the message was delivered!");

			System.out.println("Launching satandard telegram testing app!");
			App.main(new String[] { null });
		} catch (Exception e) {

		}
	}

	public static void testSendFull() {
		TelegramService service = TelegramServiceProvider.get().getService();
		OutgoingTextMessage msg = new OutgoingTextMessage();
		msg.setChatId("787074611");
		msg.setText("This is an auto-generated message from the Bot");
		msg.setDisableWebPagePreview(true);
		msg.setParseMode("Markdown");
		msg.setDisableNotification(true);
		service.sendMessage("708413344:AAHcw9VwDKGnNm063_5vjw_VVQlCigy0ZMs", msg);
	}

}
