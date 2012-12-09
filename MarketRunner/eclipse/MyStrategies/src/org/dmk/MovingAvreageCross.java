package org.dmk;

import static com.algodeal.marketData.instruments.Futures.CAC_40;
import static java.awt.Color.BLUE;
import static java.awt.Color.RED;

import com.algodeal.marketData.prices.OpenPrice;
import com.algodeal.marketrunner.indicators.BarSeries;
import com.algodeal.marketrunner.indicators.EMA;
import com.algodeal.marketrunner.indicators.SMA;
import com.algodeal.marketrunner.strategies.AbstractStrategy;
import com.algodeal.marketrunner.strategies.Instruments;
import com.algodeal.marketrunner.strategies.Optimized;

@Instruments(futures = { CAC_40 })
public class MovingAvreageCross extends AbstractStrategy {

    @Optimized(from = 1, to = 20, step = 1)
    int smaLenght = 10;

    @Optimized(from = 1, to = 20, step = 1)
    int emaLenght = 4;

    private SMA sma;
    private EMA ema;

    @Override
    public void onStrategyStart() {
	sma = newIndicator().sma().withLength(smaLenght).draw(BLUE).get();
	ema = newIndicator().ema().withLength(emaLenght).draw(RED).get();
    }

    @Override
    public void onOpen(OpenPrice openPrice) {
	BarSeries bars = getBars();
	if (bars.getCount() < smaLenght + 1) {
	    return;
	}

	if (ema.getValue() > sma.getValue()) {
	    if (!hasPosition()) {
		buy(1);
	    }
	} else {
	    closePosition(null);
	}
    }
}
