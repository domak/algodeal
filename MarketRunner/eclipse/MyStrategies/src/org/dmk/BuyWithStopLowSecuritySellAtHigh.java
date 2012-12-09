package org.dmk;

import static com.algodeal.marketData.instruments.Futures.CAC_40;
import static java.awt.Color.BLUE;

import org.joda.time.DateTime;

import com.algodeal.marketData.prices.Bar;
import com.algodeal.marketData.prices.OpenPrice;
import com.algodeal.marketrunner.indicators.SMA;
import com.algodeal.marketrunner.orders.OrderAction;
import com.algodeal.marketrunner.orders.StopOrder;
import com.algodeal.marketrunner.strategies.AbstractStrategy;
import com.algodeal.marketrunner.strategies.Instruments;
import com.algodeal.marketrunner.strategies.Optimized;
import com.algodeal.util.primitives.ToDouble;

//@ExecutionConfig(strategy = ExecutionStrategy.Standard.class)
@Instruments(futures = { CAC_40 })
public class BuyWithStopLowSecuritySellAtHigh extends AbstractStrategy {

    @Optimized(from = 1, to = 40, step = 2)
    private double marginAmount = 10;

    private SMA sma;
    private Mid mid = new Mid();

    int buyCount;
    int stopSellCancelCount;

    @Override
    public void onStrategyStart() {
	sma = newIndicator().sma().on(mid).withLength(1).draw(BLUE).get();
    }

    @Override
    public void onOpen(OpenPrice open) {
	if (hasPosition()) {
	    return;
	}
	if (getBars().isEmpty()) {
	    return;
	}

	Bar ago = getBars().ago(0);
	double midValue = sma.ago(0);
	if ((midValue < open.getPrice()) && (ago.getOpen() < ago.getClose())) {

	    buy(1, "buy at open");
	    buyCount++;
	    StopOrder sellStop = sellStop(1, ago.getLow(), "security stop");
	    sellStop.setOCAGroup("stop");
	    sellStop.onCancelled(new OrderAction<StopOrder, DateTime>() {

		@Override
		public void doThis(StopOrder order, DateTime details) {
		    stopSellCancelCount++;

		}
	    });
	    // double limit = ago.getHigh() > open.getPrice() ? ago.getHigh() : open.getPrice() + marginAmount;
	    double limit = open.getPrice() + marginAmount;
	    sellLimit(1, limit, "limit gain").setOCAGroup("stop");
	    System.out.println("open: " + open.getPrice() + " - midValue: " + midValue + " - low: " + ago.getLow()
		    + " - limit: " + limit);
	}
    }

    @Override
    public void onStrategyStop() {
	System.out.println("buy: " + buyCount + " - cancel: " + stopSellCancelCount + " -pct: "
		+ (100 * stopSellCancelCount / buyCount));
    }

    // -------------------------------------------------------------------------
    // inner class
    // -------------------------------------------------------------------------
    static class Mid implements ToDouble<Bar> {

	@Override
	public double convert(Bar bar) {
	    return (bar.getHigh() + bar.getLow()) / 2;
	}
    }
}
