package org.brewery.inventoryservice.service;

import org.brewery.common.model.BeerOrderDto;

public interface AllocationService {

    boolean allocateOrder(BeerOrderDto beerOrderDto);
}
