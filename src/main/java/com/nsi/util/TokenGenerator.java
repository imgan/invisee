package com.nsi.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.mapdb.DBMaker;

@Component
public class TokenGenerator {

    private static final int BUF_SIZE = Long.SIZE / Byte.SIZE;
    private static final SecureRandom random = new SecureRandom();
    private static final Logger logger = LoggerFactory.getLogger(TokenGenerator.class);

    private static final int RAND_LENGTH = 124;
    private static final int TOKEN_LENGTH = RAND_LENGTH + BUF_SIZE;

    private static final Map<String, String> map = DBMaker.newCacheDirect(0.01d);
    private static final Map<String, Long> lastUsageMap = DBMaker.newCacheDirect(0.01d);

    public static String generateToken() {
        byte[] b = new byte[RAND_LENGTH];

        long ctm = System.currentTimeMillis();
        byte[] t = longToBytes(ctm);
        if (logger.isDebugEnabled()) {
            logger.debug("timestamp = " + ctm);
            logger.debug("t length = " + t.length);
        }
        random.nextBytes(b);
        byte[] tstampAndRand = new byte[TOKEN_LENGTH];
        System.arraycopy(t, 0, tstampAndRand, 0, BUF_SIZE);
        System.arraycopy(b, 0, tstampAndRand, BUF_SIZE, b.length);
        String token = Base64.encodeBase64URLSafeString(tstampAndRand);
        touch(token);
        return token;
    }

    /*public static void main(String[] args) throws InterruptedException{
		BasicConfigurator.configure();
		String token = generateToken();
		System.out.println(token);
		//Thread.sleep(100);
		long s = System.currentTimeMillis();
		String hash = hash(token, "10.0.209.14");
		System.out.println("Elapsed = "+(System.currentTimeMillis()-s));
		System.out.println("Hash = "+hash);
		System.out.println(verify(token, hash, "10.0.209.14", 10000, 5000, 100));
		Thread.sleep(10000);
		System.out.println(verify(token, hash, "10.0.209.14", 10000, 5000, 100));
		System.out.println(getTimestamp(token));
		s = System.currentTimeMillis();
		System.out.println("Hash again = "+hash(token, "10.0.209.14"));
		System.out.println("Elapsed = "+(System.currentTimeMillis()-s));
		s = System.currentTimeMillis();
		System.out.println("Hash again = "+hash(token, "10.0.109.14"));
		System.out.println("Elapsed = "+(System.currentTimeMillis()-s));
	}*/
    public static String hash(String token, String salt) {
        String key = token + salt;
        String hash = map.get(key);
        if (hash == null) {
            SHA512Digest digest = new SHA512Digest();
            byte[] b;
            byte[] saltB;
            try {
                b = token.getBytes("UTF-8");
                saltB = salt.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            digest.update(b, 0, b.length);
            digest.update(saltB, 0, saltB.length);
            digest.finish();
            byte[] out = new byte[digest.getDigestSize()];
            digest.doFinal(out, 0);
            hash = Base64.encodeBase64URLSafeString(out);
            map.put(key, hash);
        }
        return hash;
    }

    public static long getTimestamp(String token) {
        return bytesToLong(Arrays.copyOfRange(Base64.decodeBase64(token), 0, BUF_SIZE));
    }

    public static byte[] longToBytes(long x) {
        return ByteBuffer.allocate(BUF_SIZE).putLong(x).array();
    }

    public static boolean isExpired(String token, long timeToLive, long timeoutMillis, long toleranceMillis) {
        long timestamp = getTimestamp(token);
        long currentTime = System.currentTimeMillis();
        if (timestamp + timeToLive + toleranceMillis < currentTime) {
            if (logger.isDebugEnabled()) {
                logger.debug("Too Old");
            }
            return true;
        }
        /*Long lastUsage = lastUsageMap.get(token);
        if (lastUsage == null || (lastUsage + timeoutMillis + toleranceMillis < currentTime)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Timeout");
            }
            return true;
        }*/
        return false;
    }

    public static void touch(String token) {
        lastUsageMap.put(token, System.currentTimeMillis());
    }

    public static boolean verify(String token, String hash, String salt, long timeToLive, long timeoutMillis, long toleranceMillis) {
        if (isExpired(token, timeToLive, timeoutMillis, toleranceMillis)) {
            return false;
        }
        return hash(token, salt).equals(hash);
    }

    public static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(BUF_SIZE).put(bytes);
        buffer.flip();//need flip 
        return buffer.getLong();
    }

}
