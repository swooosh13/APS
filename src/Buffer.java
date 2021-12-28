public class Buffer {
    private int number;
    private Request request;
    private double timeOfArrival;
    private double timeOfDeparture;
    private double timeInBuffer;

    public Buffer(int number) {
        this.number = number;
    }

    public boolean isEmpty() {
        return request == null;
    }

    public void add(Request request) {
        this.request = request;
        timeOfArrival = Main.systemTime;
        if (Main.step) {
            System.out.println("+-------------------------------------------+");
            System.out.println("|              Поступление в буфер          |");
            System.out.println("+-------------------------------------------+");
            System.out.println("Поступление заявки №" + request.getRequestNumber() + " в буфер №" + this.number + " (" + request.getSourceNumber() + ", " + request.getGenerationTime() + ")");
            System.out.println("Время : " + Main.systemTime);
            System.out.println("---------------------------------------------");
        }
    }

    public double getTimeInBuffer() {
        return timeInBuffer;
    }

    public void delete() {
        timeOfDeparture = Main.systemTime;
        timeInBuffer = timeOfDeparture - timeOfArrival;
        if (Main.step) {

            System.out.println("Заявка №" + request.getRequestNumber() + " удалена из буфера №" + this.number);
            System.out.println("Время : " + Main.systemTime);
            System.out.println("---------------------------------------------");
            request = null;
        }
    }

    public void clear() {
        request = null;
        timeOfArrival = 0;
        timeOfDeparture = 0;
        timeInBuffer = 0;
    }

    @Override
    public String toString() {
        String str = "ожидается";
        if (this.request != null)
            str = "№" + request.getRequestNumber() + " (" + request.getSourceNumber() + ", " + request.getGenerationTime() + ")";
        return ("Буфер №" + number + " Заявка " + str);
    }

    public Request getRequest() {
        return request;
    }

    public double getTimeOfArrival() {
        return timeOfArrival;
    }

    public double getTimeOfDeparture() {
        return timeOfDeparture;
    }
}
