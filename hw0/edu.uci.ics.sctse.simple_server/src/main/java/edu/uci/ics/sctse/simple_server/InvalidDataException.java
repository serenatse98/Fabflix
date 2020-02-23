package edu.uci.ics.sctse.simple_server;

public class InvalidDataException
    extends Exception
{
    public InvalidDataException() {}

    public InvalidDataException(String message)
    {
        super(message);
    }
}
