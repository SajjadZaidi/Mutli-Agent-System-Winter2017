package Ares.Common.Network;

import java.io.*;
import java.net.*;

/**
 * This class consists of a TCP socket on an Agent connected to the Ares server.
 * Reading generally has no timeout value and sending has the possibility of a
 * write block on the TCP buffered output stream if Ares stops reading off it.
 *
 * @author Jonathan Hudson
 */
public class AresSocket extends Thread {

    /**
     * Socket connection.
     */
    private Socket socket;
    /**
     * The input stream for reading messages from Ares.
     */
    private BufferedInputStream in;
    /**
     * The output stream for messages sent to Ares.
     */
    private BufferedOutputStream out;

    /**
     * Connect to Ares.
     * @param host The host of Ares.
     * @param port The port of Ares.
     * @throws AresSocketException Exception if connection fails.
     */
    public void connect(String host, int port) throws AresSocketException {
        try {
            socket = new Socket(host, port);
            in = new BufferedInputStream(socket.getInputStream());
            out = new BufferedOutputStream(socket.getOutputStream());
        } catch (Throwable th) {
            throw new AresSocketException("Unable to connect agent to Ares.");
        }
    }

    /**
     * Disconnect (if connected).
     */
    public void disconnect() {
        try {
            if (socket != null) {
                in.close();
                out.close();
                socket.close();
                socket = null;
            }
        } catch (Throwable th) {
        }
    }

    /**
     * When class is destroyed ensure that it disconnects first.
     */
    @Override
    public void finalize() {
        try {
            super.finalize();
            disconnect();
        } catch (Throwable th) {
        }
    }

    /**
     * Read a string message from socket.
     * @param timeout The milliseconds to wait for a message on socket.
     * @return The string form of the read message.
     * @throws AresSocketException Exception if reading fails.
     */
    public String readMessage(int timeout) throws AresSocketException {
        //Only read if connected
        if (socket != null) {
            setTimeout(timeout);
            return readMessage();
        } else {
            return "";
        }
    }

    /**
     * Read a string message from socket.
     * @return The string form of the read message.
     * @throws AresSocketException Exception if reading fails.
     */
    public String readMessage() throws AresSocketException {
        try {
            byte[] size_buffer = new byte[4];
            if (in.read(size_buffer, 0, 4) != 4) {
            	int size = (size_buffer[0] & 0xFF) + ((size_buffer[1] & 0xFF) << 8) + ((size_buffer[2] & 0xFF) << 16) + ((size_buffer[3] & 0xFF) << 24);
            	System.out.println("error size: " + size);
                throw new AresSocketException("Couldn't read message length.");
            }
            int size = (size_buffer[0] & 0xFF) + ((size_buffer[1] & 0xFF) << 8) + ((size_buffer[2] & 0xFF) << 16) + ((size_buffer[3] & 0xFF) << 24);
            size--;
            byte[] message_buffer = new byte[size];
            if (in.read(message_buffer, 0, size) != size) {
                throw new AresSocketException("Message is shorter than expected.");
            }
            if (in.read(size_buffer, 0, 1) != 1) {
                throw new AresSocketException("Message is shorter than expected.");
            }
            String message = new String(message_buffer).trim();
            resetTimeout();
            return message;
        } catch (SocketTimeoutException ste) {
            resetTimeout();
            return null;
        } catch (Throwable th) {
            resetTimeout();
            throw new AresSocketException(th.getMessage());
        }
    }

    public void sendMessage(String message) throws AresSocketException {
        //Only send if connected
        if (socket != null) {
            try {
                byte[] message_buffer = new byte[4];
                byte[] size_buffer = message.toString().getBytes("US-ASCII");
                int size = size_buffer.length + 1;
                message_buffer[0] = (byte) (size & 0xFF);
                message_buffer[1] = (byte) ((size >> 8) & 0xFF);
                message_buffer[2] = (byte) ((size >> 16) & 0xFF);
                message_buffer[3] = (byte) ((size >> 24) & 0xFF);
                out.write(message_buffer);
                out.write(size_buffer);
                out.write((byte) 0);
                out.flush();
              // System.out.println(message+"-----"+size);
            } catch (Exception ex) {
                throw new AresSocketException("Unable to send message to Ares.");
            }
        } 
    }

    /**
     * Set the timeout while silently handling exceptions.
     * @param timeout New milliseconds of timeout
     */
    private void setTimeout(int timeout) {
        try {
            socket.setSoTimeout(timeout);
        } catch (Throwable th) {
        }
    }

    /**
     * Reset the timeout to forever while silently handling exceptions.
     */
    private void resetTimeout() {
        try {
            socket.setSoTimeout(0);
        } catch (Throwable th) {
        }
    }
}
