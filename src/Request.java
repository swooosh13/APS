public class Request {

    private double generationTime;
    private int requestNumber;
    private int sourceNumber;

    public Request(double time, int reqNumber, int sourceNumber) {
        this.generationTime = time;
        this.requestNumber = reqNumber;
        this.sourceNumber = sourceNumber;
    }

    public double getGenerationTime() {
        return generationTime;
    }

    public int getRequestNumber() {
        return requestNumber;
    }

    public int getSourceNumber() {
        return sourceNumber;
    }
}
