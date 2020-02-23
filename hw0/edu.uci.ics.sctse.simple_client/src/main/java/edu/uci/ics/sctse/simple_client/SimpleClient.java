package edu.uci.ics.sctse.simple_client;

import java.io.*;
import java.net.*;

public class SimpleClient
{
    public static void main (String [] args)
    {
        DataInputStream in;
        DataOutputStream out;

        try
        {
            Socket socket = new Socket("127.0.0.1", 5000);
            System.out.println("Client connected. Type '!q' to quit.");

            in = new DataInputStream(System.in);;
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            out = new DataOutputStream(socket.getOutputStream());

            String line = "";
            while (!line.trim().equals("!q"))
            {
                line = br.readLine();
                out.writeUTF(line);

            }
            in.close();
            out.close();
            socket.close();
        }
        catch (Exception e)
        {
            System.out.println("Error in SimpleClient: " + e);
        }
    }
}
