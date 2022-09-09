/**
 * 
 */
package secretsharing;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
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
		byte[][] res = new byte[n][secret.length];
		res[n-1] = secret.clone();
		for (int i = 0; i < n-1; i++) {
			rng.nextBytes(res[i]);
			for (int j = 0; j < secret.length; j++) {
				res[n-1][j] ^= res[i][j];
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
		byte[] res = new byte[shares[0].length];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < res.length; j++) {
				res[j] ^= shares[i][j];
			}
		}
		return res;
	}

	private int n;

	public int getN() {
		return n;
	}

	private Random rng;

	public static void main(String[] args) throws IOException {

		XorSecretSharing test = new XorSecretSharing(3);

		// Test 1 ohne Datei
		byte[] secret1 = new byte[5];
		test.rng.nextBytes(secret1);
		System.out.println("Secret: " + Arrays.toString(secret1));
		byte[][] shares1 = test.share(secret1);
		System.out.println("Shares: " + Arrays.deepToString(shares1));
		byte[] combined1 = test.combine(shares1);
		System.out.println("Combined: " + Arrays.toString(combined1));
		System.out.println("Test bestanden: " + Arrays.equals(secret1, combined1));

		System.out.println();

		// Test 2 ohne Datei
		byte[] secret2 = new byte[5];
		test.rng.nextBytes(secret2);
		System.out.println("Secret: " + Arrays.toString(secret2));
		byte[][] shares2 = test.share(secret2);
		System.out.println("Shares: " + Arrays.deepToString(shares2));
		byte[] combined2 = test.combine(shares2);
		System.out.println("Combined: " + Arrays.toString(combined2));
		System.out.println("Test bestanden: " + Arrays.equals(secret2, combined2));

		System.out.println();

		// Test mit Datei
		// lies Test-Datei aus
		BufferedReader bufferedReader = new BufferedReader(new FileReader("test.txt"));
		String s = bufferedReader.readLine();
		byte[] dateiSecret = s.getBytes(StandardCharsets.UTF_8);
		System.out.println("Datei eingelesen mit Inhalt: " + s);
		System.out.println("Secret: " + Arrays.toString(dateiSecret));

		// erzeuge Shares
		byte[][] dateiShares = test.share(dateiSecret);
		System.out.println("Shares: " + Arrays.deepToString(dateiShares));

		// speichere einzelne Shares
		for (int i = 0; i < test.n; i++) {
			DataOutputStream dos = new DataOutputStream(new FileOutputStream("Share" + (i+1) + ".txt"));
			dos.write(dateiShares[i]);
		}

		// erzeuge wieder arr[][]
		byte[][] combinedShares = new byte[test.n][];
		for (int i = 0; i < test.n; i++) {
			combinedShares[i] = Files.readAllBytes(Path.of("Share" + (i+1) + ".txt"));
		}

		// führe zusammen
		byte[] combined = test.combine(combinedShares);

		// speichere Ergebnis
		DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream("testErgebnis.txt"));
		dataOutputStream.write(combined);

		// lies Datei wieder aus
		bufferedReader = new BufferedReader(new FileReader("testErgebnis.txt"));
		s = bufferedReader.readLine();
		System.out.println("Datei erzeugt mit Inhalt: " + s);
		System.out.println("Test bestanden: " + Arrays.equals(dateiSecret, combined));
	}

}
