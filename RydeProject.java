
public class RydeProject {
    public static void main(String[] args) throws Exception{
        SimpleGraph graph = new SimpleGraph();
        Database db = new Database(graph);
        Interface pInterface = new Interface(graph, "mapImage.png",db);
        pInterface.runGUI();
    }
}
