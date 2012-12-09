package org.dmk;

import static com.algodeal.marketData.instruments.Futures.CAC_40;
import static java.awt.Color.BLUE;
import static java.awt.Color.RED;

import com.algodeal.marketData.prices.OpenPrice;
import com.algodeal.marketrunner.indicators.BarSeries;
import com.algodeal.marketrunner.indicators.EMA;
import com.algodeal.marketrunner.indicators.Indicator;
import com.algodeal.marketrunner.indicators.SMA;
import com.algodeal.marketrunner.strategies.AbstractStrategy;
import com.algodeal.marketrunner.strategies.Instruments;
import com.algodeal.marketrunner.strategies.Optimized;

@Instruments(futures = { CAC_40 })
public class BuyBellowAverageClosePosAbove extends AbstractStrategy {

    @Optimized(from = 1, to = 20, step = 1)
    int smaLenght = 4;

    @Optimized(from = 0, to = 20, step = 2)
    int securityStopQty;

    @Optimized(booleans = { true, false })
    boolean smaFlag;

    private SMA sma;
    private EMA ema;
    private Indicator average;

    private int qty;

    @Override
    public void onStrategyStart() {
	sma = newIndicator().sma().withLength(smaLenght).draw(BLUE).get();
	ema = newIndicator().ema().withLength(smaLenght).draw(RED).get();

	average = smaFlag ? sma : ema;
    }

    @Override
    public void onOpen(OpenPrice openPrice) {
	BarSeries bars = getBars();
	if (bars.getCount() < smaLenght + 1) {
	    return;
	}

	// System.out.println("sma: " + sma.ago(0) + " - open: " + openPrice.getPrice());
	if (securityAlert()) {
	    closePosition();
	}
	if (openPrice.getPrice() < average.ago(0)) {
	    buy(1);
	    qty++;
	} else {
	    closePosition();
	}
    }

    private boolean securityAlert() {
	if (securityStopQty == 0) {
	    return false;
	}
	return qty > securityStopQty;

    }

    private void closePosition() {
	super.closePosition("close position");
	qty = 0;
    }
}
