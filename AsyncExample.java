import java.util.concurrent.CompletableFuture;

public class AsyncExample {
    public static void main(String[] args) {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            // Simula uma tarefa demorada
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 42;
        });

        // Executa uma ação quando o resultado estiver pronto
        future.thenAccept(result -> System.out.println("Resultado: " + result));

        System.out.println("Tarefa principal continua executando...");

        // Aguarda a conclusão da tarefa assíncrona
        future.join();
        future.join();
    }
}
