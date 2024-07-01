package dev.gemfire.cqworker;

import dev.gemfire.dtype.DSemaphore;
import dev.gemfire.dtype.DTypeFactory;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.query.*;
import org.apache.geode.distributed.DistributedLockService;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;

import java.util.concurrent.CountDownLatch;

public class CqWorker {


    public static void main(String[] args) throws RegionNotFoundException, CqException, CqExistsException, InterruptedException {
        ClientCache clientCache = new ClientCacheFactory()
                .addPoolLocator("localhost", 10334)
                .set("durable-client-id", "myDurableClient") // Durable client ID
                .set("durable-client-timeout", "300") // Timeout in seconds
                .setPoolSubscriptionEnabled(true)
                .setPdxSerializer(new ReflectionBasedAutoSerializer("dev.gemfire.*"))
                .create();

        Region<String, String> region = clientCache
                .<String, String>createClientRegionFactory(ClientRegionShortcut.PROXY)
                .create("test");
        region.get(1);
        DTypeFactory factory = new DTypeFactory(clientCache);
        DSemaphore semaphore = factory.createDSemaphore("Worker", 1);
        System.out.println("About to grab a lock");
        semaphore.acquire();
        System.out.println("I have grabbed the lock and I am the only worker.   Adding listeners");

        QueryService queryService = clientCache.getQueryService();
        // Create CqAttributes
        CqAttributesFactory cqAttributesFactory = new CqAttributesFactory();
        cqAttributesFactory.addCqListener(new MyCqListener());
        CqAttributes cqAttributes = cqAttributesFactory.create();

        // Create the Continuous Query
        String cqName = "MyDurableCQ";
        String queryString = "SELECT * FROM /test where guid.hashCode() % 4 = 1";
        CqQuery cqQuery = queryService.newCq(cqName, queryString, cqAttributes, true);
        cqQuery.executeWithInitialResults();
        clientCache.readyForEvents();

        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await();
    }

    private static class MyCqListener implements CqListener {
        @Override
        public void onEvent(CqEvent cqEvent) {
            System.out.println("MyCqListener.onEvent");
            System.out.println("cqEvent.getNewValue() = " + cqEvent.getNewValue());
        }

        @Override
        public void onError(CqEvent cqEvent) {
            System.out.println("MyCqListener.onError");
            System.out.println("cqEvent = " + cqEvent);
        }

    }
}
