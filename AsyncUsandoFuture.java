import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AsyncUsandoFuture {
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Criar uma instância de Callable para cada tarefa assíncrona
        Callable<Integer> task1 = new Callable<Integer>() {
            public Integer call() throws Exception {
                System.out.println("Tarefa 1 iniciada...");
                Thread.sleep(2000); // Simular uma tarefa demorada
                System.out.println("Tarefa 1 concluída!");
                return 10;
            }
        };

        Callable<Integer> task2 = new Callable<Integer>() {
            public Integer call() throws Exception {
                System.out.println("Tarefa 2 iniciada...");
                Thread.sleep(3000); // Simular uma tarefa demorada
                System.out.println("Tarefa 2 concluída!");
                return 20;
            }
        };

        // Enviar as tarefas para o executor e obter os Future
        Future<Integer> future1 = executor.submit(task1);
        Future<Integer> future2 = executor.submit(task2);

        System.out.println("Execução assíncrona iniciada...");

        // Aguardar a conclusão das tarefas
        while (!future1.isDone() || !future2.isDone()) {
            System.out.println("Aguardando a conclusão das tarefas...");
            Thread.sleep(500);
        }

        // Obter os resultados das tarefas
        int result1 = future1.get();
        int result2 = future2.get();

        // Imprimir os resultados
        System.out.println("Resultado 1: " + result1);
        System.out.println("Resultado 2: " + result2);

        // Encerrar o executor
        executor.shutdown();
    }
}
