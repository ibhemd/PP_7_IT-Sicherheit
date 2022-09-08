/**
 * 
 */
package secretsharing;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

/**
 * This class implements the simple XOR-based (n,n) secret sharing.
 * 
 * Secrets and shares are both represented as byte[] arrays.
 * 
 * Randomness is taken from a {@link java.security.SecureRandom} object.
 * 
 * @see SecureRandom
 * 
 */
public class XorSecretSharing {

	/**
	 * Creates a XOR secret sharing object for n shares
	 * 
	 * @param n
	 *            number of shares to use. Needs to fulfill n >= 2.
	 */
	public XorSecretSharing(int n) {
		assert (n >= 2);
		this.n = n;
		this.rng = new SecureRandom();
	}

	/**
	 * Shares the secret into n parts.
	 * 
	 * @param secret
	 *            The secret to share.
	 * 
	 * @return An array of the n shares.
	 */
	public byte[][] share(final byte[] secret) {
		// TODO: implement this
		int l = secret.length;
		byte[][] res = new byte[n][l];
		for (int i = 0; i < n-1; i++) {
			byte[] r = new byte[l];
			this.rng.nextBytes(r);
			res[i] = r;
		}
		for (int i = 0; i < l; i++) {
			res[n-1][i] = secret[i];
			for (int j = 0; j < n-1; j++) {
				res[n-1][i] += res[j][i];
			}
		}
		return res;
	}

	/**
	 * Recombines the given shares into the secret.
	 * 
	 * @param shares
	 *            The complete set of n shares for this secret.
	 * 
	 * @return The reconstructed secret.
	 */
	public byte[] combine(final byte[][] shares) {
		// TODO: implement this
		int l = shares[0].length;
		byte[] res = new byte[l];
		for (int i = 0; i < l; i++) {
			res[i] = shares[n-1][i];
			for (int j = 0; j < shares.length-1; j++) {
				res[i] -= shares[j][i];
			}
		}
		return res;
	}

	private int n;

	public int getN() {
		return n;
	}

	private Random rng;

	public static void main(String[] args) {
		XorSecretSharing test = new XorSecretSharing(3);
		byte[] secret1 = new byte[5];
		test.rng.nextBytes(secret1);
		System.out.println("Secret: " + Arrays.toString(secret1));
		byte[][] shares1 = test.share(secret1);
		System.out.println("Shares: " + Arrays.deepToString(shares1));
		byte[] combined1 = test.combine(shares1);
		System.out.println("Combined: " + Arrays.toString(combined1));

		System.out.println();

		byte[] secret2 = new byte[5];
		test.rng.nextBytes(secret2);
		System.out.println("Secret: " + Arrays.toString(secret2));
		byte[][] shares2 = test.share(secret2);
		System.out.println("Shares: " + Arrays.deepToString(shares2));
		byte[] combined2 = test.combine(shares2);
		System.out.println("Combined: " + Arrays.toString(combined2));

		System.out.println();

		byte[] secret3 = new byte[5];
		test.rng.nextBytes(secret3);
		System.out.println("Secret: " + Arrays.toString(secret3));
		byte[][] shares3 = test.share(secret3);
		System.out.println("Shares: " + Arrays.deepToString(shares3));
		byte[] combined3 = test.combine(shares3);
		System.out.println("Combined: " + Arrays.toString(combined3));

	}

}
