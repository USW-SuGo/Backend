package com.usw.sugo.global.rabbitMQ;

import com.usw.sugo.domain.majorchatting.messaging.dto.ChattingMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class SugoRabbitListener {

    /**
     * RabbitHandler 어노테이션을 메소드에 달아주면, RabbitListener에 명시한 큐의 데이터를 처리할 수 있다.
     * 첫 번째 인자는 들어오는 메세지인데, 현재 LinkedHashMap으로 받아들인다.
     * Jackson 라이브러리가 json 데이터를 오브젝트로 변경해주는데, 이를 표현하는 방식이 LinkedHashMap이다.
     */

    @RabbitListener(queues = "sugo.queue")
    public void receiveMessage(ChattingMessage message) {
        System.out.println(message);
    }

}

