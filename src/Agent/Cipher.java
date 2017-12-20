package Agent;

import org.apache.commons.codec.binary.Base64;

import Ares.Common.Commands.AresCommands.FWD_MESSAGE;

public class Cipher 
{
	private static final String KEY = "I_have A BIG DoG and n0 C@TZ!!1";
	
	public static String encrypt(final String text) 
	{
		return Base64.encodeBase64String(Cipher.xor(text.getBytes()));
	}
	
	public static FWD_MESSAGE decrypt(final FWD_MESSAGE fwd_msg) 
	{
		try 
		{
			String hash = fwd_msg.getMessage();
			String decrypt = new String(Cipher.xor(Base64.decodeBase64(hash.getBytes())), "UTF-8");
			FWD_MESSAGE fwd_decrypted = new FWD_MESSAGE(fwd_msg.getFromAgentID(), fwd_msg.getAgentIDList(), decrypt);
			return  fwd_decrypted;
		} 
		catch (java.io.UnsupportedEncodingException ex) 
		{
			throw new IllegalStateException(ex);
		}
	}
	
	private static byte[] xor(final byte[] input) 
	{
		final byte[] output = new byte[input.length];
		final byte[] secret = Cipher.KEY.getBytes();
		int spos = 0;
		for (int pos = 0; pos < input.length; ++pos) {
			output[pos] = (byte) (input[pos] ^ secret[spos]);
			spos += 1;
			if (spos >= secret.length) {
				spos = 0;
			}
		}
		return output;
	}
}
