package org.dmk;

import static com.algodeal.marketData.instruments.Futures.CAC_40;

import com.algodeal.marketData.prices.Bar;
import com.algodeal.marketData.prices.OpenPrice;
import com.algodeal.marketrunner.orders.MarketOrder;
import com.algodeal.marketrunner.orders.OrderAction;
import com.algodeal.marketrunner.orders.StopOrder;
import com.algodeal.marketrunner.orders.implementations.TransactionDetails;
import com.algodeal.marketrunner.positions.Position;
import com.algodeal.marketrunner.strategies.AbstractStrategy;
import com.algodeal.marketrunner.strategies.Instruments;
import com.algodeal.marketrunner.strategies.Optimized;

//@ExecutionConfig(strategy = ExecutionStrategy.Standard.class)
@Instruments(futures = { CAC_40 })
public class BuyWithStopSecuritySellIfWorth extends AbstractStrategy {

    double buyPrice;
    private StopOrder sellStop;

    @Optimized(from = 0, to = 10, step = 0.1)
    double securityAmountThreshold;

    @Override
    public void onOpen(OpenPrice open) {
	if (getBars().getCount() <= 1) {
	    return;
	}

	Bar ago = getBars().ago(1);
	if ((ago.getClose() > open.getPrice()) || (ago.getClose() < ago.getOpen())) {
	    return;
	}

	if (!hasPosition()) {
	    buy(1, "buy open");
	    sellStop = sellStop(1, open.getPrice() - securityAmountThreshold, "security stop");

	}
    }

    @Override
    public void onPositionOpened(Position newPosition) {
	buyPrice = newPosition.getUnitaryPrice();
	// System.out.println("new pos: " + newPosition);
    }

    @Override
    public void onClose(Bar bar) {
	if (!hasPosition() || getBars().getCount() <= 1 || sellStop.isFilled()) {
	    return;
	}

	double close = bar.getClose();
	double closeDayBefore = getBars().ago(1).getClose();
	if ((closeDayBefore > close) && (close > buyPrice)) {
	    MarketOrder sell = sell(1, "Sell at Close");
	    // System.out.println("day: " + bar);
	    // System.out.println("before: " + getBars().ago(1));
	    cancelOrder(sellStop);
	    sell.onFilled(new OrderAction<MarketOrder, TransactionDetails>() {

		@Override
		public void doThis(MarketOrder order, TransactionDetails details) {
		    double sellPrice = details.getPrice();
		    System.out.println("buy at: " + buyPrice + " - sell at: " + sellPrice + " - gain: "
			    + (sellPrice - buyPrice));
		}
	    });
	}

    }
}
