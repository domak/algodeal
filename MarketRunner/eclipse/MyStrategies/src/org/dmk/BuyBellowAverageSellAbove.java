package org.dmk;

import static com.algodeal.marketData.instruments.Futures.CAC_40;
import static java.awt.Color.BLUE;
import static java.awt.Color.RED;

import java.awt.Color;

import com.algodeal.marketData.prices.OpenPrice;
import com.algodeal.marketrunner.indicators.BarSeries;
import com.algodeal.marketrunner.indicators.EMA;
import com.algodeal.marketrunner.indicators.Indicator;
import com.algodeal.marketrunner.indicators.SMA;
import com.algodeal.marketrunner.strategies.AbstractStrategy;
import com.algodeal.marketrunner.strategies.Instruments;
import com.algodeal.marketrunner.strategies.Optimized;

@Instruments(futures = { CAC_40 })
public class BuyBellowAverageSellAbove extends AbstractStrategy {

    @Optimized(from = 1, to = 20, step = 1)
    int smaLenght = 10;

    @Optimized(booleans = { true, false })
    boolean smaFlag;

    private SMA sma;
    private EMA ema;
    private Indicator average;

    @Override
    public void onStrategyStart() {
	sma = newIndicator().sma().withLength(smaLenght).draw(BLUE).get();
	ema = newIndicator().ema().withLength(smaLenght).draw(RED).get();
	newIndicator().adx().withLength(smaLenght).draw(Color.YELLOW).get();
	average = smaFlag ? sma : ema;
    }

    @Override
    public void onOpen(OpenPrice openPrice) {
	BarSeries bars = getBars();
	if (bars.getCount() < smaLenght + 1) {
	    return;
	}

	if (openPrice.getPrice() < average.ago(0)) {
	    buy(1);
	} else {
	    sell(1);
	}
    }
}
