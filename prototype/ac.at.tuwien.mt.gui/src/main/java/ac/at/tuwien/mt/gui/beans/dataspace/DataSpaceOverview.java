package ac.at.tuwien.mt.gui.beans.dataspace;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.ws.rs.ServiceUnavailableException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ac.at.tuwien.mt.gui.beans.UserControllerBean;
import ac.at.tuwien.mt.gui.primefaces.Messages;
import ac.at.tuwien.mt.gui.primefaces.exception.CommunicationException;
import ac.at.tuwien.mt.gui.rest.RESTDataContractClient;
import ac.at.tuwien.mt.gui.rest.RESTMicroserviceLocator;
import ac.at.tuwien.mt.gui.rest.RESTPersonClient;
import ac.at.tuwien.mt.model.microservice.MicroserviceInfo;
import ac.at.tuwien.mt.model.microservice.MicroserviceType;
import ac.at.tuwien.mt.model.person.Person;
import ac.at.tuwien.mt.model.thing.Thing;

@SuppressWarnings("serial")
@ManagedBean(name = "dso")
@ViewScoped
public class DataSpaceOverview implements Serializable {

	private static final Logger LOGGER = LogManager.getLogger(DataSpaceOverview.class);

	private List<Person> list = new ArrayList<Person>();

	@ManagedProperty(value = "#{ucb}")
	private UserControllerBean userControllerBean;

	@PostConstruct
	public void init() {
		try {
			LOGGER.debug("Loading dataspaces...");
			// return all users except
			String currentPersonId = userControllerBean.getPerson().getPersonId();
			MicroserviceInfo locateDataContractMicroservice = RESTMicroserviceLocator.locateMicroservice(MicroserviceType.DATACONTRACT);
			RESTDataContractClient client = new RESTDataContractClient(locateDataContractMicroservice);

			list = RESTPersonClient.findExcept(currentPersonId);
			// see if the respective person has devices and if not remove it as
			// he/she is not a provider
			Iterator<Person> iter = list.iterator();
			while (iter.hasNext()) {
				Person nextPerson = iter.next();
				List<Thing> personThings = client.find(nextPerson.getPersonId());
				if (personThings.isEmpty()) {
					iter.remove();
				}
			}
		} catch (CommunicationException | ServiceUnavailableException e) {
			LOGGER.error(e);
			Messages.addMessage(FacesMessage.SEVERITY_ERROR, "An unexpected technical error occurred, please contact support!");
		}
	}

	/**
	 * @return the userControllerBean
	 */
	public UserControllerBean getUserControllerBean() {
		return userControllerBean;
	}

	/**
	 * @param userControllerBean
	 *            the userControllerBean to set
	 */
	public void setUserControllerBean(UserControllerBean userControllerBean) {
		this.userControllerBean = userControllerBean;
	}

	/**
	 * @return the list
	 */
	public List<Person> getList() {
		return list;
	}

	/**
	 * @param list
	 *            the list to set
	 */
	public void setList(List<Person> list) {
		this.list = list;
	}

}
