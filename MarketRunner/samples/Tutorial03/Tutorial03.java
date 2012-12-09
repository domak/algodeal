/**
 * Applies a counter-trend strategy, based on a channel. Uses two indicators: a
 * Simple Moving Average and an Average True Range, which is a measure of
 * volatility and market noise.
 */
package com.marketrunner.strategies.tutorial;

import static com.algodeal.marketData.instruments.Futures.*;
import static java.awt.Color.*;
import org.joda.time.*;
import com.algodeal.marketData.prices.*;
import com.algodeal.marketrunner.indicators.*;
import com.algodeal.marketrunner.orders.Order;
import com.algodeal.marketrunner.positions.Position;
import com.algodeal.marketrunner.recorders.records.IndicatorConfigRecord.DrawMode;
import com.algodeal.marketrunner.strategies.*;

@Instruments(futures = { CAC_40, CRUDE_OIL })
@MarketData(start = "1999/10/01", end = "2010/10/01")
public class Tutorial03 extends AbstractStrategy {
	int channelLag = 9;
	double maxDurationBar = 8;
	double atrMultiplier = 1;
	double stopLossPct = 0.10;
	double targetProfitPct = 0.10;
	double qtyPct = 0.5;
	boolean exitAtMAComeBack = false;
	Order limitSellOrder, stopSellOrder;
	double highLevel, lowLevel;
	int barCount = 0;
	int ocaCount = 1;
	TimeSeries lowestLowSeries, highestHighSeries;
	SMA maSeries;
	ATR atr;

	@Override
	public void onStrategyStart() {
		atr = newIndicator().atr().withLength(channelLag).draw(RED, DrawMode.INDEPENDENT).get();
		lowestLowSeries = newIndicator().timeSeries("lowest").draw(BLUE).get();
		highestHighSeries = newIndicator().timeSeries("highest").draw(BLUE).get();
		maSeries = newIndicator().sma().withLength(channelLag).draw(ORANGE).get();
	}

	@Override
	public void onOpen(OpenPrice open) {
		if (getBars().getCount() >= channelLag) {
			if (!hasPosition()) {
				if (highLevel < open.getPrice()) {
					sell(calculateQty(), "Entry");
				} else if (lowLevel > open.getPrice()) {
					buy(calculateQty(), "Entry");
				}
			} else {
				barCount++;
				closePositionOnFriday(open.getDate());
				closePositionIfMaximumDurationIsReached();
				closePositionIfSMACrossed(maSeries, getBars().ago(0));
			}
			double movingAverage = maSeries.getValue();
			double atrValue = atr.getValue();

			highLevel = movingAverage + (atrMultiplier * atrValue);
			lowLevel = movingAverage - (atrMultiplier * atrValue);

			highestHighSeries.add(open.getDate(), highLevel);
			lowestLowSeries.add(open.getDate(), lowLevel);
		}
	}

	@Override
	public void onPositionOpened(Position newPosition) {
		double entryPrice = getPosition().getUnitaryEntryPrice();
		long quantity = getPosition().getQuantity();

		barCount = 0;

		cancelOrderIfNecessary(limitSellOrder);
		cancelOrderIfNecessary(stopSellOrder);

		if (getPosition().isLong()) {
			double targetProfitExitPrice = entryPrice * (1 + targetProfitPct);
			limitSellOrder = sellLimit(quantity, targetProfitExitPrice, "Exit @ Limit");

			double stopLossExitPrice = (entryPrice * (1 - stopLossPct));
			stopSellOrder = sellStop(quantity, stopLossExitPrice, "Exit @ Stop");
		} else {
			double targetProfitExitPrice = (entryPrice * (1 - targetProfitPct));
			limitSellOrder = buyLimit(quantity, targetProfitExitPrice, "Exit @ Limit");

			double stopLossExitPrice = (entryPrice * (1 + stopLossPct));
			stopSellOrder = buyStop(quantity, stopLossExitPrice, "Exit @ Stop");
		}

		limitSellOrder.setOCAGroup(getInstrument().getSymbol() + " OCA " + ocaCount);
		stopSellOrder.setOCAGroup(getInstrument().getSymbol() + " OCA " + ocaCount);
		ocaCount++;
	}

	// Close position if we exceeded the trade's maximum duration
	void closePositionIfMaximumDurationIsReached() {
		if (hasPosition() && (maxDurationBar > 0) && (barCount > maxDurationBar)) {
			closePosition("Max Duration");
		}
	}

	// Close position if we crossed back the moving average
	void closePositionIfSMACrossed(SMA sma, Bar bar) {
		if (exitAtMAComeBack) {
			if (getPosition().isLong() && sma.crossesBelow(sma, bar)) {
				closePosition("Exit SMA cross back");
			} else if (getPosition().isShort() && sma.crossesAbove(sma, bar)) {
				closePosition("Exit SMA cross back");
			}
		}
	}

	// Close position on friday
	void closePositionOnFriday(DateTime barDate) {
		if (barDate.getDayOfWeek() == DateTimeConstants.FRIDAY) {
			closePosition("Close before Weekend");
		}
	}

	int calculateQty() {
		double prevClose = getBars().ago(1).getClose();
		return (int) (1000000 * qtyPct / (getInstrument().getFactor() * prevClose));
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
