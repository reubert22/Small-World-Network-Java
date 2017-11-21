package src;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import org.graphstream.algorithm.ConnectedComponents;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

/**
 * @author Reubert
 */
public class Main {
    /**
     * Função responsável por adicionar os vértices ao grafo
     * @param g
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static void addVertex(Graph g) throws FileNotFoundException, IOException{
        BufferedReader in = new BufferedReader(new FileReader("VerticesFacebook.txt"));
        String row;
        row = in.readLine();
        
        /**Percorrendo o arquivo e settando os vértices
         * enquanto não chegar ao final, não para.
         */
        while (row != null) {
            g.addNode(row);
            row = in.readLine();
        }
        in.close();
    }
    
    /**
     * Função responsável por adicionar as arestas ao grafo
     * @param g
     * @throws IOException 
     */
    public static void addEdges(Graph g) throws IOException{
        BufferedReader in = new BufferedReader(new FileReader("ArestasFacebook.txt"));
        String row;
        row = in.readLine();
        
        /**
         * Separa as os vertices de origem e de destino
         * coloca-os em columnA and columnB
         */
        while (row != null) {
            int position = 0;
            String columnA, columnB;

            for (int i = 0; true; i++) {
                if (row.charAt(i) == '-') {
                    position = i;
                    break;
                }
            }

            columnA = row.substring(0, position);

            columnB = row.substring(position + 1, row.length());

            /**
             * Adicionamos a aresta (nome da aresta, origem, destino)
             */
            g.addEdge(row, columnA, columnB);

            /**
             * Avança uma linha do arquivo
             */
            row = in.readLine();
        }
        in.close();
    }
    
    /**
     * Função responsável por identificar e retornar o maior component (Maior grafo, dos gerados)
     * @param g
     * @return 
     */
    public static List<Node> getBiggerComponent(Graph g){
        ConnectedComponents conComp = new ConnectedComponents();
        conComp.init(g);
        
        List<Node> giantComponent = conComp.getGiantComponent();
        
        return giantComponent;
    }
    
    /**
     * Função responsável por calcular a quantidades de arestas do maior componente
     * @param biggerComponent
     * @return
     * @throws FileNotFoundException
     * @throws IOException 
     */
    private static int getQuantEdgeBiggerComponent(List<Node> biggerComponent) throws FileNotFoundException, IOException {
        String row, columnA;
        int position = 0;
        int counter = 0;
        
        BufferedReader in = new BufferedReader(new FileReader("ArestasFacebook.txt"));
        row = in.readLine();
        
        
        for(int i = 0; i < 40000; i++) {
            
            for (int k = 0; true; k++) {
                if (row.charAt(k) == '-') {
                    position = k;
                    break;
                }
            }

            columnA = row.substring(0, position);
            
            int j = 0;
            while(j < biggerComponent.size()) {
                
                if (biggerComponent.get(j).toString().equals(columnA)) {
                    counter++;
                }
                j++;
            }
            
            row = in.readLine();
        }
                
        in.close();
        return counter;
    }

    /**
     * Função responsável por calcular o comprimento médio do maior componente
     * @param biggerComponent
     * @param g
     * @return 
     */
    private static double calculateAverageLenght(List<Node> biggerComponent, Graph g) {
        double average = 0;
        
        Dijkstra dijkstra = new Dijkstra();
        dijkstra.init(g);
        dijkstra.setSource(biggerComponent.get(0));
        dijkstra.compute();
        
        /**
         * Percorre o maior componente e calcula a distancia de Dij, de cada
         */
        for(Node node : biggerComponent){
            average += dijkstra.getPathLength(node);
        }
        
        /**
         * Retorna a função calculando a distância média entre todos pares de vértices
         */
        return (1/((0.5) * biggerComponent.size() * (biggerComponent.size() + 1))) * average;
    }
    
    /**
     * Função responsável por imprimir os parametros requisitados
     * @param quantEdges
     * @param averageLenght
     * @param biggerComponentSize 
     */
    public static void printParameters(int quantEdges, double averageLenght, int biggerComponentSize){
        System.out.println("..:Tamanho do maior componente:..");
        System.out.println("Total Vértices: " + biggerComponentSize);
        System.out.println("Total Arestas: " + quantEdges+"\n");
        
        System.out.printf("Distância média: %.7f \n", averageLenght);
    }
    
    public static void gerarGrafo(Graph g){
        g.display();
    }
    
    public static void main(String[] args) throws IOException {
        Graph g = new SingleGraph("Grafo Amizades Facebook");
        addVertex(g);
        addEdges(g);
        
        for(Node n: g){
            n.addAttribute("label", n.getId());
        }
        
        
        List<Node> biggerComponent = getBiggerComponent(g);
        
        int quantEdges = getQuantEdgeBiggerComponent(biggerComponent);
        double averageLenght = calculateAverageLenght(biggerComponent, g);
        int biggerComponentSize = biggerComponent.size();
        
        gerarGrafo(g);
        printParameters(quantEdges, averageLenght, biggerComponentSize);
    }
}
