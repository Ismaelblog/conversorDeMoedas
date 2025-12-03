import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.InputMismatchException;
import java.util.Scanner;
import com.google.gson.Gson;

public class Principal {

    private static final String API_KEY = "170e40145ed38aca349a396c";  //  Minha chave de API temporária.
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/";

    public static double getTaxaConversao(String origem, String destino) throws Exception {

        String urlString = BASE_URL + API_KEY + "/pair/" + origem + "/" + destino;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();

        if (statusCode != 200) {
            throw new IOException("Erro ao consultar a API. Código HTTP: " + statusCode);
        }

        String jsonResponse = response.body();
        Gson gson = new Gson();
        ExchangeRateResponse rateResponse = gson.fromJson(jsonResponse, ExchangeRateResponse.class);

        return rateResponse.getConversion_rate();
    }

    public static void exibirMenu() {
        System.out.println(" ");
        System.out.println("*************************************************");
        System.out.println("Seja Bem-vindo(a) ao Conversor de Moedas!");
        System.out.println(" ");
        System.out.println("1) Dólar (USD) => Peso argentino (ARS)");
        System.out.println("2) Peso argentino (ARS) => Dólar (USD)");
        System.out.println("3) Dólar (USD) => Real brasileiro (BRL)");
        System.out.println("4) Real brasileiro (BRL) => Dólar (USD)");
        System.out.println("5) Dólar (USD) => Peso colombiano (COP)");
        System.out.println("6) Peso colombiano (COP) => Dólar (USD)");
        System.out.println("7) Sair");
        System.out.println("*************************************************");
        System.out.println(" ");
        System.out.print(">>> Escolha uma opção válida: ");
    }

    public static void realizarConversao(String origem, String destino, Scanner scanner) {
        try {
            double taxa = getTaxaConversao(origem, destino);

            System.out.print("\n>>> Digite o valor em " + origem + " para converter: ");

            double valorOriginal = scanner.nextDouble();
            double valorConvertido = valorOriginal * taxa;

            System.out.println("\n-------------------------------------------------");
            System.out.printf("Taxa de Câmbio Atual (%s para %s): %.4f\n", origem, destino, taxa);
            System.out.println(" ");
            System.out.printf("%.2f %s equivalem a %.2f %s.\n",
                    valorOriginal, origem, valorConvertido, destino);
            System.out.println("-------------------------------------------------");

        } catch (InputMismatchException e) {
            System.out.println("\n*** ERRO: Entrada inválida. Por favor, digite apenas números! ***\n");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("\n*** ERRO NA API: Não foi possível obter a taxa de câmbio! ***");
            System.out.println("Detalhe: " + e.getMessage());
        }
    }

    public static int exibirMenuContinuar(Scanner scanner) {
        System.out.println("\n*************************************************");
        System.out.println("O que você gostaria de fazer agora?\n");
        System.out.println("1) Voltar ao Menu Principal");
        System.out.println("2) Sair do Programa");
        System.out.println("*************************************************\n");
        System.out.print(">>> Escolha uma opção (1 ou 2): ");

        try {
            int escolha = scanner.nextInt();
            scanner.nextLine();
            return escolha;
        } catch (InputMismatchException e) {
            System.out.println("\n*** ERRO: Entrada inválida. Escolha 1 ou 2. ***\n");
            scanner.nextLine();
            return 0;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcao = 0;

        do {
            exibirMenu();

            try {
                opcao = scanner.nextInt();

                String moedaOrigem = "";
                String moedaDestino = "";

                switch (opcao) {
                    case 1: moedaOrigem = "USD"; moedaDestino = "ARS"; break;
                    case 2: moedaOrigem = "ARS"; moedaDestino = "USD"; break;
                    case 3: moedaOrigem = "USD"; moedaDestino = "BRL"; break;
                    case 4: moedaOrigem = "BRL"; moedaDestino = "USD"; break;
                    case 5: moedaOrigem = "USD"; moedaDestino = "COP"; break;
                    case 6: moedaOrigem = "COP"; moedaDestino = "USD"; break;
                    case 7:
                        System.out.println("\nObrigado por usar o Conversor de Moedas!\n");
                        System.out.println("<<<...Encerrando...>>>");

                        break;
                    default:
                        System.out.println("\n*** ERRO: Opção inválida. Por favor, escolha um número de 1 a 7. ***");
                        continue;
                }

                if (opcao == 7) break;

                realizarConversao(moedaOrigem, moedaDestino, scanner);

            } catch (InputMismatchException e) {
                System.out.println("\n*** ERRO: Entrada inválida. Por favor, digite apenas números. ***\n");
                scanner.nextLine();
                opcao = 0;
            }

            int acaoPosConversao = 0;
            while (true) {
                acaoPosConversao = exibirMenuContinuar(scanner);

                if (acaoPosConversao == 2) {
                    opcao = 7;
                    System.out.println("\nObrigado por usar o Conversor de Moedas!\n");
                    System.out.println("<<<...Encerrando...>>>");
                    break;
                } else if (acaoPosConversao == 1) {
                    break;
                }
            }

        } while (opcao != 7);

        scanner.close();
    }
}
