package org.dmk;

import static com.algodeal.marketData.instruments.Futures.CAC_40;

import com.algodeal.marketData.prices.Bar;
import com.algodeal.marketData.prices.OpenPrice;
import com.algodeal.marketrunner.strategies.AbstractStrategy;
import com.algodeal.marketrunner.strategies.Instruments;
import com.algodeal.marketrunner.strategies.Optimized;

//@ExecutionConfig(strategy = ExecutionStrategy.Standard.class)
@Instruments(futures = { CAC_40 })
public class BuyWithStopSecuritySellWtihMargin extends AbstractStrategy {

    @Optimized(from = 0, to = 2, step = 0.1)
    double securityAmountThreshold;

    @Optimized(from = 1, to = 40, step = 2)
    double marginAmount;

    @Override
    public void onOpen(OpenPrice open) {
	if (hasPosition()) {
	    return;
	}
	if (getBars().getCount() <= 1) {
	    return;
	}

	Bar ago = getBars().ago(1);
	if ((ago.getClose() < open.getPrice()) && (ago.getClose() > ago.getOpen())) {

	    buy(1, "buy at open");
	    sellStop(1, open.getPrice() - securityAmountThreshold, "security stop").setOCAGroup("stop");
	    sellLimit(1, open.getPrice() + marginAmount, "limit gain").setOCAGroup("stop");
	}
    }
}
