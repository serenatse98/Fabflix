package edu.uci.ics.sctse.service.api_gateway.threadpool;

import edu.uci.ics.sctse.service.api_gateway.logger.ServiceLogger;

import static edu.uci.ics.sctse.service.api_gateway.GatewayService.ANSI_PURPLE;
import static edu.uci.ics.sctse.service.api_gateway.GatewayService.ANSI_RESET;

public class ThreadPool {
    private int numWorkers;
    private Worker[] workers;
    private ClientRequestQueue queue;

    public ThreadPool(int numWorkers)
    {
        queue = new ClientRequestQueue();
        workers = new Worker[numWorkers];
        this.numWorkers = numWorkers;
        for (int i = 0; i < numWorkers; ++i)
        {
            workers[i] = Worker.CreateWorker(i, this);
            ServiceLogger.LOGGER.info(ANSI_PURPLE + "New worker created: " + workers[i] + ANSI_RESET);
        }

        for (Worker w : workers)
        {
            w.start();
        }
    }

    public void add(ClientRequest clientRequest)
    {
        queue.enqueue(clientRequest);
    }

    public ClientRequest remove()
    {
        return queue.dequeue();
    }

    public ClientRequestQueue getQueue()
    {
        return queue;
    }


}
