package psg.asyncrest.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.stream.Collectors;

//This class could be augmented to support throttling of outgoing requests
public class ParallelRest<T> {

    private List<CompletableFuture<T>> restTemplateList;


    public ParallelRest(){
        restTemplateList = new ArrayList<CompletableFuture<T>>();
    }

    public void QueueForExecution(ExecuteRest<T> restExecution){

        CompletableFuture<T> cf = CompletableFuture.supplyAsync(new Supplier<T>() {

            @Override
            public T get() {
                return restExecution.execute();
            }

        });

        restTemplateList.add(cf);

    }

    public List<T> GetAll() throws ExecutionException, InterruptedException {
        CompletableFuture<T>[] array = restTemplateList.toArray(new CompletableFuture[restTemplateList.size()]);

        CompletableFuture<Void> result = CompletableFuture.allOf(array);

        //Once every async call is completed, combine results into one list
        CompletableFuture<List<T>> getList = result.thenApply(v -> Arrays.stream(array).map(future -> future.join()).collect(Collectors.<T>toList()));

        return getList.get();
    }

    public interface ExecuteRest<T>{
        T execute();
    }


}

