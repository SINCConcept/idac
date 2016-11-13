package ac.at.tuwien.mt.monitoring.internal.test;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.lang.StringUtils;

public class MainConsumer {
	public static void main(String[] args) throws Exception {

		Connection connection = null;
		Session session = null;
		// Create a ConnectionFactory
		String brokerURL = "tcp://localhost:61616";
		if (StringUtils.isBlank(brokerURL)) {
			return;
		}

		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);

		// Create a Connection
		connection = connectionFactory.createConnection();
		connection.start();

		connection.setExceptionListener(null);

		// Create a Session
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		// Create the destination (Topic or Queue)
		String queueName = "2016092418311622310476165228";
		Destination destination = session.createQueue(queueName);

		// Create a MessageConsumer from the Session to the Topic or Queue
		MessageConsumer consumer = session.createConsumer(destination, "ThingTopic = 't1'");

		// Wait for a message
		int timeout = 1000;
		Message message = consumer.receive(timeout);

		if (message == null) {
			// could not read from queue - nothing to do
			return;
		}

		String messageToSave = null;

		if (message instanceof TextMessage) {
			TextMessage textMessage = (TextMessage) message;
			String text = textMessage.getText();
			messageToSave = text;
		} else {
			messageToSave = message.toString();
		}

		consumer.close();

		System.out.println(messageToSave);

	}
}
