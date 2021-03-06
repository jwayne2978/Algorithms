package taocp.ntuples;

import java.util.Iterator;

/**
 * See which implementation is faster
 * <p>
 *   Note that this doesn't count setup time, which is larger
 *   for the loopless algorithm.
 * </p>
 */
public class GraySpeedComparison {
  private static final int NTEST = 16;

  public static void main(String[] args) {
    Iterator<Integer> binaryGray = new GrayCode(NTEST).iterator();
    Iterator<Integer> looplessGray = new LooplessGrayCode(NTEST).iterator();

    // Do them both once first to trigger the JIT
    int value;
    while (binaryGray.hasNext()) {
      value = binaryGray.next();
    }
    while (looplessGray.hasNext()) {
      value = looplessGray.next();
    }

    binaryGray = new GrayCode(NTEST).iterator();
    looplessGray = new LooplessGrayCode(NTEST).iterator();
    long startTime = System.nanoTime();
    while (binaryGray.hasNext()) {
      value = binaryGray.next();
    }
    long elapsedBinary = System.nanoTime() - startTime;

    startTime = System.nanoTime();
    while (looplessGray.hasNext()) {
      value = looplessGray.next();
    }
    long elapsedLoopless = System.nanoTime() - startTime;

    System.out.println("Binary gray algorithm took   " + elapsedBinary + "ns");
    System.out.println("Loopless gray algorithm took " + elapsedLoopless + "ns");
  }
}
