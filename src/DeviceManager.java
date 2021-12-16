import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class DeviceManager {
    static int devicePointer;
    static int selectedSource = -1;

    static class Package {
        static int packageNumber = 0;
        static int packageCount = 0;
    }

    static public void updatePackageNumber(List<Buffer> buffers, int countSources) {
        if (Package.packageCount > 0) {
            Package.packageCount -= 1;
            return;
        }

        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (int i = 0; i < countSources; i++) {
            map.put(i, 0);
        }

        for (int i = 0; i < buffers.size(); i++) {
            Request req = buffers.get(i).getRequest();
            if (req != null)
                map.put(req.getSourceNumber(), map.get(req.getSourceNumber()) + 1);
        }

        Map.Entry<Integer, Integer> minEntry = null;

        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (minEntry == null || entry.getValue() > 0) {
                minEntry = entry;
            }
        }

        Package.packageNumber = minEntry.getKey();
        Package.packageCount = minEntry.getValue();


        System.out.println("Номер пакета: " + Package.packageNumber + " кол-во в пакете: " + Package.packageCount);
    }

    static public int getPackageNumber() {
        return Package.packageNumber;
    }

    private int findDevice(List<Device> devices, List<Buffer> buffers) {

        // выбор девайса по кольцу
        int index = -1;
        int emptyCount = 0;
        for (Buffer buffer : buffers) { // если буферы пустые то не нужно выбирать device
            if (buffer.isEmpty()) {
                emptyCount++;
            }
        }

        if (emptyCount == buffers.size())
            return -1;

        for (int i = devicePointer; i < devices.size(); i++) {
            if (devices.get(i).getRequest() == null) {
                if (devices.size() - 1 == devicePointer) {
                    devicePointer = 0;
                } else {
                    devicePointer = i + 1;
                }
                System.out.println("Device указатель №" + i);
                return i;
            }
        }
        for (int i = 0; i < devicePointer; i++) {
            if (devices.get(i).getRequest() == null) {
                devicePointer = i + 1;
                System.out.println("Device указатель №" + i);
                return i;
            }
        }
        return index;
    }

    public void sendToDevice(List<Buffer> buffers, List<Device> devices,
                             List<Request> requests, List<Source> sources) {

        updatePackageNumber(buffers, sources.size());

        if (Main.step) {
            new Scanner(System.in).nextLine();
        }

        // Проверяем свободен ли хоть один прибор и по кольцу выбираем
        // Д2П2
        int index = findDevice(devices, buffers);

        if (Main.step) {
            System.out.println("+-------------------------------------------+");
            System.out.println("|             Содержимое буфера             |");
            System.out.println("+-------------------------------------------+");
            for (Buffer buffer : buffers) {
                System.out.println(buffer.toString());
            }
        }


        // Д2Б5
        if (index != -1) { // если не все пустые
            boolean isBuffersEmpty = true;  // Если все буферы пустые
            for (Buffer buffer : buffers) {
                if (!buffer.isEmpty())
                    isBuffersEmpty = false;
            }

            if (!isBuffersEmpty) {
                // Приоритет по номеру источника
                Request req = null;
//                int numberBuffer = 0;
//                int minSourceNumber = Integer.MAX_VALUE;
                int numberSource = Main.countOfSources + 1;
                int numberRequest = Main.countOfRequests;

//                for (int i = 0; i < buffers.size(); ++i) {
//
//                    if (buffers.get(i).getRequest() != null
//                            &&
//                            buffers.get(i).getRequest().getSourceNumber() < minSourceNumber) {
//                        req = buffers.get(i).getRequest();
//                        minSourceNumber = req.getSourceNumber();
//                        numberBuffer = i;
//                    }
//                }

                for (int i = 0; i < buffers.size(); ++i) {
                    if (buffers.get(i).getRequest() != null && buffers.get(i).getRequest().getSourceNumber() == selectedSource) {
                        numberSource = buffers.get(i).getRequest().getSourceNumber();
                        break;
                    }
                    if (buffers.get(i).getRequest() != null
                            &&
                            buffers.get(i).getRequest().getSourceNumber() < numberSource) {
                        req = buffers.get(i).getRequest();
                        numberSource = req.getSourceNumber();
                    }
                }

                selectedSource = numberSource;

                int numberBuffer = 0;
                for (int i = 0; i < buffers.size(); i++) {
                    if (buffers.get(i).getRequest() != null && buffers.get(i).getRequest().getSourceNumber() == numberSource && buffers.get(i).getRequest().getRequestNumber() < numberRequest
                    ) {
                        numberRequest = buffers.get(i).getRequest().getRequestNumber();
                        req = buffers.get(i).getRequest();
                        numberBuffer = i;
                    }
                }


                buffers.get(numberBuffer).delete();
                sources.get(numberSource).setTimeInBuffer(buffers.get(numberBuffer).getTimeInBuffer());
//                sources.get(req.getSourceNumber()).setTimeInBuffer(buffers.get(numberBuffer).getTimeInBuffer());
                Main.systemTime = buffers.get(numberBuffer).getTimeOfDeparture();

                // отправляем на прибор
                if (req != null) {
                    devices.get(index).add(req);
                    Main.systemTime = devices.get(index).getTimeOfArrival();
                }
                buffers.get(numberBuffer).clear();
                if (Main.step) {
                    System.out.println("+-------------------------------------------+");
                    System.out.println("|             Содержимое буфера             |");
                    System.out.println("+-------------------------------------------+");
                    for (Buffer buffer : buffers) {
                        System.out.println(buffer.toString());
                    }
                    new Scanner(System.in).nextLine();
                }
            }
        }

        // Проверяем есть ли прибор, который закончит обработку заявки раньше,
        // чем сгенерировалась самая ранняя из заявок, находящихся в листе
        Device device = devices.get(0);
        for (Device d : devices) {
            if (!d.isEmpty()) {
                device = d;
                break;
            }
        }

        double minTimeTreatment = device.getTimeToTreatment();
        int numberDevice = device.getNumber();
        for (Device d : devices) {
            if (d.getTimeToTreatment() < minTimeTreatment && !d.isEmpty()) {
                minTimeTreatment = d.getTimeToTreatment();
                numberDevice = d.getNumber();
                device = d;
            }
        }

        double minTimeOfGeneration = requests.get(0).getGenerationTime();
        for (Request req : requests) {
            if (req.getGenerationTime() < minTimeOfGeneration)
                minTimeOfGeneration = req.getGenerationTime();
        }

        if (Main.step) {
            System.out.println("+-------------------------------------------+");
            System.out.println("|            Содержимое девайсов            |");
            System.out.println("+-------------------------------------------+");
            for (Device d : devices) {
                System.out.println(d.toString());
            }
            new Scanner(System.in).nextLine();
            System.out.println("+-------------------------------------------+");
        }


        // Если нашли такой прибор, то фиксируем системное время и удаляем заявку из прибора
        if (!device.isEmpty()) {
            if (minTimeTreatment < minTimeOfGeneration) {
                Main.systemTime = minTimeTreatment;     //Фиксируем событие - Завершение обработки и удаление заявки из прибора.
                int numberSource = device.getRequest().getSourceNumber();
                devices.get(numberDevice).delete();     // удаляем заявку из прибора
                sources.get(numberSource).setTimeOfService(devices.get(numberDevice).getTimeInDevice());
                if (!buffers.isEmpty()) {
                    if (Main.step) {
                        System.out.println("+-------------------------------------------+");
                        System.out.println("|             Содержимое буфера             |");
                        System.out.println("+-------------------------------------------+");
                        for (Buffer buffer : buffers) {
                            System.out.println(buffer.toString());
                        }
                        new Scanner(System.in).nextLine();
                        System.out.println("+-------------------------------------------+");
                        System.out.println("|            Содержимое девайсов            |");
                        System.out.println("+-------------------------------------------+");
                        for (Device d : devices) {
                            System.out.println(d.toString());
                        }
                        System.out.println("---------------------------------------------");
                    }
                    this.sendToDevice(buffers, devices, requests, sources);
                }
            }
        }

    }
}
