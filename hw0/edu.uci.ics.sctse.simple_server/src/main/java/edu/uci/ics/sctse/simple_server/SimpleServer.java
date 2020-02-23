package edu.uci.ics.sctse.simple_server;

import java.io.*;
import java.net.*;
import java.util.regex.*;

public class SimpleServer
{
    private ServerSocket server;

    DataInputStream in;

    public SimpleServer(int port)
    {
        try
        {
            server = new ServerSocket(port);
            System.out.println("Server started. \nWaiting for a client...");
        }

        catch (Exception e)
        {
            System.out.println("Error in SimpleServer constructor: " + e);
        }
    }

    public void runServer()
    {
        try
        {
            Socket socket = server.accept();
            System.out.println("Client accepted.");

            BufferedInputStream bin = new BufferedInputStream(socket.getInputStream());
            in = new DataInputStream(bin);

            String line = "";
            while (!line.trim().equals("!q"))
            {
                line = in.readUTF();
                try
                {
                    if (line.equals("")) { }

                    else if (line.matches("\\d+[+\\-*/]\\d+"))
                    {
                        String[] getOP = line.split("\\d+");
                        String operand = getOP[1];

                        String[] getNum = line.split("[+\\-*/]");
                        int num1 = Integer.parseInt(getNum[0]);
                        int num2 = Integer.parseInt(getNum[1]);

                        switch (operand)
                        {
                            case "+":
                                System.out.println((num1 + num2));
                                break;

                            case "-":
                                System.out.println((num1 - num2));
                                break;

                            case "*":
                                System.out.println((num1 * num2));
                                break;

                            case "/":
                                System.out.println((Double.parseDouble(getNum[0]) / num2));
                                break;
                        }
                    }

                    else if (line.matches("([A-Za-z]+\\s{0,1})+[.!?]{0,1}"))
                    {
                        String[] splitLine = line.split("\\s");
                        System.out.println(splitLine.length);
                    }

                    else
                    {
                        throw new InvalidDataException("InvalidDataException: " + line);
                    }
                }

                catch (InvalidDataException ide)
                {
                    System.out.println("InvalidDataException: Incorrect formatting: " + line);
                }
            }

            System.out.println("Closing connection...");
            socket.close();
            in.close();
            System.out.println("Connection closed.");
        }
        catch (Exception e)
        {
            System.out.println("Error in runServer: " + e);
        }
    }

    public static void main (String [] args)
    {
        SimpleServer s = new SimpleServer(5000);
        s.runServer();
    }
}
