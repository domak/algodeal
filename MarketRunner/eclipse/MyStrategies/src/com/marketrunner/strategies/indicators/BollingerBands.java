/**
 * Buys at the lower Bollinger limit and sell when the price goes up to the SMA.
 */
package com.marketrunner.strategies.indicators;

import static com.algodeal.marketData.instruments.Futures.CAC_40;
import static java.awt.Color.BLUE;
import static java.awt.Color.RED;
import static java.awt.Color.YELLOW;

import com.algodeal.marketData.prices.Bar;
import com.algodeal.marketrunner.indicators.BollingerBand;
import com.algodeal.marketrunner.indicators.SMA;
import com.algodeal.marketrunner.orders.Order;
import com.algodeal.marketrunner.positions.Position;
import com.algodeal.marketrunner.strategies.AbstractStrategy;
import com.algodeal.marketrunner.strategies.Instruments;

@Instruments(futures = { CAC_40 })
public class BollingerBands extends AbstractStrategy {
    Order buyOrder, sellOrder;
    SMA sma;
    BollingerBand bbl;
    BollingerBand bbu;

    @Override
    public void onStrategyStart() {
	sma = newIndicator().sma().withLength(3).draw(YELLOW).get();
	bbl = newIndicator().bollingerLow().withLength(3).withFactor(2).draw(BLUE).get();
	bbu = newIndicator().bollingerUp().withLength(3).withFactor(2).draw(RED).get();
    }

    @Override
    public void onClose(Bar bar) {
	if (!hasPosition()) {
	    updateEntryLimit();
	} else {
	    updateExitLimit();
	}
    }

    @Override
    public void onPositionOpened(Position newPosition) {
	updateExitLimit();
    }

    private void updateEntryLimit() {
	if (buyOrder != null) {
	    if (!buyOrder.isFilled()) {
		cancelOrder(buyOrder);
	    }
	}

	buyOrder = buyLimit(1, bbl.getValue(), "Entry");
    }

    private void updateExitLimit() {
	if (sellOrder != null) {
	    if (!sellOrder.isFilled()) {
		cancelOrder(sellOrder);
	    }
	}
	sellOrder = sellLimit(1, sma.getValue(), "Exit");
    }
}