package guru.sfg.beer.inventory.service.service;

import guru.sfg.beer.inventory.service.config.JmsConfig;
import guru.sfg.beer.inventory.service.domain.BeerInventory;
import guru.sfg.beer.inventory.service.event.NewInventoryEvent;
import guru.sfg.beer.inventory.service.repositories.BeerInventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryListener {

    private final BeerInventoryRepository repository;

    @Transactional
    @JmsListener(destination = JmsConfig.NEW_INVENTORY_QUEUE)
    public void listen(NewInventoryEvent event) {

        var beer = event.getBeerDto();
        log.info("Received new inventory event for beer [{}]", beer.getId());

        repository.save(BeerInventory.builder()
                .beerId(beer.getId())
                .upc(beer.getUpc())
                .quantityOnHand(beer.getQuantityOnHand())
                .build());
        
        log.info("Updated inventory for beer [{}]", beer.getId());
    }
}
