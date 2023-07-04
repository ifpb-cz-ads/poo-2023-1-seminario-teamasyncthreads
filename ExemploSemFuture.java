

public class ExemploSemFuture {
    public static void main(String[] args) throws InterruptedException {
        Thread tarefaAssincrona = new Thread(new TarefaAssincrona());
        tarefaAssincrona.start();

        // Realiza outras operações enquanto a tarefa está em execução
        System.out.println("Realizando outras operações...");

        // Aguarda a conclusão da tarefa
        tarefaAssincrona.join();

        System.out.println("Tarefa concluída!");

        // Aguarda um pequeno atraso antes de encerrar o programa
        Thread.sleep(1000);
    }

    static class TarefaAssincrona implements Runnable {
        @Override
        public void run() {
            // Simula uma tarefa assíncrona
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
