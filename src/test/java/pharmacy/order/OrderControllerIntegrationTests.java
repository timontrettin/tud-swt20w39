package pharmacy.order;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.salespointframework.order.Cart;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManagement;
import org.salespointframework.payment.Cash;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import pharmacy.AbstractIntegrationTests;
import pharmacy.catalog.Medicine;
import pharmacy.catalog.MedicineCatalog;
import pharmacy.order.OrderFilter.Filter;
import pharmacy.user.UserManagement;

class OrderControllerIntegrationTests extends AbstractIntegrationTests {

	@Autowired
	OrderController controller;

	@Autowired
	private MedicineCatalog catalog;

	@Autowired
	private OrderManagement<Order> orderManagement;

	@Autowired
	private UserAccountManagement userManagement;

	@Test
	@SuppressWarnings("unchecked")
	public void controllerIntegrationTest() {

		Model model = new ExtendedModelMap();

		String res = controller.basket(new Cart(), model);

		assertThat(res).isEqualTo("cart");
		// -------getCart()
		Cart c = this.controller.getCart();

		assertEquals(c.getClass().getSimpleName(), "Cart");
		assertEquals(c.get().count(), 0);
		// -------emptyCart()
		res = this.controller.emptycart(c);
		assertEquals(res, "redirect:/cart");
		assertTrue(c.isEmpty());
		// -------addItem()
		Medicine m = this.catalog.findAll().stream().findFirst().get();

		res = this.controller.addItem(m, 2, c);
		assertEquals(res, "redirect:/");
		assertEquals(c.stream().findFirst().get().getQuantity().getAmount().intValue(), 2);
		// -------updateItem()
		String id = c.stream().findFirst().get().getId();
		res = this.controller.updateItem(id, 3, c);
		assertEquals(res, "redirect:/cart");
		assertEquals(c.stream().findFirst().get().getQuantity().getAmount().intValue(), 3);

		// -------basket() & getAvailability()
		res = this.controller.basket(c, model);
		assertEquals(res, "cart");
		Map<String, Integer> map = (Map<String, Integer>) model.asMap().get("availability");
		assertEquals(map.get(id), 2);
		// -------haspresonly()
		assertFalse(this.controller.haspresonly(c));
		// -------basket2()
		res = this.controller.basket2();
		assertEquals(res, "redirect:/cart");
		// -------deleteItem()
		res = this.controller.deleteItem(id, c, model);
		assertTrue(c.isEmpty());
		map = (Map<String, Integer>) model.asMap().get("availability");
		assertEquals(map, Collections.emptyMap());

		// -------buy()
		// -------add-/checkout() / -Address
	}

	@Test
	void theNonBuyPart() {

		Optional<UserAccount> apo = this.userManagement.findByUsername("apo");
		Optional<UserAccount> hans = this.userManagement.findByUsername("hans");
		Optional<UserAccount> hansi = this.userManagement.findByUsername("hansi");

		Order boss, empl, cust;

		boss = new Order(apo.get());//will be completed
		empl = new Order(hans.get());//will be open
		cust = new Order(hansi.get());//will be paid
		
		boss.setPaymentMethod(Cash.CASH);
		boss.addChargeLine(Money.of(10, "EUR"), "Example");
		cust.setPaymentMethod(Cash.CASH);
		cust.addChargeLine(Money.of(11, "EUR"), "Example 2");

		this.orderManagement.save(boss);
		this.orderManagement.save(empl);
		this.orderManagement.save(cust);
		this.orderManagement.payOrder(cust);
		this.orderManagement.payOrder(boss);
		this.orderManagement.completeOrder(boss);
		
// -------orders()
		Model model = new ExtendedModelMap();
		
		assertTrue(apo.isPresent());
		String res = this.controller.orders(model, apo);
		assertEquals(res, "orders");
		List<Order> list = (List<Order>) model.asMap().get("rech");
		assertEquals(list.size(), 3);
		this.controller.orders(model, hans);
		list = (List<Order>) model.asMap().get("rech");
		assertEquals(list.size(), 1);
		this.controller.orders(model, hansi);
		list = (List<Order>) model.asMap().get("rech");
		assertEquals(list.size(), 1);
		this.controller.orders(model, Optional.empty());
		list = (List<Order>) model.asMap().get("rech");
		assertEquals(list.size(), 0);
//-------postorders()
		OrderFilter f = new OrderFilter();
		f.setFilter(Filter.ALLE);
		assertTrue(apo.isPresent());
		res = this.controller.postorders(f,model, apo);
		assertEquals(res, "orders");
		list = (List<Order>) model.asMap().get("rech");
		assertEquals(list.size(), 3);

		f.setFilter(Filter.BEZAHLT);
		this.controller.postorders(f,model, apo);
		list = (List<Order>) model.asMap().get("rech");
		assertEquals(list.size(), 1);

		f.setFilter(Filter.COMPLETED);
		this.controller.postorders(f,model, apo);
		list = (List<Order>) model.asMap().get("rech");
		assertEquals(list.size(), 1);

		f.setFilter(Filter.OFFEN);
		this.controller.postorders(f,model, apo);
		list = (List<Order>) model.asMap().get("rech");
		assertEquals(list.size(), 1);

		this.controller.postorders(f,model, hans);
		list = (List<Order>) model.asMap().get("rech");
		assertEquals(list.size(), 1);
		this.controller.postorders(f,model, hansi);
		list = (List<Order>) model.asMap().get("rech");
		assertEquals(list.size(), 1);
		this.controller.postorders(f, model, Optional.empty());
		list = (List<Order>) model.asMap().get("rech");
		assertEquals(list.size(), 0);
//-------detail()
		res = this.controller.detail(boss.getId(), model, Optional.empty());
		assertEquals(res, "orders");
		res = this.controller.detail(boss.getId(), model, hans);
		assertEquals(res, "orderdetails");
		assertEquals(model.asMap().get("det"), boss);
//-------orderfail()
		res = this.controller.orderfail(model);
		assertEquals(res, "ordercompletionfail" );
		assertEquals(model.asMap().get("order"), null);
//-------cancel()
		res = this.controller.cancel(empl.getId(), model);
		assertEquals(res, "redirect:/orders");
		assertTrue(this.orderManagement.contains(empl.getId()));
		assertTrue(this.orderManagement.get(empl.getId()).get().isCanceled());
//-------complete()
		res = this.controller.complete(cust.getId(), model);
		assertEquals(res, "redirect:/orders");
		assertTrue(this.orderManagement.contains(cust.getId()));
		assertTrue(this.orderManagement.get(cust.getId()).get().isCompleted());
//-------myorders()
		res = this.controller.myorders(model, hans);
		list = (List<Order>) model.asMap().get("rech");
		assertEquals(list.size(), 1);

	}

}
