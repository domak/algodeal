/**
 * Slow Turtle strategy that demonstrates implementation of trailing stops.
 */
package com.marketrunner.strategies.advanced;

import static com.algodeal.marketData.instruments.Futures.*;
import static java.awt.Color.*;
import com.algodeal.marketData.prices.*;
import com.algodeal.marketrunner.indicators.SMA;
import com.algodeal.marketrunner.orders.StopOrder;
import com.algodeal.marketrunner.strategies.*;

@Instruments(futures = { CAC_40, E_MINI_SP })
public class TrailingStopSlowTurtle extends AbstractStrategy {
	int slowSmaLength = 210;
	int fastSmaLength = 70;
	double lossLimit = 0.96;

	SMA fastSMA;
	SMA slowSMA;
	StopOrder trailingStop;

	@Override
	public void onStrategyStart() {
		fastSMA = newIndicator().sma().withLength(fastSmaLength).draw(RED).get();
		slowSMA = newIndicator().sma().withLength(slowSmaLength).draw(MAGENTA).get();
	}

	@Override
	public void onClose(Bar bar) {
		if (fastSMA.crossesAbove(slowSMA)) {
			buy(1);
			double stopLevel = bar.getClose() * lossLimit;
			trailingStop = sellStop(1, stopLevel, "Trailing stop @" + stopLevel);
		}
	}

	@Override
	public void onOpen(OpenPrice openPrice) {
		double newStopLevel = openPrice.getPrice() * lossLimit;
		trailingStop = upgradeOrder(trailingStop, newStopLevel);
	}

	private StopOrder upgradeOrder(StopOrder order, double stopLevel) {
		if ((order == null) || (!order.isPending() && !order.isInMarket()) || (order.getStopLevel() >= stopLevel)) {
			return order; // Nothing to upgrade
		}
		cancelOrder(order);

		return sellStop(order.getQuantity(), stopLevel, "Upgrading trailing stop to " + stopLevel);
	}
}
