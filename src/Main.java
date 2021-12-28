import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static int countOfSources;
    public static int countOfRequests;
    public static int countOfDevices;
    public static int countOfBuffers;
    public static double systemTime;
    public static double alfa = 0.5;
    public static double beta = 1.5;
    public static double lambda = 1.3;
    public static boolean step = false;

    private static List<Source> sources;
    private static List<Device> devices;
    private static List<Buffer> buffers;
    private static List<Request> requests;

    public static void print() {

        System.out.println("+----------+");
        System.out.println("|РЕЗУЛЬТАТЫ|");
        System.out.println("+---------+--------------+-----------+--------------+------------+--------------+--------------+------------------+--------+");
        System.out.println("|Источник |Кол-во заявок | %  отказа | Т пребывания | Т ожидания |Т обслуживания|Дисп. ожидания|Дисп. обслуживания|Отказов |");
        System.out.println("+---------+--------------+-----------+--------------+------------+--------------+--------------+------------------+--------+");
        DecimalFormat df = new DecimalFormat("0.000");

        int countTotalRefused = 0;
        int countTotalTimeInUse = 0;
        int countTotalTimeInDevice = 0;

        for (Device d : devices) {
            countTotalTimeInDevice += d.getTimeAllInDevice();
        }
        for (Source s : sources) {
            double averageTimeOfService = s.getTimeOfService() / s.getCountRequest();
            double averageTimeInBuffer = s.getTimeInBuffer() / s.getCountRequest();
            StringBuilder builder = new StringBuilder();
            builder.append("   И" + s.getSourceNumber());
            builder.append(" ".repeat(5));
            builder.append("|"); // кол-во заявок
            builder.append(" ".repeat(1));
            builder.append(s.getCountRequest()); // кол-во заявок
            builder.append(" ".repeat(11));
            builder.append("|"); // кол-во заявок
            builder.append(" ".repeat(1));
            builder.append(df.format((double) s.getCountRefusal() / s.getCountRequest() * 100)); // % отказа
            builder.append(" ".repeat(5));
            builder.append("|");
            builder.append(df.format(averageTimeOfService + averageTimeInBuffer)); // Т пребывания
            builder.append(" ".repeat(8));
            builder.append("|");
            builder.append(df.format(averageTimeInBuffer)); // T ожидания
            builder.append(" ".repeat(7));
            builder.append("|");
            builder.append(df.format(averageTimeOfService)); // Т обслуживания
            builder.append(" ".repeat(9));
            builder.append("|");
            builder.append(df.format(s.getDispWait()));  // Дисп. Ожидания
            builder.append(" ".repeat(9));
            builder.append("|");
            builder.append(df.format(s.getDispService())); // Дисп. обслуживания
            builder.append(" ".repeat(14));
            builder.append("|");
            builder.append(s.getCountRefusal()); // кол-во отказов
            countTotalRefused += s.getCountRefusal();
            countTotalTimeInUse += s.getTimeOfService();
            System.out.println(builder);
        }
        int priceBuffer = 10000;
        int priceDevice = 45000;

        System.out.println("Источники| Буфер| Приборы| Лямбда| Альфа| Бета| Время работы| Отказы| Коэф.работы| Сумма");
        System.out.println("\n" + sources.size() + "    |" + buffers.size() + "    |" + devices.size() + "   |" + lambda + "   |" + alfa + "   |" + beta + "   |" + countTotalTimeInUse + "   |" + df.format((double) countTotalRefused / countOfRequests) + "   |" + df.format((double) countTotalTimeInDevice / (systemTime * devices.size())) + "    |" + (priceBuffer * buffers.size() + priceDevice * devices.size()));
    }

    public static Request getMinRequest() {
        // Находим заявку с минимальным временем генерации
        double minTime = requests.get(0).getGenerationTime();
        Request newRequest = requests.get(0);
        int numberSource = requests.get(0).getSourceNumber();
        int position = 0;

        for (int i = 0; i < requests.size(); i++) {
            if (requests.get(i).getGenerationTime() < minTime) {
                minTime = requests.get(i).getGenerationTime();
                newRequest = requests.get(i);
                numberSource = requests.get(i).getSourceNumber();
                position = i;
            }
        }

        systemTime = newRequest.getGenerationTime();

        requests.set(position, sources.get(numberSource).generate());

        return newRequest;
    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите количество источников: ");
        countOfSources = scanner.nextInt();
        System.out.println("Введите количество заявок: ");
        countOfRequests = scanner.nextInt();
        System.out.println("Введите размер буфера");
        countOfBuffers = scanner.nextInt();
        System.out.println("Введите количество приборов");
        countOfDevices = scanner.nextInt();
        System.out.println("Выберите режим a - автоматический, p - пошаговый");
        String str = scanner.next();
        if (str.equals("p"))
            step = true;

        sources = new ArrayList<>(countOfSources);
        for (int i = 0; i < countOfSources; i++) {
            sources.add(new Source(i));
        }

        devices = new ArrayList<>(countOfDevices);
        for (int i = 0; i < countOfDevices; i++) {
            devices.add(new Device(i));
        }

        buffers = new ArrayList<>(countOfBuffers);
        for (int i = 0; i < countOfBuffers; i++) {
            buffers.add(new Buffer(i));
        }

        requests = new ArrayList<>(countOfRequests);
        for (Source s : sources) {
            requests.add(s.generate());
        }
        BufferManager bufferManager = new BufferManager(buffers);
        DeviceManager manager = new DeviceManager();

        while (Source.countOfAllRequests != countOfRequests) {
            Request newRequest = getMinRequest();
            // постановка на свободное место Д10033
            Buffer buffer = bufferManager.getBuffer(); // возвращаем первый пустой буфер или null
            if (buffer != null) {
                buffer.add(newRequest);
                systemTime = buffer.getTimeOfArrival();
            } else {
                int i = bufferManager.knockOutTheRequest(); // выбивание заявки если не нашел пустой буфер
                sources.get(i).setCountRefusal();
                buffer = bufferManager.getBuffer();
                buffer.add(newRequest);
                systemTime = buffer.getTimeOfArrival();
            }
            manager.sendToDevice(buffers, devices, requests, sources);
            if (step) {
                new Scanner(System.in).nextLine();
            }
        }

        System.out.println();

        DecimalFormat df = new DecimalFormat("###.###");

        System.out.println();
        System.out.println("Количество заявок: " + countOfRequests);
        System.out.println("Количество источников: " + countOfSources);
        System.out.println("Количество приборов: " + countOfDevices);
        System.out.println("Количество буфферов: " + countOfBuffers);
        System.out.println();
        print();

        System.out.println("+-------+---------------------------+");
        System.out.println("|Прибор | Коэффициент использования |");
        System.out.println("+-------+---------------------------+");
        for (Device d : devices) {
            StringBuilder string = new StringBuilder();
            string.append(" П" + d.getNumber());
            string.append(" ".repeat(5));
            string.append("|");
            string.append(df.format(d.getTimeAllInDevice() / systemTime));
            System.out.println(string);

        }
    }
}
