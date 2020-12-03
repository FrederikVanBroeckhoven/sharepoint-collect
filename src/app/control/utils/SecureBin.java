package app.control.utils;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SecureBin {

	private ByteBuffer cypher;
	private ByteBuffer data;

	private SecureBin() {
		UUID uuid = generateUUID();

		cypher = ByteBuffer.wrap(new byte[Long.SIZE * 2 / 8]);
		cypher.putLong(uuid.getMostSignificantBits());
		cypher.putLong(uuid.getLeastSignificantBits());		
	}
	
	public SecureBin(int capacity) {
		this();
		data = ByteBuffer.wrap(new byte[capacity + Integer.SIZE / 8]);
		data.putInt(capacity, Arrays.hashCode(cypher.array()));
	}

	public SecureBin(byte[] enc) {
		this();
		data = ByteBuffer.wrap(enc);
	}

	public byte[] getData() {
		return data.array();
	}

	public void encode(int off, byte[] in) {
		ByteBuffer enc = ByteBuffer.wrap(new byte[in.length]);

		for (int i = 0; i < in.length; i++) {
			enc.put((byte) (in[i] ^ cypher.get((off + i) % cypher.capacity())));
		}
		
		data.position(off);
		data.put(enc.array());
	}

	public void encode(byte[] in) {
		encode(0, in);
	}

	public byte[] decode(int off, int length) {
		if (data.capacity() <= Integer.SIZE / 8) {
			return new byte[] {};
		}

		int hash = data.getInt(data.capacity() - Integer.SIZE / 8);

		if (hash != Arrays.hashCode(cypher.array())) {
			return new byte[] {};
		}
		
		ByteBuffer dec = ByteBuffer.wrap(new byte[length]);

		for (int i = 0; i < dec.capacity(); i++) {
			dec.put((byte) (data.get(off + i) ^ cypher.get((off + i) % cypher.capacity())));
		}

		return dec.array();
	}
	
	public byte[] decode() {
		return decode(0, data.capacity() - Integer.SIZE / 8);
	}

	private static UUID generateUUID() {
		return generateUUID(new int[] {});
	}
	
	/**
	 * generate an (standardized) id unique to the current system. This should give:
	 * - different values for different systems
	 * - the same value for the same
	 * system, on different time-stamps
	 */
	private static UUID generateUUID(int[] seeds) {
		return UUID.nameUUIDFromBytes(
				Stream.concat(
						Stream.concat(
								IntStream.of(seeds)
										.boxed()
										.map(i -> Integer.toHexString(i)),
								Stream.of(
										System.getProperty("user.name"),
										System.getProperty("os.name"),
										System.getProperty("os.arch") //
								).filter(str -> str != null)
										.map(str -> str.toLowerCase())
						),
						getMACStrings().stream() //
				).collect(
						Collectors.joining(".") //
				).getBytes() //
		);

	}

	private static List<String> getMACStrings() {
		try {
			return NetworkInterface.networkInterfaces()
					.map(ni -> {
						try {
							return ni.getHardwareAddress();
						} catch (SocketException e) {
							return null;
						}
					})
					.filter(hwa -> hwa != null)
					.map(hwa -> {
						ByteBuffer bb = ByteBuffer.wrap(hwa);
						bb.rewind();
						String result = "";
						if (!bb.hasRemaining()) {
							return result;
						}
						result += Integer.toHexString(bb.get() & 0xff);
						while (bb.hasRemaining()) {
							result += ":" + Integer.toHexString(bb.get() & 0xff);
						}
						return result;
					})
					// to handle quite rare cases, but still important!
					.sorted(Comparator.reverseOrder())
					.collect(Collectors.toList());
		} catch (SocketException e) {
			return Collections.emptyList();
		}

	}

}
