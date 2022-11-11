package com.course.rabbitconsumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.course.model.CourseStatus;
import com.course.util.RabbitConstant;

@Component
public class MessageConsumer {

    @RabbitListener(queues = RabbitConstant.QUEUE)
    public void consumeMessageFromQueue(CourseStatus courseStatus) {
        System.out.println("Message recieved from queue : " + courseStatus);
    }
}
