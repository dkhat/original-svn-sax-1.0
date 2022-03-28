package com.xdx.sax.processor.ct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xdx.sax.bo.RawWellDataBO;
import com.xdx.sax.exceptions.SaxException;
import com.xdx.sax.processor.XdxProcessorComponent;
import com.xdx.sax.util.LinearCurveImpl;
import com.xdx.sax.util.LinearCurve;
import com.xdx.sax.util.StatsLibrary;

public class HTxSingleCtProcessor extends XdxProcessorComponent {

	private final Log log = LogFactory.getLog(getClass());
	
	// the well that the processor operates on
	private RawWellDataBO well = null;
	private double rn[];

	// Array of delta RN values. Delta RN is the difference of RN and baseline RN.
	private double[] deltaRN;

	// The cycle at which the percent rise of RN in the cycle window is maximum for the RN curve.
	private int maxPR_Cycle;

	// The maximum RN increase for the sliding cycle windows of RN curve.
	private double maxRise;

	// The starting cycle number of the baseline
	private int baselineCycleStart;

	// The ending cycle number of the baseline
	private int baselineCycleEnd;

	// The regression line for the the saturation portion of the RN curve
	private LinearCurve saturationCurve;

	// The regression line of the baseline portion of the delta RN curve
	private LinearCurve dtBaselineCurve;

	private double Cts[];

	// The starting cycle number of the saturation portion of the RN curve.
	//TODO: Add initialization!
	private int saturationCycleStart;
	
	// The ending cycle number of the saturation portion of the RN curve.
	//TODO: add initialization!
	private int saturationCycleEnd;

