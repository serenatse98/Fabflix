package edu.uci.ics.sctse.service.api_gateway.threadpool;

import edu.uci.ics.sctse.service.api_gateway.logger.ServiceLogger;

import static edu.uci.ics.sctse.service.api_gateway.GatewayService.ANSI_RESET;
import static edu.uci.ics.sctse.service.api_gateway.GatewayService.ANSI_YELLOW;


public class ClientRequestQueue
{
    private ListNode head;
    private ListNode tail;

    public ClientRequestQueue()
    {
        head = tail = null;
    }

    public synchronized void enqueue(ClientRequest clientRequest)
    {
        ServiceLogger.LOGGER.info("Enqueuing: " + clientRequest.toString());

        if (isEmpty())
        {
            this.notifyAll();
        }

        ListNode newNode = new ListNode(clientRequest, null);

        if (tail == null)
        {
            head = tail = newNode;
            return;
        }
        else
        {
            tail.setNext(newNode);
            tail = tail.getNext();
        }
//        this.notify();
    }

    public synchronized ClientRequest dequeue()
    {
        if (isEmpty())
        {
            try
            {
                this.wait();
            }
            catch (InterruptedException e)
            {
                ServiceLogger.LOGGER.warning(ANSI_YELLOW + "Interrupted Exception in Worker process." + ANSI_RESET);

            }
//            return null;
        }

        ListNode oldHead = head;
        head = head.getNext();

        if (head == null)
        {
            tail = null;
        }

        return oldHead.getClientRequest();
    }

    boolean isEmpty()
    {
        return head == null;
    }
}
