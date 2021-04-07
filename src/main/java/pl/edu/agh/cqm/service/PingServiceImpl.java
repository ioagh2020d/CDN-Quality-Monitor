package pl.edu.agh.cqm.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.agh.cqm.data.repository.RTTSampleRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class PingServiceImpl implements PingService {
    static class PingData{
        private float min;
        private float avg;
        private float max;
        private float packageLoss;

        public void setMin(float min) {
            this.min = min;
        }
        public void setAvg(float avg) {
            this.avg = avg;
        }
        public void setMax(float max) {
            this.max = max;
        }
        public void setPackageLoss(float packageLoss) {
            this.packageLoss = packageLoss;
        }

        public float getMin() {
            return min;
        }
        public float getAvg() {
            return avg;
        }
        public float getMax() {
            return max;
        }
        public float getPackageLoss() {
            return packageLoss;
        }
    }

    private final RTTSampleRepository rttSampleRepository;

    @Override
    public void addRTTSample(String ip) {
        PingData pingData = this.ping(ip);
        rttSampleRepository.add(
                pingData.getAvg(),
                pingData.getMin(),
                pingData.getMax(),
                pingData.getPackageLoss()
        );
    }

    private PingData ping(String host){
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");

        String command = String.join(" ","ping", isWindows? "-n" : "-c", "100 -i 0,2", host);
        System.out.println(command);
        List<String> lines = new ArrayList<>();
        try {
            BufferedReader inputStream = runSystemCommand(command);
            String s = "";
            // reading output stream of the command
            while ((s = inputStream.readLine()) != null) {
                lines.add(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new InternalError("System command execution failed.");
        }
        PingData pingData = new PingData();
        pingData.setPackageLoss(getValFromString(lines.get(lines.size()-2), "(\\d+)% packet loss"));
        pingData.setMin(getValFromString(lines.get(lines.size()-1), " ((\\d+)(\\.)(\\d+))/"));
        pingData.setAvg(getValFromString(lines.get(lines.size()-1), "/((\\d+)(\\.)(\\d+))/"));
        pingData.setMax(getValFromString(lines.get(lines.size()-1), "/((\\d+)(\\.)(\\d+))/((\\d+)(\\.)(\\d+)) ms"));
        return pingData;
    }

    private static BufferedReader runSystemCommand(String command) throws IOException {
        Process p = Runtime.getRuntime().exec(command);
        return new BufferedReader(
                new InputStreamReader(p.getInputStream()));
    }

    private static float getValFromString(String inputLine, String patternS){
        Pattern pattern = Pattern.compile(patternS);
        Matcher m = pattern.matcher(inputLine);
        if (m.find()) {
            return Float.parseFloat((m.group(1)));
        }
        return -1;
    }
}