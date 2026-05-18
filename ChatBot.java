import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ChatBot {

    public String handleCommand(String command) {
        command = command.trim();
        if (command.equalsIgnoreCase("/bot help")) {
            return getHelpMessage();
        }
        if (command.toLowerCase().startsWith("/bot stock ")) {
            String ticker = command.substring("/bot stock ".length()).trim();
            return getStockData(ticker);
        }
        return "[Bot] Unknown command. Type /bot help";
    }

    private String getHelpMessage() {
        return "[Bot] Commands:\n"
                + "/bot help\n"
                + "/bot stock <ticker>\n"
                + "Examples:\n"
                + "/bot stock AAPL\n"
                + "/bot stock NVDA\n"
                + "/bot stock TSM\n"
                + "/bot stock VOO\n"
                + "/bot stock QQQ";
    }

    private String getStockData(String ticker) {
        try {
            if (ticker == null || ticker.isEmpty()) {
                return "[Bot] Please enter a ticker. Example: /bot stock AAPL";
            }

            ticker = ticker.toLowerCase();

            String stooqSymbol = ticker + ".us";

            String urlString =
                    "https://stooq.com/q/l/?s="
                            + stooqSymbol
                            + "&f=sd2t2ohlcv&h&e=csv";

            String response = sendGetRequest(urlString);

            String[] lines = response.split("\n");

            if (lines.length < 2) {
                return "[Bot] No stock data found for " + ticker.toUpperCase();
            }

            String[] data = lines[1].split(",");

            if (data.length < 8 || data[3].equals("N/D")) {
                return "[Bot] Invalid ticker or unsupported market: "
                        + ticker.toUpperCase();
            }

            String symbol = data[0];
            String date = data[1];
            String time = data[2];
            String open = data[3];
            String high = data[4];
            String low = data[5];
            String close = data[6];
            String volume = data[7];

            return "[Bot] Stock Data for " + ticker.toUpperCase() + "\n"
                    + "Symbol: " + symbol + "\n"
                    + "Date: " + date + "\n"
                    + "Time: " + time + "\n"
                    + "Open: $" + open + "\n"
                    + "High: $" + high + "\n"
                    + "Low: $" + low + "\n"
                    + "Close: $" + close + "\n"
                    + "Volume: " + volume;

        } catch (Exception e) {
            return "[Bot] Error getting stock data.";
        }
    }

    private String sendGetRequest(String urlString) throws Exception {
        URL url = new URL(urlString);

        HttpURLConnection connection =
                (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");

        BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line).append("\n");
        }

        reader.close();

        return response.toString();
    }
}