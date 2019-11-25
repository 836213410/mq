package cn.rt.test;

import java.util.UUID;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cn.rt.mqsend.RabbitOrderSender;
import cn.rt.service.OrderService;
import cn.rt.vo.Order;

@SpringBootTest
@RunWith(SpringRunner.class)
public class Test {

	@Autowired
	private RabbitOrderSender rabbitOrderSender;

	@Autowired
	private OrderService orderService;

	@org.junit.Test
	public void test() throws Exception {
		Order order = new Order();
		order.setId("2018080400000001");
		order.setName("测试订单");
		order.setMessageId(System.currentTimeMillis() + "$" + UUID.randomUUID().toString());
		rabbitOrderSender.sendOrder(order);
	}

	@org.junit.Test
	public void testCreateOrder() throws Exception {
		Order order = new Order();
		order.setId("2018080400000004");
		order.setName("测试创建订单");
		order.setMessageId(System.currentTimeMillis() + "$" + UUID.randomUUID().toString());
		orderService.createOrder(order);
	}

}
