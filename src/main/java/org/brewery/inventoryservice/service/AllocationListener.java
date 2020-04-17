package org.brewery.inventoryservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brewery.common.model.event.AllocateBeerOrderRequest;
import org.brewery.common.model.event.AllocateBeerOrderResponse;
import org.brewery.inventoryservice.config.JmsConfig;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AllocationListener {

    private final AllocationService service;
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_QUEUE)
    public void allocateOrder(AllocateBeerOrderRequest request) {
        var dto = request.getBeerOrderDto();

        log.info("Received allocation request for order {}", dto.getId());

        var pendingInventory = true;
        var error = false;
        try {
            pendingInventory = service.allocateOrder(dto);
            log.info("Allocation request for order {} has been processed successfully", dto.getId());
        } catch (Exception e) {
            log.error("Error when processing allocation request for order {}", dto.getId(), e);
            error = true;
        }

        var response = AllocateBeerOrderResponse.builder()
                .beerOrderDto(dto)
                .allocationError(error)
                .pendingInventory(pendingInventory)
                .build();

        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE, response);
    }
}
