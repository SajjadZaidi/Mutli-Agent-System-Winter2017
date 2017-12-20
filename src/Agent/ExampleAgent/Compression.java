package Agent.ExampleAgent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import Ares.Common.Commands.AresCommands.FWD_MESSAGE;

public class Compression {
	 public static String compressString(String srcTxt)
		      throws IOException {
		    ByteArrayOutputStream rstBao = new ByteArrayOutputStream();
		    GZIPOutputStream zos = new GZIPOutputStream(rstBao);
		    zos.write(srcTxt.getBytes());
		    IOUtils.closeQuietly(zos);
		    
		    byte[] bytes = rstBao.toByteArray();
		    // In my solr project, I use org.apache.solr.co mmon.util.Base64.
		    // return = org.apache.solr.common.util.Base64.byteArrayToBase64(bytes, 0,
		    // bytes.length);
		    return Base64.encodeBase64String(bytes);
		  }
	 @SuppressWarnings("deprecation")
	public static FWD_MESSAGE  uncompressString(FWD_MESSAGE fwd_message)
		      throws IOException {
		    String result = null;
		    String zippedBase64Str=fwd_message.getMessage();
		 
		    // In my solr project, I use org.apache.solr.common.util.Base64.
		    // byte[] bytes =
		    // org.apache.solr.common.util.Base64.base64ToByteArray(zippedBase64Str);
		    byte[] bytes = Base64.decodeBase64(zippedBase64Str);
		    GZIPInputStream zi = null;
		    try {
		      zi = new GZIPInputStream(new ByteArrayInputStream(bytes));
		      result = IOUtils.toString(zi);
		    } finally {
		      IOUtils.closeQuietly(zi);
		    }
		   // fwd_message.STR_SEND_MESSAGE;
		    return new FWD_MESSAGE(fwd_message.getFromAgentID(),fwd_message.getAgentIDList(),result);
		  }
}
