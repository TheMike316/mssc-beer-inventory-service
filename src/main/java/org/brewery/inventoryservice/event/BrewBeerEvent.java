package org.brewery.inventoryservice.event;

import lombok.NoArgsConstructor;
import org.brewery.common.model.BeerDto;

@NoArgsConstructor
public class BrewBeerEvent extends BeerEvent {

    public BrewBeerEvent(BeerDto beerDto) {
        super(beerDto);
    }
}
