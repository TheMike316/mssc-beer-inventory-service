package org.brewery.inventoryservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brewery.common.model.BeerOrderDto;
import org.brewery.common.model.BeerOrderLineDto;
import org.brewery.inventoryservice.repositories.BeerInventoryRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class AllocationServiceImpl implements AllocationService {

    private final BeerInventoryRepository repository;

    @Override
    public boolean allocateOrder(BeerOrderDto beerOrderDto) {
        log.info("Allocating inventory for order {}", beerOrderDto.getId());

        var totalOrdered = new AtomicInteger();
        var totalAllocated = new AtomicInteger();

        beerOrderDto.getBeerOrderLines().forEach(l -> {
            var orderQuantity = Objects.requireNonNullElse(l.getOrderQuantity(), 0);
            var allocatedQuantity = Objects.requireNonNullElse(l.getAllocatedQuantity(), 0);

            if (orderQuantity - allocatedQuantity > 0) {
                allocateBeerOrderLine(l);
            }
            totalOrdered.set(totalOrdered.get() + l.getOrderQuantity());
            totalAllocated.set(totalAllocated.get() + l.getAllocatedQuantity());
        });

        log.debug("Total ordered: {}; Total allocated: {}.", totalOrdered, totalAllocated);

        return totalOrdered.get() == totalAllocated.get();
    }

    private void allocateBeerOrderLine(BeerOrderLineDto beerOrderLineDto) {
        var inventories = repository.findAllByUpc(beerOrderLineDto.getUpc());

        inventories.forEach(beerInventory -> {
            int inventory = Objects.requireNonNullElse(beerInventory.getQuantityOnHand(), 0);
            int orderQty = beerOrderLineDto.getOrderQuantity();
            int allocatedQty = beerOrderLineDto.getAllocatedQuantity();
            int qtyToAllocate = orderQty - allocatedQty;

            if (inventory >= qtyToAllocate) { //full allocation
                beerOrderLineDto.setAllocatedQuantity(orderQty);

                beerInventory.setQuantityOnHand(inventory - orderQty);
                repository.save(beerInventory);
            } else if (inventory > 0) { // partial allocation
                beerOrderLineDto.setAllocatedQuantity(allocatedQty + inventory);
                beerInventory.setQuantityOnHand(0);
                repository.delete(beerInventory);
            }
        });

    }
}
