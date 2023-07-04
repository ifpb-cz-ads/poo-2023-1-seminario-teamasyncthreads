import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

interface EstadoGalinhaCallback {
    void onMudancaEstado(String novoEstado);
}

class Galinha {
    private ExecutorService executor;
    private EstadoGalinhaCallback estadoCallback;

    public Galinha(EstadoGalinhaCallback callback) {
        executor = Executors.newSingleThreadExecutor();
        setEstadoCallback(callback);
    }

    public void setEstadoCallback(EstadoGalinhaCallback callback) {
        this.estadoCallback = callback;
    }

    public void botarOvo() {
        executor.execute(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (estadoCallback != null) {
                estadoCallback.onMudancaEstado("Botando ovo");
            }
        });
    }

    public void cacarejar() {
        executor.execute(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (estadoCallback != null) {
                estadoCallback.onMudancaEstado("Cacarejando");
            }
        });
    }

    public void encerrar() {
        executor.shutdown();
    }
}

public class CallbackTest {
    public static void main(String[] args) {
        Galinha galinha = new Galinha(novoEstado -> System.out.println("Novo estado: " + novoEstado));

        galinha.cacarejar();
        galinha.botarOvo();

        galinha.encerrar();
    }
}
