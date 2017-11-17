import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ChatFilter {
    private String badWordsFileName;

    public ChatFilter(String badWordsFileName) {
        this.badWordsFileName = badWordsFileName;
    }

    public String filter(String msg) throws IOException {
        File bw = new File(badWordsFileName);
        FileReader fr = new FileReader(bw);
        BufferedReader bfr = new BufferedReader(fr);
        String s = "";
        String ast = "";
        while ((s = bfr.readLine()) != null) {
            ast = "";
            for (int u = 0; u < s.length(); u++) {
                ast = ast + "*";
            }

            msg = msg.replaceAll("(?i)" + s, ast);

        }
        return msg;
    }
}
