package org.eclipse.jetty.server;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jetty.io.AsyncEndPoint;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.IO;
import org.junit.Test;

public class SelectChannelAsyncContextTest extends LocalAsyncContextTest
{
    volatile AsyncEndPoint _endp;

    @Override
    protected Connector initConnector()
    {
        return new ChannelHttpConnector(){

            @Override
            public void customize(EndPoint endpoint, Request request) throws IOException
            {
                super.customize(endpoint,request);
                _endp=(AsyncEndPoint)endpoint;
            }
            
        };
    }

    @Override
    protected String getResponse(String request) throws Exception
    {
        ChannelHttpConnector connector = (ChannelHttpConnector)_connector;
        Socket socket = new Socket((String)null,connector.getLocalPort());
        socket.getOutputStream().write(request.getBytes("UTF-8"));
        return IO.toString(socket.getInputStream());
    }

    @Test
    public void testSuspendResumeWithAsyncDispatch() throws Exception
    {
        // Test that suspend/resume works in the face of spurious asyncDispatch call that may be
        // produced by the SslConnection
        final AtomicBoolean running = new AtomicBoolean(true);
        Thread thread = new Thread()
        {
            public void run()
            {
                while (running.get())
                {
                    try 
                    {
                        TimeUnit.MILLISECONDS.sleep(200);
                        AsyncEndPoint endp=_endp;
                        if (endp!=null && endp.isOpen())
                            endp.asyncDispatch();
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        };
        
        try
        {
            thread.start();
            testSuspendResume();
        }
        finally
        {
            running.set(false);
            thread.join();
        }
    }
}
