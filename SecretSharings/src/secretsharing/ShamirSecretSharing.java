package secretsharing;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

/**
 * This class implements Shamir's (t,n) secret sharing.
 * 
 * Secrets are represented as BigInteger objects, shares as ShamirShare objects.
 * 
 * Randomness is taken from a {@link java.security.SecureRandom} object.
 * 
 //* @see ShamirShare
 * @see BigInteger
 * @see SecureRandom
 * 
 * @author ewti
 * 
 */
public class ShamirSecretSharing {

	/**
	 * Creates a (t,n) Shamir secret sharing object for n shares with threshold t.
	 * 
	 * @param t
	 *            threshold: any subset of t <= i <= n shares can recover the
	 *            secret.
	 * @param n
	 *            number of shares to use. Needs to fulfill n >= 2.
	 */
	public ShamirSecretSharing(int t, int n) {
		assert (t >= 2);
		assert (n >= t);

		this.t = t;
		this.n = n;
		this.rng = new SecureRandom();

		// use p = 2^2048 + 981
		this.p = BigInteger.ONE.shiftLeft(2048).add(BigInteger.valueOf(981));
	}

	/**
	 * Shares the secret into n parts.
	 * 
	 * @param secret
	 *            The secret to share.
	 * 
	 * @return An array of the n shares.
	 */
	public ShamirShare[] share(BigInteger secret) {
		// TODO: implement this
		ShamirShare[] shares = new ShamirShare[n];
		BigInteger[] coefficients = new BigInteger[t];
		coefficients[0] = secret;
		for (int i = 1; i < t; i++) {
			coefficients[i] = new BigInteger(2048, rng);
		}
		for(int i = 0; i < n; i++) {
			BigInteger x = BigInteger.valueOf(i + 1);
			BigInteger y = BigInteger.ZERO;
			for(int j = 0; j < t; j++) {
				y = y.add(coefficients[j].multiply(x.pow(j)));
			}
			shares[i] = new ShamirShare(x, y.mod(p));
		}

		return shares;
	}

	/**
	 * Evaluates the polynomial a[0] + a[1]*x + ... + a[t-1]*x^(t-1) modulo p at
	 * point x using Horner's rule.
	 * 
	 * @param x
	 *            point at which to evaluate the polynomial
	 * @param a
	 *            array of coefficients
	 * @return value of the polynomial at point x
	 */
	private BigInteger horner(BigInteger x, BigInteger[] a) {
		// TODO: implement this
		BigInteger s = a[t-1];
		for (int i = t-2; i >= 0; i--) {
			s = s.multiply(x).add(a[i]).mod(p);
		}
		return s;
	}

	/**
	 * Recombines the given shares into the secret.
	 * 
	 * @param shares
	 *            A set of at least t out of the n shares for this secret.
	 * 
	 * @return The reconstructed secret.
	 */
	public BigInteger combine(ShamirShare[] shares) {
		// TODO: implement this
		BigInteger secret = BigInteger.ZERO;
		for (int i = 0; i < t; i++) {
			BigInteger numerator = BigInteger.ONE;
			BigInteger denominator = BigInteger.ONE;
			for (int j = 0; j < t; j++) {
				if (i != j) {
					numerator = numerator.multiply(shares[j].getX().negate()).mod(p);
					denominator = denominator.multiply(shares[j].getX().subtract(shares[i].getX())).mod(p);
				}
			}
			secret = secret.add(shares[i].getS().multiply(numerator)
					.multiply(denominator.modInverse(p))).mod(p);
		}
		return secret;
	}

	public int getT() {
		return t;
	}
	public int getN() {
		return n;
	}

	private int t;
	private int n;
	private SecureRandom rng;
	private BigInteger p;

	public static void main(String[] args) {
		int t = 5, n = 10;
		ShamirSecretSharing test = new ShamirSecretSharing(t,n);
		BigInteger secret = new BigInteger(test.p.bitLength(), test.rng);
		System.out.println("Secret: " + secret);
		ShamirShare[] shares = test.share(secret);
		System.out.println("Shares: " + Arrays.toString(shares));
		BigInteger combined = test.combine(shares);
		System.out.println("Combined: " + combined);
	}

}
