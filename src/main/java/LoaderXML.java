/**
 * Created by wiragotama on 11/18/15.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import javafx.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** For Parsing XML Files */
public class LoaderXML {

    private Query query;

    /**
     *
     * @param filePath
     */
    public LoaderXML(String filePath)
    {
        File file = new File(filePath);
        exploreDirectory(file, filePath);
    }

    /**
     *
     * @param node
     */
    public void exploreDirectory(File node, String filePath) {
        if (getFileExtension(node.getName()).equalsIgnoreCase(".xml")) {
            loadFile(filePath);
        }
        else {
            System.out.println("input is not an xml file");
        }
    }

    /*
    * Returns file extension
    * @param filename, full filename (including path is ok)
    */
    private String getFileExtension(String filename)
    {
        try {
            return filename.substring(filename.lastIndexOf("."));
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Load 1 XML file
     * @param filePath
     */
    private void loadFile(String filePath)
    {
        try {
            System.out.println("Parsing File "+filePath);
            File file = new File(filePath);
            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            if (doc.hasChildNodes()) { //explore the child node
                this.query= buildQuery(doc.getChildNodes());
                if (this.query==null)
                    System.out.println("Syntax error");
                else System.out.println(this.query);
            }
            else {
                System.out.println("Syntax error");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     *
     * @param nodeList
     */
    private Query buildQuery(NodeList nodeList) {
        if (nodeList.getLength()>1) {
            return null;
        }
        else {
            Node tempNode = nodeList.item(0);
            // make sure it's element node.
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                // get node name and value
                //System.out.println(tempNode.getNodeName().toLowerCase());
                if ( tempNode.getNodeName().equalsIgnoreCase("insert") || tempNode.getNodeName().equalsIgnoreCase("read")
                    || tempNode.getNodeName().equalsIgnoreCase("update") || tempNode.getNodeName().equalsIgnoreCase("delete")) {
                    return Parse(tempNode);
                }
                else return null;
            }
            return null;
        }
    }

    /**
     * Parse Query
     * @param parent
     * @return
     */
    private Query Parse(Node parent) {
        if (parent.hasChildNodes()) {
            NodeList children = parent.getChildNodes();
            int count=0;
            List<String> tags = new ArrayList<String>();
            List<Integer> index = new ArrayList<Integer>();
            for (int i=0; i<children.getLength(); i++) {
                if (children.item(i).getNodeType()==Node.ELEMENT_NODE) {
                    count++;
                    tags.add(children.item(i).getNodeName().toLowerCase());
                    index.add(i);
                }
            }
            if (parent.getNodeName().equalsIgnoreCase("insert") && count==2) {
                if (tags.contains("table") && tags.contains("instance")) {
                    if (noElementChild(children.item(index.get(0))) && noElementChild(children.item(index.get(1)))) {
                        String tableText = children.item(index.get(tags.indexOf("table"))).getChildNodes().item(0).getTextContent();
                        String keyValText = children.item(index.get(tags.indexOf("instance"))).getChildNodes().item(0).getTextContent();
                        List<Pair<String, String>> instance = new ArrayList<Pair<String, String>>();
                        try {
                            instance = keyValueParse(keyValText);
                        } catch (Exception e) {
                            return null; //syntax error
                        }
                        Query q = new Query(parent.getNodeName().toLowerCase(), tableText.trim(), instance);
                        return q;
                    }
                    else return null;
                }
                else return null;
            }
            else if (parent.getNodeName().equalsIgnoreCase("update") && count==3) {
                if (tags.contains("table") && tags.contains("instance") && tags.contains("filter")) {
                    if (noElementChild(children.item(index.get(0))) && noElementChild(children.item(index.get(1))) && noElementChild(children.item(index.get(2)))) {
                        String tableText = children.item(index.get(tags.indexOf("table"))).getChildNodes().item(0).getTextContent();
                        String instanceText = children.item(index.get(tags.indexOf("instance"))).getChildNodes().item(0).getTextContent();
                        String filterText = children.item(index.get(tags.indexOf("filter"))).getChildNodes().item(0).getTextContent();
                        List<Pair<String, String>> instance = new ArrayList<Pair<String, String>>();
                        List<Pair<String, String>> filter = new ArrayList<Pair<String, String>>();
                        try {
                            instance = keyValueParse(instanceText);
                            filter = keyValueParse(filterText);
                        } catch (Exception e) {
                            return null; //syntax error
                        }
                        Query q = new Query(parent.getNodeName().toLowerCase(), tableText.trim(), filter, instance);
                        return q;
                    }
                    else return null;
                }
                else return null;
            }
            else if (parent.getNodeName().equalsIgnoreCase("delete") && count==2) {
                if (tags.contains("table") && tags.contains("filter")) {
                    if (noElementChild(children.item(index.get(0))) && noElementChild(children.item(index.get(1)))) {
                        String tableText = children.item(index.get(tags.indexOf("table"))).getChildNodes().item(0).getTextContent();
                        String keyValText = children.item(index.get(tags.indexOf("filter"))).getChildNodes().item(0).getTextContent();
                        List<Pair<String, String>> filter = new ArrayList<Pair<String, String>>();
                        try {
                            filter = keyValueParse(keyValText);
                        } catch (Exception e) {
                            return null; //syntax error
                        }
                        Query q = new Query(parent.getNodeName().toLowerCase(), tableText.trim(), filter, true);
                        return q;
                    }
                    else return null;
                }
                else return null;
            }
            else if (parent.getNodeName().equalsIgnoreCase("read") && count==3) {
                if (tags.contains("table") && tags.contains("filter") && tags.contains("columns")) {
                    if (noElementChild(children.item(index.get(0))) && noElementChild(children.item(index.get(1))) && noElementChild(children.item(index.get(2)))) {
                        String tableText = children.item(index.get(tags.indexOf("table"))).getChildNodes().item(0).getTextContent();
                        String keyValText = children.item(index.get(tags.indexOf("filter"))).getChildNodes().item(0).getTextContent();
                        String colText = children.item(index.get(tags.indexOf("columns"))).getChildNodes().item(0).getTextContent();
                        List<Pair<String, String>> filter = new ArrayList<Pair<String, String>>();
                        String[] columns = colText.trim().split("\\r?','");
                        try {
                            filter = keyValueParse(keyValText);
                        } catch (Exception e) {
                            return null; //syntax error
                        }
                        Query q = new Query(parent.getNodeName().toLowerCase(), tableText.trim(), Arrays.asList(columns), filter, true);
                        return q;
                    }
                    else return null;
                }
                else return null;
            }
            else return null;
        }
        else return null; //syntax error
    }

    /**
     * Check if a node has child with type=element
     * @param node
     * @return
     */
    private boolean noElementChild(Node node) {
        NodeList children = node.getChildNodes();
        for (int i=0; i<children.getLength(); i++) {
            if (children.item(i).getNodeType()==Node.ELEMENT_NODE) {
                return false;
            }
        }
        return true;
    }

    /**
     * Parse key value
     * @param keyValue
     * @return
     * @throws Exception
     */
    private List<Pair<String, String>> keyValueParse(String keyValue) throws Exception {
        String lines[] = keyValue.split("\\r?\\n");
        List<Pair<String, String>> result = new ArrayList<Pair<String, String>>();
        //evaluate each pair
        for (int i=0; i<lines.length; i++) {
            if (lines[i].trim().length()>0) {
                String str = lines[i].trim();
                int idx = lines[i].trim().indexOf('=');
                if (idx==-1) {
                    throw new Exception("Syntax error");
                }
                else {
                    String key = str.substring(0, idx);
                    String value = str.substring(idx+1, str.length());
                    Pair<String, String> pair = new Pair(key, value);
                    result.add(pair);
                }
            }
        }
        return result;
    }
}
