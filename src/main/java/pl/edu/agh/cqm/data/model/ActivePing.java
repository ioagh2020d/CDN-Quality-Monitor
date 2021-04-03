package pl.edu.agh.cqm.data.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActivePing{
    private String host;

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

    private float min;
    private float avg;
    private float max;
    private float packageLoss;

    public ActivePing(String host){
        this.host = host;
    }

    public void ping(){
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
            return;
        }
        System.out.println(lines.get(lines.size()-2));
        this.packageLoss = getValFromString(lines.get(lines.size()-2), "(\\d+)% packet loss");
        System.out.println(this.packageLoss);
        System.out.println(lines.get(lines.size()-1));
        this.min = getValFromString(lines.get(lines.size()-1), " ((\\d+)(\\.)(\\d+))/");
        System.out.println(this.min);
        this.avg = getValFromString(lines.get(lines.size()-1), "/((\\d+)(\\.)(\\d+))/");
        System.out.println(this.avg);
        this.max = getValFromString(lines.get(lines.size()-1), "/((\\d+)(\\.)(\\d+))/((\\d+)(\\.)(\\d+)) ms");
        System.out.println(this.max);
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

    public static void main(String[] args) {
        String ip = "stackoverflow.com"; //Any IP Address on your network / Web
        ActivePing pi = new ActivePing(ip);
        pi.ping();
    }
}