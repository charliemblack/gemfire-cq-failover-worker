package dev.gemfire.cqworker;

import io.codearte.jfairy.Fairy;
import io.codearte.jfairy.producer.person.Person;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.distributed.DistributedLockService;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;

import java.util.UUID;

public class DataLoader {

    public static void main(String[] args) throws InterruptedException {

        ClientCache clientCache = new ClientCacheFactory()
                .addPoolLocator("localhost", 10334)
                .setPdxSerializer(new ReflectionBasedAutoSerializer("dev.gemfire.*"))
                .setPdxReadSerialized(false)
                .create();

        Region<String, Customer> region = clientCache
                .<String, Customer>createClientRegionFactory(ClientRegionShortcut.PROXY)
                .create("test");

        DistributedLockService lockService = DistributedLockService.create("CqWorkerServiceLock", clientCache.getDistributedSystem());
        System.out.println("About to grab lock");
        boolean locked = lockService.lock("mylock", -1, -1);
        System.out.println("I have grabbed the lock and I am the only worker "+ locked + ".   Adding listeners");

        Fairy fairy = Fairy.create();
        while(true){
            Person person = fairy.person();

            Customer customer = Customer.builder()
                    .firstName(person.getFirstName())
                    .middleName(person.getMiddleName())
                    .lastName(person.getLastName())
                    .email(person.getEmail())
                    .username(person.getUsername())
                    .passportNumber(person.getPassportNumber())
                    .password(person.getPassword())
                    .telephoneNumber(person.getTelephoneNumber())
                    .dateOfBirth(person.getDateOfBirth().toString())
                    .age(person.getAge())
                    .companyEmail(person.getCompanyEmail())
                    .nationalIdentificationNumber(person.getNationalIdentificationNumber())
                    .nationalIdentityCardNumber(person.getNationalIdentityCardNumber())
                    .passportNumber(person.getPassportNumber())
                    .guid(UUID.randomUUID().toString()).build();
            region.put(customer.getGuid(), customer);
            System.out.println("customer = " + customer);
            Thread.sleep(10);
        }
    }
}
