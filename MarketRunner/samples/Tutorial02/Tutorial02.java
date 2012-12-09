/**
 * Follows trends, using a Channel Breakout.
 */
package com.marketrunner.strategies.tutorial;

import static com.algodeal.marketData.instruments.Futures.*;
import static java.awt.Color.*;
import com.algodeal.marketData.prices.OpenPrice;
import com.algodeal.marketrunner.indicators.TimeSeries;
import com.algodeal.marketrunner.orders.Order;
import com.algodeal.marketrunner.positions.Position;
import com.algodeal.marketrunner.strategies.*;

@Instruments(futures = { CAC_40, CRUDE_OIL })
@MarketData(start = "1999/10/01", end = "2010/10/01")
public class Tutorial02 extends AbstractStrategy {
	int channelLag = 30;
	double limitSellExitTargetProfitPct = 0.1;
	double stopSellExitTargetProfitPct = 0.04;
	int maxDurationBar = 15;
	double qtyPct = 0.5;
	int barCount, ocaCount;
	double highestHigh, lowestLow;
	Order stopSellOrder, limitSellOrder;
	TimeSeries lowestLowSeries, highestHighSeries;

	@Override
	public void onStrategyStart() {
		lowestLowSeries = newIndicator().timeSeries("lowest").draw(RED).get();
		highestHighSeries = newIndicator().timeSeries("highest").draw(BLUE).get();
	}

	@Override
	public void onOpen(OpenPrice open) {
		if (getBars().getCount() >= channelLag) {
			if (hasPosition()) {
				if (++barCount > maxDurationBar) {
					closePosition("Max duration reached");
				}
			} else if (highestHigh < open.getPrice()) {
				buy(calculateQty(), "Entry");
			} else if (lowestLow > open.getPrice()) {
				sell(calculateQty(), "Entry");
			}

			highestHigh = getBars().getHighestHigh(channelLag);
			highestHighSeries.add(open.getDate(), highestHigh);

			lowestLow = getBars().getLowestLow(channelLag);
			lowestLowSeries.add(open.getDate(), lowestLow);
		}
	}

	long calculateQty() {
		double prevClose = getBars().ago(1).getClose();
		return Math.round(1000000 * qtyPct / (prevClose * getInstrument().getFactor()));
	}

	@Override
	public void onPositionOpened(Position newPosition) {
		double entryPrice = getPosition().getUnitaryEntryPrice();
		long quantity = getPosition().getQuantity();

		barCount = 0;

		cancelOrderIfNecessary(stopSellOrder);
		cancelOrderIfNecessary(limitSellOrder);

		if (getPosition().isLong()) {
			double targetProfitExitPrice = entryPrice * (1 + limitSellExitTargetProfitPct);
			limitSellOrder = sellLimit(quantity, targetProfitExitPrice, "Exit @ Limit");

			double stopLossExitPrice = entryPrice * (1 - stopSellExitTargetProfitPct);
			stopSellOrder = sellStop(quantity, stopLossExitPrice, "Exit @ Stop");
		} else {
			double targetProfitExitPrice = entryPrice * (1 - limitSellExitTargetProfitPct);
			limitSellOrder = buyLimit(quantity, targetProfitExitPrice, "Exit @ Limit");

			double stopLossExitPrice = entryPrice * (1 + stopSellExitTargetProfitPct);
			stopSellOrder = buyStop(quantity, stopLossExitPrice, "Exit @ Stop");
		}

		limitSellOrder.setOCAGroup(getInstrument().getSymbol() + " OCA " + ocaCount);
		stopSellOrder.setOCAGroup(getInstrument().getSymbol() + " OCA " + ocaCount);
		ocaCount++;
	}

	@Override
	public void onPositionClosed(Position previousPosition) {
		cancelOrderIfNecessary(stopSellOrder);
		cancelOrderIfNecessary(limitSellOrder);
	}

	void cancelOrderIfNecessary(Order order) {
		if ((order != null) && !order.isCancelled() && !order.isFilled()) {
			cancelOrder(order);
		}
	}
}
