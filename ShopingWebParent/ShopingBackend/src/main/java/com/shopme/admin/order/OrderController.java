package com.shopme.admin.order;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopme.admin.paginig.PagingAndSortingHelper;
import com.shopme.admin.paginig.PagingAndSortingParam;
import com.shopme.admin.setting.SettingService;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Setting;
import com.shopme.common.entity.order.Order;

@Controller
public class OrderController {

	private String defaultRedirectURL = "redirect:/orders/page/1?sortField=orderTime&sortDir=desc";

	@Autowired
	private OrderService orderService;

	@Autowired
	private SettingService settingService;

	@GetMapping("/orders")
	public String listFirstPage() {
		return defaultRedirectURL;
	}

	@GetMapping("/orders/page/{pageNum}")
	public String listByPage(
			@PagingAndSortingParam(listName = "listOrders", moduleURL = "/orders") PagingAndSortingHelper helper,
			@PathVariable(name = "pageNum") int pageNum, HttpServletRequest request)
			throws StreamWriteException, DatabindException, IOException {

		Page orders = orderService.listByPage(pageNum, helper);
		// debug
		List<Order> order = orders.getContent();

		ObjectMapper mapper = new ObjectMapper();

//		List<OrderResponse> responses = new ArrayList<>();

//		for (Order odr : order) {
//			OrderResponse response = new OrderResponse();
//			response.setId(odr.getId());
//			response.setFirstName(odr.getFirstName());
//			response.setLastName(odr.getLastName());
//			response.setAddress1(odr.getAddress1());
//			response.setAddress2(odr.getAddress2());
//			response.setCity(odr.getCity());
//			response.setPhoneNumber(odr.getPhoneNumber());
//			response.setState(odr.getState());
//			responses.add(response);
//		}
		JsonNode jsonNode = mapper.valueToTree(order.stream().map(OrderResponse::new).collect(Collectors.toList()));
//		System.out.println(jsonNode);
		System.out.println(jsonNode.toPrettyString());

//		mapper.writeValue(new File("target/order.json"), responses);
//		System.out.print(order);
		loadCurrencySetting(request);
		return "orders/orders";
	}


	@GetMapping("/orders/edit/{id}")
	public String editOrder(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes rediect,
			HttpServletRequest request) {

		try {
			Order order = orderService.get(id);
			List<Country> listCountries = orderService.listCountries();
			model.addAttribute("pageTitle", "Edit Order(ID:" + id + ")");
			model.addAttribute("order", order);
			model.addAttribute("listCountries", listCountries);
			return "orders/order_form";
		} catch (OrderNotFoundException ex) {
			rediect.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;
		}
	}

	@GetMapping("/orders/view/{id}")
	public String viewOrderDetails(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirect,
			HttpServletRequest request) {

		try {
			Order order = orderService.get(id);
			loadCurrencySetting(request);
			model.addAttribute("order", order);
			return "orders/order_details_modal";

		} catch (OrderNotFoundException ex) {
			redirect.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;
		}
	}

	@GetMapping("/orders/delete/{id}")
	public String orderDelete(@PathVariable(name = "id") Integer id, Model redirectAttributes) {

		try {
			orderService.deleteOrder(id);
		} catch (OrderNotFoundException ex) {
			redirectAttributes.addAttribute("message", ex.getMessage());
		}
		return defaultRedirectURL;

	}

	private void loadCurrencySetting(HttpServletRequest request) {

		List<Setting> currencySetting = settingService.getCurrencySetting();

		for (Setting setting : currencySetting) {
			request.setAttribute(setting.getKey(), setting.getValue());
		}
	}

	public String jar() {

		return "I'm jar file";
	}

}
