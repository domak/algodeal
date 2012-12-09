/**
 * Demonstrates various ways to configure parameters for optimization.
 * Essentially, values can either be exhaustively specified, or they can be
 * calculated (for numbers only).
 */
package com.marketrunner.strategies.advanced;

import static com.algodeal.marketData.instruments.Futures.*;
import static com.algodeal.marketData.instruments.Indices.*;
import java.util.List;
import com.algodeal.marketData.instruments.*;
import com.algodeal.marketData.prices.OpenPrice;
import com.algodeal.marketrunner.orders.Side;
import com.algodeal.marketrunner.runner.optimizer.OptimizationValuesGenerator;
import com.algodeal.marketrunner.strategies.*;
import com.google.common.collect.Lists;

@Instruments(futures = { CAC_40, E_MINI_SP }, indices = { VIX_INDEX })
public class OptimizedParameters extends AbstractStrategy {
	final static String BUY_WHEN_UP = "buy when up";
	final static String BUY_WHEN_DOWN = "buy when down";

	@Optimized(strings = { BUY_WHEN_DOWN, BUY_WHEN_UP })
	String mode = BUY_WHEN_DOWN;

	@Optimized(numbers = { 2l, 100l })
	long quantityBuy = 2;

	@Optimized(generator = Fibonacci.class)
	double quantitySell = 1;

	@Optimized(from = 0.01, to = 0.02, step = 0.01)
	double factor = 0.01;

	@Optimized(futures = { CAC_40, E_MINI_SP })
	Tradeable tradedInstrument = CAC_40;

	@Optimized(indices = { VIX_INDEX })
	Indices observedIndex = VIX_INDEX;

	@Optimized
	Boolean skipEveryOtherBar = false;

	@Optimized(booleans = false)
	Boolean disabled = false;

	boolean evenBar = true;

	@Override
	public void onOpen(OpenPrice open) {
		if (getBars().isEmpty() || disabled) {
			return;
		}
		if (this.isUsing(observedIndex) && (open.getPrice() < getBars().ago(0).getClose())) {
			return;
		}

		if (skipEveryOtherBar) {
			evenBar = !evenBar;
			if (evenBar) {
				return;
			}
		}

		Side side;
		if (open.getPrice() > getBars().ago(0).getClose()) {
			side = mode.equals(BUY_WHEN_UP) ? Side.BUY : Side.SELL;
		} else {
			side = mode.equals(BUY_WHEN_UP) ? Side.SELL : Side.BUY;
		}

		sendLimit(side, quantityBuy, open.getPrice() * (1.0 - factor), tradedInstrument, "");
	}

	static class Fibonacci implements OptimizationValuesGenerator<Long> {
		@Override
		public Iterable<Long> values() {
			List<Long> fibo = Lists.newArrayList(3l, 5l);

			for (int i = 2; i < 3; i++) {
				fibo.add(fibo.get(i - 2) + fibo.get(i - 1));
			}

			return fibo;
		}
	}
}
