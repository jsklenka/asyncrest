package psg.asyncrest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import psg.asyncrest.models.AdvancedRestTemplate;
import psg.asyncrest.models.ParallelRest;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;


@RunWith(SpringRunner.class)
@SpringBootTest
public class RestLoggingTests {

    private static Logger logger = LoggerFactory.getLogger(psg.asyncrest.AsyncrestApplicationTests.class);
    private static final String apiEndPointTemplate = "https://jsonplaceholder.typicode.com/todos/";

    @Test()
    public void RunRestWithLogging() throws ExecutionException, InterruptedException {


        String todo1Url = apiEndPointTemplate + '1';
        String todo2Url = apiEndPointTemplate + '2';

        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(new Supplier<String>() {

            @Override
            public String get() {
                return MakeRestCallWithLogging(todo1Url);
            }

        });

        CompletableFuture<String> cf2 = CompletableFuture.supplyAsync(new Supplier<String>() {

            @Override
            public String get() {
                return MakeRestCallWithLogging(todo2Url);
            }

        });

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assert.isTrue(cf1.isDone());
        Assert.isTrue(cf2.isDone());
    }

    public String MakeRestCallWithLogging(String url){
        AdvancedRestTemplate art = new AdvancedRestTemplate();
        String response = art.getForObject(url, String.class);
        logger.info("rest call: " + response + " completed");
        return response;
    }

}

