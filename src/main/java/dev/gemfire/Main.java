package dev.gemfire;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.query.CqAttributesFactory;
import org.apache.geode.cache.query.CqEvent;
import org.apache.geode.cache.query.CqListener;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;

import java.util.Random;

public class Main {

    public static void main(String[] args) throws Exception {
        ClientCache clientCache = new ClientCacheFactory()
                .addPoolLocator("localhost", 10334)
                .create();

        Region<String, String> region = clientCache
                .<String, String>createClientRegionFactory(ClientRegionShortcut.PROXY)
                .create("test");

        region.put("myKey", "myValue");
        String result = region.get("myKey");
        System.out.println("result = " + result);
    }
}
