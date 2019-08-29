# asyncrest

Examples of the various options we have to do async apis in Java Spring boot

WebClient is the new alternative to AsyncRestTemplate. It does the job but it is a little bit cumbersome. 
Also if our web calls are all in rest templates, it would involve changing the standard.

The other two examples use CompletableFuture class, which allows for asynchronous processing of otherwise synchronous code. 

We could choose to use the completableFuture class directly (Test titled RunRestAsAsync) or create a wrapper class (Test titled ParallelRestClassTest)

