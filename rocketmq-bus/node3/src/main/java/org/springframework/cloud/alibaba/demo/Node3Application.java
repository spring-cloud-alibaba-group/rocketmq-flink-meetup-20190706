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

import io.micrometer.core.instrument.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.alibaba.GenericEvent;
import org.springframework.cloud.alibaba.RemoteEvent;
import org.springframework.cloud.bus.jackson.RemoteApplicationEventScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@SpringBootApplication
@RemoteApplicationEventScan(basePackages = "org.springframework.cloud.alibaba")
public class Node3Application {

    private static final Logger logger = LoggerFactory.getLogger(Node3Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Node3Application.class, args);
    }

    @Value("${spring.cloud.bus.id}")
    private String busId;

    @EventListener(classes = RemoteEvent.class)
    public void receiveRemoteEvent(RemoteEvent remoteEvent) {
        logger.info("{} get remoteEvent: {}", busId, remoteEvent.getEchoMessage());
    }

    @EventListener(classes = GenericEvent.class)
    public void receiveGenericEvent(GenericEvent genericEvent) {
        logger.info("{} get genericEvent: {}", busId, genericEvent.getEchoMessage());
    }

    @RestController
    class EventController {

        @Autowired
        private ApplicationContext applicationContext;

        @GetMapping("/genericEvent")
        public String genericEvent(@RequestParam String msg) {
            GenericEvent event = new GenericEvent(this, msg);
            this.applicationContext.publishEvent(event);
            return "publish genericEvent success";
        }

        @GetMapping("/remoteEvent")
        public String remoteEvent(@RequestParam String msg, @RequestParam(required = false) String dest) {
            RemoteEvent event = new RemoteEvent(this, null, null, msg);
            if (StringUtils.isEmpty(dest)) {
                event = new RemoteEvent(this, busId, null, msg);
            } else {
                event = new RemoteEvent(this, busId, dest, msg);
            }
            this.applicationContext.publishEvent(event);
            return "publish remoteEvent success";
        }
    }

}
