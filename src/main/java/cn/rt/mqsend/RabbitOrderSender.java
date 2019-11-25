package cn.rt.mqsend;

import java.util.Date;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.rt.constant.Constants;
import cn.rt.mapper.BrokerMessageLogMapper;
import cn.rt.vo.Order;

@Component
public class RabbitOrderSender {
 
    //自动注入RabbitTemplate模板类
    @Autowired
    private RabbitTemplate rabbitTemplate;  
     
    @Autowired
    private BrokerMessageLogMapper brokerMessageLogMapper;
     
    //回调函数: confirm确认
    final ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        	System.out.println("进入回调confirm确认函数-----");
        	System.out.println("ack----"+ack);
            System.err.println("correlationData: " + correlationData);
            String messageId = correlationData.getId();
            if(ack){
            	System.out.println("对信息进行成功进行状态更新");
                //如果confirm返回成功 则进行更新
                brokerMessageLogMapper.changeBrokerMessageLogStatus(messageId, Constants.ORDER_SEND_SUCCESS, new Date());
                System.out.println("对信息进行成功进行状态更新完成----------------");
            } else {
                //失败则进行具体的后续操作:重试 或者补偿等手段
                System.err.println("异常处理...");
                System.out.println("原因是：---"+cause);
            }
        }
    };
     
    //发送消息方法调用: 构建自定义对象消息
    public void sendOrder(Order order) throws Exception {
        rabbitTemplate.setConfirmCallback(confirmCallback);
        //消息唯一ID
        CorrelationData correlationData = new CorrelationData(order.getMessageId());
        rabbitTemplate.convertAndSend("order-exchange11", "order.ABC", order, correlationData);
    }
     
}