	public HTxSingleCtProcessor (RawWellDataBO well) {
		log.debug(buildString("Initializing ", getClass().getName(), " for well ", well.getId()));

		this.well = well;
		
		this.deltaRN = new double[TOTCYCLES + 1];

		this.dtBaselineCurve = new LinearCurveImpl();
		this.saturationCurve = new LinearCurveImpl();
		
		// set RN array
		this.rn = new double[well.getRn().length];
		for (int i=0; i < well.getRn().length; i++) {
			this.rn[i] = well.getRn()[i].getRn();
		}
		log.debug(buildString("rn = ", this.rn, " size ", this.rn.length));
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void calculateCT() {
		log.debug(buildString("Calculating CT for well ", well.getId(), ", error from previous: ", well.getErrornum()));
		
		// TODO: remove this in production
		well.setNormalizedXdxCt(null);

		calcMaxRise(MIN_BASELINE_CYCLE_START, CYCLE_RISE_RANGE);

		if (well.getIsEmpty() || well.getErrornum() == EMPTY_WELL_ERR) {
			log.debug("Empty well");
			
			if (well.getErrornum() != FLAT_LINE_ERR) {
				log.debug(buildString("Resetting error num to 0"));
				well.setErrornum(0); // to make it compatible with V2.9
			}

			well.setAmbiguitymetric(new Double(100));
			well.setCterror(1.0);
			well.setXdxct(null);
		} else {
			// Reset errornum in case of a rerun of the data set
			if (well.getErrornum() != CYCLES_ERR) well.setErrornum(0);
			
			calcBaseline();
			calculateLogPhase(PERCENT_RISE_RATIO, THRESHOLD);
			double ct = calculateCycle(THRESHOLD);
			
			log.debug(buildString("Setting XDx CT for well ", well.getId(), " to ", ct));
			if (ct != 0)
				well.setXdxct(ct);
			else
				well.setXdxct(null);
			
			calculateAmbiguity(THRESHOLD);
			calculateCTError();
		}
	}

	/**
	 * Find the cycle at which the percent rise of RN is the maximum.
	 *
	 * @param cycleStart
	 * @param rise_range
	 * @throws Exception
	 */
	private final void calcMaxRise(int cycleStart, int rise_range) {
		log.debug("Calculating max rise");
		
		this.maxPR_Cycle = -999;

		// Cycle at which the percent rise is maximum
		double maxPR_Cycle_Rise = -999;

		int limit= TOTCYCLES - rise_range - 1;

		for (int i= cycleStart;  i <= limit;  i++) {

			double rise= getRN(i + rise_range) - getRN(i);

			if (rise > this.maxRise) {
				this.maxRise = rise;
			}

			if (getRN(i) > 0) {

				rise /= getRN(i);

				if (rise > maxPR_Cycle_Rise) {
					maxPR_Cycle_Rise = rise;
					this.maxPR_Cycle = i;
				}
			}
		}

		if (maxPR_Cycle_Rise < MIN_PERCENT_RISE) {
			log.debug(buildString("flat line error, errornum: ", FLAT_LINE_ERR));
			well.setErrornum(FLAT_LINE_ERR); 	// to be compatible with V2.9
		}
		log.debug(buildString("maxRise: ", this.maxRise));
		log.debug(buildString("maxPR_Cycle: ", this.maxPR_Cycle));
	}

	/**
	 * Determine the baseline of RN curve.
	 *
	 * @throws Exception
	 */
	private final void calcBaseline() {
		log.debug("Calculating baseline");
		
		// The regression line of the baseline portion of the RN curve
		LinearCurve baselineCurve = new LinearCurveImpl();

		// Array of baseline RN values
		double[] baselineRN = new double[TOTCYCLES + 1];
		
		// The maximum delta RN increase for the sliding cycle windows of RN curve.
		double maxDeltaRN;

		if (well.getIsEmpty()) {
			return;
		}

		// calcMaxRise(MIN_BASELINE_CYCLE_START, CYCLE_RISE_RANGE);
		if (this.maxRise < 0.09) {
			log.debug(buildString("Well ", well.getId(), ", max rise too low: maxRise=", this.maxRise));
			well.setErrornum(FLAT_LINE_ERR);
//			return;
//			throw new SaxException(getErrorMessage(well.getErrornum()));
		}

		calculateSaturationCurve(this.maxPR_Cycle);
		calculateBaselineEnds(MIN_BASELINE_CYCLE_START, this.maxPR_Cycle, EXPONENT);

		if (well.getErrornum() != 0) {
			return;
		}

		maxDeltaRN = -9999;
		
		baselineCurve.fitCurve(this.rn, this.baselineCycleStart, this.baselineCycleEnd);

		for (int i= 1; i <= TOTCYCLES; i++) {

			baselineRN[i]= baselineCurve.calculateY(i);
			this.deltaRN[i]= getRN(i) - baselineRN[i];
			maxDeltaRN = Math.max(this.deltaRN[i], maxDeltaRN);
			log.debug(buildString("delta RN[",i,"]: ", this.deltaRN[i]));
		}

		this.dtBaselineCurve.fitCurve(this.deltaRN, this.baselineCycleStart, this.baselineCycleEnd);
		
		log.debug(buildString("maxDeltaRN: ", maxDeltaRN));
	}

	/**
	 * Determine the Log linear phase of the Log(deltaRN) curve.
	 *
	 * @param percentRiseRatio
	 * @param threshold
	 * @throws Exception
	 */
	private final void calculateLogPhase(double percentRiseRatio, double threshold) {
		log.debug("Calculating log phase");
		
		well.setLoglinearleftend(0);
		well.setLoglinearrightend(0);

		if (well.getErrornum() != 0 || well.getIsEmpty()) {
			return;
		}

		// Find the baseline maximum residual
		double max_residual= -999999;

		for (int i= this.baselineCycleStart; i <=  this.baselineCycleEnd; i++) {

			double d= this.dtBaselineCurve.calculateY(i);
			double residual= Math.abs(this.deltaRN[i] - d);
			max_residual= Math.max(residual, max_residual);
}

		double p= 2;
		max_residual= p * max_residual;
		well.setLoglinearleftend(this.baselineCycleEnd + 3);

		for (int i= this.baselineCycleEnd; i <= TOTCYCLES; i++) {

			if (this.deltaRN[i] > max_residual) {

				well.setLoglinearleftend(i);
				break;
			}
		}

		for (int i= well.getLoglinearleftend();  i <= TOTCYCLES;  i++) {

			if (this.deltaRN[i]  <  0) {
				well.setLoglinearleftend(i + 1);
			}
		}

		if (well.getLoglinearleftend()  >=  MAX_LLLE) {
			log.debug(buildString("Log linear left end too high, errornum: ", LLLE_TOO_HIGH_ERR));
			setError(LLLE_TOO_HIGH_ERR);
			throw new SaxException(ERROR_MESSAGE[LLLE_TOO_HIGH_ERR]);
		}

		LinearCurve curve= new LinearCurveImpl();
		int minLogCycles= 3;
		double[] avgResidual= new double[TOTCYCLES + 1];
		double[] logdrn= new double[TOTCYCLES + 1];

		if (well.getLoglinearleftend() + minLogCycles  >=  TOTCYCLES - 1) {
			log.debug(buildString("Initializing LLRE to", TOTCYCLES));
			well.setLoglinearrightend(TOTCYCLES);
		}
		else {

			double min_residual= 999999;

			for (int i= TOTCYCLES; i >= well.getLoglinearleftend() + minLogCycles; i--) {

				if (threshold != -1 && this.deltaRN[i] < threshold) {
					break;
				}

				for (int j= well.getLoglinearleftend(); j <= i; j++) {
					logdrn[j]= StatsLibrary.Log10(this.deltaRN[j]);
				}

				curve.fitCurve(logdrn, well.getLoglinearleftend(), i);
				
				double tot_residual= 0;

				for (int j= well.getLoglinearleftend(); j <= i; j++) {

					double d= curve.calculateY(j);
					double residual= Math.abs(logdrn[j] - d);
					tot_residual += residual;
				}

				avgResidual[i]= tot_residual / (i - well.getLoglinearleftend() + 1);
				log.debug(buildString("avgResidual[",i,"]: ", avgResidual[i]));
			}
			
			for (int i= TOTCYCLES; i >= well.getLoglinearleftend() + minLogCycles; i--) {

				if (avgResidual[i] < min_residual) {

					min_residual= avgResidual[i];
					well.setLoglinearrightend(i);
					log.debug(buildString("Setting LLRE to ", i));
				}
			}
		}

		// Move LLRE to the right if it's under the threshold
		for (int i= well.getLoglinearrightend(); i <= TOTCYCLES; i++) {

			if (threshold != -1 && this.deltaRN[i] > threshold) {

				well.setLoglinearrightend(i);
				log.debug(buildString("Adjusting LLRE to ", i));
				break;
			}
		}

		// Recalculate LLLE using LLRE
		// Find the best LLLE with the least avg redidual
		int LLLE1= well.getLoglinearleftend() - 2;
		int LLLE2= (int) Math.min(well.getLoglinearleftend() + 2, well.getLoglinearrightend() - 1);

		// Make sure there's no negative delta RN
		for (int i= LLLE2; i >= LLLE1; i--) {

			if (this.deltaRN[i] < 0) {

				LLLE1= i + 1;
				break;
			}
		}

		double min_residual= 999999;

		for (int i= LLLE1; i <= well.getLoglinearleftend(); i++) {

			for (int j= i; j <= well.getLoglinearrightend(); j++) {
				logdrn[j]= StatsLibrary.Log10(this.deltaRN[j]);
			}

			curve.fitCurve(logdrn, i, well.getLoglinearrightend());
			
			avgResidual[i]= curve.getAvgResidual();
		}

		int end= well.getLoglinearleftend();

		for (int i= LLLE1; i <= end; i++) {

			if (avgResidual[i] < min_residual) {
				min_residual= avgResidual[i];
				well.setLoglinearleftend(i);
			}
		}

		// Make sure LLLE is below the calibrator threshold
		for (int i= well.getLoglinearleftend(); i >= this.baselineCycleEnd; i--) {

			if (threshold != -1 && this.deltaRN[i] < threshold) {

				well.setLoglinearleftend(i);
				break;
			}
		}

		// Make sure there's no negative deltaRN in the log phase.
		for (int i= well.getLoglinearrightend(); i >= well.getLoglinearleftend(); i--) {

			if (this.deltaRN[i] < 0) {

				well.setLoglinearleftend(i + 1);
				break;
			}
		}

		if (well.getLoglinearleftend() <= this.baselineCycleEnd) {
			well.setLoglinearleftend(this.baselineCycleEnd);
		}

		if (well.getLoglinearrightend() > MAX_LLRE) {
			log.debug(buildString("Log linear right end too high, errornum: ", LLRE_TOO_HIGH_ERR));
			well.setErrornum(LLRE_TOO_HIGH_ERR);
		}
	}

	/**
	 * Determine the threshold cycle using a specific RN threshold.
	 *
	 * @param threshold The RN threshold value
	 * @return threshold cycle as a <code>double</code>
	 * @throws Exception
	 */
	private final double calculateCycle(double threshold) {
		log.debug("Determining threshold cycle");
		
		// The regression line of the Log(deltaRN) curve
		LinearCurve logLinearCurve = new LinearCurveImpl();

		if (well.getIsEmpty()) {
			return 0;
		}

		if (well.getLoglinearleftend() == well.getLoglinearrightend() || well.getLoglinearrightend() == 0) {
			log.debug(buildString("Error calculating threshold cycle, errornum: ", NO_LOGPHASE_ERR));
			setError(NO_LOGPHASE_ERR);
			return 0;
		}

		double[] values= new double[TOTCYCLES + 1];

		for (int i= well.getLoglinearleftend(); i <= well.getLoglinearrightend(); i++) {

			try {
				values[i]= StatsLibrary.Log10(this.deltaRN[i]);
			}
			catch (Exception e) {
				// deltaRN has negative value. Skip.
			}
		}

		logLinearCurve.fitCurve(values, well.getLoglinearleftend(), well.getLoglinearrightend());

		if (logLinearCurve.getCoefficient() == 0) {
			log.debug(buildString("Division by zero error, errornum: ", DIVIDE_BY_ZERO_ERR));
			setError(DIVIDE_BY_ZERO_ERR);
			throw new SaxException(getErrorMessage(DIVIDE_BY_ZERO_ERR));
		}

		threshold= StatsLibrary.Log10(threshold);

		double thresholdCycle= logLinearCurve.calculateX(threshold);

		if (thresholdCycle > TOTCYCLES || thresholdCycle < MIN_THRESHOLD_CYCLE) {
			log.debug(buildString("Threshold cycle out of range, errornum: ", THRESHOLD_CYCLE_OUT_OF_RANGE_ERR));
			setError(THRESHOLD_CYCLE_OUT_OF_RANGE_ERR);
			thresholdCycle= 0;
		}
		else if (thresholdCycle < this.baselineCycleEnd) {
			log.debug(buildString("Threshold cycle too low, errornum: ", THRESHOLD_CYCLE_TOO_LOW_ERR));
			setError(THRESHOLD_CYCLE_TOO_LOW_ERR);
			thresholdCycle= 0;
		}

		if (thresholdCycle == 0 && well.getErrornum() == LLRE_TOO_HIGH_ERR) {
			log.debug(buildString("Left linear log end too high and threshold cycle 0, errornum: ", LLRE_TOO_HIGH_AND_NULL_ERR));
			well.setErrornum(LLRE_TOO_HIGH_AND_NULL_ERR);
		}
		return thresholdCycle;
	}

	/**
	 *
	 * @param threshold
	 * @throws Exception
	 */
	private final void calculateAmbiguity(double threshold) {
		log.debug("Calculating ambiguity");
		
		double binWidth, sigma, startCt, maxSmoothHistogram, d_smoothHistogram;
		double thisSigma, maxValueLeft, maxValueRight, nextValue, secondPlaceValue, maxValue;
		int numberOfBins, maxBin;
		int bin, bin1, bin2, bin3, bin4, minRadius;

		binWidth= 0.1;
		sigma= 0.2;
		numberOfBins= TOTCYCLES;
		getCTs (threshold);
		binWidth= 0.1;
		sigma= 0.2;
		numberOfBins= 40;
		startCt= this.Cts[0] - 2;

		double smoothHistograms[];
		smoothHistograms= getSmoothHistograms(startCt, numberOfBins, binWidth, sigma);
		maxBin= 0;
		maxSmoothHistogram= 0;

		for (bin= 0; bin < numberOfBins; bin++) {

			d_smoothHistogram= smoothHistograms[bin];

			if (maxSmoothHistogram < d_smoothHistogram) {

				maxSmoothHistogram= d_smoothHistogram;
				maxBin= bin;
			}
		}

		double y0, y1, y2, ym1, yp1, deltaYm1, deltaYp1, deltaX, deltaY, a, b;
		double modeCt, height, halfHeight, rightSide, leftSide, thisDeltaX;
		deltaX= 0;
		deltaY= 0;
		y0= smoothHistograms[maxBin];

		if (0 < maxBin && maxBin < numberOfBins - 1) {

			ym1= smoothHistograms[maxBin - 1];
			yp1= smoothHistograms[maxBin + 1];
			deltaYm1= ym1 - y0;
			deltaYp1= yp1 - y0;
			a= 0.5 * (deltaYp1 + deltaYm1);
			b= 0.5 * (deltaYp1 - deltaYm1);
			deltaX= -b / (2 * a);
			deltaY= -b * b / (4 * a);
		}

		modeCt= startCt + (maxBin + deltaX) * binWidth;
		height= y0 + deltaY;
		halfHeight= 0.5 * height;
		rightSide= modeCt + 2;

		for (bin= maxBin + 1; bin < numberOfBins; bin++) {

			d_smoothHistogram= smoothHistograms[bin];

			if (d_smoothHistogram < halfHeight) {

				y1= smoothHistograms[bin - 1];
				y2= d_smoothHistogram;
				thisDeltaX= (halfHeight - y1) / (y2 - y1);
				rightSide= startCt + (bin - 1 + thisDeltaX) * binWidth;
				break;
			}
		}

		leftSide= modeCt - 2;

		for (bin= maxBin - 1; bin >= 0; bin--) {

			d_smoothHistogram= smoothHistograms[bin];

			if (d_smoothHistogram < halfHeight) {

				y1= d_smoothHistogram;
				y2= smoothHistograms[bin + 1];
				thisDeltaX= (halfHeight - y1) / (y2 - y1);
				leftSide= startCt + (bin + thisDeltaX) * binWidth;

				break;
			}
		}

		// thisSigma: widthMetric
		thisSigma= 0.5 * (rightSide - leftSide);
		well.setWidthmetric(thisSigma);
		minRadius= 5;
		bin1= 0;
		bin2= maxBin - minRadius;

		if (bin2 < 0) {
			bin2= 0;
		}

		bin3= maxBin + minRadius;

		if (bin3 > TOTCYCLES - 1) {
			bin3= TOTCYCLES - 1;
		}

		bin4= TOTCYCLES - 1;
		maxValueLeft= smoothHistograms[bin1];
		double value;

		for (bin= bin1 + 1; bin <= bin2; bin++) {

			value= smoothHistograms[bin];
			nextValue= smoothHistograms[bin + 1];

			if (maxValueLeft < value && nextValue < value) {

				maxValueLeft= value;
			}
		}

		maxValueRight= smoothHistograms[bin4];

		for (bin= bin4 - 1; bin >= bin3; bin--) {

			value= smoothHistograms[bin];
			nextValue= smoothHistograms[bin - 1];

			if (maxValueRight < value && nextValue < value) {
				maxValueRight= value;
			}
		}

		secondPlaceValue= Math.max(maxValueLeft, maxValueRight);
		maxValue= height;

		if (maxValue <= 0) {
			maxValue= 1;
		}

		well.setAmbiguitymetric(Math.floor(100 * secondPlaceValue / maxValue));
	}

	/**
	 * 
	 */
	private final void calculateCTError() {
		log.debug("Calculating CT error");
		Double cterror;
		
		try {
			double rms= StatsLibrary.RMS(this.deltaRN, this.baselineCycleStart, this.baselineCycleEnd);
			int ct1= (int) Math.floor(well.getXdxct());
			int ct2= ct1 + 1;
			cterror = rms / (this.deltaRN[ct2] - this.deltaRN[ct1]);
		} catch (Exception e) {
			log.debug(buildString("Error during calculation of CT error: ", e.getMessage()));
			cterror = 1.0;
		}
		
		if (cterror.isNaN())
			cterror = 1.0;

		well.setCterror(cterror);
		log.debug(buildString("CT error = ", cterror));
	}

	/**
	 * Determine the saturation portion of RN curve.
	 *
	 * @param cycleStart
	 * @throws Exception
	 */
	private final void calculateSaturationCurve(int cycleStart) {
		log.debug("Calculating saturation curve");
		
		double min_threshold_residual= 99999;
		this.saturationCurve = new LinearCurveImpl();

		for (int k= TOTCYCLES - 1; k <= TOTCYCLES; k++) {

			if (cycleStart == k - MIN_BASELINE_CYCLES + 1) {

				saturationCycleStart = TOTCYCLES - 1;
				saturationCycleEnd = TOTCYCLES;

				break;
			}

			for (int i= cycleStart; i <= k - MIN_BASELINE_CYCLES + 1; i++) {

				this.saturationCurve.fitCurve(this.rn, i, k);

				if (this.saturationCurve.getAvgResidual() < min_threshold_residual) {

					min_threshold_residual= this.saturationCurve.getAvgResidual();
					saturationCycleStart = i;
					saturationCycleEnd = k;
				}
			}
		}

		this.saturationCurve.fitCurve(this.rn, saturationCycleStart, saturationCycleEnd);
	}

	/**
	 * Determine the starting and ending cycle of RN curve.
	 *
	 * @param cycleStart
	 * @param cycleEnd
	 * @param exponent
	 * @throws Exception
	 */
	private final void calculateBaselineEnds(int cycleStart, int cycleEnd, double exponent) {
		log.debug("Calculating baseline ends");
		
		double min_threshold_residual= 9999999;
		int penalty= 10;
		LinearCurve curve= new LinearCurveImpl();

		this.baselineCycleEnd = 0;
		this.baselineCycleStart = 0;

		for (int k= 6; k <= cycleEnd; k++) {

			for (int i= cycleStart; i <= Math.min(k - MIN_BASELINE_CYCLES + 1, 3 + k / 2); i++) {

				curve.fitCurveN(this.rn, i, k, 3);

				int cycles= k - i + 1;
				double avg_residual= curve.getAvgResidualN();

				double adjustment=
					(Math.abs(curve.getCoefficient()) + 0.2) +
					(Math.max(curve.getCoefficient() - this.saturationCurve.getCoefficient(), 0) +
				 	Math.max(-curve.getCoefficient(), 0)) * penalty;

				double threshold_residual=
					adjustment * (avg_residual + 0.014) *
					Math.pow(1 / (double)(cycles - MIN_BASELINE_CYCLES + 1), exponent);

				if (threshold_residual < min_threshold_residual) {

					min_threshold_residual= threshold_residual;
					this.baselineCycleStart = i;
					this.baselineCycleEnd = k;
				}
			}
		}

		if (this.baselineCycleStart == 0 || this.baselineCycleEnd == 0) {
			log.debug(buildString("Cyclestart and end are 0, errornum: ", FLAT_LINE_ERR));
			setError(FLAT_LINE_ERR);
			throw new SaxException(ERROR_MESSAGE[FLAT_LINE_ERR]);
		}
		else if (this.baselineCycleEnd - this.baselineCycleStart + 1 < MIN_BASELINE_CYCLES) {
			log.debug(buildString("Not enough baseline cycles, errornum: ", CYCLE_RANGE_ERR));
			setError(CYCLE_RANGE_ERR);
			throw new SaxException(ERROR_MESSAGE[CYCLE_RANGE_ERR]);
		}
	}

	/**
	 *
	 * @param startCt
	 * @param numberOfBins
	 * @param binWidth
	 * @param sigma
	 * @return the Smooth Histograms, as a collection of type <code>double</code>
	 */
	private double[] getSmoothHistograms(double startCt, int numberOfBins, double binWidth, double sigma) {
		log.debug("Start: getSmoothHistograms");
		
		double factor, maxY, thisCt, sumOfGaussians, ct, delta, gaussian;
		int numberOfCts, bin, ctIndex;
		double smoothHistograms[]= new double[numberOfBins];
		numberOfCts= this.Cts.length;
		factor= -1 / (2 * sigma * sigma);
		maxY= 0;

		for (bin= 0; bin < numberOfBins; bin++) {

			thisCt= startCt + bin * binWidth;
			sumOfGaussians= 0;

			for (ctIndex= 0; ctIndex < numberOfCts; ctIndex++) {

				ct= this.Cts[ctIndex];
				delta= thisCt - ct;
				gaussian= Math.exp(factor * delta * delta);
				sumOfGaussians= sumOfGaussians + gaussian;
			}

			smoothHistograms[bin]= sumOfGaussians;

			if (maxY < sumOfGaussians) {
				maxY= sumOfGaussians;
			}
		}

		if (maxY <= 0) {
			maxY= 1;
		}

		for (bin= 0; bin < numberOfBins; bin++) {
			smoothHistograms[bin]= smoothHistograms[bin] / maxY;
		}

		return smoothHistograms;
	}

	/**
	 *
	 * @param threshold
	 * @throws Exception
	 */
	private final void getCTs(double threshold) {
		double firstCt;
		firstCt= getCT(4, 8, threshold);

		if (firstCt < 16) {

			this.Cts = new double[(int)firstCt];
			this.Cts[0]= firstCt;

			return;
		}

		double secondCt;
		int intSecondCt, lastBaselineStart, lastBaselineStop;
		int firstBaselineStart, firstBaselineStop, baselineStart, baselineStop;
		int maxNumberOfBaselineStops, numberOfCts, ctIndex;
		secondCt= getCT(5, 14, threshold);
		intSecondCt= (int) secondCt;
		lastBaselineStop= intSecondCt - 5;
		lastBaselineStart= lastBaselineStop - 3;
		firstBaselineStart= lastBaselineStart - 4;
		firstBaselineStop= firstBaselineStart + 4;
		maxNumberOfBaselineStops= lastBaselineStop - firstBaselineStop + 1;
		numberOfCts= 1 + maxNumberOfBaselineStops * (maxNumberOfBaselineStops + 1) / 2;
		this.Cts = new double[numberOfCts];
		int baselineStarts[]= new int[numberOfCts];
		int baselineStops[]= new int[numberOfCts];
		ctIndex= 0;
		baselineStarts[ctIndex]= 5;
		baselineStops[ctIndex]= 14;
		this.Cts[ctIndex]= secondCt;
		ctIndex= ctIndex + 1;

		for (baselineStart= firstBaselineStart; baselineStart <= lastBaselineStart; baselineStart++) {

			for (baselineStop= baselineStart + 4; baselineStop <= lastBaselineStop; baselineStop++) {

				baselineStarts[ctIndex]= baselineStart;
				baselineStops[ctIndex]= baselineStop;
				this.Cts[ctIndex]= getCT(baselineStart, baselineStop, threshold);
				ctIndex= ctIndex + 1;
			}
		}
	}

	/**
	 *
	 * @param baselineStart
	 * @param baselineStop
	 * @param threshold
	 * @return the CT (EV) value, as a <code>double</code>
	 * @throws Exception
	 */
	private final double getCT(int baselineStart, int baselineStop, double threshold) {
		double slope, intercept, lastDelta, thisDelta, logThreshold, alpha;
		int numberOfCycles, cycle;
		LinearCurve curve=  new LinearCurveImpl();
		curve.fitCurve(this.rn, baselineStart, baselineStop);

		slope= curve.getCoefficient();
		intercept= curve.getIntercept();
		lastDelta= 100;
		numberOfCycles= TOTCYCLES;
		double y0, y1;

		for (cycle= numberOfCycles - 1; cycle >= 0; cycle--) {

			thisDelta= getRN(cycle) - (slope * cycle + intercept);

			if (thisDelta < threshold) {

				if (thisDelta > 0.5 * threshold) {

					y0= Math.log(thisDelta);
					y1= Math.log(lastDelta);
					logThreshold= Math.log(threshold);
					alpha= (logThreshold - y0) / (y1 - y0);

					return (1 + cycle + alpha);
				}
				else {

					y0= thisDelta;
					y1= lastDelta;
					alpha= (threshold - y0) / (y1 - y0);

					return (1 + cycle + alpha);
				}
			}

			lastDelta= thisDelta;
		}

		return 0;
	}

	/**
	 * Set an error number only if this is the first.
	 *
	 * @param value error code
	 */
	private void setError(int value) {

		if (well.getErrornum() == 0) {
			well.setErrornum(value);
		} else {
			log.debug(buildString("Not setting errorcode ", value, " because of previous errorcode ", well.getErrornum()));
		}
	}

	/**
	 * RN is the ratio FAM/ROX for ABI.
	 * Values are set during SDS file processing
	 */
	private double getRN(int i) {
		return this.rn[i]; 
	}

}
