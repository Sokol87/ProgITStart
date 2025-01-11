package org.example;

import net.thauvin.erik.crypto.CryptoPrice;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

public class MyBot extends TelegramLongPollingBot {
    public MyBot() {
        super("YOUR_BOT_TOKEN");
    }

    @Override
    public void onUpdateReceived(Update update) {
        var chatId = update.getMessage().getChatId();
        var text = update.getMessage().getText();

        try {
            var message = new SendMessage();
            message.setChatId(chatId);

            if (text.equals("/start")) {
                message.setText("Hello! Use commands 'btc', 'eth', 'doge', '/all', or send an amount in USD to see how much cryptocurrency you can buy.");
            } else if (text.equals("btc")) {
                sendCryptoPriceWithImage(chatId, "BTC", "1.png");
            } else if (text.equals("eth")) {
                sendCryptoPriceWithImage(chatId, "ETH", "1027png");
            } else if (text.equals("doge")) {
                sendCryptoPriceWithImage(chatId, "DOGE", "74.png");
            } else if (text.equals("/all")) {
                var btcPrice = CryptoPrice.spotPrice("BTC").getAmount().doubleValue();
                var ethPrice = CryptoPrice.spotPrice("ETH").getAmount().doubleValue();
                var dogePrice = CryptoPrice.spotPrice("DOGE").getAmount().doubleValue();

                String allPrices = "Current prices:\n" +
                        "BTC: $" + btcPrice + "\n" +
                        "ETH: $" + ethPrice + "\n" +
                        "DOGE: $" + dogePrice;
                message.setText(allPrices);
            } else if (text.matches("^(btc|eth|doge) \\d+(\\.\\d+)?$")) {
                // Парсимо криптовалюту та суму
                String[] parts = text.split(" ");
                String crypto = parts[0].toUpperCase();
                double dollars = Double.parseDouble(parts[1]);

                // Отримуємо курс для розрахунку
                var price = CryptoPrice.spotPrice(crypto).getAmount().doubleValue();
                double amount = dollars / price;

                message.setText(String.format("On $%.2f, you can buy %.8f %s.", dollars, amount, crypto));
            } else {
                try {
                    // Пытаемся преобразовать текст в число
                    double dollars = Double.parseDouble(text);

                    // Получаем текущие курсы для расчета
                    var btcPrice = CryptoPrice.spotPrice("BTC").getAmount().doubleValue();
                    var ethPrice = CryptoPrice.spotPrice("ETH").getAmount().doubleValue();
                    var dogePrice = CryptoPrice.spotPrice("DOGE").getAmount().doubleValue();

                    // Рассчитываем количество каждой криптовалюты
                    double btcAmount = dollars / btcPrice;
                    double ethAmount = dollars / ethPrice;
                    double dogeAmount = dollars / dogePrice;

                    // Формируем сообщение с расчетами
                    String response = "On $" + dollars + ", you can buy:\n" +
                            String.format("BTC: %.8f\n", btcAmount) +
                            String.format("ETH: %.8f\n", ethAmount) +
                            String.format("DOGE: %.8f", dogeAmount);

                    message.setText(response);
                } catch (NumberFormatException e) {
                    message.setText("Unknown command! Please use 'btc', 'eth', 'doge', '/all', or provide a dollar amount to calculate cryptocurrency.");
                }
            }

            execute(message);
        } catch (Exception e) {
            System.out.println("Error sending message: " + e.getMessage());
        }
    }

    // Метод для відправки зображення та ціни криптовалюти
    private void sendCryptoPriceWithImage(long chatId, String crypto, String logoFileName) throws Exception {
        var price = CryptoPrice.spotPrice(crypto).getAmount().doubleValue();

        // Надсилаємо зображення логотипа
        var photo = getClass().getClassLoader().getResourceAsStream(logoFileName);
        if (photo == null) {
            System.out.println("Error: File not found - " + logoFileName);
            return;
        }

        var sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(new InputFile(photo, logoFileName));
        execute(sendPhoto);

        // Надсилаємо повідомлення з ціною
        var message = new SendMessage();
        message.setChatId(chatId);
        message.setText(crypto + " price: $" + price);
        execute(message);
    }

    @Override
    public String getBotUsername() {
        return "MyJoko_bot";
    }

    @Override
    public String getBotToken() {
        return "7808259987:AAEyBsBfLc2MKbdjzxiJ3NL8H0TLXC_Vx4U"; // Вставьте сюда ваш токен
    }
}
