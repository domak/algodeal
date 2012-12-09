/**
 * Demonstrates usage of the third-party library <a
 * href="http://ta-lib.org/">TA-Lib</a>, a library that provides indicators and
 * other market analysis tools. This implements a very classic slow turtle
 * strategy. Note that our own custom implementation of SMA is much faster than
 * this one.
 * <p>
 * We do not provide support for using TA-Lib. Please visit their website if you
 * require further information.
 */
package com.marketrunner.strategies.libraries;

import static com.algodeal.marketData.instruments.Futures.*;
import com.algodeal.marketData.prices.Bar;
import com.algodeal.marketrunner.strategies.*;
import com.tictactec.ta.lib.*;

@Instruments(futures = { CAC_40 })
public class SlowTurtleUsingTaLib extends AbstractStrategy {
	int fastSMALength = 90;
	int slowSMALength = 300;

	@Override
	public void onClose(Bar bar) {
		if (getBars().getCount() < slowSMALength + 1) {
			return;
		}

		boolean currentFastOverSlow = computeSma(fastSMALength, 0) > computeSma(slowSMALength, 0);
		boolean previousFastOverSlow = computeSma(fastSMALength, 1) > computeSma(slowSMALength, 1);

		boolean fastSmaCrossesAboveSlow = !previousFastOverSlow && currentFastOverSlow;
		boolean fastSmaCrossesBelowSlow = previousFastOverSlow && !currentFastOverSlow;
		if (fastSmaCrossesAboveSlow) {
			buy(1, "Reverse to Long");
		} else if (fastSmaCrossesBelowSlow) {
			sell(1, "Reverse to Short");
		}
	}

	private double computeSma(int smaLength, int ago) {
		double[] input = new double[smaLength];
		for (int i = 0; i < input.length; i++) {
			input[i] = getBars().ago(i + ago).getClose();
		}

		double[] output = new double[smaLength];

		new Core().movingAverage(0, smaLength - 1, input,//
				smaLength, MAType.Sma, //
				new MInteger(), new MInteger(), output);

		return output[0];
	}
}
