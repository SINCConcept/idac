package ac.at.tuwien.mt.monitoring.beans;

import java.util.Calendar;
import java.util.Date;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.stereotype.Component;

import ac.at.tuwien.mt.middleware.internal.model.jms.Message;
import ac.at.tuwien.mt.model.helper.DefaultIDGenerator;

@Component
public class ActiveMQProcessorBean {

	private static final Logger LOGGER = LogManager.getLogger(ActiveMQProcessorBean.class);

	@Handler
	public void process(Exchange exchange, @Body String message) {
		LOGGER.debug("Processing message from activemq.");
		LOGGER.debug("Message: " + message);
		Message messageToSave = new Message();
		Object object = exchange.getIn().getHeaders().get("Destination");
		if (object instanceof String) {
			String queueId = (String) object;
			messageToSave.setQueueId(queueId);
		}

		messageToSave.setMessageId(DefaultIDGenerator.generateID());
		messageToSave.setMessage(message);
		Date creationDate = Calendar.getInstance().getTime();
		messageToSave.setCreationDate(creationDate);
		exchange.getOut().setBody(messageToSave.getDocument(), Document.class);
	}
}
