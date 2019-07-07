/*
 * Copyright (C) 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.alibaba.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.integration.annotation.Filter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

import static org.springframework.cloud.alibaba.demo.ConsumerApplication.DISCARD_NAME;
import static org.springframework.cloud.alibaba.demo.ConsumerApplication.FILTER_NAME;
import static org.springframework.cloud.alibaba.demo.ConsumerApplication.TRANSFORM_NAME;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@Service
public class MessageReceiver {

    private static final Logger logger = LoggerFactory.getLogger(MessageReceiver.class);

    @StreamListener(value = Sink.INPUT, condition = "headers['type']=='user'")
    public void receiveUser(@Payload User user) {
        logger.info("receive by StreamListener, user={}", user);
    }

    @StreamListener(value = Sink.INPUT, condition = "headers['type']=='string'")
    public void receiveString(String str) {
        logger.info("receive by StreamListener, string={}", str);
    }

    @StreamListener(value = Sink.INPUT)
    public void receiveAll(Message message,
                           @Header(value = "type", required = false) String type,
                           @Header(value = "test", required = false) String test) {
        logger.info("receive by StreamListener, payload={}, header[type]={}, header[test]={}",
            message.getPayload(), type, test);
    }

    @ServiceActivator(inputChannel = Sink.INPUT, outputChannel = TRANSFORM_NAME)
    public String receiveNormalMsg(Message message) {
        logger.info("receive by ServiceActivator, payload={}", message.getPayload());
        if (message.getPayload() instanceof byte[]) {
            return "shanghai -> " + new String((byte[])message.getPayload());
        }
        return "shanghai -> " + message.getPayload().toString();
    }

    @Filter(inputChannel = TRANSFORM_NAME, discardChannel = DISCARD_NAME, outputChannel = FILTER_NAME)
    public boolean receiveTransformMsg(String transformMsg) {
        logger.info("receiveTransformMsg: " + transformMsg);
        return transformMsg.toLowerCase().contains("rocketmq");
    }

    @ServiceActivator(inputChannel = FILTER_NAME)
    public void receiveFilterMsg(String filterMsg) {
        logger.info("filterMsg: " + filterMsg);
    }

    @ServiceActivator(inputChannel = DISCARD_NAME)
    public void receiveDiscardMsg(String discardMsg) {
        logger.info("discardMsg: " + discardMsg);
    }

}
