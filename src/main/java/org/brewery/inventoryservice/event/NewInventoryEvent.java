package org.brewery.inventoryservice.event;

import lombok.NoArgsConstructor;
import org.brewery.common.model.BeerDto;

@NoArgsConstructor
public class NewInventoryEvent extends BeerEvent {

    public NewInventoryEvent(BeerDto beerDto) {
        super(beerDto);
    }
}
