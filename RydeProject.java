// the main class of the project
public class RydeProject {
    public static void main(String[] args) throws Exception{
        SimpleGraph graph = new SimpleGraph();
        Interface pInterface = new Interface(graph, "mapImage.png");
        pInterface.runGUI();
    }
}
