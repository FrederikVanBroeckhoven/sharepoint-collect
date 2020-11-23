package test.java;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.javatuples.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import app.control.BinBucket;
import app.control.BinBucket.Token;

class BinBucketTest {

	// just a static random seed
	private static final long SEED = 1459723l;
	private static final char[] ALPHABET = "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~"
			.toCharArray();

	private Random random = new Random();

	private static BinBucket createBinBucket() {
		return BinBucket.createInstance(new BinBucket.Storage() {

			Map<String, Token> store = new HashMap<String, Token>();

			public boolean store(Map<String, Token> toStore) {
				this.store = toStore;
				return true;
			}

			public Map<String, Token> load() {
				return store;
			}
		});
	}

	private String generateUser(int len) {
		return IntStream.range(0, len)
				.boxed()
				.map(i -> random.nextInt(ALPHABET.length))
				.map(i -> String.valueOf(ALPHABET[i]))
				.collect(Collectors.joining());
	}

	private char[] generatePass(int len) {
		return generateUser(len).toCharArray();
	}

	@BeforeEach
	public void reseed() {
		// reset the random number generator, so each time the result is 'predictable'
		random.setSeed(SEED);
	}

	@Test
	void testKeyGeneration() {

		// generate a list of users to test on. Less than 10, so passes are bound to be overwritten
		List<String> givenUsers = Stream.generate(() -> generateUser(10)).limit(10).sorted().collect(Collectors.toList());

		List<Token> tokens = givenUsers.stream().map(user -> Token.encrypt(user, generatePass(10))).collect(Collectors.toList());
		
		List<String> retrievedUsers = tokens.stream().map(token -> token.getKey()).collect(Collectors.toList());
		
		assertEquals(givenUsers, retrievedUsers);
	}

	@Test
	void testSetSinglePass() {
		BinBucket binBucket = createBinBucket();

		String givenUser = generateUser(10);
		char[] givenPass = generatePass(10);

		binBucket.setPass(givenUser, givenPass);

		char[] retrievedPass = binBucket.getPass(givenUser);

		assertArrayEquals(givenPass, retrievedPass);
	}

	@Test
	void testSetSingleSmallPass() {
		BinBucket binBucket = createBinBucket();

		String givenUser = generateUser(10);
		char[] givenPass = generatePass(1);

		binBucket.setPass(givenUser, givenPass);

		char[] retrievedPass = binBucket.getPass(givenUser);

		assertArrayEquals(givenPass, retrievedPass);
	}

	@Test
	void testSetSingleEmptyPass() {
		BinBucket binBucket = createBinBucket();

		String givenUser = generateUser(10);
		char[] givenPass = generatePass(0);

		binBucket.setPass(givenUser, givenPass);

		char[] retrievedPass = binBucket.getPass(givenUser);

		assertArrayEquals(givenPass, retrievedPass);
	}

	@Test
	void testSetMultiPass() {
		BinBucket binBucket = createBinBucket();

		Map<String, char[]> givenUserPasses = Stream.generate(
				() -> Pair.with(generateUser(10), generatePass(10)))
				.limit(10)
				.collect(Collectors.toMap(pair -> pair.getValue0(), pair -> pair.getValue1()));

		givenUserPasses.entrySet().forEach(
				userPass -> binBucket.setPass(userPass.getKey(), userPass.getValue()));

		Set<CharBuffer> retrievedPass = givenUserPasses.keySet().stream().map(
				givenUser -> CharBuffer.wrap(binBucket.getPass(givenUser)))
				.collect(Collectors.toSet());
		Set<CharBuffer> givenPasses = givenUserPasses.entrySet().stream().map(userPass -> CharBuffer.wrap(userPass
				.getValue())).collect(Collectors.toSet());

		assertEquals(retrievedPass, givenPasses);
	}

	@Test
	void testOverwritePass() {
		BinBucket binBucket = createBinBucket();

		// generate a list of users to test on. Less than 10, so passes are bound to be overwritten
		List<String> givenUsers = Stream.generate(() -> generateUser(10)).limit(3).sorted().collect(Collectors.toList());

		// set all of these users with a generated pass, make sure they get overwritten, and collect the mappings that have been set
		Map<String, char[]> givenbUserPasses = new HashMap<String, char[]>();
		IntStream.range(0, 10).boxed()
				.map(i -> Pair.with(givenUsers.get(i % 3), generatePass(10)))
				.forEach(pair -> {
					binBucket.setPass(pair.getValue0(), pair.getValue1());
					givenbUserPasses.put(pair.getValue0(), pair.getValue1());
				});

		// separate the given passes in a set
		Set<CharBuffer> givenPasses = givenbUserPasses.entrySet().stream().map(userPass -> CharBuffer.wrap(userPass
				.getValue())).collect(Collectors.toSet());		
		// collect the passes set in the binBucket
		Set<CharBuffer> retrievedPass = givenbUserPasses.keySet().stream().map(
				givenUser -> CharBuffer.wrap(binBucket.getPass(givenUser)))
				.collect(Collectors.toSet());

		// check if the passes are the same
		assertEquals(retrievedPass, givenPasses);

		// check if the list of users in the binBucket conforms with the given list of users
		List<String> retrievedUsers = binBucket.getUserList();
		assertEquals(retrievedUsers, givenUsers);
	}

}
