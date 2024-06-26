/**************************************************
 * CS 391 - Spring 2024 - A5
 *
 * File: Server.java
 *
 **************************************************/

import java.io.FileInputStream;
import java.io.File;

/**
 * The server program for the application-level protocol
 */
public class Server
{
    private RDT rdt;     // the server-side rdt instance

    /**
     * Constructor: creates the rdt instance with its IP address and the port
     *              number of its receiver, as well as the port number of its
     *              peer's receiver; then the thread sleeps of 0.1 second.
     */
    public Server(String ipAddress, 
                  int rcvPortNum, 
                  int peerRcvPortNum) throws Exception
    {
    	 rdt = new RDT(ipAddress, rcvPortNum, peerRcvPortNum, "S");
    	 run();
    }// constructor

    /**
     * Implements the server-side of the application-level protocol as 
     * described in the handout. The messages to be sent to the console are
     * also specified in the traces given in the handout, namely, the lines
     * starting with the string "SERVER ".
     *
     * When the server is done, make the thread wait for two seconds before
     * terminating the program.
     */
    public void run() throws Exception
    {
    	A5.print("S", "SERVER started");
        getRequest();
        String fileName = getRandomImageFile();
        if (fileName != null) {
        	 sendFileName(fileName);
             sendFile(new FileInputStream(fileName));
        }
        else {
        	rdt.sendData(new byte[] {A5.MSG_NO_IMG_FILE_AVAILABLE});
        }
    	A5.print("S", "SERVER done");
        try { Thread.sleep(2000); } catch (Exception e) { }
    	A5.print("S", "SERVER shutting down");
    }// run

    // Do not modify this method
    private String getRandomImageFile()
    {
        File dir = new File(A5.IMG_SUBFOLDER);
        String [] entireFileList = dir.list();
        String [] imgFileList = new String[entireFileList.length];
        int imgCount = 0;
        
        for( int i = 0; i < entireFileList.length; i++ )
        {
            String filename = entireFileList[i].toLowerCase();
            if ( filename.endsWith(".jpg") ||
                 filename.endsWith(".jpeg") ||
                 filename.endsWith(".gif") ||
                 filename.endsWith(".png"))
                imgFileList[imgCount++] = entireFileList[i];
        }
        if (imgCount == 0)
            return null;
        int index = new java.util.Random().nextInt(imgCount);
        return imgFileList[index];
    }// getRandomImageFile

    // Do not modify this method
    public static void main(String[] args)
    {
        try
        {
            new Server(args.length != 1 ? null : args[0],
                       A5.SERVER_RCV_PORT_NUM,
                       A5.SERVER_PEER_RCV_PORT_NUM).run();
        }
        catch (Exception e)
        {
            A5.print("S","SERVER closing due to error in main: "
                               + e);
        }
    }// main

    /******************************************************************
     *   private methods
     ******************************************************************/

    // sends to the client, the name of the image file given as parameter
    private void sendFileName(String inFileName)
    {
    	byte[] data = inFileName.getBytes();
    	byte[] updated = new byte[data.length + 1];
    	updated[0] = A5.MSG_FILE_NAME;
    	for (int i = 1; i < data.length; i++) {
    		updated[i] = data[i-1];
    	}
        rdt.sendData(updated);
    	A5.print("S", "SERVER sent file name \"" + inFileName + "\"");
    }// sendFileName

    // sends to the client the chunk(s) of the file to be read from the given
    // input stream
    private void sendFile(FileInputStream in) throws Exception
    {
        // To be completed
    	
    }// sendFile

    // waits for the file request from the client.
    // As explained in the handout, uses a flag-based loop that causes the
    // thread to yield until a MSG_REQUEST_IMG_FILE sent by the client is
    // received
    private void getRequest()
    {
    	boolean waiting = true;
    	A5.print("S", "SERVER looping in getRequest");
        while (waiting) {
        	if (rdt.receiveData()[0] == A5.MSG_REQUEST_IMG_FILE) {
            	A5.print("S", "SERVER got request for image file");
        		waiting = false;
        	}
        }
    }// getRequest

}// Server
