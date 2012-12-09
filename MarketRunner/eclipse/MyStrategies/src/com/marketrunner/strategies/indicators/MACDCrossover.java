/**
 * Implements a strategy using MACD. If the market is bullish: buy if the macd
 * crosses above the signal line and the signal line is increasing (8 ups during
 * the last 10 days). Keep buying while macd is above and signal line is
 * increasing. Close position when macd crosses below the signal line.
 */

package com.marketrunner.strategies.indicators;

import static com.algodeal.marketData.instruments.Futures.CAC_40;
import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static java.awt.Color.RED;

import com.algodeal.marketData.prices.Bar;
import com.algodeal.marketrunner.indicators.EMA;
import com.algodeal.marketrunner.indicators.MACD;
import com.algodeal.marketrunner.indicators.MACDSignalLine;
import com.algodeal.marketrunner.strategies.AbstractStrategy;
import com.algodeal.marketrunner.strategies.Instruments;

@Instruments(futures = { CAC_40 })
public class MACDCrossover extends AbstractStrategy {
    boolean mayBuy, maySell;
    EMA ema;
    MACD macd;
    MACDSignalLine sl;

    @Override
    public void onStrategyStart() {
	ema = newIndicator().ema().withLength(130).draw(RED).get();
	macd = newIndicator().macd().withFastLenth(14).withSlowLenth(32).drawFastAndSlowEMA(BLUE, GREEN).get();
	sl = newIndicator().macdSignalLine().withSignalLenth(9).withMACD(macd).get();
    }

    @Override
    public void onClose(Bar bar) {
	if (isMarketBullish()) {
	    if (macd.crossesAbove(sl)) {
		mayBuy = true;
	    } else if (macd.crossesBelow(sl)) {
		closePosition("Bullish and macd crosses below");
		mayBuy = false;
	    }

	    if (mayBuy && isSlSlowlyIncreasing()) {
		buy(1, "Bullish and slowly increasing");
	    }
	} else if (isMarketBearish()) {
	    if (macd.crossesBelow(sl)) {
		sell(1, "Bearish and macd crosses below");
		maySell = true;
	    } else if (macd.crossesAbove(sl)) {
		closePosition("Bearish and macd crosses above");
		maySell = false;
	    }

	    if (maySell && isSlSlowlyDecreasing()) {
		sell(1, "Bearish and slowly decreasing");
		maySell = false;
	    }
	}
    }

    boolean isMarketBearish() {
	return ema.getValue() > macd.getFastEMA().getValue();
    }

    boolean isMarketBullish() {
	return ema.getValue() < macd.getFastEMA().getValue();
    }

    boolean isSlSlowlyIncreasing() {
	int decreaseCount = 0;
	for (int i = 0; i <= 9; i++) {
	    if (sl.ago(i) < sl.ago(i + 1)) {
		decreaseCount++;
	    }
	}
	return decreaseCount < 3;
    }

    boolean isSlSlowlyDecreasing() {
	int increaseCount = 0;
	for (int i = 0; i <= 9; i++) {
	    if (sl.ago(i) > sl.ago(i + 1)) {
		increaseCount++;
	    }
	}
	return increaseCount < 3;
    }
}
