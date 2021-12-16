import java.util.List;

public class BufferManager {
    private List<Buffer> buffers;

    public BufferManager(List<Buffer> buffers) {
        this.buffers = buffers;
    }

    public Buffer getBuffer() {
        for (int i = 0; i < buffers.size(); i++) {
            if (buffers.get(i).isEmpty()) {
                return buffers.get(i);
            }
        }
        return null;
    }

    public int knockOutTheRequest() {

        // Д1002
        Request min = buffers.get(0).getRequest();

        int index = 0;
        for (int i = 0; i < buffers.size(); i++) {
            if (buffers.get(i).getRequest().getSourceNumber() < min.getSourceNumber()) { // если приоритет по номеру источника ниже то берем
                min = buffers.get(i).getRequest();
                index = i;
            } else if (buffers.get(i).getRequest().getSourceNumber() == min.getSourceNumber()) { // если одинаковый источник смотрим по времени
                if (buffers.get(i).getRequest().getGenerationTime() < min.getGenerationTime()) {
                    min = buffers.get(i).getRequest();
                    index = i;
                }
            }
        }

        int numberSource = buffers.get(index).getRequest().getSourceNumber();
        if (Main.step) {
            System.out.println("Заявка №" + buffers.get(index).getRequest().getRequestNumber() + " выбита");
        }
        buffers.get(index).delete();
        Main.systemTime = buffers.get(index).getTimeOfDeparture();
        buffers.get(index).clear();
        return numberSource;
    }
}
