package guru.sfg.beer.inventory.service.config;

import guru.sfg.beer.inventory.service.event.NewInventoryEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import java.util.HashMap;

@Configuration
public class JmsConfig {

    public static final String NEW_INVENTORY_QUEUE = "new-inventory";

    @Bean
    MessageConverter messageConverter() {
        var converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");

        var typeMap = new HashMap<String, Class<?>>();
        typeMap.put("new-inventory-event", NewInventoryEvent.class);
        converter.setTypeIdMappings(typeMap);

        return converter;
    }
}
