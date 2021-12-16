
public class Device {
    private Request request;
    private int number;
    private double timeOfArrival;
    private double timeOfDeparture;
    private double timeAllInDevice;
    private double timeToTreatment;
    private double timeInDevice;

    public Device(int number) {
        this.number = number;
    }

    public void add(Request request) {
        this.request = request;
        timeOfArrival = Main.systemTime;
        if (Main.step) {
            System.out.println("Поступление заявки №" + request.getRequestNumber() + " на прибор №" + this.number + " (" + request.getSourceNumber() + ", " + request.getGenerationTime() + ")");
            System.out.println("Время : " + Main.systemTime);
//            System.out.println("---------------------------------------------");
        }
        timeToTreat();
    }

    public void delete() {
        timeOfDeparture = Main.systemTime;
        timeAllInDevice += timeOfDeparture - timeOfArrival;
        timeInDevice = timeOfDeparture - timeOfArrival;
        if (Main.step) {
            System.out.println("Заявка №" + request.getRequestNumber() + " удалена из прибора №" + this.number);
            System.out.println("Время : " + Main.systemTime);
            System.out.println("---------------------------------------------");
        }
        request = null;
    }


    public void timeToTreat() {
        // Эксп. закон распределения
        // П31
        timeToTreatment = timeOfArrival + Math.log(1 - Math.random()) / (-Main.lambda);
    }

    public boolean isEmpty() {
        return request == null;
    }

    @Override
    public String toString() {
        String str = "ожидается";
        if (this.request != null) str = "№" + request.getRequestNumber() + " (" + request.getSourceNumber() + ", " + request.getGenerationTime() + ")";
        return ("Девайс №" + number + " Заявка " + str);
    }

    public Request getRequest() {
        return request;
    }

    public int getNumber() {
        return number;
    }

    public double getTimeOfArrival() {
        return timeOfArrival;
    }

    public double getTimeAllInDevice() {
        return timeAllInDevice;
    }

    public double getTimeToTreatment() {
        return timeToTreatment;
    }

    public double getTimeInDevice() {
        return timeInDevice;
    }

}
