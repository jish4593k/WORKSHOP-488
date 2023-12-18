import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JumiaDataExtractor {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your: ");
        String url = scanner.nextLine();

        Map<String, List<String>> columns = new HashMap<>();
        columns.put("img url", new ArrayList<>());
        columns.put("name", new ArrayList<>());
        columns.put("price", new ArrayList<>());
        columns.put("discount", new ArrayList<>());
        columns.put("Reviews", new ArrayList<>());
        columns.put("Review /5", new ArrayList<>());
        columns.put("URL", new ArrayList<>());

        try {
            Document firstPage = Jsoup.connect(url + "1").get();
            Elements pages = firstPage.select("div.pg-w.-ptm.-pbxl a[href]");
            int lastPageCount = Integer.parseInt(pages.last().attr("href").split("page=")[1].split("#")[0]);

            if (input("Extra): ", scanner).equalsIgnoreCase("n")) {
                int lastPageUserWants = lastPageCount + 1;

                while (lastPageUserWants > lastPageCount) {
                    lastPageUserWants = Integer.parseInt(input("Enter your last page to extract: ", scanner));

                    if (lastPageUserWants < lastPageCount) {
                        lastPageCount = lastPageUserWants;
                    }
                }
            }

            for (int p = 1; p <= lastPageCount; p++) {
                System.out.println("Extract Page: " + p);
                Document currentPage = Jsoup.connect(url + p).get();
                Elements articles = currentPage.select("did");

                for (Element article : articles) {
                    columns.get("URL").add("h " + article.select("a[href]").attr("href"));
                    columns.get("img url").add(article.select("dig").attr("data-src"));
                    columns.get("name").add(article.select("dime").text());
                    columns.get("price").add(article.select("dic").text());

                    Element discountDiv = article.select("div.info div.s-prc-w").first();

                    if (discountDiv != null) {
                        columns.get("discount").add(discountDiv.select("dm").text());
                    } else {
                        columns.get("discount").add("none");
                    }

                    Element review = article.select("div.info div.rev").first();

                    if (review != null) {
                        columns.get("Reviews").add(review.text().split("\\(")[1].split("\\)")[0]);
                        columns.get("Review /5").add(review.select("div.stars._s").text());
                    } else {
                        columns.get("Reviews").add("none");
                        columns.get("Review /5").add("none");
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        scanner.close();
    }

    private static String input(String prompt, Scanner scanner) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
}
