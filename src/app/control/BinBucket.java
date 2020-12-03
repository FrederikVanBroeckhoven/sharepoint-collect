package app.control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.javatuples.Pair;

import app.control.utils.SecureBin;
import io.reactivex.rxjava3.core.Observable;

public class BinBucket {

	public static class Token {

		public String lock;

		public Token(String lock) {
			this.lock = lock;
		}

		public static Token encrypt(String user, char[] pass) {
			Charset charset = Charset.defaultCharset();

			ByteBuffer userBytes = charset.encode(CharBuffer.wrap(user));
			ByteBuffer passBytes = charset.encode(CharBuffer.wrap(pass));
			ByteBuffer tokenBytes = ByteBuffer.wrap(
					new byte[userBytes.limit() + passBytes.limit() + 2 * Integer.BYTES]);

			tokenBytes.putInt(userBytes.limit()).put(userBytes);
			tokenBytes.putInt(passBytes.limit()).put(passBytes);

			SecureBin tokenSBin = new SecureBin(tokenBytes.capacity());
			tokenSBin.encode(tokenBytes.array());

			String encoded = Base64.getEncoder().encodeToString(tokenSBin.getData());

			return new Token(encoded);
		}

		public Pair<String, char[]> decrypt() {
			SecureBin passBin = new SecureBin(Base64.getDecoder().decode(lock));

			ByteBuffer decoded = ByteBuffer.wrap(passBin.decode());

			int userSize = decoded.getInt();
			byte[] userBytes = new byte[userSize];
			decoded.get(userBytes, 0, userSize);

			int passSize = decoded.getInt();
			byte[] passBytes = new byte[passSize];
			decoded.get(passBytes, 0, passSize);

			Charset charset = Charset.defaultCharset();

			return Pair.with(
					new String(charset.decode(ByteBuffer.wrap(userBytes)).array()),
					charset.decode(ByteBuffer.wrap(passBytes)).array());
		}

		public String decryptUser() {
			SecureBin passBin = new SecureBin(Base64.getDecoder().decode(lock));

			ByteBuffer decoded = ByteBuffer.wrap(passBin.decode());

			int userSize = decoded.getInt();
			byte[] userBytes = new byte[userSize];
			decoded.get(userBytes, 0, userSize);

			Charset charset = Charset.defaultCharset();

			return new String(charset.decode(ByteBuffer.wrap(userBytes)).array());
		}

		public String getKey() {

			SecureBin passBin = new SecureBin(Base64.getDecoder().decode(lock));

			ByteBuffer decoded = ByteBuffer.wrap(passBin.decode());

			int userSize = decoded.getInt();

			byte[] userBytes = new byte[userSize];
			decoded.get(userBytes, 0, userSize);

			Charset charset = Charset.defaultCharset();
			
			return new String(charset.decode(ByteBuffer.wrap(userBytes)).array());
			
//			return calculateKey(userBytes);
		}

		public static String calculateKey(String key) {
//			Charset charset = Charset.defaultCharset();
//			return calculateKey(charset.encode(CharBuffer.wrap(key)).array());
			return key;
		}

//		private static String calculateKey(byte[] key) {
//			String String = String.nameStringFromBytes(key);
////			ByteBuffer StringBuffer = ByteBuffer.allocate(Long.BYTES * 2);
////			StringBuffer.putLong(String.getLeastSignificantBits());
////			StringBuffer.putLong(String.getMostSignificantBits());
//
//			return String;
//		}

		@Override
		public int hashCode() {
			return Objects.hash(getKey());
		}

		@Override
		public boolean equals(Object obj) {
			return super.equals((Token) obj);
		}

		public boolean equals(Token token) {
			return getKey().equals(token.getKey());
		}

	}

	public static interface Storage {

		public Map<String, Token> load();

		public boolean store(Map<String, Token> toStore);

	}

	public static class DefaultDiskStorage implements Storage {

		private static final String PASSES_PATH = "data/binbucket";

		public DefaultDiskStorage() {
		}

		@Override
		public Map<String, Token> load() {
			File file = new File(PASSES_PATH);

			if (file.isDirectory()) {
				// error
				return null;
			}

			if (!file.exists()) {
				return new HashMap<String, Token>();
			}

			BufferedReader reader = null;
			Map<String, Token> tokens = null;
			try {
				reader = new BufferedReader(new FileReader(file));

				tokens = reader.lines()
						.map(line -> new Token(line))
						.collect(Collectors.toMap(token -> token.getKey(), token -> token));

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			return tokens;
		}

		@Override
		public boolean store(Map<String, Token> tokens) {

			File file = new File(PASSES_PATH);

			if (file.isDirectory()) {
				// error
				return false;
			}

			file.getParentFile().mkdirs();

			if (file.exists()) {
				file.delete();
			}

			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}

			PrintWriter writer = null;
			try {
				writer = new PrintWriter(new FileWriter(file));

				final PrintWriter finalWriter = writer;
				tokens.entrySet().stream()
						.map(entry -> entry.getValue())
						.map(token -> token.lock)
						.forEach(lock -> finalWriter.println(lock));
				finalWriter.close();

				return true;
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				writer.close();
			}

			return false;
		}

	}

	private static BinBucket binBucket;

	public static BinBucket getInstance() {
		if (binBucket == null) {
			binBucket = createInstance(new DefaultDiskStorage());
		}
		return binBucket;
	}
	
	public static BinBucket createInstance(Storage storage) {
		return new BinBucket(storage);
	}	

	private Map<String, Token> tokens;
	private Storage storage;

	private BinBucket(Storage storage) {
		this.storage = storage;
	}

	public void setPass(String user, char[] passcode) {
		if (tokens == null) {

			tokens = storage.load();

			if (tokens == null) {
				return;
			}

		}
		
		Token token = Token.encrypt(user, passcode);

		String key = token.getKey();

		tokens.put(key, token);
	}

	public char[] getPass(String user) {
		if (tokens == null) {

			tokens = storage.load();

			if (tokens == null) {
				return null;
			}

		}

		String key = Token.calculateKey(user);
		
		Token token = tokens.get(key);

		if (token != null) {
			
			Pair<String, char[]> pair = token.decrypt();

			return pair.getValue1();
		}

		return null;

	}

	public List<String> getUserList() {
		if (tokens == null) {

			tokens = storage.load();

			if (tokens == null) {
				return Collections.emptyList();
			}

		}

		return tokens.entrySet().stream()
				.map(entry -> entry.getValue())
				.map(token -> token.decryptUser())
				.sorted()
				.distinct()
				.collect(Collectors.toList());
	}

	public Observable<String> getUsers$() {

		return Observable.defer(
				() -> {
					List<String> users = getUserList();
					return Observable.fromIterable(users);
				});

	}

	public void invalidate(boolean flush) {
		if (flush) {
			if(!storage.store(tokens)) {
				return;
			}
		}
		tokens.clear();
		tokens = null;
	}

}
