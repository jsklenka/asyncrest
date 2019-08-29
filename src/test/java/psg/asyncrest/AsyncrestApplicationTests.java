package psg.asyncrest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import psg.asyncrest.models.ParallelRest;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;


@RunWith(SpringRunner.class)
@SpringBootTest
public class AsyncrestApplicationTests {

    @Test
    public void contextLoads() {
    }

    private static Logger logger = LoggerFactory.getLogger(psg.asyncrest.AsyncrestApplicationTests.class);
    private static final String apiEndPointTemplate = "https://jsonplaceholder.typicode.com/todos/";


    @Test()
    public  void ParallelRestClassTest() throws ExecutionException, InterruptedException {

        //Example for testing multiple web calls in the same execution path, that we want to combine as one execution

        ParallelRest pr = new ParallelRest<String>();

        String todo1Url = apiEndPointTemplate + '1';
        String todo2Url = apiEndPointTemplate + '2';

        //Queue for execution starts rest calls immediately
        pr.QueueForExecution(() -> MakeRestCall(todo1Url));
        pr.QueueForExecution(() -> MakeRestCall(todo2Url));

        List<String> output = pr.GetAll();

        for (String s : output){
            logger.info("output from RestParallel: " + s);
        }

    }

    @Test()
    public  void RunRestAsAsync() throws ExecutionException, InterruptedException {


        String todo1Url = apiEndPointTemplate + '1';
        String todo2Url = apiEndPointTemplate + '2';

        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(new Supplier<String>() {

            @Override
            public String get() {
                return MakeRestCall(todo1Url);
            }

        });

        CompletableFuture<String> cf2 = CompletableFuture.supplyAsync(new Supplier<String>() {

            @Override
            public String get() {
                return MakeRestCall(todo2Url);
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


    public String MakeRestCall(String url){
        RestTemplate rt = new RestTemplateBuilder().build();
        String response = rt.getForObject(url, String.class);
        logger.info("rest call: " + response + " completed");
        return response;
    }

    @Test
    public void ParallelWebClientTest() throws ExecutionException, InterruptedException {


        logger.info("Before: Without completable futures");
        Mono<ClientResponse> call1 = MakeWebClientCall(1);
        Mono<ClientResponse> call2 = MakeWebClientCall(2);
        Mono<ClientResponse> call3 = MakeWebClientCall(3);
        logger.info("After: Without completable futures");

        call1.block().bodyToMono(String.class).block();
        call2.block().bodyToMono(String.class).block();
        call3.block().bodyToMono(String.class).block();

    }


    public Mono<ClientResponse> MakeWebClientCall(int id){
        String url = apiEndPointTemplate + id;

        WebClient client3 = WebClient
                .builder()
                .baseUrl(url)
                .defaultHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        Mono<ClientResponse> response = client3.get().exchange();

        return response;
    }


}
