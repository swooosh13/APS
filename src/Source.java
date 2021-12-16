import java.util.ArrayList;
import java.util.List;

public class Source {
    public static int countOfAllRequests = 0;
    private int sourceNumber;
    private double prevTime = 0.0;

    private int countRequest = 0;
    private int countRefusal = 0;

    private double timeOfService = 0.0;
    private double timeInBuffer = 0.0;

    public static List<Double> serviceDispersion = new ArrayList<>(); // обсуживания
    public static List<Double> waitDispersion = new ArrayList<>();    // ожидания

    public Source(int number) {
        this.sourceNumber = number;
    }

    public Request generate() {
        // бесконечный источник, равномерный закон распределния
        // ИБ И32
        countOfAllRequests++;
        countRequest++;
        double timeGeneration = prevTime + (Main.beta - Main.alfa) * Math.random() + Main.alfa;
        Request request = new Request(timeGeneration, countOfAllRequests, sourceNumber);
        prevTime = timeGeneration;
        if (Main.step) {
            System.out.println("+-------------------------------------------+");
            System.out.println("|                 Генерация                 |");
            System.out.println("+-------------------------------------------+");
            System.out.println("Заявка №" + request.getRequestNumber() + " сгенерирована в " + request.getGenerationTime() + " источником №" + sourceNumber);
//            System.out.println("+-------------------------------------------+");
        }
        return request;
    }

    public void setCountRefusal() {
        this.countRefusal++;
    }

    public void setTimeOfService(double timeOfService) {
        serviceDispersion.add(timeOfService);
        this.timeOfService += timeOfService;
    }

    public double getTimeOfService() {
        return timeOfService;
    }

    public double getTimeInBuffer() {
        return timeInBuffer;
    }

    public void setTimeInBuffer(double timeInBuffer) {
        waitDispersion.add(timeInBuffer);
        this.timeInBuffer += timeInBuffer;
    }

    public double getDispService() {
        double sum = 0;
        for (int i = 0; i < serviceDispersion.size(); i++) {
            sum += (serviceDispersion.get(i) - timeOfService / countRequest) * (serviceDispersion.get(i) - timeOfService / countRequest);
        }
        return sum / (countRequest - 1);
    }

    public double getDispWait() {
        double sum = 0;
        for (int i = 0; i < waitDispersion.size(); i++) {
            sum += (waitDispersion.get(i) - timeInBuffer / countRequest) * (waitDispersion.get(i) - timeInBuffer / countRequest);
        }
        return sum / (countRequest - 1);
    }

    public int getSourceNumber() {
        return sourceNumber;
    }

    public int getCountRequest() {
        return countRequest;
    }

    public int getCountRefusal() {
        return countRefusal;
    }
}
