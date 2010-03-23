/**
 *
 */
package code.google.magja.service.order;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.axis2.AxisFault;

import code.google.magja.magento.ResourcePath;
import code.google.magja.model.customer.Customer;
import code.google.magja.model.customer.Customer.Gender;
import code.google.magja.model.order.Order;
import code.google.magja.model.order.OrderAddress;
import code.google.magja.model.order.OrderFilter;
import code.google.magja.model.order.OrderItem;
import code.google.magja.service.GeneralServiceImpl;
import code.google.magja.service.ServiceException;

/**
 * @author andre
 *
 */
public class OrderRemoteServiceImpl extends GeneralServiceImpl<Order> implements
		OrderRemoteService {

	/**
	 * Build a object Order
	 *
	 * @param attributes
	 * @return Order
	 * @throws ServiceException
	 */
	private Order buildOrderObject(Map<String, Object> attributes)
			throws ServiceException {
		Order order = new Order();

		for (Map.Entry<String, Object> att : attributes.entrySet())
			order.set(att.getKey(), att.getValue());

		// customer
		if (attributes.get("customer_id") != null) {
			Customer customer = new Customer();

			customer.setId(new Integer((String) attributes.get("customer_id")));
			if (attributes.get("customer_email") != null)
				customer.setEmail((String) attributes.get("customer_email"));
			if (attributes.get("customer_prefix") != null)
				customer.setPrefix((String) attributes.get("customer_prefix"));
			if (attributes.get("customer_firstname") != null)
				customer.setFirstName((String) attributes
						.get("customer_firstname"));
			if (attributes.get("customer_middlename") != null)
				customer.setMiddleName((String) attributes
						.get("customer_middlename"));
			if (attributes.get("customer_lastname") != null)
				customer.setLastName((String) attributes
						.get("customer_lastname"));
			if (attributes.get("customer_lastname") != null)
				customer.setLastName((String) attributes
						.get("customer_lastname"));
			if (attributes.get("customer_group_id") != null)
				customer.setGroupId(new Integer((String) attributes
						.get("customer_group_id")));
			if (attributes.get("customer_gender") != null) {
				Integer gender = new Integer((String) attributes
						.get("customer_gender"));
				customer.setGender(gender.equals(new Integer(1)) ? Gender.MALE
						: Gender.FEMALE);
			}

			order.setCustomer(customer);
		}

		// shipping address
		if (attributes.get("shipping_address") != null) {
			OrderAddress shippingAddress = new OrderAddress();

			Map<String, Object> atts = (Map<String, Object>) attributes
					.get("shipping_address");
			for (Map.Entry<String, Object> att : atts.entrySet())
				shippingAddress.set(att.getKey(), att.getValue());

			order.setShippingAddress(shippingAddress);
		}

		// billing address
		if (attributes.get("billing_address") != null) {
			OrderAddress billingAddress = new OrderAddress();

			Map<String, Object> atts = (Map<String, Object>) attributes
					.get("billing_address");
			for (Map.Entry<String, Object> att : atts.entrySet())
				billingAddress.set(att.getKey(), att.getValue());

			order.setBillingAddress(billingAddress);
		}

		// items
		if (attributes.get("items") != null) {
			List<Map<String, Object>> res = (List<Map<String, Object>>) attributes
					.get("items");

			for (Map<String, Object> i : res) {
				OrderItem item = new OrderItem();
				for (Map.Entry<String, Object> att : i.entrySet())
					item.set(att.getKey(), att.getValue());

				order.getItems().add(item);
			}
		}

		return order;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * code.google.magja.service.order.OrderRemoteService#addComment(code.google
	 * .magja.model.order.Order, java.lang.String, java.lang.String,
	 * java.lang.Boolean)
	 */
	@Override
	public void addComment(Order order, String status, String comment,
			Boolean notify) throws ServiceException {

		List<Object> params = new LinkedList<Object>();
		params.add(order.getId());
		params.add(status);
		params.add(comment);
		params.add((notify ? new Integer(1) : new Integer(0)));

		try {
			soapClient.call(ResourcePath.SalesOrderAddComment, params);
		} catch (AxisFault e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * code.google.magja.service.order.OrderRemoteService#cancel(code.google
	 * .magja.model.order.Order)
	 */
	@Override
	public void cancel(Order order) throws ServiceException {

		try {
			soapClient.call(ResourcePath.SalesOrderCancel, order.getId());
		} catch (AxisFault e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * code.google.magja.service.order.OrderRemoteService#getById(java.lang.
	 * Integer)
	 */
	@Override
	public Order getById(Integer id) throws ServiceException {

		Map<String, Object> order_remote = null;
		try {
			order_remote = (Map<String, Object>) soapClient.call(
					ResourcePath.SalesOrderInfo, id);
		} catch (AxisFault e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}

		if (order_remote == null)
			return null;
		else
			return buildOrderObject(order_remote);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * code.google.magja.service.order.OrderRemoteService#hold(code.google.magja
	 * .model.order.Order)
	 */
	@Override
	public void hold(Order order) throws ServiceException {

		try {
			soapClient.call(ResourcePath.SalesOrderHold, order.getId());
		} catch (AxisFault e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * code.google.magja.service.order.OrderRemoteService#list(code.google.magja
	 * .model.order.OrderFilter)
	 */
	@Override
	public List<Order> list(OrderFilter filter) throws ServiceException {

		List<Order> results = new ArrayList<Order>();

		if (filter != null) {
			if (filter.getItems() != null) {
				if (filter.getItems().isEmpty())
					filter = null;
			} else
				filter = null;
		}

		List<Map<String, Object>> order_list = null;
		try {
			order_list = (List<Map<String, Object>>) soapClient.call(
					ResourcePath.SalesOrderList, (filter != null ? filter
							.serializeToApi() : ""));
		} catch (AxisFault e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}

		for (Map<String, Object> map : order_list)
			results.add(buildOrderObject(map));

		return results;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * code.google.magja.service.order.OrderRemoteService#unhold(code.google
	 * .magja.model.order.Order)
	 */
	@Override
	public void unhold(Order order) throws ServiceException {

		try {
			soapClient.call(ResourcePath.SalesOrderUnhold, order.getId());
		} catch (AxisFault e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}

	}

}
