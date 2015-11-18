/**
 * Created by wiragotama on 11/18/15.
 */
public class Main {
    public static void main(String[] args) {
        LoaderXML xmlLoader = new LoaderXML("XMLexamples/delete.xml");
        Query q = xmlLoader.getQuery();
        if (xmlLoader.getErrorFlag()==false) {
            QueryValidator qv = new QueryValidator(q);
            if (qv.verify()) {
                System.out.println("Query Verified");
                System.out.println("Parsing Result:");
                System.out.println(q);
            }
        }
    }
}
