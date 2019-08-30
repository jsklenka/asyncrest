package psg.asyncrest.models;

import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class AdvancedRestTemplate extends RestTemplate {


    public AdvancedRestTemplate(){
        super();
        this.setInterceptors(Collections.singletonList(new LoggingInterceptor()));
    }

}
