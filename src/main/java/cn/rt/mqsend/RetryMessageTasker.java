package cn.rt.mqsend;

import java.util.Date;
import java.util.List;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import cn.rt.constant.Constants;
import cn.rt.mapper.BrokerMessageLogMapper;
import cn.rt.vo.BrokerMessageLog;
import cn.rt.vo.Order;
/**
 * 消息重试、最大努力尝试策略（定时任务）
 *Description: <br/>
 *Create info: hongyang.zhao, 2019年11月11日 <br/>
 *Copyright (c) 2019, RunTong Information Technology Co.,Ltd. All Rights Reserved. <br/>
 *@author hongyang.zhao
 *@Version 1.0
 */
@Component
public class RetryMessageTasker {
 
     
    @Autowired
    private RabbitOrderSender rabbitOrderSender;
     
    @Autowired
    private BrokerMessageLogMapper brokerMessageLogMapper;
     
    @Scheduled(initialDelay = 5000, fixedDelay = 15000)
    public void reSend(){
    	System.out.println("进入重发方法");
        //pull status = 0 and timeout message 
        List<BrokerMessageLog> list = brokerMessageLogMapper.query4StatusAndTimeoutMessage();
        list.forEach(messageLog -> {
            if(messageLog.getTryCount() >= 3){
            	System.out.println("进入重发方法--大于3次重试");
                //update fail message 
                brokerMessageLogMapper.changeBrokerMessageLogStatus(messageLog.getMessageId(), Constants.ORDER_SEND_FAILURE, new Date());
            } else {
                // resend 
            	System.out.println("进入重发方法--3次一下重试重试");
            	System.out.println("message--"+messageLog.getMessage());
                brokerMessageLogMapper.update4ReSend(messageLog.getMessageId(),  new Date());
                Order reSendOrder = JSONObject.parseObject(messageLog.getMessage(), Order.class);
                System.out.println("order--"+reSendOrder.toString());
                System.out.println("rabbitOrderSender--"+rabbitOrderSender);
                try {
                    rabbitOrderSender.sendOrder(reSendOrder);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("-----------异常处理-----------");
                }
            }            
        });
    }
    
	@Bean(name = "orderQueue")
	public Queue logUserQueue() {
		return new Queue("orderQueue", true);
	}

	@Bean
	public DirectExchange logUserExchange() {
		return new DirectExchange("order-exchange", true, false);
	}

	@Bean
	public Binding logUserBinding() {
		return BindingBuilder.bind(logUserQueue()).to(logUserExchange()).with("order.ABC");
	}
}
