import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.math.BigInteger;

public class CompletableFutureTest {

    private static long ms = System.currentTimeMillis();

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        System.out.println("Começando a processar a tarefa ...");

        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            try {
                Random rand = new Random();
                System.out.println("Gerando numero 1");
                Thread.sleep(rand.nextInt(1000));
                Integer number = rand.nextInt(1000);
                return number;
            } catch (InterruptedException e) {
                throw new RuntimeException("Erro na tarefa assíncrona: " + e.getMessage());
            }
        });

        CompletableFuture<BigInteger> fatorialFuture = future.thenCompose(number -> calcularFatorial(number));

        CompletableFuture<BigInteger> fallbackFuture = fatorialFuture.exceptionally(ex -> {
            System.out.println("Ocorreu uma exceção: " + ex.getMessage());
            return BigInteger.ZERO; // Valor de fallback, caso ocorra uma exceção
        });

        CompletableFuture<String> resultadoFinalFuture = fallbackFuture.thenApply(fatorial -> {
            return "O fatorial do número 1 gerado foi: " + fatorial;
        }); 

        

        CompletableFuture<Void> finalFuture = resultadoFinalFuture.thenAccept(resultado -> {
            System.out.println("Tarefa 1 completa!");
            System.out.println(resultado);
        });

        CompletableFuture<Void> finalFuture2 = CompletableFuture.supplyAsync(() -> {
                try {
                    Random rand = new Random();
                    System.out.println("Gerando numero 2");
                    Thread.sleep(rand.nextInt(1000));
                    Integer number = rand.nextInt(1000);
                    return number;
                } catch (InterruptedException e) {
                    throw new RuntimeException("Erro na tarefa assíncrona: " + e.getMessage());
                }
            })
            .thenCompose(number -> calcularFatorial(number))
            .exceptionally(ex -> {
                System.out.println("Ocorreu uma exceção: " + ex.getMessage());
                return BigInteger.ZERO; // Valor de fallback, caso ocorra uma exceção
            })
            .thenApply(fatorial -> "O fatorial do número gerado foi: " + fatorial)
            .thenAccept(fatorial -> {
                System.out.println("Tarefa 2 completa!");
                System.out.println("O fatorial do número 2 gerado foi: " + fatorial);
            }); 

        finalFuture.join();
    }

    private static CompletableFuture<BigInteger> calcularFatorial(Integer number) {
	ms = System.currentTimeMillis();
    System.out.println("Calculando Fatorial...");
    CompletableFuture<BigInteger> fatorialFuture = CompletableFuture.supplyAsync(() -> {
        BigInteger fatorial = new BigInteger("1");
        boolean once = true;
        for (Integer i = (number < 1) ? number*-1 : number; i > 1; i--) {
            fatorial = fatorial.multiply(new BigInteger(String.valueOf(i)));

            // Adicionar um atraso de 1 segundo a cada iteração
            try {
                if(once) Thread.sleep(1000);

            } catch (InterruptedException e) {
                throw new RuntimeException("Erro durante o atraso: " + e.getMessage());
            }
			if(once && (System.currentTimeMillis() - ms) > 100) {
				System.out.println("Continuando a calcular");
                once = false;
			}
        }
        return fatorial;
    });
    return fatorialFuture;
}
}
